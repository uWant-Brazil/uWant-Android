package br.com.uwant.flow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AuthenticationActivity extends Activity implements View.OnClickListener {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; encoding=utf-8;");
    private static final String AUTH_URL = "http://192.168.1.5:9000/v1/mobile/authorize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        final Button buttonEnter = (Button) findViewById(R.id.auth_button_enter);
        buttonEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText editTextLogin = (EditText) findViewById(R.id.auth_editText_login);
        EditText editTextPassword = (EditText) findViewById(R.id.auth_editText_password);

        String login = editTextLogin.getText().toString();
        String password = editTextPassword.getText().toString();

        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("login", login);
        json.addProperty("password", password);

        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MEDIA_TYPE, gson.toJson(json));
        final Request request = new Request.Builder()
                .url(AUTH_URL)
                .post(body)
                .build();

        new Thread() {

            @Override
            public void run() {
                super.run();
                String message = "Ops...";
                try {
                    Response response = client.newCall(request).execute();
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(response.body().string());
                    JsonObject jsonResponse = jsonElement.getAsJsonObject();

                    if (jsonResponse.has("status")) {
                        boolean status = jsonResponse.get("status").getAsBoolean();
                        if (status) {
                            message = "Deu certo!";
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final String finalMessage = message;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(AuthenticationActivity.this, finalMessage, Toast.LENGTH_SHORT).show();
                    }

                });
            }

        }.start();
    }
}
