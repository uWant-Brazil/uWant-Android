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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.ExcludeAccountModel;

public class ConfigurationsActivity extends PreferenceActivity {

    public static final int RESULT_EXCLUDE = -9854;

    private ProgressDialog mProgressDialog;
    private Method mHeaders = null;
    private Method mHasHeaders = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            mHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class );
            mHasHeaders = getClass().getMethod("hasHeaders");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (!isNewV11Prefs()) {
            mapEarlierThenV11();
        }
    }

    private void mapEarlierThenV11() {
        addPreferencesFromResource(R.layout.activity_preferences);

        Preference button = findPreference("buttonExclude");
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
                        }
                    }

                    @Override
                    public void onExecute(Boolean result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigurationsActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage("Sua conta acaba de ser desativada do uWant.")
                                .setNeutralButton(R.string.text_ok, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setResult(RESULT_EXCLUDE);
                                        finish();
                                    }

                                });
                        builder.create().show();
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
                    }

                });
                return true;
            }

        });
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

    public static class ConfigurationsFragment extends PreferenceFragment {

        private ProgressDialog mProgressDialog;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.activity_preferences);

            Preference preference = findPreference("buttonExclude");
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
                            try {
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
                            try {
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    return true;
                }

            });

        }

    }

}
