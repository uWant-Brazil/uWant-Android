package br.com.uwant.flow;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.models.classes.SocialProvider;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.AuthModel;
import br.com.uwant.models.cloud.models.RecoveryPasswordModel;
import br.com.uwant.models.cloud.models.SocialRegisterModel;

public class AuthenticationActivity extends FragmentActivity implements View.OnClickListener, IRequest.OnRequestListener<User> {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        final Button buttonEnter = (Button) findViewById(R.id.auth_button_enter);
        buttonEnter.setOnClickListener(this);
        final Button buttonRegister = (Button) findViewById(R.id.auth_button_register);
        buttonRegister.setOnClickListener(this);
        final Button buttonFacebook = (Button) findViewById(R.id.auth_button_facebook);
        buttonFacebook.setOnClickListener(this);
        final TextView textForgotPassword = (TextView) findViewById(R.id.auth_textView_forgotPassword);
        textForgotPassword.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.auth_button_enter:
                executeLogin();
                break;

            case R.id.auth_button_register:
                Intent intentRegister = new Intent(this, RegisterActivity.class);
                startActivity(intentRegister);
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
                // Do nothing...
            }

        };

        final DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                executeRecoveryPassword(editTextMail);
            }

        };

        final AlertFragmentDialog afd = AlertFragmentDialog.
                create("Recuperar senha", editTextMail, listenerOk, listenerCancel);

        editTextMail.setLayoutParams(params);
        editTextMail.setHint("Digite seu email de cadastro");
        editTextMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editTextMail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextMail.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    afd.dismiss();
                    executeRecoveryPassword(editTextMail);
                    return true;
                }
                return false;
            }

        });

        afd.show(getSupportFragmentManager(), "RecuperarSenhaTag");
    }

    private void executeRecoveryPassword(EditText editText) {
        String mail = editText.getText().toString();

        if (mail.isEmpty()) {
            Toast.makeText(this, "Você precisa digitar o email do seu cadastro.", Toast.LENGTH_SHORT).show();
            return;
        }

        RecoveryPasswordModel model = new RecoveryPasswordModel();
        model.setMail(mail);
        Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

            @Override
            public void onPreExecute() {
                mProgressDialog = ProgressDialog.show(AuthenticationActivity.this, getString(R.string.app_name), "Aguarde...");
            }

            @Override
            public void onExecute(Boolean result) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                AlertFragmentDialog afd = AlertFragmentDialog.create("Atenção", "Foi enviado um email para você redefinir sua senha.");
                afd.show(getSupportFragmentManager(), "AtencaoDialog");
            }

            @Override
            public void onError(RequestError error) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                Toast.makeText(AuthenticationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void executeFacebook() {
        final Session.StatusCallback callback = new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(GraphUser graphUser, com.facebook.Response response) {
                            if (graphUser != null) {
                                final String id = graphUser.getId();
                                final String login = graphUser.getUsername();
                                final String name = graphUser.getName();
                                final String birthday = graphUser.getBirthday();
                                final String mail = (String) graphUser.getProperty("email");

                                SocialRegisterModel model = new SocialRegisterModel();
                                model.setLogin(login == null ? mail : login);
                                model.setProvider(SocialProvider.FACEBOOK);
                                model.setToken(id);

                                Requester.executeAsync(model, new IRequest.OnRequestListener<Boolean>() {

                                    @Override
                                    public void onPreExecute() {
                                        mProgressDialog = ProgressDialog.show(AuthenticationActivity.this, getString(R.string.app_name), "Aguarde...");
                                    }

                                    @Override
                                    public void onExecute(Boolean result) {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }

                                        User user = new User();
                                        user.setLogin(login == null ? mail : login);
                                        user.setName(name);
                                        user.setMail(mail);
                                        try {
                                            user.setBirthday(DateUtils.parseDate(birthday, new String[] { "MM/dd/yyyy" }));
                                        } catch (DateParseException e) {
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
                                        intent.putExtra(User.EXTRA, user);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(RequestError error) {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
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

        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(this)
                        .setPermissions(Arrays.asList("public_profile", "email"))
                        .setCallback(callback));
            } else {
                Session.openActiveSession(this, true, Arrays.asList("public_profile", "email", "user_birthday"), callback);
            }
        } else {
            Session.openActiveSession(this, true, Arrays.asList("public_profile", "email", "user_birthday"), callback);
        }
    }

    private void executeLogin() {
        EditText editTextLogin = (EditText) findViewById(R.id.auth_editText_login);
        EditText editTextPassword = (EditText) findViewById(R.id.auth_editText_password);

        String login = editTextLogin.getText().toString();
        String password = editTextPassword.getText().toString();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Digite todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthModel model = new AuthModel();
        model.setLogin(login);
        model.setPassword(password);

        Requester.executeAsync(model, this);
    }

    @Override
    public void onPreExecute() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.app_name), "Aguarde...");
    }

    @Override
    public void onExecute(User result) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        Toast.makeText(this, "Deu certo!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(RequestError error) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }
}
