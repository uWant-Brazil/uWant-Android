package br.com.uwant.flow;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.FeedsFragment;
import br.com.uwant.flow.fragments.WishListButtonFragment;
import br.com.uwant.models.adapters.DrawerAdapter;
import br.com.uwant.models.adapters.FriendsCircleAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.UserSearchModel;
import br.com.uwant.utils.DebugUtil;
import br.com.uwant.utils.GoogleCloudMessageUtil;
import br.com.uwant.utils.KeyboardUtil;
import br.com.uwant.utils.PictureUtil;

public class MainActivity extends UWActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int RQ_DRAWER = 924;

    private boolean mIsCanceled;
    private FeedsFragment mFeedsFragment;
    private WishListButtonFragment mWishListButtonFragment;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DrawerAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private EditText mEditTextSearch;
    private ImageView mImageViewPicture;
    private ImageView mImageViewPictureDetail;
    private TextView mTextViewUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleCloudMessageUtil.registerAsync(this);

        if (DebugUtil.DEBUG_LOG) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    DebugUtil.debug("Facebook Hash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }

            } catch (PackageManager.NameNotFoundException e) {
                DebugUtil.debug("Facebook Hash: name not found - " + e.toString());
            } catch (NoSuchAlgorithmException e) {
                DebugUtil.debug("Facebook Hash: no such an algorithm - " + e.toString());
            }
        }

        setContentView(R.layout.activity_main);

        mFeedsFragment = new FeedsFragment();
        mWishListButtonFragment = new WishListButtonFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout_content, mFeedsFragment, FeedsFragment.TAG).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.feed_frameLayout_root, mWishListButtonFragment, WishListButtonFragment.TAG).commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        final View view = getLayoutInflater().inflate(R.layout.view_drawer_header, mDrawerList, false);
        mEditTextSearch = (EditText) view.findViewById(R.id.drawer_editText_search);
        mImageViewPicture = (ImageView) view.findViewById(R.id.drawer_imageView_picture);
        mImageViewPictureDetail = (ImageView) view.findViewById(R.id.drawer_imageView_pictureDetail);
        mTextViewUserName = (TextView) view.findViewById(R.id.drawer_textView_userName);
        mDrawerList.addHeaderView(view);

        final LinearLayout linearLayoutPerfil = (LinearLayout) view.findViewById(R.id.drawer_linearLayout_perfil);
        linearLayoutPerfil.setOnClickListener(this);

        mDrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.text_wishes);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.uwantnavigationdrawer_ic_navigation_drawer, R.string.text_open, R.string.text_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View view, float v) {
                KeyboardUtil.hide(mEditTextSearch);
            }

            @Override
            public void onDrawerOpened(View view) {
            }

            @Override
            public void onDrawerClosed(View view) {
                mEditTextSearch.setText("");
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }

        });

        mEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            private List<Person> mSearchFriends = new ArrayList<Person>();
            private FriendsCircleAdapter mSearchAdapter = new FriendsCircleAdapter(MainActivity.this, mSearchFriends);

            private IRequest.OnRequestListener<List<Person>> searchListener = new IRequest.OnRequestListener<List<Person>>() {

                @Override
                public void onPreExecute() {
                    mDrawerList.setAdapter(mSearchAdapter);
                }

                @Override
                public void onExecute(List<Person> result) {
                    if (!mIsCanceled && result.size() > 0) {
                        mSearchFriends.addAll(result);
                        mSearchAdapter.notifyDataSetChanged();
                    }
                    mIsCanceled = true;
                }

                @Override
                public void onError(RequestError error) {
                    mIsCanceled = true;
                    mDrawerList.setAdapter(mDrawerAdapter);
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }

            };

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        String s = mEditTextSearch.getText().toString();
                        if (s.length() > 2) {
                            mIsCanceled = false;
                            mSearchFriends.clear();
                            mSearchAdapter.notifyDataSetChanged();

                            UserSearchModel model = new UserSearchModel();
                            model.setQuery(mEditTextSearch.getText().toString());
                            Requester.executeAsync(model, searchListener);
                        } else {
                            mIsCanceled = true;
                            Toast.makeText(MainActivity.this, R.string.text_fill_three_characteres, Toast.LENGTH_LONG).show();
                        }

                        return true;
                    }
                }

                return false;
            }

        });

        mEditTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mDrawerList.setAdapter(mDrawerAdapter);
                    mEditTextSearch.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_DRAWER) {
            if (resultCode == RESULT_FIRST_USER) {
                performLogoff();
            }
        }
    }

    private void updateUser() {
        if (super.mIsLogOff) {
            return;
        }

        User user = User.getInstance();
        String name = user.getName();
        if (name != null) {
            mTextViewUserName.setText(name);
        }

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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        KeyboardUtil.hide(this.mEditTextSearch);

        Intent it = null;
        Adapter adapter = parent.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter hAdapter = (HeaderViewListAdapter) adapter;
            ListAdapter listAdapter = hAdapter.getWrappedAdapter();
            if (listAdapter instanceof FriendsCircleAdapter) {
                Person person = (Person) hAdapter.getItem(position);

                it = new Intent(this, PerfilActivity.class);
                it.putExtra(Person.EXTRA, person);
            } else {
                switch (position) {
                    case 1:
                        // Minhas Listas
                    case 2:
                        // Amigos
                        Toast.makeText(this, "Desativado...", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        it = new Intent(this, ConfigurationsActivity.class);
                        break;

                    default:
                        it = null;
                        break;
                }
            }
        }

        if (it != null) {
            startActivityForResult(it, RQ_DRAWER);
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        KeyboardUtil.hide(this.mEditTextSearch);

        switch (view.getId()) {
            case R.id.drawer_linearLayout_perfil:
                Intent it = new Intent(this, PerfilActivity.class);
                startActivity(it);

                mDrawerLayout.closeDrawer(mDrawerList);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            super.onBackPressed();
        }
    }

    public void fadeIn() {
        View fadeView = findViewById(R.id.main_frameLayout_fade);
        if (fadeView != null) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(600);

            fadeView.setVisibility(View.VISIBLE);
            fadeView.startAnimation(fadeIn);
        }
    }

    public void fadeOut() {
        View fadeView = findViewById(R.id.main_frameLayout_fade);
        if (fadeView != null) {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(600);

            fadeView.startAnimation(fadeOut);
            fadeView.setVisibility(View.GONE);
        }
    }

}