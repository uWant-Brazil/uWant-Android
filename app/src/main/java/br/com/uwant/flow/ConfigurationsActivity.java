package br.com.uwant.flow;/*
 * Copyright (C) 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Cleibson Gomes
 * @date {07/08/14}
 *
 */

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.models.classes.SocialProvider;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.ExcludeAccountModel;
import br.com.uwant.models.cloud.models.SocialLinkModel;
import br.com.uwant.models.databases.UserDatabase;

public class ConfigurationsActivity extends PreferenceActivity {

    private static final String EXIT_DIALOG = "Exit_Dialog";
    private static final String CONST_HAS_HEADERS = "hasHeaders";
    private static final String CONST_LOAD_HEADERS_FROM_RESOURCE = "loadHeadersFromResource";
    private static final String ID_BUTTON_EXCLUDE = "buttonExclude";
    private static final String ID_BUTTON_FACEBOOK = "buttonFacebook";
    private static final String ID_PREFERENCE_VERSION = "preferenceVersion";
    private static final String ID_PREFERENCE_SUPPORT = "preferenceSupport";
    private static final String ID_PREFERENCE_SITE = "preferenceSite";
    private static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email", "user_birthday", "user_friends");

    final Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                // FIXME Compatibility
                //mProgressDialog = ProgressFragmentDialog.show(getFragmentManager());

