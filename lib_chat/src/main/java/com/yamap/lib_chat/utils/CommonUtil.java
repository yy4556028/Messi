package com.yamap.lib_chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.yamap.lib_chat.Constants;
import com.yamap.lib_chat.FuncGridView;
import com.yamap.lib_chat.R;
import com.yamap.lib_chat.adapter.emoticonadapter.BigEmoticonsAdapter;
import com.yamap.lib_chat.adapter.emoticonadapter.BigEmoticonsAndTitleAdapter;
import com.yamap.lib_chat.adapter.emoticonadapter.TextEmoticonsAdapter;
import com.yamap.lib_chat.emotions.DefXhsEmoticons;
import com.yamap.lib_chat.filter.EmojiFilter;
import com.yamap.lib_chat.filter.XhsFilter;
import com.yamap.lib_chat.keyboard.adpater.EmoticonAdapter;
import com.yamap.lib_chat.keyboard.adpater.PageSetAdapter;
import com.yamap.lib_chat.keyboard.data.EmoticonEntity;
import com.yamap.lib_chat.keyboard.data.EmoticonPageEntity;
import com.yamap.lib_chat.keyboard.data.EmoticonPageSetEntity;
import com.yamap.lib_chat.keyboard.data.PageEntity;
import com.yamap.lib_chat.keyboard.data.PageSetEntity;
import com.yamap.lib_chat.keyboard.interfaces.EmoticonClickListener;
import com.yamap.lib_chat.keyboard.interfaces.EmoticonDisplayListener;
import com.yamap.lib_chat.keyboard.interfaces.PageViewInstantiateListener;
import com.yamap.lib_chat.keyboard.utils.imageloader.ImageBase;
import com.yamap.lib_chat.keyboard.utils.imageloader.ImageLoader;
import com.yamap.lib_chat.keyboard.widget.ChatEditText;
import com.yamap.lib_chat.keyboard.widget.ChatPageView;

import java.io.IOException;
import java.lang.reflect.Constructor;


public class CommonUtil {

    public static void initEmoticonsEditText(ChatEditText chatEditText) {
        chatEditText.addEmoticonFilter(new EmojiFilter());
        chatEditText.addEmoticonFilter(new XhsFilter());
    }


    public static PageSetAdapter sCommonPageSetAdapter;

    public static PageSetAdapter getCommonAdapter(Context context, EmoticonClickListener emoticonClickListener) {

        if(sCommonPageSetAdapter != null){
            return sCommonPageSetAdapter;
        }

        PageSetAdapter pageSetAdapter = new PageSetAdapter();

//        addEmojiPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addXhsPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addWechatPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addGoodGoodStudyPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addKaomojiPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addTestPageSetEntity(pageSetAdapter, context);

        return pageSetAdapter;
    }


