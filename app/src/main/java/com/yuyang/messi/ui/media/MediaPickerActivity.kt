package com.yuyang.messi.ui.media

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.yuyang.lib_base.bean.PopBean
import com.yuyang.lib_base.helper.SelectImageUtil
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog
import com.yuyang.lib_base.utils.BitmapUtil
import com.yuyang.lib_base.utils.StorageUtil
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.bean.ImageBean
import com.yuyang.messi.databinding.ActivityMediaPickerBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.utils.PermissionUtil
import java.io.File

class MediaPickerActivity : AppBaseActivity() {

    private lateinit var binding: ActivityMediaPickerBinding

    private var cropFile: File? = null

    private val pickMediaSys =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                ToastUtil.showToast("Selected URI: $uri")
                Glide.with(activity).load(uri).into(binding.imageView)
            } else {
                ToastUtil.showToast("No media selected")
            }
        }

    private val pickMultipleMediaSys =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(9)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                ToastUtil.showToast("Number of items selected: ${uris.size}")
                Glide.with(activity).load(uris[0]).into(binding.imageView)
            } else {
                ToastUtil.showToast("No media selected")
            }
        }

    private val pickImgCustomPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result: Map<String, Boolean> ->
            val deniedAskList: MutableList<String> = ArrayList()
            val deniedNoAskList: MutableList<String> = ArrayList()
            for ((key, value) in result) {
                if (!value) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, key)) {
                        deniedAskList.add(key)
                    } else {
                        deniedNoAskList.add(key)
                    }
                }
            }
            if (deniedAskList.isEmpty() && deniedNoAskList.isEmpty()) { //全通过
                pickImageCustom.launch(
                    AlbumActivity.getLaunchIntent(
                        activity,
                        7,
                        true,
                        true,
                        true,
                        null
                    )
                )
            } else if (deniedNoAskList.isNotEmpty()) {
                PermissionUtil.showMissingPermissionDialog(activity)
            } else {
                ToastUtil.showToast("您拒绝了部分权限")
            }
        }

    private val pickImageCustom =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (RESULT_OK != result.resultCode) return@registerForActivityResult
            val selectImages =
                result.data?.getParcelableArrayListExtra<ImageBean>(AlbumActivity.SELECTED_PHOTOS)
            ToastUtil.showToast(selectImages?.size.toString() + "")
            if (selectImages != null && selectImages.size > 0) {
                Glide.with(activity).load(selectImages[0].imageUri).into(binding.imageView)
            }
        }

    private val pickFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data

                val photoPath = SelectImageUtil.getPath(activity, uri)
                val bitmap = BitmapUtil.loadBitmapFromUri(uri)
                Glide.with(activity).load(uri).into(binding.imageView)
