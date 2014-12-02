package br.com.uwant.flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.LogoffModel;
import br.com.uwant.models.databases.UserDatabase;
import br.com.uwant.utils.GoogleCloudMessageUtil;

public class UWActivity extends ActionBarActivity {

    protected User mUser = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.mUser.getToken() == null) {
            UserDatabase database = new UserDatabase(this);
            if (database.existAnything()) {
                User.newInstance(database.selectAll().get(0));
            } else {
                performLogoff();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void performLogoff() {
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

                UserDatabase db = new UserDatabase(UWActivity.this);
                db.removeAll();
                User.clearInstance();
                GoogleCloudMessageUtil.clear(UWActivity.this);

                Intent intent = new Intent(UWActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish(); // TODO Remover toda a pilha de execução das Activities.
            }

            @Override
            public void onError(RequestError error) {
                if (progressFragmentDialog != null) {
                    progressFragmentDialog.dismiss();
                }

                Intent intent = new Intent(UWActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

}
