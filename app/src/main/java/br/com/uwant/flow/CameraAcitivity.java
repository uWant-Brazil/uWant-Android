package br.com.uwant.flow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import br.com.uwant.R;

public class CameraAcitivity extends UWActivity implements View.OnClickListener, SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_camera);

        ImageButton imageButtonGallery = (ImageButton) findViewById(R.id.camera_imageButton_gallery);
        ImageButton imageButtonTake = (ImageButton) findViewById(R.id.camera_imageButton_take);
        ImageButton imageButtonChange = (ImageButton) findViewById(R.id.camera_imageButton_change);

        imageButtonGallery.setOnClickListener(this);
        imageButtonTake.setOnClickListener(this);
        imageButtonChange.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.camera_surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        rawCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    String path = String.format("/storage/emulated/0/Pictures/%d.jpg", System.currentTimeMillis());
                    FileOutputStream fos = new FileOutputStream(path);
                    fos.write(data);
                    fos.flush();
                    fos.close();

                    Intent intent = new Intent();
                    intent.putExtra("data", path);
                    setResult(RESULT_OK, intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CameraAcitivity.this.finish();
            }

        };

        shutterCallback = new Camera.ShutterCallback() {

            @Override
            public void onShutter() {

            }

        };
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_imageButton_gallery:
                break;

            case R.id.camera_imageButton_take:
                camera.takePicture(shutterCallback, null, rawCallback);
                break;

            case R.id.camera_imageButton_change:
                break;

            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        } catch (Exception e) {
            return;
        }

        Camera.Parameters p = camera.getParameters();
        List<Camera.Size> supportedSizes = p.getSupportedPreviewSizes();

        Camera.Size size = supportedSizes != null && supportedSizes.size() > 0 ? supportedSizes.get(2) : null;
        p.setPreviewSize(size.height, size.width);
        setCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, camera);
        camera.setParameters(p);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        camera.stopPreview();

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
