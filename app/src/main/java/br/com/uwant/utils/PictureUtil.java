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
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import br.com.uwant.R;
import br.com.uwant.flow.CameraAcitivity;
import br.com.uwant.flow.GalleryActivity;

/**
 * Classe utilitária responsável por métodos relacionados a fotos do sistema.
 */
public abstract class PictureUtil {

    public static final String MIME_IMAGE = "image/*";

    /**
     * Método responsável por decodificar a foto aplicando os algoritmos de ajuste na foto.
     * Serão aplicados três métodos: 1. crop, 2. scale e 3. circle
     * @param bitmap
     * @param imageView
     * @return
     */
    private static Bitmap decodeBitmap(Bitmap bitmap, ImageView imageView, boolean isCircle) {
        bitmap = cropToFit(bitmap);
        bitmap = scale(bitmap, imageView);

        if (isCircle) {bitmap = circle(bitmap);}

        imageView.setImageBitmap(bitmap);

        return bitmap;
    }

    /**
     * Método responsável por decodificar a foto (em formato de arquivo) aplicando os algoritmos de ajuste na foto.
     * @param file - Arquivo da Foto
     * @param imageView - Container
     * @return
     */
    public static Bitmap decodePicture(File file, ImageView imageView, boolean isCircle) {
        // Obtém o tamanho da ImageView
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Obtém a largura e altura da foto
        BitmapFactory.Options bmOptions =
                new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

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

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        return decodeBitmap(bitmap, imageView, isCircle);
    }

    /**
     * Método responsável por deixar a foto proporcional a partir do tamanho do ImageView (LARGURA x ALTURA).
     * @param bitmap
     * @param imageView
     * @return
     */
    public static Bitmap scale(Bitmap bitmap, ImageView imageView) {
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        return scale(bitmap, width, height);
    }

    /**
     * Método responsável por deixar a foto proporcional a partir do tamanho passado por parâmetro.
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap scale(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    /**
     * Método responsável por realizar recorte no centro da foto, sem perder o foco da foto.
     * @param bitmap
     * @return
     */
    public static Bitmap cropToFit(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output;
        if (width >= height){
            output = Bitmap.createBitmap(bitmap, (width/2) - (height/2), 0, height, height);
        } else {
            output = Bitmap.createBitmap(bitmap, 0, (height/2) - (width/2), width, width);
        }
        return output;
    }

    /**
     * Método responsável por realizar recorte para deixar a foto em formato circular.
     * @param bitmap
     * @return
     */
    public static Bitmap circle(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle((width/2F), (height/2F), (width/2.2F), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Método auxiliar para abertura da galeria do usuário a partir de uma Intent Implícita.
     * @param activity
     * @param requestCode
     * @param allowMultiple
     */
    public static void openGallery(Activity activity, int requestCode, boolean allowMultiple) {
        if (allowMultiple) {
            Intent intent = new Intent(activity, GalleryActivity.class);
            activity.startActivityForResult(intent, requestCode);
        } else {
            Intent intent = new Intent(
                    Intent.ACTION_GET_CONTENT);
            intent.setType(MIME_IMAGE);

            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                Toast.makeText(activity, R.string.text_capture_picture_warning, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método auxiliar para abertura da câmera do aparelho a partir de uma Intent Implícita.
     * @param activity
     * @param requestCode
     */
    public static void takePicture(Activity activity, int requestCode) {
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (i.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivityForResult(i, requestCode);
//        } else {
//            Toast.makeText(activity, R.string.text_image_capture_warning, Toast.LENGTH_LONG).show();
//        }
        Intent i = new Intent(activity, CameraAcitivity.class);
        activity.startActivityForResult(i, requestCode);
    }

}
