package br.com.uwant.flow;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.FeedCommentFragment;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.ActionReportModel;
import br.com.uwant.models.cloud.models.BlockFriendModel;
import br.com.uwant.models.cloud.models.ExcludeFriendModel;
import br.com.uwant.models.cloud.models.FeedsModel;
import br.com.uwant.models.cloud.models.ShareModel;
import br.com.uwant.models.cloud.models.WantModel;
import br.com.uwant.utils.PictureUtil;
import br.com.uwant.utils.UserUtil;

public class PerfilActivity extends UWActivity implements View.OnClickListener,
        IRequest.OnRequestListener<List<Action>>, PopupMenu.OnMenuItemClickListener {

    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_END_INDEX = 20;

    private Person mPerson;
    private List<Action> mActions;
    private Action mActionSelected;
    private Multimedia mMultimedia;

    private View mFadeView;
    private FeedsAdapter mAdapter;
    private ImageView mImageViewPicture;
    private ImageView mImageViewPictureDetail;
    private ProgressFragmentDialog mProgressDialog;

    private final Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                UserUtil.shareFacebook(PerfilActivity.this, this, mProgressDialog, mActionSelected.getWishList(), mMultimedia);
            } else if (session.isClosed() && !session.isOpened()) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        }

    };

    private final IRequest.OnRequestListener<Action> LISTENER_WANT = new IRequest.OnRequestListener<Action>() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onExecute(Action action) {
        }

        @Override
        public void onError(RequestError error) {
            // TODO ... Caso de erro? Voltar? Rollback?
        }

    };
    private final IRequest.OnRequestListener<Action> LISTENER_SHARE = new IRequest.OnRequestListener<Action>() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onExecute(Action action) {
        }

        @Override
        public void onError(RequestError error) {
            // TODO ... Caso de erro? Voltar? Rollback?
        }

    };
    private final IRequest.OnRequestListener<Boolean> LISTENER_REPORT = new IRequest.OnRequestListener<Boolean>() {

        private ProgressFragmentDialog mProgressDialog;

        @Override
        public void onPreExecute() {
            mProgressDialog = ProgressFragmentDialog.show(PerfilActivity.this.getSupportFragmentManager());
        }

        @Override
        public void onExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(PerfilActivity.this, R.string.text_report_activity, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(RequestError error) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(PerfilActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };
    private final IRequest.OnRequestListener<Boolean> LISTENER_EXCLUDE_BLOCK = new IRequest.OnRequestListener<Boolean>() {

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onExecute(Boolean result) {
            updateFeeds();
        }

        @Override
        public void onError(RequestError error) {
            Toast.makeText(PerfilActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        boolean isMe;
        if (getIntent().hasExtra(Person.EXTRA)) {
            mPerson = (Person) getIntent().getSerializableExtra(Person.EXTRA);
            isMe = false;
        } else {
            mPerson = User.getInstance();
            isMe = true;
        }

        setContentView(R.layout.activity_perfil);

        ImageView imageAdd = (ImageView) findViewById(R.id.perfil_imageView_add_friend);

        if (!isMe) {
            imageAdd.setVisibility(View.VISIBLE);
//            imageAdd.setImageResource(mPerson.isFriend() ? 0 : 1);
        }

        TextView textViewName = (TextView) findViewById(R.id.perfil_textView_name);
        textViewName.setText(mPerson.getName());

        TextView textViewMail = (TextView) findViewById(R.id.perfil_textView_mail);
        textViewMail.setText(mPerson.getMail());

        mActions = new ArrayList<Action>(10);
        mAdapter = new FeedsAdapter(this, mActions, this);

        ListView mListViewFeeds = (ListView) findViewById(R.id.perfil_listView_feeds);
        mListViewFeeds.setAdapter(mAdapter);

        mFadeView = findViewById(R.id.main_frameLayout_fade);

        mImageViewPicture = (ImageView) findViewById(R.id.perfil_imageView_picture);
        mImageViewPictureDetail = (ImageView) findViewById(R.id.perfil_imageView_pictureDetail);

        User user = User.getInstance();
        final Multimedia picture = user.getPicture();
        if (picture != null) {
            Bitmap bitmap = picture.getBitmap();
            String url = picture.getUrl();
            if (bitmap != null) {
                mImageViewPicture.setImageBitmap(bitmap);
                mImageViewPictureDetail.setVisibility(View.VISIBLE);
            } else if (url != null && !url.isEmpty()) {
                float dpi = getResources().getDisplayMetrics().density;
                int size = (int) (dpi * 76);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .cacheOnDisk(true)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .considerExifParams(true)
                        .displayer(new FadeInBitmapDisplayer(300))
                        .build();
                ImageSize imageSize = new ImageSize(size, size);

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadImage(url, imageSize, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        loadedImage = PictureUtil.cropToFit(loadedImage);
                        loadedImage = PictureUtil.scale(loadedImage, mImageViewPicture);
                        loadedImage = PictureUtil.circle(loadedImage);

                        mImageViewPicture.setImageBitmap(loadedImage);
                        mImageViewPictureDetail.setVisibility(View.VISIBLE);

                        picture.setBitmap(loadedImage);
                    }

                });
            }
        }

        if (mFadeView == null) {
            mFadeView = getLayoutInflater().inflate(R.layout.fade_in_out, null);
            addContentView(mFadeView, new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateFeeds();
    }

    private void updateFeeds() {
        mActions.clear();
        mAdapter.notifyDataSetChanged();

        FeedsModel model = new FeedsModel();
        model.setPerson(this.mPerson);
        model.setStartIndex(DEFAULT_START_INDEX);
        model.setEndIndex(DEFAULT_END_INDEX);

        Requester.executeAsync(model, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        Action action = null;
        if (position != null)
            action = mAdapter.getItem(position);

        switch (view.getId()) {
            case R.id.adapter_feeds_button_uwants:
                WantModel wantModel = new WantModel();
                wantModel.setAction(action);

                Requester.executeAsync(wantModel, LISTENER_WANT);
                toggleWant(action);
                break;

            case R.id.adapter_feeds_button_comments:
                showComments(action);
                break;

            case R.id.adapter_feeds_button_shares:
                if (UserUtil.hasFacebook()) {
                    performShare(action);
                } else {
                    UserUtil.showFacebookDialog(this);
                }
                break;

            case R.id.adapter_feeds_imageButton:
                openPopUp(view, action);
                break;

            default:
                break;
        }
    }

    private void performShare(Action action) {
        shareOnFacebook(action);

        ShareModel shareModel = new ShareModel();
        shareModel.setAction(action);

        Requester.executeAsync(shareModel, LISTENER_SHARE);
        toggleShare(action);
    }

    private void shareOnFacebook(Action action) {
        this.mProgressDialog = ProgressFragmentDialog.show(R.string.text_sharing, getSupportFragmentManager());

        this.mActionSelected = action;
        this.mMultimedia = action.getWishList().getProducts().get(0).getPicture();
        UserUtil.shareFacebook(this, callback, mProgressDialog, mActionSelected.getWishList(), mMultimedia);
    }

    private void openPopUp(View v, Action action) {
        this.mActionSelected = action;

        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.feed_actions, popup.getMenu());
        popup.show();
    }

    private void showComments(Action action) {
        mActionSelected = action;
        FeedCommentFragment fragment = FeedCommentFragment.newInstance(action);

        mFadeView.setClickable(true);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top, R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
                .replace(android.R.id.content, fragment, FeedCommentFragment.TAG)
                .addToBackStack(FeedCommentFragment.TAG)
                .commit();
    }

    private void toggleShare(Action action) {
        int count = action.getSharesCount();
        if (count >= 0) {
            action.setSharesCount(++count);
            action.setuShare(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void toggleWant(Action action) {
        boolean isWanted = action.isuWant();
        int count = action.getUWantsCount();
        if (!isWanted || count > 0) { // Equivalente a [!isWanted || (count > 0 && isWanted)]
            action.setUWantsCount(isWanted ? --count : ++count);
            action.setuWant(!isWanted);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPreExecute() {
        mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
    }

    @Override
    public void onExecute(List<Action> result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        this.mActions.addAll(result);
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RequestError error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (this.mActionSelected != null) {
            switch (item.getGroupId()) {
                case R.id.group_action:
                    switch (item.getItemId()) {
                        case R.id.menu_report:
                            report();
                            break;
                    }
                    return true;

                case R.id.group_friend:
                    switch (item.getItemId()) {
                        case R.id.menu_activities:
                            cancelActivities();
                            break;

                        case R.id.menu_exclude:
                            excludeFriend();
                            break;
                    }
                    return true;
            }
        }
        return false;
    }

    private void excludeFriend() {
        Person person = this.mActionSelected.getFrom();
        ExcludeFriendModel model = new ExcludeFriendModel();
        model.setPerson(person);
        Requester.executeAsync(model, this.LISTENER_EXCLUDE_BLOCK);
    }

    private void cancelActivities() {
        Person person = this.mActionSelected.getFrom();
        BlockFriendModel model = new BlockFriendModel();
        model.setPerson(person);
        Requester.executeAsync(model, this.LISTENER_EXCLUDE_BLOCK);
    }

    private void report() {
        ActionReportModel model = new ActionReportModel();
        model.setAction(this.mActionSelected);
        Requester.executeAsync(model, this.LISTENER_REPORT);
    }

}
