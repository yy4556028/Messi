package com.yuyang.messi.ui.card;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.meipai.MeipaiBean;
import com.yuyang.messi.view.GravityViewUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeipaiRecyclerAdapter extends RecyclerView.Adapter<MeipaiRecyclerAdapter.CardHolder> {

    private int cardWidth;
    private int cardHeight;

    private LayoutInflater mInflater;
    private Context context;
    public MediaPlayer mediaPlayer;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public List<MeipaiBean> beanList = new ArrayList<>();

    public MeipaiRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cardWidth = (int) (CommonUtil.getScreenWidth() * 0.9f);
        cardHeight = cardWidth + PixelUtils.dp2px(45);
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new CardHolder(mInflater.inflate(R.layout.fragment_meipai_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        MeipaiBean meipaiBean = beanList.get(position);

        holder.coverImage.setVisibility(View.VISIBLE);
        GlideApp.with(context)
                .load(meipaiBean.getCover_pic())
                .into(holder.coverImage);
        GlideApp.with(context)
                .load(meipaiBean.getAvatar())
                .into(holder.avatarImage);
        holder.titleText.setText(meipaiBean.getCaption());
        holder.timeText.setText(sdf.format(new Date(meipaiBean.getCreated_at())));
        holder.viewsText.setText(meipaiBean.getPlays_count() + "播放");
        holder.commentText.setText(meipaiBean.getComments_count() + "评论");
        holder.sizeText.setText(meipaiBean.getLikes_count() + "赞");
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public class CardHolder extends RecyclerView.ViewHolder {
        TextureView textureView;
        ImageView coverImage;
        ImageView avatarImage;
        TextView titleText;
        TextView timeText;
        TextView viewsText;
        TextView commentText;
        TextView sizeText;
        ImageView praiseImage;
        ImageView passImage;
        ImageView reportImage;

        public CardHolder(View itemView) {
            super(itemView);
            itemView.getLayoutParams().width = cardWidth;
            itemView.getLayoutParams().height = cardHeight;
            textureView = itemView.findViewById(R.id.fragment_meipai_recycler_item_textureView);
            coverImage = itemView.findViewById(R.id.fragment_meipai_recycler_item_coverImage);
            avatarImage = itemView.findViewById(R.id.fragment_meipai_recycler_item_avatarImage);
            titleText = itemView.findViewById(R.id.fragment_meipai_recycler_item_titleText);
            timeText = itemView.findViewById(R.id.fragment_meipai_recycler_item_timeText);
            viewsText = itemView.findViewById(R.id.fragment_meipai_recycler_item_viewsText);
            commentText = itemView.findViewById(R.id.fragment_meipai_recycler_item_commentText);
            sizeText = itemView.findViewById(R.id.fragment_meipai_recycler_item_cheerText);
            praiseImage = itemView.findViewById(R.id.fragment_meipai_recycler_item_praiseImage);
            passImage = itemView.findViewById(R.id.fragment_meipai_recycler_item_passImage);
            reportImage = itemView.findViewById(R.id.fragment_meipai_recycler_item_reportImage);

            coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCardListener != null) {
                        onCardListener.onCardClick(beanList.get(getAdapterPosition()));
                    }
                    playVideo(textureView, coverImage, beanList.get(getAdapterPosition()).getUrl());
                }
            });

            textureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer == null) {
                        playVideo(textureView, coverImage, beanList.get(getAdapterPosition()).getUrl());
                        return;
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }
                }
            });

            if (context instanceof LifecycleOwner) {
                GravityViewUtil.addGravityMonitor(
                        (LifecycleOwner) context,
                        avatarImage);
            }
        }
    }

    public interface OnCardListener {
        void onCardClick(MeipaiBean meipaiBean);
    }

    private OnCardListener onCardListener;

    public void setOnCardListener(OnCardListener listener) {
        this.onCardListener = listener;
    }

    private void playVideo(TextureView textureView, final ImageView coverImage, final String videoUrl) {
        if (textureView.isAvailable()) {
            Surface surface = new Surface(textureView.getSurfaceTexture());
            initMediaPlayer(surface, coverImage, videoUrl);
        } else {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    Surface surface = new Surface(surfaceTexture);
                    initMediaPlayer(surface, coverImage, videoUrl);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            });
        }
    }

    private void initMediaPlayer(Surface surface, final ImageView coverImage, String videoUrl) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
//        mediaPlayer.setVolume(0.0f, 0.0f);//静音
            mediaPlayer.reset();
            mediaPlayer.setSurface(surface);
//            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setDataSource("http://vodfile11.news.cn//data/cdn_transfer/47/7F/4734e7487efe12d2185d87be89934d90d30cfd7f.mp4");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.pause();
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (coverImage != null) {
                        coverImage.setVisibility(View.GONE);
                    }
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