    /**
     * 插入xhs表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addXhsPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity xhsPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(ParseDataUtils.ParseXhsData(DefXhsEmoticons.xhsEmoticonArray, ImageBase.Scheme.ASSETS))
                .setIPageViewInstantiateItem(getDefaultEmoticonPageViewInstantiateItem(getCommonEmoticonDisplayListener(emoticonClickListener, Constants.EMOTICON_CLICK_TEXT)))
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(ImageBase.Scheme.ASSETS.toUri("xhsemoji_19.png"))
                .build();
        pageSetAdapter.add(xhsPageSetEntity);
    }

    /**
     * 插入微信表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addWechatPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        String filePath = getFolderPath("wxemoticons");
        EmoticonPageSetEntity<EmoticonEntity> emoticonPageSetEntity = ParseDataUtils.parseDataFromFile(context, filePath, "wxemoticons.zip", "wxemoticons.xml");
        if (emoticonPageSetEntity == null) {
            return;
        }
        EmoticonPageSetEntity pageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(emoticonPageSetEntity.getLine())
                .setRow(emoticonPageSetEntity.getRow())
                .setEmoticonList(emoticonPageSetEntity.getEmoticonList())
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(BigEmoticonsAdapter.class, emoticonClickListener))
                .setIconUri(ImageBase.Scheme.FILE.toUri(filePath + "/" + emoticonPageSetEntity.getIconUri()))
                .build();
        pageSetAdapter.add(pageSetEntity);
    }

    /**
     * 插入我们爱学习表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addGoodGoodStudyPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        String filePath = getFolderPath("goodgoodstudy");
        EmoticonPageSetEntity<EmoticonEntity> emoticonPageSetEntity = ParseDataUtils.parseDataFromFile(context, filePath, "goodgoodstudy.zip", "goodgoodstudy.xml");
        if (emoticonPageSetEntity == null) {
            return;
        }
        EmoticonPageSetEntity pageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(emoticonPageSetEntity.getLine())
                .setRow(emoticonPageSetEntity.getRow())
                .setEmoticonList(emoticonPageSetEntity.getEmoticonList())
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(BigEmoticonsAndTitleAdapter.class, emoticonClickListener))
                .setIconUri(ImageBase.Scheme.FILE.toUri(filePath + "/" + emoticonPageSetEntity.getIconUri()))
                .build();
        pageSetAdapter.add(pageSetEntity);
    }


    /**
     * 插入颜文字表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addKaomojiPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity kaomojiPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(3)
                .setEmoticonList(ParseDataUtils.parseKaomojiData(context))
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(TextEmoticonsAdapter.class, emoticonClickListener))
                .setIconUri(ImageBase.Scheme.DRAWABLE.toUri("icon_kaomoji"))
                .build();
        pageSetAdapter.add(kaomojiPageSetEntity);
    }

    /**
     * 测试页集
     *
     * @param pageSetAdapter
     * @param context
     */
    public static void addTestPageSetEntity(PageSetAdapter pageSetAdapter, Context context) {
        PageSetEntity pageSetEntity = new PageSetEntity.Builder()
                .addPageEntity(new PageEntity(new FuncGridView(context)))
                .setIconUri(ImageBase.Scheme.DRAWABLE.toUri("icon_kaomoji"))
                .setShowIndicator(false)
                .build();
        pageSetAdapter.add(pageSetEntity);
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(Class _Class, Object... args) throws Exception {
        return newInstance(_Class, 0, args);
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(Class _Class, int constructorIndex, Object... args) throws Exception {
        Constructor cons = _Class.getConstructors()[constructorIndex];
        return cons.newInstance(args);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getDefaultEmoticonPageViewInstantiateItem(final EmoticonDisplayListener<Object> emoticonDisplayListener) {
        return getEmoticonPageViewInstantiateItem(EmoticonAdapter.class, null, emoticonDisplayListener);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getEmoticonPageViewInstantiateItem(final Class _class, EmoticonClickListener onEmoticonClickListener) {
        return getEmoticonPageViewInstantiateItem(_class, onEmoticonClickListener, null);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getEmoticonPageViewInstantiateItem(final Class _class, final EmoticonClickListener onEmoticonClickListener, final EmoticonDisplayListener<Object> emoticonDisplayListener) {
        return new PageViewInstantiateListener<EmoticonPageEntity>() {
            @Override
            public View instantiateItem(ViewGroup container, int position, EmoticonPageEntity pageEntity) {
                if (pageEntity.getRootView() == null) {
                    ChatPageView pageView = new ChatPageView(container.getContext());
                    pageView.setNumColumns(pageEntity.getRow());
                    pageEntity.setRootView(pageView);
                    try {
                        EmoticonAdapter adapter = (EmoticonAdapter) newInstance(_class, container.getContext(), pageEntity, onEmoticonClickListener);
                        if (emoticonDisplayListener != null) {
                            adapter.setOnDisPlayListener(emoticonDisplayListener);
                        }
                        pageView.getGridView().setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return pageEntity.getRootView();
            }
        };
    }

    public static EmoticonDisplayListener<Object> getCommonEmoticonDisplayListener(final EmoticonClickListener onEmoticonClickListener, final int type) {
        return new EmoticonDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, EmoticonAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {

                final EmoticonEntity emoticonEntity = (EmoticonEntity) object;
                if (emoticonEntity == null && !isDelBtn) {
                    return;
                }
                viewHolder.lyt.setBackgroundResource(R.drawable.bg_emoticon);

                if (isDelBtn) {
                    viewHolder.icon.setImageResource(R.mipmap.icon_del);
                } else {
                    try {
                        ImageLoader.getInstance(viewHolder.icon.getContext()).displayImage(emoticonEntity.getIconUri(), viewHolder.icon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onEmoticonClickListener != null) {
                            onEmoticonClickListener.onEmoticonClick(emoticonEntity, type, isDelBtn);
                        }
                    }
                });
            }
        };
    }

    public static void delClick(EditText editText) {
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    public static float dp2px(float dp) {
        return Resources.getSystem().getDisplayMetrics().density * dp;
    }

    private static String getFolderPath(String folder) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/XhsEmoticonsKeyboard/Emoticons/" + folder;
    }
}
