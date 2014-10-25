package br.com.uwant.flow;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.helpers.UWFileBodyListener;
import br.com.uwant.models.cloud.models.RegisterModel;
import br.com.uwant.models.cloud.models.RegisterPictureModel;
import br.com.uwant.models.cloud.models.SocialRegisterModel;
import br.com.uwant.models.databases.UserDatabase;
import br.com.uwant.utils.DateUtil;
import br.com.uwant.utils.KeyboardUtil;
import br.com.uwant.utils.PictureUtil;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IRequest.OnRequestListener<User>, DatePickerDialog.OnDateSetListener,UWFileBodyListener {

    private static final int NOTIFICATION_ID = 0x613;
    private static final int PICTURE_REQUEST_CODE = 9898;
    private static final int GALLERY_REQUEST_CODE = 9797;
//    private static final String URL_FACEBOOK_PICTURE = "http://graph.facebook.com/%s/picture";
    private static final String TAG_REGISTER_CANCEL_DIALOG = "RegisterCancelTag";

    private Calendar mBirthday;
    private File mPicturePath;
    private User.Gender mGender = Person.Gender.UNKNOWN;

    private EditText mEditTextLogin;
    private EditText mEditTextName;
    private EditText mEditTextMail;
    private EditText mEditTextPassword;
    private TextView mTextViewBirthday;
    private ImageView mImageViewPicture;
    private ImageView mImageViewPictureDetail;
    private RadioGroup mRadioGroupGender;
    private ProgressFragmentDialog mProgressDialog;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_register);

        final Button buttonRegister = (Button) findViewById(R.id.register_button_register);
        buttonRegister.setOnClickListener(this);
        mTextViewBirthday = (TextView) findViewById(R.id.register_editText_birthday);
        mTextViewBirthday.setOnClickListener(this);

        mEditTextLogin = (EditText) findViewById(R.id.register_editText_login);
        mEditTextName = (EditText) findViewById(R.id.register_editText_name);
        mEditTextMail = (EditText) findViewById(R.id.register_editText_mail);
        mEditTextPassword = (EditText) findViewById(R.id.register_editText_password);
        mEditTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtil.hide(mEditTextPassword);
                    return true;
                }
                return false;
            }

        });

        mImageViewPictureDetail = (ImageView) findViewById(R.id.register_imageView_pictureDetail);
        mImageViewPicture = (ImageView) findViewById(R.id.register_imageView_picture);
        mImageViewPicture.setOnClickListener(this);

        mRadioGroupGender = (RadioGroup) findViewById(R.id.register_radioGroup_gender);
        mRadioGroupGender.setOnCheckedChangeListener(this);

        Intent it = getIntent();
        if (it.hasExtra(User.EXTRA)) {
            User user = (User) it.getSerializableExtra(User.EXTRA);
            String login = user.getLogin();
            String name = user.getName();
            String mail = user.getMail();
            Date birthday = user.getBirthday();
            Person.Gender gender = user.getGender();

            if (login != null && !login.isEmpty()) {
                mEditTextLogin.setText(user.getLogin());
                mEditTextLogin.setEnabled(false);
            }

            if (name != null && !name.isEmpty()) {
                mEditTextName.setText(user.getName());
                mEditTextName.setEnabled(false);
            }

            if (mail != null && !mail.isEmpty()) {
                mEditTextMail.setText(user.getMail());
                mEditTextMail.setEnabled(false);
            }

            if (birthday != null) {
                mTextViewBirthday.setText(DateUtil.format(user.getBirthday(), DateUtil.DATE_PATTERN));
                mTextViewBirthday.setEnabled(false);
            }

            if (gender != null) {
                if (gender == Person.Gender.MALE) {
                    mRadioGroupGender.check(R.id.register_radioButton_male);
                } else {
                    mRadioGroupGender.check(R.id.register_radioButton_female);
                }
            }
        }
    }

