package br.com.uwant.flow;

<<<<<<< HEAD
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.uwant.R;
import br.com.uwant.models.adapters.DrawerAdapter;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.User;
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

        View view = getLayoutInflater().inflate(R.layout.view_drawer_header, mDrawerList, false);
        mEditTextSearch = (EditText)view.findViewById(R.id.drawer_editText_search);
        mImageViewPicture = (ImageView)view.findViewById(R.id.drawer_imageView_picture);
        mImageViewPictureDetail = (ImageView)view.findViewById(R.id.drawer_imageView_pictureDetail);
        mTextViewUserName = (TextView)view.findViewById(R.id.drawer_textView_userName);
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

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        mDrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.uwantnavigationdrawer_ic_navigation_drawer, R.string.text_open, R.string.text_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mEditTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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

            default:
                it = null;
                break;
        }

        if (it != null) {
            startActivity(it);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId())
        {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
=======
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import br.com.uwant.R;
import br.com.uwant.utils.DebugUtil;

public class MainActivity extends ActionBarActivity {

    SlidingPaneLayout pane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new PaneListener());

        if (!pane.isSlideable()) {
            getFragmentManager().findFragmentById(R.id.fragmet_left).setHasOptionsMenu(false);
            getFragmentManager().findFragmentById(R.id.fragmet_right).setHasOptionsMenu(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class PaneListener implements SlidingPaneLayout.PanelSlideListener{

        @Override
        public void onPanelSlide(View view, float v) {
            DebugUtil.debug("Panel sliding");
        }

        @Override
        public void onPanelOpened(View view) {
            DebugUtil.debug("Panel opened");
            getFragmentManager().findFragmentById(R.id.fragmet_left).setHasOptionsMenu(true);
            getFragmentManager().findFragmentById(R.id.fragmet_right).setHasOptionsMenu(false);
        }

        @Override
        public void onPanelClosed(View view) {
            DebugUtil.debug("Panel closed");
            getFragmentManager().findFragmentById(R.id.fragmet_left).setHasOptionsMenu(false);
            getFragmentManager().findFragmentById(R.id.fragmet_right).setHasOptionsMenu(true);
        }
>>>>>>> FETCH_HEAD
    }
}
