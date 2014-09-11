package br.com.uwant.flow;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.FriendsCircleFragment;
import br.com.uwant.flow.fragments.WishListFragment;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.utils.PictureUtil;

public class PerfilActivity extends ActionBarActivity {

    private Person mPerson;
    private static String[] TABS;

    private PerfilPagerAdapter mAdapter;
    private SearchView.OnQueryTextListener mCurrentFragment;
    private SearchView mSearchView;
    private ImageView mImageViewPictureDetail;
    private ImageView mImageViewPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        TABS = getResources().getStringArray(R.array.options_perfil);

        if (getIntent().hasExtra(Person.EXTRA)) {
            mPerson = (Person) getIntent().getSerializableExtra(Person.EXTRA);
        } else {
            mPerson = User.getInstance();
        }

        setContentView(R.layout.activity_perfil);

        mImageViewPicture = (ImageView) findViewById(R.id.perfil_imageView_picture);
        mImageViewPictureDetail = (ImageView) findViewById(R.id.perfil_imageView_pictureDetail);

        mAdapter = new PerfilPagerAdapter(getSupportFragmentManager());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.perfil_viewPager);
        viewPager.setAdapter(mAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentFragment = (SearchView.OnQueryTextListener) mAdapter.getItem(i);
                actionBar.setSelectedNavigationItem(i);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mCurrentFragment = (SearchView.OnQueryTextListener) mAdapter.getItem(tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
                supportInvalidateOptionsMenu();
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

        };

        for (String tabName : TABS) {
            ActionBar.Tab tab = actionBar.newTab();
            tab.setText(tabName);
            tab.setTag(tabName);
            tab.setTabListener(tabListener);
            actionBar.addTab(tab);
        }

        String name = mPerson.getName();
        Multimedia multimedia = mPerson.getPicture();

        final TextView textViewName = (TextView) findViewById(R.id.perfil_textView_name);
        textViewName.setText(name);

        if (multimedia != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(multimedia.getUrl(), mImageViewPicture, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                    mImageViewPictureDetail.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                    bitmap = PictureUtil.cropToFit(bitmap);
                    bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                    bitmap = PictureUtil.circle(bitmap);
                    mImageViewPicture.setImageBitmap(bitmap);
                    mImageViewPictureDetail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    mImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                    mImageViewPictureDetail.setVisibility(View.INVISIBLE);
                }

            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.perfil_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_perfil_search);
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        setupSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setupSearchView();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_perfil_search:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnQueryTextListener(mCurrentFragment);
        mSearchView.setQueryHint(getString(R.string.text_hint_perfil_search));
    }

    private class PerfilPagerAdapter extends FragmentStatePagerAdapter {

        private SparseArray<Fragment> mFragments;

        public PerfilPagerAdapter(FragmentManager fm) {
            super(fm);
            this.mFragments = new SparseArray<Fragment>(getCount());
        }

        @Override
        public int getCount() {
            return TABS.length;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = this.mFragments.get(i);

            if (fragment == null) {
                switch (i) {
                    case 0:
                        fragment = WishListFragment.newInstance(mPerson);
                        break;

                    case 1:
                        fragment = FriendsCircleFragment.newInstance(mPerson);
                        break;

                    default:
                        throw new RuntimeException();
                }
                this.mFragments.put(i, fragment);
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TABS[position];
        }
    }

}
