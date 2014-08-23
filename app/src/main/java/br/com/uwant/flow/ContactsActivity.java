package br.com.uwant.flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AgendaFragment;
import br.com.uwant.flow.fragments.ContactsFragment;
import br.com.uwant.flow.fragments.FacebookFragment;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.models.ContactsModelAbstract;

public class ContactsActivity extends ActionBarActivity implements View.OnClickListener {

    private static String[] TABS;

    private ContactsPagerAdapter mAdapter;
    private ViewPager mViewPager;
    protected static List<Person> mFacebookPersons;
    private boolean mIsFromPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Intent it = getIntent();
        if (it.hasExtra(Person.EXTRA)) {
            TABS = getResources().getStringArray(R.array.options_contacts_with_facebook);
            this.mFacebookPersons = (ArrayList<Person>) it.getSerializableExtra(Person.EXTRA);
        } else {
            TABS = getResources().getStringArray(R.array.options_contacts);
        }

        mIsFromPerfil = it.getBooleanExtra(User.EXTRA_ADD_CONTACTS, false);
        if (mIsFromPerfil) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_contacts);

        final Button buttonDone = (Button) findViewById(R.id.contacts_button_done);
        buttonDone.setOnClickListener(this);

        mAdapter = new ContactsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.contacts_viewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mIsFromPerfil) {
            getMenuInflater().inflate(R.menu.contacts_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_contacts_skip:
                skipToFeeds();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void skipToFeeds() {
        if (mIsFromPerfil) {
            setResult(RESULT_OK);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contacts_button_done:
                sendContacts();
                break;

            default:
                break;
        }
    }

    private void sendContacts() {
        boolean canSkip = false;
        List<String> contacts = new ArrayList<String>();
        for (int i = 0;i < TABS.length;i++) {
            Fragment f = mAdapter.getItem(i);
            if (f instanceof ContactsFragment) {
                ContactsFragment cf = (ContactsFragment) f;
                if (cf.hasPersons()) {
                    canSkip = true;
                    List<String> emails = cf.getCheckedContacts();
                    contacts.addAll(emails);
                }
            }
        }

        if (canSkip) {
            if (contacts != null && contacts.size() > 0) {
                ContactsModelAbstract model = new ContactsModelAbstract();
                model.setEmails(contacts);

                Requester.executeAsync(model);
            }

            skipToFeeds();
        } else {
            int textId;
            if (mIsFromPerfil) {
                textId = R.string.text_contacts_from_perfil_invitation;
            } else {
                textId = R.string.text_contacts_invitation;
            }

            Toast.makeText(this, textId, Toast.LENGTH_SHORT).show();
        }
    }

    private static class ContactsPagerAdapter extends FragmentStatePagerAdapter {

        private SparseArray<ContactsFragment> mFragments;

        public ContactsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.mFragments = new SparseArray<ContactsFragment>(getCount());
        }

        @Override
        public int getCount() {
            return TABS.length;
        }

        @Override
        public Fragment getItem(int i) {
            ContactsFragment fragment = this.mFragments.get(i);
            if (fragment == null) {
                switch (i) {
                    case 0:
                        fragment = new AgendaFragment();
                        break;

                    case 1:
                        fragment = FacebookFragment.newInstance(mFacebookPersons);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0;i < TABS.length;i++) {
            Fragment f = mAdapter.getItem(i);
            if (f instanceof ContactsFragment) {
                ContactsFragment cf = (ContactsFragment) f;
                cf.cancelTask();
            }
        }
    }

}
