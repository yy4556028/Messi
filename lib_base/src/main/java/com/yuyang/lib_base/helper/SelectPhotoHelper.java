package com.yuyang.lib_base.helper;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
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

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

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
                        //裁剪参数 return-data 为 true 时，返回 bitmap，但是图片过大会崩溃
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

        pickMedia = activity.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                String photoPath = SelectImageUtil.getPath(activity, uri);
                if (mCropPhoto) {
                    cropPhoto(uri);
                } else {
                    if (onResultListener != null) {
                        onResultListener.onPhotoResult(getBitmapFromUri(uri), uri, photoPath);
                    }
                }
            } else {
                Log.d("SelectPhotoHelper", "No media selected");
            }
        });

        pickMultipleMedia = activity.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (!uris.isEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: " + uris.size());
            } else {
                Log.d("PhotoPicker", "No media selected");
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

//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);   //打开后可选择是图库获取还是图片获取
//        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //直接打开的是图片
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK);  //直接打开的就是图库
//        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);//单选
//        galleryIntent.setType("image/*");
//        galleryResultLauncher.launch(galleryIntent);

        // Launch the photo picker and let the user choose images and videos.
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
//                .build());

        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        // Launch the photo picker and let the user choose only videos.
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
//                .build());

        // Launch the photo picker and let the user choose only images/videos of a
        // specific MIME type, such as GIFs.
//        String mimeType = "image/gif";
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(new ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType))
//                .build());
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
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
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
            // 读取uri所在的图片
            return MediaStore.Images.Media.getBitmap(BaseApp.getInstance().getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}