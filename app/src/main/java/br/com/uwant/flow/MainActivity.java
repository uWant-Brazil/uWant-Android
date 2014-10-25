package br.com.uwant.flow;

import android.content.DialogInterface;
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
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.FeedsFragment;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.adapters.DrawerAdapter;
import br.com.uwant.models.adapters.FriendsCircleAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.LogoffModel;
import br.com.uwant.models.cloud.models.UserSearchModel;
import br.com.uwant.models.databases.UserDatabase;
import br.com.uwant.utils.DebugUtil;
import br.com.uwant.utils.GoogleCloudMessageUtil;
import br.com.uwant.utils.KeyboardUtil;
import br.com.uwant.utils.PictureUtil;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String EXIT_DIALOG = "Exit_Dialog";
    private FeedsFragment mFeedsFragment;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout_content, mFeedsFragment, FeedsFragment.TAG).commit();

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
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadImage(url, new SimpleImageLoadingListener() {

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

        mDrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

                    mEditTextSearch.requestFocus();
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
                    case 3:
                        it = new Intent(this, ConfigurationsActivity.class);
                        break;

                    case 4:
                        it = new Intent(this, AboutActivity.class);
                        break;

                    case 5:
                        askForLogoff();
                        // Deixar sem break para que a intent seja nula!

                    default:
                        it = null;
                        break;
                }
            }
        }

        if (it != null) {
            startActivity(it);
        }

        mDrawerLayout.closeDrawer(mDrawerList);
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
        afd.show(getSupportFragmentManager(), EXIT_DIALOG);
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

                UserDatabase db = new UserDatabase(MainActivity.this);
                db.removeAll();
                User.clearInstance();

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

    @Override
    public void onClick(View view) {
        KeyboardUtil.hide(this.mEditTextSearch);

        switch (view.getId()) {
            case R.id.drawer_linearLayout_perfil:
                Intent it = new Intent(this, PerfilActivity.class);
                startActivity(it);
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
}