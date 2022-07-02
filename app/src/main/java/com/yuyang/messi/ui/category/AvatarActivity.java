package com.yuyang.messi.ui.category;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.helper.SelectPhotoHelper;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.helper.AutoClickHelper;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.GroupAvatarView;
import com.yuyang.messi.view.PraiseView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AvatarActivity extends AppBaseActivity {

    private ImageButton imageButton;

    private GroupAvatarView groupAvatarView;

    private final SelectPhotoHelper selectPhotoHelper = new SelectPhotoHelper(getActivity());

    @Override
    protected int getLayoutId() {
        return R.layout.activity_avatar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initPraiseView();
        AutoClickHelper autoTouch = new AutoClickHelper();
        autoTouch.autoClickPos(500, 580, 520, 500);

        selectPhotoHelper.setOnResultListener(new SelectPhotoHelper.OnResultListener() {
            @Override
            public void onPhotoResult(Bitmap bitmap, Uri uri, String photoPath) {
                imageButton.setImageBitmap(bitmap);
            }
        });
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("头像");

        imageButton = findViewById(R.id.activity_avatar_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomChooseDialog.showSingle(getActivity(),
                        null,
                        Arrays.asList(new PopBean(null, "拍照"), new PopBean(null, "相册")),
                        new BottomChooseDialog.SingleChoiceListener() {
                            @Override
                            public void onItemClick(int index, PopBean popBean) {
                                switch (index) {
                                    case 0: {    //相册
                                        selectPhotoHelper.takePhoto(new File(StorageUtil.getPrivateCache(), "avatar.jpg"), true);
                                        break;
                                    }
                                    case 1: {    //拍照
                                        selectPhotoHelper.takeGallery(new File(StorageUtil.getPrivateCache(), "avatar.jpg"), true);
                                        break;
                                    }
                                }
                            }
                        });
            }
        });

        final ArrayList<Bitmap> mBmps = new ArrayList<>();
        mBmps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        groupAvatarView = findViewById(R.id.activity_avatar_groupAvatarView);
        final List<Bitmap> allBitmapList = new ArrayList<>();

        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_add));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_avatar));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_emoji));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.icon_card_cheer));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.icon_card_pass));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.icon_face_normal));
        allBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.activity_camera_flash_auto));
        groupAvatarView.setBitmapList(null);

        final List<Bitmap> bitmapList = new ArrayList<>();
        groupAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapList.add(allBitmapList.get(new Random().nextInt(allBitmapList.size())));
                if (bitmapList.size() > groupAvatarView.getMaxLineNum() * groupAvatarView.getMaxLineNum()) {
                    bitmapList.clear();
                }
                groupAvatarView.setBitmapList(bitmapList);
            }
        });
        groupAvatarView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (groupAvatarView.getArrangeGravity() == Gravity.START) {
                    groupAvatarView.setArrangeGravity(Gravity.CENTER);
                } else {
                    groupAvatarView.setArrangeGravity(Gravity.START);
                }
                return true;
            }
        });
    }

    private void initPraiseView() {

        PraiseView praiseView = new PraiseView(getActivity());
        ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).addView(praiseView);
        praiseView.setOnPraiseListener(new PraiseView.onPraiseListener() {
            @Override
            public void onPraise(int count) {

            }
        });
    }
}