//    private void retrieveFacebookPicture(final String url) {
//        Picasso.with(RegisterActivity.this).load(url).into(new Target() {
//
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                bitmap = PictureUtil.cropToFit(bitmap);
//                bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
//                bitmap = PictureUtil.circle(bitmap);
//
//                mImageViewPicture.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//            }
//
//        });
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button_register:
                executeRegister();
                break;

            case R.id.register_imageView_picture:
                showPictureOptions();
                break;

            case R.id.register_editText_birthday:
                DateUtil.picker(this, this);
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
                askBeforeCancel();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        askBeforeCancel();
    }

    private void askBeforeCancel() {
        String title = getString(R.string.text_attention);
        String message = getString(R.string.text_ask_before_cancel_registration);
        String positiveText = getString(R.string.text_yes);
        String negativeText = getString(R.string.text_no);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mPicturePath != null && mPicturePath.exists()) {
                    mPicturePath.delete();
                }

                finish();
            }

        };

        DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing...
            }

        };

        AlertFragmentDialog afd = AlertFragmentDialog.create(title, message, positiveText, listenerOk, negativeText, listenerCancel);
        afd.show(getSupportFragmentManager(), TAG_REGISTER_CANCEL_DIALOG);
    }

    private void executeRegister() {
        KeyboardUtil.hide(mEditTextPassword);
        KeyboardUtil.hide(mEditTextMail);
        KeyboardUtil.hide(mEditTextName);
        KeyboardUtil.hide(mEditTextLogin);

        String login = mEditTextLogin.getText().toString();
        String password = mEditTextPassword.getText().toString();
        String name = mEditTextName.getText().toString();
        String mail = mEditTextMail.getText().toString();
        String birthday = mTextViewBirthday.getText().toString();

        if (login.isEmpty() || password.isEmpty() || name.isEmpty()
                || mail.isEmpty() || birthday.isEmpty() || mGender == Person.Gender.UNKNOWN) {
            Toast.makeText(this, R.string.text_field_all_fields_correctly, Toast.LENGTH_LONG).show();
            return;
        }

        Date date;
        try {
            date = DateUtil.parse(birthday, DateUtil.DATE_PATTERN);
            easterEager(date);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        User user = new User();
        user.setLogin(login);
        user.setName(name);
        user.setMail(mail);
        user.setGender(mGender);
        user.setBirthday(date);

        RegisterModel model = new RegisterModel();
        model.setUser(user);
        model.setPassword(password);
        model.setBirthday(birthday);

        Intent it = getIntent();
        if (it.hasExtra(SocialRegisterModel.EXTRA)) {
            SocialRegisterModel socialModel = (SocialRegisterModel) it.getSerializableExtra(SocialRegisterModel.EXTRA);
            model.setSocialModel(socialModel);
        }

        Requester.executeAsync(model, this);
    }

    private void easterEager(Date birthday) {
        Calendar c = Calendar.getInstance();
        c.setTime(birthday);

        int year = c.get(Calendar.YEAR);
        if (year <= 1950) {
            Toast.makeText(this, R.string.text_ancient_easter_eager, Toast.LENGTH_LONG).show();
        }
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
    public void onExecute(final User result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        String token = result.getToken();
        if (token != null) {
            UserDatabase db = new UserDatabase(this);
            db.removeAll();
            db.create(result);
        }

        if (this.mPicturePath != null && this.mPicturePath.exists()) {
            Multimedia multimedia = new Multimedia();
            multimedia.setUri(Uri.fromFile(this.mPicturePath));
            result.setPicture(multimedia);

            RegisterPictureModel model = new RegisterPictureModel();
            model.setUser(result);
            model.setListener(this);
            Requester.executeAsync(model, new IRequest.OnRequestListener<Multimedia>() {

                @Override
                public void onPreExecute() {

                }

                @Override
                public void onExecute(Multimedia picture) {
                    result.setPicture(picture);
                    UserDatabase db = new UserDatabase(getApplicationContext());
                    db.update(result);
                }

                @Override
                public void onError(RequestError error) {

                }

            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setNeutralButton(R.string.text_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(RESULT_OK);
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

    @Override
    public void preWrite(int totalAmount) {
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.ic_action_uwant)
                        .setContentTitle(getString(R.string.app_name))
                        .setProgress(totalAmount, 0, false)
                        .setContentText(getString(R.string.text_sending_picture));

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void written(int totalBytes, int amountOfBytes) {
        mBuilder.setProgress(totalBytes, amountOfBytes, false);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (totalBytes > amountOfBytes) {
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            mNotifyMgr.cancel(NOTIFICATION_ID);
            mBuilder = null;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mBirthday = Calendar.getInstance();
        mBirthday.set(Calendar.YEAR, year);
        mBirthday.set(Calendar.MONTH, monthOfYear);
        mBirthday.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String birthday = DateUtil.format(mBirthday.getTime(), DateUtil.DATE_PATTERN);
        mTextViewBirthday.setText(birthday);
    }

}
