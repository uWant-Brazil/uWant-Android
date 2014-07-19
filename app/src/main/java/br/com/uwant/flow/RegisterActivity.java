package br.com.uwant.flow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.impl.cookie.DateUtils;

import java.io.File;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.RegisterModel;
import br.com.uwant.utils.PictureUtil;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IRequest.OnRequestListener {

    private static final int PICTURE_REQUEST_CODE = 9898;
    private static final int GALLERY_REQUEST_CODE = 9797;
    private static final String URL_FACEBOOK_PICTURE = "http://graph.facebook.com/%s/picture";

    private File mPicturePath;
    private User.Gender mGender;

    private EditText mEditTextLogin;
    private EditText mEditTextName;
    private EditText mEditTextMail;
    private EditText mEditTextPassword;
    private EditText mEditTextBirthday;
    private ImageView mImageViewPicture;
    private ImageView mImageViewPictureDetail;
    private RadioGroup mRadioGroupGender;
    private ProgressFragmentDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_register);

        final Button buttonRegister = (Button) findViewById(R.id.register_button_register);
        buttonRegister.setOnClickListener(this);

        mEditTextLogin = (EditText) findViewById(R.id.register_editText_login);
        mEditTextName = (EditText) findViewById(R.id.register_editText_name);
        mEditTextMail = (EditText) findViewById(R.id.register_editText_mail);
        mEditTextPassword = (EditText) findViewById(R.id.register_editText_password);
        mEditTextBirthday = (EditText) findViewById(R.id.register_editText_birthday);

        mImageViewPictureDetail = (ImageView) findViewById(R.id.register_imageView_pictureDetail);
        mImageViewPicture = (ImageView) findViewById(R.id.register_imageView_picture);
        mImageViewPicture.setOnClickListener(this);

        mRadioGroupGender = (RadioGroup) findViewById(R.id.register_radioGroup_gender);
        mRadioGroupGender.setOnCheckedChangeListener(this);

        Intent it = getIntent();
        if (it.hasExtra(User.EXTRA)) {
            User user = (User) it.getSerializableExtra(User.EXTRA);
            mEditTextLogin.setText(user.getLogin());
            mEditTextName.setText(user.getName());
            mEditTextMail.setText(user.getMail());
            mEditTextBirthday.setText(DateUtils.formatDate(user.getBirthday(), "dd/MM/yyyy"));

            mEditTextLogin.setEnabled(false);
            mEditTextName.setEnabled(false);
            mEditTextMail.setEnabled(false);
            mEditTextBirthday.setEnabled(false);
        }

        if (it.hasExtra("facebookId")) {
            retrieveFacebookPicture(it.getStringExtra("facebookId"));
        }
    }

    private void retrieveFacebookPicture(final String id) {
        String url = String.format(URL_FACEBOOK_PICTURE, id);
        Picasso.with(RegisterActivity.this).load(url).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmap = PictureUtil.cropToFit(bitmap);
                bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                bitmap = PictureUtil.circle(bitmap);

                mImageViewPicture.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button_register:
                executeRegister();
                break;

            case R.id.register_imageView_picture:
                showPictureOptions();
                break;

            default:
                break;
        }
    }

    private void showPictureOptions() {
        final String[] options = getResources().getStringArray(R.array.options_register_picture);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    PictureUtil.openGallery(RegisterActivity.this, GALLERY_REQUEST_CODE);
                } else {
                    mPicturePath = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES),
                            "uwant_picture");
                    PictureUtil.takePicture(RegisterActivity.this, mPicturePath, PICTURE_REQUEST_CODE);
                }
            }

        });
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeRegister() {
        String login = mEditTextLogin.getText().toString();
        String password = mEditTextPassword.getText().toString();
        String name = mEditTextName.getText().toString();
        String mail = mEditTextMail.getText().toString();
        String birthday = mEditTextBirthday.getText().toString();

        if (login.isEmpty() || password.isEmpty() || name.isEmpty()
                || mail.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, R.string.text_field_all_fields_correctly, Toast.LENGTH_LONG).show();
            return;
        }

        RegisterModel model = new RegisterModel();
        model.setLogin(login);
        model.setPassword(password);
        model.setName(name);
        model.setMail(mail);
        model.setBirthday(birthday);
        model.setGender(mGender);

        Requester.executeAsync(model, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST_CODE) {
                mImageViewPictureDetail.setVisibility(View.VISIBLE);
                PictureUtil.decodePicture(mPicturePath, mImageViewPicture);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {
                        MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(
                        filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                mPicturePath = new File(filePath);
                mImageViewPictureDetail.setVisibility(View.VISIBLE);
                PictureUtil.decodePicture(mPicturePath, mImageViewPicture);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (id == R.id.register_radioButton_male) {
            mGender = Person.Gender.MALE;
        } else {
            mGender = Person.Gender.FEMALE;
        }
    }

    @Override
    public void onPreExecute() {
        mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
    }

    @Override
    public void onExecute(Object result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setNeutralButton(R.string.text_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }

        });
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.text_register_congratulations);
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onError(RequestError error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }
}
