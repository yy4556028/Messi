package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.bean.PhotoDirectory;
import com.yuyang.lib_base.utils.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.MyHolder> {

    private final LayoutInflater inflater;

    private final List<PhotoDirectory> photoDirectories;
    private final ArrayList<ImageBean> selectedImageBeans = new ArrayList<>();

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;

    private final boolean hasCamera;
    private final boolean supportPreview;

    private final int itemSize;

    public int currentDirectoryIndex = 0;

    public AlbumRecyclerAdapter(Context context,
                                List<PhotoDirectory> photoDirectories,
                                ArrayList<ImageBean> originalPhotos,
                                int columnNumber,
                                boolean showCamera,
                                boolean supportPreview) {
        this.photoDirectories = photoDirectories;
        this.hasCamera = showCamera;
        this.supportPreview = supportPreview;
        inflater = LayoutInflater.from(context);
        itemSize = CommonUtil.getScreenWidth() / columnNumber;
        if (originalPhotos != null) {
            selectedImageBeans.addAll(originalPhotos);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.activity_album_recycler_item, parent, false);
        MyHolder holder = new MyHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.checkBox.setVisibility(View.GONE);
            holder.photoImageView.setScaleType(ImageView.ScaleType.CENTER);

            holder.photoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onGalleryGirdListener != null) {
                        onGalleryGirdListener.onCameraClick();
                    }
                }
            });
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        holder.itemView.getLayoutParams().height = itemSize;

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {
            List<ImageBean> imageBeans = photoDirectories.get(currentDirectoryIndex).getImageBeans();
            final ImageBean imageBean;

            if (showCamera()) {
                imageBean = imageBeans.get(position - 1);
            } else {
                imageBean = imageBeans.get(position);
            }

//            if (photo.getPath().toLowerCase().endsWith("gif")) {
            GlideApp.with(holder.itemView.getContext())
                    .load(imageBean.getImageUri())
                    .placeholder(R.drawable.photo_loading)
                    .error(R.drawable.photo_error)
//                    .dontTransform()
                    .transform(new CenterCrop(), new RoundedCorners(PixelUtils.dp2px(6)))
//                        .override(itemSize, itemSize)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                    .skipMemoryCache(true)
                    .into(holder.photoImageView);
//            } else {
//                GlideApp.with(holder.itemView.getContext())
//                        .load(photo.getPath())
//                        .placeholder(R.drawable.photo_loading)
//                        .error(R.drawable.photo_error)
//                        .thumbnail(0.5f)
//                        .dontAnimate()
//                        .dontTransform()
//                        .override(itemSize, itemSize)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
//                        .skipMemoryCache(true)
//                        .into(holder.photoImageView);
//            }

//            holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.shake));

            final boolean isChecked = isSelected(imageBean);

            holder.checkBox.setChecked(isChecked);
            holder.photoImageView.setSelected(isChecked);

            holder.photoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (supportPreview) {
                        if (onGalleryGirdListener != null) {
                            int pos = holder.getAdapterPosition();
                            onGalleryGirdListener.onPhotoClick(view, pos, showCamera());
                        }
                    } else {
                        holder.checkBox.performClick();
                    }
                }
            });
            holder.photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    List<ImageBean> imageBeans = photoDirectories.get(currentDirectoryIndex).getImageBeans();
                    ImageBean imageBean = imageBeans.get(holder.getAdapterPosition());
                    File file = new File(imageBean.getPath());
                    ToastUtil.showToast(DateUtil.formatTimestampToString(0) + "\r\n" +
                            DateUtil.formatTimestampToString(1000) + "\r\n" +
                            DateUtil.formatTimestampToString(file.lastModified()) + "\r\n" +
                            DateUtil.formatTimestampToString(file.lastModified() * 1000));
                    return true;
                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    boolean isEnable = true;

                    if (onGalleryGirdListener != null) {
                        isEnable = onGalleryGirdListener.OnItemCheck(pos, imageBean, isChecked, selectedImageBeans.size());
                    }
                    if (isEnable) {
                        toggleSelection(imageBean);
                        notifyItemChanged(pos);
                    } else {
                        holder.checkBox.setChecked(isChecked);
                    }
                }
            });
        } else {
            holder.photoImageView.setImageResource(R.drawable.fragment_photo_picker_camera);
        }
    }


    @Override
    public int getItemCount() {
        int photosCount = photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }

    @Override
    public void onViewRecycled(@NonNull MyHolder holder) {
        GlideApp.with(holder.photoImageView.getContext()).clear(holder.photoImageView);
        super.onViewRecycled(holder);
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private final ImageView photoImageView;
        private final AppCompatCheckBox checkBox;

        public MyHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.activity_album_recycler_item_photo);
            checkBox = itemView.findViewById(R.id.activity_album_recycler_item_check);
        }
    }

    private void toggleSelection(ImageBean imageBean) {
        if (selectedImageBeans.contains(imageBean)) {
            selectedImageBeans.remove(imageBean);
        } else {
            selectedImageBeans.add(imageBean);
        }
    }

    public List<ImageBean> getCurrentPhotos() {
        return photoDirectories.get(currentDirectoryIndex).getImageBeans();
    }

    public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
        this.currentDirectoryIndex = currentDirectoryIndex;
    }

    private boolean isSelected(ImageBean imageBean) {
        return selectedImageBeans.contains(imageBean);
    }

    public ArrayList<ImageBean> getSelectedImageBeans() {
        return selectedImageBeans;
    }

    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == 0);
    }

    private OnGalleryGirdListener onGalleryGirdListener;

    public interface OnGalleryGirdListener {

        void onCameraClick();

        void onPhotoClick(View v, int position, boolean showCamera);

        /***
         * @param position          所选图片的位置
         * @param imageBean             所选的图片
         * @param isCheck           当前状态
         * @param selectedItemCount 已选数量
         * @return enable check
         */
        boolean OnItemCheck(int position, ImageBean imageBean, boolean isCheck, int selectedItemCount);
    }

    public void setOnGalleryGirdListener(OnGalleryGirdListener listener) {
        onGalleryGirdListener = listener;
    }
}