//                Glide.with(activity).load(File(photoPath)).into(binding.imageView)
//                Glide.with(activity).load(bitmap).into(binding.imageView)
            }
        }

    private val externalStorageManagerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? -> }

    private val pickFileImageCrop =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data!!
                cropImage(uri, cropFile!!)
            }
        }

    private val cropSystemLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //裁剪参数 return-data 为 true 时，返回 bitmap，但是图片过大会崩溃
                //Bitmap photoBitmap = result.getData().getParcelableExtra("data");
                //Bitmap photo = extras.getParcelable("data");
                val cropUri = Uri.fromFile(cropFile)
                val cropBitmap = BitmapUtil.loadBitmapFromUri(cropUri)
                if (cropBitmap != null) {
                    binding.imageView.setImageBitmap(cropBitmap)
                } else {
                    Glide.with(activity).load(cropFile).into(binding.imageView)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMediaPickerBinding.inflate(layoutInflater).run {
            setContentView(root)
            binding = this
            bindViews(this)
        }
    }

    private fun bindViews(binding: ActivityMediaPickerBinding) {
        with(binding) {
            //无需请求任何权限
            pickPhotoSysNew.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    BottomChooseDialog.showSingle(
                        activity,
                        "请选择",
                        listOf(
                            PopBean(null, "单张照片或视频"),
                            PopBean(null, "单张照片"),
                            PopBean(null, "单张视频"),
                            PopBean(null, "单张GIF"),
                            PopBean(null, "多张图片或视频")
                        )
                    ) { index: Int, popBean: PopBean? ->
                        when (index) {
                            0 -> pickMediaSys.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )

                            1 -> pickMediaSys.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )

                            2 -> pickMediaSys.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.VideoOnly
                                )
                            )

                            3 -> {
                                val mimeType = "image/gif"
                                pickMediaSys.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.SingleMimeType(
                                            mimeType
                                        )
                                    )
                                )
                            }

                            4 -> pickMultipleMediaSys.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )
                        }
                    }
                } else {
                    ToastUtil.showToast("仅支持Android 11及以上(>=30)")
                }
            }

            pickPhotoCustom.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    pickImgCustomPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                        )
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImgCustomPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                        )
                    )
                } else {
                    pickImgCustomPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                        )
                    )
                }
            }

            //选择无需请求任何权限，crop需要权限(具体后面研究)
            actionPick.setOnClickListener {
                BottomChooseDialog.showSingle(
                    activity,
                    "请选择",
                    listOf(
                        PopBean(null, "单张照片"),
                        PopBean(null, "单张视频"),
                        PopBean(null, "单张图片后裁剪")
                    )
                ) { index: Int, popBean: PopBean? ->
                    when (index) {
                        0 -> {
                            pickFile.launch(Intent().apply {
                                action = Intent.ACTION_PICK
                                type = "image/*"
                                // 无效参数 只能单个
//                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            })
                        }

                        1 -> {
                            pickFile.launch(Intent().apply {
                                action = Intent.ACTION_PICK
                                type = "video/*"
                            })
                        }

                        2 -> {
                            // 先判断有没有权限
                            // Android11之后存储发生变更，APP只能访问自己的私有目录或者公共目录，不能访问别的app的私有目录
                            // 除非请求 ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION 或 ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION 权限
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                if (!Environment.isExternalStorageManager()) {
                                    val intent =
                                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                    intent.setData(Uri.parse("package:$packageName"))
                                    externalStorageManagerLauncher.launch(intent)
                                    return@showSingle
                                }

                                // 从 Android 10（API 级别 29）开始，应用不再需要存储权限即可向共享存储空间添加文件。
                                cropFile = File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    "crop.jpg"
                                )
                            } else {
                                //低版本只能crop在私有目录|
                                cropFile = File(StorageUtil.getPrivateCache(), "avatar.jpg")
                            }

                            pickFileImageCrop.launch(Intent().apply {
                                action = Intent.ACTION_PICK
                                type = "image/*"
                            })
                        }
                    }
                }
            }

            //无需请求任何权限 SAF(存储访问框架--Storage Access Framework)
//            https://stackoverflow.com/questions/36182134/what-is-the-real-difference-between-action-get-content-and-action-open-document
            openDocument.setOnClickListener {
                pickFile.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
//                    type = "application/pdf"
                    type = "image/*"
                    // Optionally, specify a URI for the file that should appear in the
                    // system file picker when it loads.
//                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                })
            }
            //无需请求任何权限
            getContent.setOnClickListener {
                pickFile.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                })
            }
        }
    }

    private fun cropImage(uri: Uri, outputFile: File) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cropIntent.setDataAndType(uri, "image/*")
        cropIntent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        // aspectX aspectY 是宽高的比例
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        // outputX outputY 是裁剪图片宽高
        cropIntent.putExtra("outputX", 200)
        cropIntent.putExtra("outputY", 200)
        cropIntent.putExtra("scale", true)
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile))
        // 裁剪参数 return-data 为 true 时，返回 bitmap，但是图片过大会崩溃
        cropIntent.putExtra("return-data", false)
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        cropIntent.putExtra("noFaceDetection", true)
        cropSystemLauncher.launch(cropIntent)
    }
}