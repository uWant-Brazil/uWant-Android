package br.com.uwant.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;

public abstract class PictureUtil {

    public static void decodePicture(File picture, ImageView imageViewPicture) {
        // Obtém o tamanho da ImageView
        int targetW = imageViewPicture.getWidth();
        int targetH = imageViewPicture.getHeight();

        // Obtém a largura e altura da foto
        BitmapFactory.Options bmOptions =
                new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picture.getAbsolutePath(), bmOptions);

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

        Bitmap bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), bmOptions);
        bitmap = cropToFit(bitmap);
        bitmap = scale(bitmap, imageViewPicture);
        bitmap = circle(bitmap);

        imageViewPicture.setImageBitmap(bitmap);
    }

    public static Bitmap scale(Bitmap bitmap, ImageView imageViewPicture) {
        return Bitmap.createScaledBitmap(bitmap, imageViewPicture.getWidth(), imageViewPicture.getHeight(), false);
    }

    public static Bitmap cropToFit(Bitmap srcBmp) {
        Bitmap output;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            output = Bitmap.createBitmap(srcBmp, srcBmp.getWidth()/2 - srcBmp.getHeight()/2, 0, srcBmp.getHeight(), srcBmp.getHeight());
        } else {
            output = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight()/2 - srcBmp.getWidth()/2, srcBmp.getWidth(), srcBmp.getWidth());
        }
        return output;
    }

    public static Bitmap circle(Bitmap bitmap) {
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

    public static void openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(
                Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    public static void takePicture(Activity activity, File picture, int requestCode) {
        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
        activity.startActivityForResult(intentPicture, requestCode);
    }

}