                com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(final GraphUser graphUser, com.facebook.Response response) {
                        if (graphUser != null) {
                            final String id = graphUser.getId();

                            final String login = graphUser.getUsername();
                            final String mail = (String) graphUser.getProperty("email");

                            final SocialLinkModel model = new SocialLinkModel();
                            model.setLogin(login == null ? mail : login);
                            model.setProvider(SocialProvider.FACEBOOK);
                            model.setToken(session.getAccessToken());
                            model.setFacebookId(id);

                            Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

                                @Override
                                public void onPreExecute() {
                                }

                                @Override
                                public void onExecute(Boolean linked) {
                                    dismissProgress();
                                    updateUserFacebookToken(linked, model.getToken(), model.getFacebookId());
                                }

                                @Override
                                public void onError(RequestError error) {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(ConfigurationsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            });
                        }
                    }

                }).executeAsync();
            } else if (session.isClosed() && mProgressDialog.isShowing()) {
                dismissProgress();
            }
        }

    };

    private void updateUserFacebookToken(Boolean linked, String token, String id) {
        User user = User.getInstance();
        if (linked) {
            setResult(RESULT_OK);

            user.setFacebookToken(token);
            user.setFacebookId(id);
            Toast.makeText(this, R.string.text_link_facebook, Toast.LENGTH_LONG).show();
        } else {
            user.setFacebookToken(null);
            user.setFacebookId(null);
            Toast.makeText(this, R.string.text_unlink_facebook, Toast.LENGTH_LONG).show();
        }

        UserDatabase udb = new UserDatabase(this);
        udb.update(user);
    }

    private void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ProgressDialog mProgressDialog;
    private Method mHeaders = null;
    private Method mHasHeaders = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Session session = Session.getActiveSession();

        if (session == null && savedInstanceState != null) {
            session = Session.restoreSession(this, null, callback, savedInstanceState);
            Session.setActiveSession(session);
        }

        try {
            mHeaders = getClass().getMethod(CONST_LOAD_HEADERS_FROM_RESOURCE, int.class, List.class );
            mHasHeaders = getClass().getMethod(CONST_HAS_HEADERS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (!isNewV11Prefs()) {
            mapEarlierThenV11();
        }

        if (getIntent().getBooleanExtra("executeFacebook", false)) {
            executeFacebook();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    private void mapEarlierThenV11() {
        addPreferencesFromResource(R.layout.preferences_account);

        Preference button = findPreference(ID_BUTTON_EXCLUDE);
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                ExcludeAccountModel model = new ExcludeAccountModel();
                Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

                    @Override
                    public void onPreExecute() {
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(ConfigurationsActivity.this,
                                    getString(R.string.app_name),
                                    getString(R.string.text_wait),
                                    true,
                                    false);
                        } else {
                            mProgressDialog.show();
                        }
                    }

                    @Override
                    public void onExecute(Boolean result) {
                        dismissProgress();

                        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigurationsActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.text_exclude_account_success)
                                .setNeutralButton(R.string.text_ok, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(ConfigurationsActivity.this, AuthenticationActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                    }

                                });
                        builder.create().show();
                    }

                    @Override
                    public void onError(RequestError error) {
                        dismissProgress();
                    }

                });
                return true;
            }

        });

        Preference preferenceFacebook = findPreference(ID_BUTTON_FACEBOOK);
        preferenceFacebook.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                executeFacebook();
                return true;
            }

        });
    }

    private void executeFacebook() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this,
                    getString(R.string.app_name),
                    getString(R.string.text_wait),
                    true,
                    false);
        } else {
            mProgressDialog.show();
        }

        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isOpened() && !session.isClosed()) {
                if (session.getPermissions().containsAll(FACEBOOK_PERMISSIONS)) {
                    session.openForRead(new Session.OpenRequest(this)
                            .setPermissions(FACEBOOK_PERMISSIONS)
                            .setCallback(callback));
                } else {
                    session.requestNewReadPermissions(new Session.NewPermissionsRequest(this, FACEBOOK_PERMISSIONS));
                }
            } else {
                Session.openActiveSession(this, true, FACEBOOK_PERMISSIONS, callback);
            }
        } else {
            Session.openActiveSession(this, true, FACEBOOK_PERMISSIONS, callback);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        try {
            mHeaders.invoke(this,new Object[]{R.xml.configurations_header, target});
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        super.onBuildHeaders(target);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (position == 2) {
            Intent it = new Intent(this, AboutActivity.class);
            startActivity(it);
        } else if (position == 3) {
            askForLogoff();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private void askForLogoff() {
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(RESULT_FIRST_USER);
                finish();
            }

        };

        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_default, null);
        TextView titleView = (TextView) contentView.findViewById(R.id.alertTitle);
        titleView.setText(R.string.text_attention);
        titleView.setTextColor(getResources().getColor(R.color.BLACK));

        TextView messageView = (TextView) contentView.findViewById(R.id.message);
        messageView.setText(R.string.text_exit_message);

        final ImageView imageViewIcon = (ImageView) contentView.findViewById(R.id.icon);
        imageViewIcon.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(contentView)
                .setPositiveButton(R.string.text_yes, positiveListener)
                .setNegativeButton(R.string.text_no, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isNewV11Prefs() {
        if (mHasHeaders != null && mHeaders != null) {
            try {
                return (Boolean) mHasHeaders.invoke(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        if (session != null) {
            session.saveSession(session, outState);
        }
    }

    /**
     * Configurations Fragment
     */
    public static class ConfigurationsFragment extends PreferenceFragment {

        private ProgressDialog mProgressDialog;

        final Session.StatusCallback callback = new Session.StatusCallback() {

            @Override
            public void call(final Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(final GraphUser graphUser, Response response) {
                            if (graphUser != null) {
                                final String id = graphUser.getId();

                                final String login = graphUser.getUsername();
                                final String mail = (String) graphUser.getProperty("email");

                                final SocialLinkModel model = new SocialLinkModel();
                                model.setLogin(login == null ? mail : login);
                                model.setProvider(SocialProvider.FACEBOOK);
                                model.setToken(session.getAccessToken());
                                model.setFacebookId(id);

                                Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

                                    @Override
                                    public void onPreExecute() {
                                    }

                                    @Override
                                    public void onExecute(Boolean linked) {
                                        dismissProgress();
                                        updateUserFacebookToken(linked, model.getToken(), model.getFacebookId());
                                    }

                                    @Override
                                    public void onError(RequestError error) {
                                        dismissProgress();

                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                });
                            } else {
                                dismissProgress();
                            }
                        }

                    }).executeAsync();
                } else if (session.isClosed()) {
                    dismissProgress();
                }
            }

        };

        private void dismissProgress() {
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void updateUserFacebookToken(Boolean linked, String token, String id) {
            User user = User.getInstance();
            if (linked) {
                user.setFacebookToken(token);
                user.setFacebookId(id);
                Toast.makeText(getActivity(), R.string.text_link_facebook, Toast.LENGTH_LONG).show();
            } else {
                user.setFacebookToken(null);
                user.setFacebookId(null);
                Toast.makeText(getActivity(), R.string.text_unlink_facebook, Toast.LENGTH_LONG).show();
            }

            UserDatabase udb = new UserDatabase(getActivity());
            udb.update(user);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preferences_account);

            Preference preference = findPreference(ID_BUTTON_EXCLUDE);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ExcludeAccountModel model = new ExcludeAccountModel();
                    Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

                        @Override
                        public void onPreExecute() {
                            if (mProgressDialog == null) {
                                mProgressDialog = ProgressDialog.show(getActivity(),
                                        getString(R.string.app_name),
                                        getString(R.string.text_wait),
                                        true,
                                        false);
                            }
                        }

                        @Override
                        public void onExecute(Boolean result) {
                            dismissProgress();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.app_name)
                                    .setMessage(R.string.text_exclude_account)
                                    .setNeutralButton(R.string.text_ok, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                        }

                                    });
                            builder.create().show();
                        }

                        @Override
                        public void onError(RequestError error) {
                            dismissProgress();
                        }

                    });
                    return true;
                }

            });

            Preference preferenceFacebook = findPreference(ID_BUTTON_FACEBOOK);
            preferenceFacebook.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    executeFacebook();
                    return true;
                }

            });
        }

        private void executeFacebook() {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(getActivity(),
                        getString(R.string.app_name),
                        getString(R.string.text_wait),
                        true,
                        false);
            } else {
                mProgressDialog.show();
            }

            Session session = Session.getActiveSession();
            if (session != null) {
                if (!session.isOpened() && !session.isClosed()) {
                    if (session.getPermissions().containsAll(FACEBOOK_PERMISSIONS)) {
                        session.openForRead(new Session.OpenRequest(getActivity())
                                .setPermissions(FACEBOOK_PERMISSIONS)
                                .setCallback(callback));
                    } else {
                        session.requestNewReadPermissions(new Session.NewPermissionsRequest(getActivity(), FACEBOOK_PERMISSIONS));
                    }
                } else {
                    Session.openActiveSession(getActivity(), true, FACEBOOK_PERMISSIONS, callback);
                }
            } else {
                Session.openActiveSession(getActivity(), true, FACEBOOK_PERMISSIONS, callback);
            }
        }

    }

    /**
     * Informations Fragment
     */
    public static class InformationsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preferences_informations);

            String versionName;
            try {
                 versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                versionName = "ERROR! :-(";
            }

            Preference preferenceVersion = findPreference(ID_PREFERENCE_VERSION);
            preferenceVersion.setSummary(versionName);

            Preference preferenceSupport = findPreference(ID_PREFERENCE_SUPPORT);
            preferenceSupport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "[uWant] Android Support");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { preference.getSummary().toString() });
                    startActivity(Intent.createChooser(intent, "Suporte"));
                    return true;
                }

            });

            Preference preferenceSite = findPreference(ID_PREFERENCE_SITE);
            preferenceSite.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(preference.getSummary().toString()));
                    startActivity(intent);
                    return true;
                }

            });
        }

    }


}
