package br.com.uwant.flow;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.SocialProvider;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.AuthModel;
import br.com.uwant.models.cloud.models.RecoveryPasswordModel;
import br.com.uwant.models.cloud.models.SocialRegisterModel;
import br.com.uwant.models.databases.UserDatabase;
import br.com.uwant.utils.GoogleCloudMessageUtil;
import br.com.uwant.utils.KeyboardUtil;

public class AuthenticationActivity extends FragmentActivity implements View.OnClickListener, IRequest.OnRequestListener<User> {

    private static final int REQUEST_REGISTER = 8724;
    private static final String TAG_RECOVERY_PASSWORD = "RecuperarSenhaTag";
    private static final String TAG_ATTENTION_DIALOG = "AtencaoDialog";
    private static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email", "user_birthday", "user_friends");

    private ProgressFragmentDialog mProgressDialog;

    final Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());

                com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(final GraphUser graphUser, com.facebook.Response response) {
                        if (graphUser != null) {
                            final String id = graphUser.getId();

                            final String login = graphUser.getUsername();
                            final String name = graphUser.getName();
                            final String birthday = graphUser.getBirthday();
                            final String mail = (String) graphUser.getProperty("email");

                            final SocialRegisterModel model = new SocialRegisterModel();
                            model.setLogin(login == null ? mail : login);
                            model.setProvider(SocialProvider.FACEBOOK);
                            model.setToken(session.getAccessToken());

                            Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

                                @Override
                                public void onPreExecute() {
                                    if (mProgressDialog == null) {
                                        mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
                                    }
                                }

                                @Override
                                public void onExecute(Boolean registered) {
                                    if (registered) {
                                        final List<Person> persons = new ArrayList<Person>(200);

                                        Bundle params = new Bundle();
                                        params.putString("fields", "id,first_name,last_name,email,picture");
                                        Request request = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {

                                            @Override
                                            public void onCompleted(List<GraphUser> users, Response response) {
                                                if (response.getError() == null) {
                                                    for (GraphUser friend : users) {
                                                        Person person = new Person(friend);
                                                        persons.add(person);
                                                    }
                                                }

                                                if (mProgressDialog != null) {
                                                    mProgressDialog.dismiss();
                                                }

                                                successLogin(persons);
                                            }

                                        });
                                        request.setParameters(params);
                                        request.executeAsync();
                                    } else {
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                        }

                                        Person.Gender gender = null;
                                        Map<String, Object> userMap = graphUser.asMap();
                                        if (userMap.containsKey("gender")) {
                                            String genderStr = ((String) userMap.get("gender")).toLowerCase(Locale.getDefault());
                                            if (!genderStr.isEmpty()) {
                                                if (genderStr.startsWith("m")) {
                                                    gender = Person.Gender.MALE;
                                                } else {
                                                    gender = Person.Gender.FEMALE;
                                                }
                                            }
                                        }

                                        User user = new User();
                                        user.setLogin(login == null ? mail : login);
                                        user.setName(name);
                                        user.setMail(mail);
                                        user.setGender(gender);

                                        if (birthday != null) {
                                            try {
                                                user.setBirthday(DateUtils.parseDate(birthday, new String[]{"MM/dd/yyyy"}));
                                            } catch (DateParseException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        final Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
                                        intent.putExtra(User.EXTRA, user);
                                        intent.putExtra(SocialRegisterModel.EXTRA, model);
                                        startActivityForResult(intent, REQUEST_REGISTER);
                                    }
                                }

                                @Override
                                public void onError(RequestError error) {
                                    if (mProgressDialog != null) {
                                        mProgressDialog.dismiss();
                                    }

                                    Toast.makeText(AuthenticationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            });
                        }
                    }

                }).executeAsync();
            }
        }

    };

    private void successLogin(List<Person> persons) {
        Toast.makeText(this, R.string.text_welcome, Toast.LENGTH_SHORT).show();

        Intent it = new Intent(this, ContactsActivity.class);
        if (persons != null) {
            it.putExtra(Person.EXTRA, (java.io.Serializable) persons);
        }
        startActivity(it);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session session = Session.getActiveSession();

        if (session == null && savedInstanceState != null) {
            session = Session.restoreSession(this, null, callback, savedInstanceState);
            Session.setActiveSession(session);
        }

        setContentView(R.layout.activity_authentication);

        final Button buttonEnter = (Button) findViewById(R.id.auth_button_enter);
        buttonEnter.setOnClickListener(this);
        final Button buttonRegister = (Button) findViewById(R.id.auth_button_register);
        buttonRegister.setOnClickListener(this);
        final Button buttonFacebook = (Button) findViewById(R.id.auth_button_facebook);
        buttonFacebook.setOnClickListener(this);
        final TextView textForgotPassword = (TextView) findViewById(R.id.auth_textView_forgotPassword);
        textForgotPassword.setOnClickListener(this);

        final EditText editTextPassword = (EditText) findViewById(R.id.auth_editText_password);
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    executeLogin();
                    return true;
                }
                return false;
            }

        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        if (session != null) {
            session.saveSession(session, outState);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
                finish();
                successLogin();
            }
        } else {
            Session session = Session.getActiveSession();
            if (session != null) {
                session.onActivityResult(this, requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.auth_button_enter:
                executeLogin();
                break;

            case R.id.auth_button_register:
                final Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
                break;

            case R.id.auth_button_facebook:
                executeFacebook();
                break;

            case R.id.auth_textView_forgotPassword:
                showRecoveryPasswordDialog();
                break;
        }
    }

    private void showRecoveryPasswordDialog() {
        final float scale = getResources().getDisplayMetrics().density;
        int marginDP = (int) (10 * scale + 0.5f);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginDP, marginDP, marginDP, marginDP);

        final EditText editTextMail = new EditText(this);

        final DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                KeyboardUtil.hide(editTextMail);
            }

        };

        final DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                executeRecoveryPassword(editTextMail);
            }

        };

        final AlertFragmentDialog afd = AlertFragmentDialog.
                create(getString(R.string.text_recovery_password), editTextMail, listenerOk, listenerCancel);

        editTextMail.setLayoutParams(params);
        editTextMail.setHint(R.string.text_fill_your_registered_mail);
        editTextMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editTextMail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextMail.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    executeRecoveryPassword(editTextMail);
                    afd.dismiss();
                    return true;
                }
                return false;
            }

        });

        afd.show(getSupportFragmentManager(), TAG_RECOVERY_PASSWORD);
    }

    private void executeRecoveryPassword(EditText editText) {
        KeyboardUtil.hide(editText);
        String mail = editText.getText().toString();

        if (mail.isEmpty()) {
            Toast.makeText(this, R.string.text_must_fill_registered_mail, Toast.LENGTH_SHORT).show();
            return;
        }

        RecoveryPasswordModel model = new RecoveryPasswordModel();
        model.setMail(mail);
        Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

            @Override
            public void onPreExecute() {
                mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
            }

            @Override
            public void onExecute(Boolean result) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                AlertFragmentDialog afd = AlertFragmentDialog.create(getString(R.string.text_attention), getString(R.string.text_was_sended_mail_recovery_password));
                afd.show(getSupportFragmentManager(), TAG_ATTENTION_DIALOG);
            }

            @Override
            public void onError(RequestError error) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                Toast.makeText(AuthenticationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void executeFacebook() {
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

    private void executeLogin() {
        EditText editTextLogin = (EditText) findViewById(R.id.auth_editText_login);
        EditText editTextPassword = (EditText) findViewById(R.id.auth_editText_password);

        KeyboardUtil.hide(editTextPassword);
        KeyboardUtil.hide(editTextLogin);

        String login = editTextLogin.getText().toString();
        String password = editTextPassword.getText().toString();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.text_field_all_fields_correctly, Toast.LENGTH_SHORT).show();
            return;
        }

        AuthModel model = new AuthModel();
        model.setLogin(login);
        model.setPassword(password);

        Requester.executeAsync(model, this);
    }

    private void successLogin() {
        successLogin(null);
    }

    @Override
    public void onPreExecute() {
        mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
    }

    @Override
    public void onExecute(User result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        String token = result.getToken();
        if (token != null) {
            UserDatabase db = new UserDatabase(this);
            db.removeAll();
            db.create(result);
        }

        successLogin();
    }

    @Override
    public void onError(RequestError error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        // TODO Realizar tratamento de todos os erros.
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }
}
