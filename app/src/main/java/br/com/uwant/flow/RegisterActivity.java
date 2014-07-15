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

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IRequest.OnRequestListener {

    private static final int PICTURE_REQUEST_CODE = 9898;
    private static final int GALLERY_REQUEST_CODE = 9797;

    private byte[] mPictureBytes;
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
        String url = "http://graph.facebook.com/" + id +  "/picture";
        Picasso.with(RegisterActivity.this).load(url).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmap = cropToFit(bitmap);
                bitmap = scale(bitmap);
                bitmap = circle(bitmap);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(new String[] { "Tirar foto", "Buscar na galeria" }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            takePicture();
                        } else {
                            openGallery();
                        }
                    }

                });
                builder.create().show();
                break;

            default:
                break;
        }
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
            Toast.makeText(this, "Preencha todos os campos corretamente.", Toast.LENGTH_LONG).show();
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

    private void takePicture() {
        mPicturePath = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                "uwant_picture");

        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPicturePath));
        startActivityForResult(intentPicture, PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST_CODE) {
                decodePicture();
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
                decodePicture();
            }
        }
    }

    private void decodePicture() {
        mImageViewPictureDetail.setVisibility(View.VISIBLE);

        // Obtém o tamanho da ImageView
        int targetW = mImageViewPicture.getWidth();
        int targetH = mImageViewPicture.getHeight();

        // Obtém a largura e altura da foto
        BitmapFactory.Options bmOptions =
                new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPicturePath.getAbsolutePath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determina o fator de redimensionamento
        int scaleFactor = Math.min(
                photoW/targetW, photoH/targetH);

        // Decodifica o arquivo de imagem em
        // um Bitmap que preencherá a ImageView
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mPicturePath.getAbsolutePath(), bmOptions);
        bitmap = cropToFit(bitmap);
        bitmap = scale(bitmap);
        bitmap = circle(bitmap);

        mImageViewPicture.setImageBitmap(bitmap);
    }

    private Bitmap scale(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, mImageViewPicture.getWidth(), mImageViewPicture.getHeight(), false);
    }

    private Bitmap cropToFit(Bitmap srcBmp) {
        Bitmap output;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            output = Bitmap.createBitmap(srcBmp, srcBmp.getWidth()/2 - srcBmp.getHeight()/2, 0, srcBmp.getHeight(), srcBmp.getHeight());
        } else {
            output = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight()/2 - srcBmp.getWidth()/2, srcBmp.getWidth(), srcBmp.getWidth());
        }
        return output;
    }

    private Bitmap circle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2F, bitmap.getHeight() / 2F,
                bitmap.getWidth() / 2.2F, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
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
        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }

        });
        builder.setTitle(R.string.app_name);
        builder.setMessage("Parabéns, o seu registro foi efetuado com sucesso." +
                "\nPor favor, realize um login para acessar o uWant!");
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
