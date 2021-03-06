package com.yuyang.lib_base.helper;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.config.Config;
import com.yuyang.lib_base.utils.BitmapUtil;

import java.io.File;

public class SelectPhotoHelper {

    private File mOutputFile;
    private boolean mCropPhoto;

    private OnResultListener onResultListener;

    private final ActivityResultLauncher<Intent> photoResultLauncher;
    private final ActivityResultLauncher<Intent> galleryResultLauncher;
    private final ActivityResultLauncher<Intent> cropResultLauncher;

    public SelectPhotoHelper(ComponentActivity activity) {
        photoResultLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    if (mOutputFile != null && mOutputFile.exists()) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Bitmap bitmap = BitmapFactory.decodeFile(mOutputFile.getAbsolutePath());

//                                if (true) {
//                                    Uri tempUri = NullActivity.getImageUri(App.getInstance(), bitmap);
//                                    String path = NullActivity.getRealPathFromURI(App.getInstance(), tempUri);
//                                    activity.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ToastUtil.showToast(path);
//                                        }
//                                    });
//                                    return;
//                                }

                                int degree = BitmapUtil.getBitmapDegree(mOutputFile.getAbsolutePath());
                                if (degree != 0) {
                                    bitmap = BitmapUtil.rotateBitmapByDegree(bitmap, degree);
                                    BitmapUtil.saveBitmap(bitmap, mOutputFile);
                                }

                                Uri photoUri;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    photoUri = FileProvider.getUriForFile(activity, Config.FILEPROVIDER_AUTHORITY, mOutputFile);
                                } else {
                                    photoUri = Uri.fromFile(mOutputFile);
                                }

                                if (mCropPhoto) {
                                    cropPhoto(photoUri);
                                } else {
                                    BitmapUtil.insertBitmap(activity, mOutputFile.getAbsolutePath());

                                    final Bitmap finalPhotoBitmap = bitmap;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (onResultListener != null) {
                                                onResultListener.onPhotoResult(finalPhotoBitmap, photoUri, mOutputFile.getAbsolutePath());
                                            }
                                            mOutputFile = null;
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                }
            }
        });
        galleryResultLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    String photoPath = SelectImageUtil.getPath(activity, uri);

                    if (mCropPhoto) {
                        cropPhoto(uri);
                    } else {
                        if (onResultListener != null) {
                            onResultListener.onPhotoResult(getBitmapFromUri(uri), uri, photoPath);
                        }
                    }
                }
            }
        });
        cropResultLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        //???????????? return-data ??? true ???????????? bitmap??????????????????????????????
//                        Bitmap photoBitmap = result.getData().getParcelableExtra("data");
//                        Bitmap photo = extras.getParcelable("data");
                        if (onResultListener != null) {
                            onResultListener.onPhotoResult(getBitmapFromUri(Uri.fromFile(mOutputFile)), Uri.fromFile(mOutputFile), mOutputFile.getAbsolutePath());
                        }
                        mOutputFile = null;
                    }
                }
            }
        });
    }

    public void takePhoto(File outputFile, boolean cropPhoto) {

        File storageDir = outputFile.getParentFile();
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return;
            }
        }
        this.mOutputFile = outputFile;
        this.mCropPhoto = cropPhoto;

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(BaseApp.getInstance(), Config.FILEPROVIDER_AUTHORITY, mOutputFile);
        } else {
            uri = Uri.fromFile(mOutputFile);
        }

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        photoResultLauncher.launch(takePhotoIntent);
    }

    public void takeGallery(File outputFile, boolean cropPhoto) {
        if (outputFile == null && cropPhoto) {
            throw new RuntimeException("If cropPhoto, outputFile must not be null!");
        }
        this.mOutputFile = outputFile;
        this.mCropPhoto = cropPhoto;

        if (outputFile != null) {
            File storageDir = outputFile.getParentFile();
            if (!storageDir.exists()) {
                if (!storageDir.mkdirs()) {
                    return;
                }
            }
        }

//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);   //???????????????????????????????????????????????????
//        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //????????????????????????
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);  //???????????????????????????
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);//??????
        galleryIntent.setType("image/*");
        galleryResultLauncher.launch(galleryIntent);
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY ??????????????????
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY ?????????????????????
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutputFile));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        cropResultLauncher.launch(intent);
    }

    public static class OnResultListener {
        public void onPhotoResult(Bitmap bitmap, Uri uri, String path) {

        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // ??????uri???????????????
            return MediaStore.Images.Media.getBitmap(BaseApp.getInstance().getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}