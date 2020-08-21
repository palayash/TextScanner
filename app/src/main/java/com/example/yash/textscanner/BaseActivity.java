package com.example.yash.textscanner;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;
import android.widget.Toast;

import java.io.File;

import static android.os.Build.VERSION_CODES.M;

public class BaseActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    public static final int WRITE_STORAGE = 100;
    public static final int SELECT_PHOTO = 102;
    public static final String ACTION_BAR_TITLE = "action_bar_title";
    public File photo;
    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    DisplayMetrics displayMetrics ;
    int width, height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(ACTION_BAR_TITLE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


//If “gallery_action” is selected, then...//

            case R.id.gallery_action:

//...check we have the WRITE_STORAGE permission//

                checkPermission(WRITE_STORAGE);
                break;

            case  R.id.camera_action:

                EnableRuntimePermission();
                checkPermission(0);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_STORAGE:

//If the permission request is granted, then...//

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//...call selectPicture//

                    selectPicture();

//If the permission request is denied, then...//

                } else {

//...display the “permission_request” string//

                    requestPermission(this, requestCode, R.string.permission_request);
                }
                break;

            case 0:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(BaseActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                       ClickImageFromCamera();

                } else {

                    Toast.makeText(BaseActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }



    }







//Display the permission request dialog//

    public static void requestPermission(final Activity activity, final int requestCode, int msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(msg);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent permissonIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                permissonIntent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(permissonIntent, requestCode);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

//Check whether the user has granted the WRITE_STORAGE permission//

    public void checkPermission(int requestCode) {
        switch (requestCode) {
            case WRITE_STORAGE:
                int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

//If we have access to external storage...//

                if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {

//...call selectPicture, which launches an Activity where the user can select an image//

                    selectPicture();

//If permission hasn’t been granted, then...//

                } else {

//...request the permission//

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;

            case 0:


                int hasCameraStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

                if (hasCameraStoragePermission == PackageManager.PERMISSION_GRANTED) {

//...call selectPicture, which launches an Activity where the user can select an image//

                    ClickImageFromCamera();

//If permission hasn’t been granted, then...//

                }
        }


    }

    private void selectPicture() {
        photo = MyHelper.createTempFile(photo);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//Start an Activity where the user can choose an image//
        file = new File(Environment.getExternalStorageDirectory(),"file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        //uri = Uri.fromFile(file);

        uri = FileProvider.getUriForFile(BaseActivity.this, BaseActivity.this.getApplicationContext().getPackageName() + ".provider", file);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        intent.putExtra("return-data", true);

        startActivityForResult(intent, SELECT_PHOTO);
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this,
                Manifest.permission.CAMERA))
        {

            Toast.makeText(BaseActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);

        } else {

            ActivityCompat.requestPermissions(BaseActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    public void ClickImageFromCamera() {

        photo = MyHelper.createTempFile(photo);
        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


       file = new File(Environment.getExternalStorageDirectory(),"file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        //uri = Uri.fromFile(file);

         uri = FileProvider.getUriForFile(BaseActivity.this, BaseActivity.this.getApplicationContext().getPackageName() + ".provider", file);

        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

       CamIntent.putExtra("return-data", true);


        startActivityForResult(CamIntent, 1888);

    }

    public void ImageCropFunction() {
                // Image Crop Code
                try {
                    CropIntent = new Intent("com.android.camera.action.CROP");
                    CropIntent.setDataAndType(uri, "image/*");
                    CropIntent.putExtra("crop", "true");
                    CropIntent.putExtra("outputX", 200);
                    CropIntent.putExtra("outputY", 200);
                    CropIntent.putExtra("aspectX", 1);
                    CropIntent.putExtra("aspectY", 1);
                    CropIntent.putExtra("scaleUpIfNeeded", true);
                    CropIntent.putExtra("return-data", true);

                    startActivityForResult(CropIntent, 0);


                } catch (ActivityNotFoundException e) {

                }
            }
    }