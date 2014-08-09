package br.com.uwant.flow;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.adapters.DrawerAdapter;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.adapters.FriendsCircleAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.LogoffModel;
import br.com.uwant.models.cloud.models.UserSearchModel;
import br.com.uwant.utils.DebugUtil;
import br.com.uwant.utils.PictureUtil;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private GridView mGridView;
    private DrawerAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private EditText mEditTextSearch;
    private ImageView mImageViewPicture;
    private ImageView mImageViewPictureDetail;
    private TextView mTextViewUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FeedsAdapter mFeedsAdapter = new FeedsAdapter(this);

        mGridView = (GridView) findViewById(R.id.main_gridView);
        mGridView.setNumColumns(GridView.AUTO_FIT);
        mGridView.setAdapter(mFeedsAdapter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        final View view = getLayoutInflater().inflate(R.layout.view_drawer_header, mDrawerList, false);
        mEditTextSearch = (EditText) view.findViewById(R.id.drawer_editText_search);
        mImageViewPicture = (ImageView) view.findViewById(R.id.drawer_imageView_picture);
        mImageViewPictureDetail = (ImageView) view.findViewById(R.id.drawer_imageView_pictureDetail);
        mTextViewUserName = (TextView) view.findViewById(R.id.drawer_textView_userName);
        mDrawerList.addHeaderView(view);

        User user = User.getInstance();
        String name = user.getName();
        if (name != null) {
            mTextViewUserName.setText(name);
        }

        Multimedia picture = user.getPicture();
        if (picture != null) {
            String url = picture.getUrl();
            Picasso.with(this).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    bitmap = PictureUtil.cropToFit(bitmap);
                    bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                    bitmap = PictureUtil.circle(bitmap);

                    mImageViewPicture.setImageBitmap(bitmap);
                    mImageViewPictureDetail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        mDrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.uwantnavigationdrawer_ic_navigation_drawer, R.string.text_open, R.string.text_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mEditTextSearch.addTextChangedListener(new TextWatcher() {

            private boolean mIsCanceled;
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mIsCanceled = true;
                    mDrawerList.setAdapter(mDrawerAdapter);
                } else {
                    mIsCanceled = false;
                    mSearchFriends.clear();
                    mSearchAdapter.notifyDataSetChanged();

                    UserSearchModel model = new UserSearchModel();
                    model.setQuery(s.toString());
                    Requester.executeAsync(model, this.searchListener);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
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
        Intent it;
        switch (position) {
            case 2:
                it = new Intent(this, ConfigurationsActivity.class);
                break;

            case 3:
                it = new Intent(this, AboutActivity.class);
                break;

            case 4:
                askForLogoff();
                // Deixar sem break para que a intent seja nula!

            default:
                it = null;
                break;
        }

        if (it != null) {
            startActivity(it);
        }
    }

    private void askForLogoff() {
        String title = getString(R.string.text_attention);
        String message = getString(R.string.text_exit_message);
        String positiveText = getString(R.string.text_yes);
        String negativeText = getString(R.string.text_no);
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performLogoff();
            }

        };
        AlertFragmentDialog afd = AlertFragmentDialog.create(title, message, positiveText, positiveListener, negativeText, null);
        afd.show(getSupportFragmentManager(), "Exit_Dialog");
    }

    private void performLogoff() {
        LogoffModel model = new LogoffModel();
        Requester.executeAsync(model, new IRequest.OnRequestListener() {

            public ProgressFragmentDialog progressFragmentDialog;

            @Override
            public void onPreExecute() {
                progressFragmentDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
            }

            @Override
            public void onExecute(Object result) {
                if (progressFragmentDialog != null) {
                    progressFragmentDialog.dismiss();
                }

                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(RequestError error) {
                if (progressFragmentDialog != null) {
                    progressFragmentDialog.dismiss();
                }

                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }

        });
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

}