package br.com.uwant.flow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import br.com.uwant.R;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.AuthModel;

public class AuthenticationActivity extends Activity implements View.OnClickListener, IRequest.OnRequestListener<User> {

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
                // TODO Esperando o layout...
                break;
        }
    }

    private void executeFacebook() {
        // TODO ...
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
