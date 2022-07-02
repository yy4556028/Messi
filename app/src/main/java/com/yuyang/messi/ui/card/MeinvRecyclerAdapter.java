package com.yuyang.messi.ui.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.GankBean;
import com.yuyang.lib_base.utils.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MeinvRecyclerAdapter extends RecyclerView.Adapter<MeinvRecyclerAdapter.CardHolder> {

    private int cardWidth;
    private int cardHeight;

    private LayoutInflater mInflater;
    private Context context;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public List<GankBean> beanList = new ArrayList<>();

    public MeinvRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cardWidth = (int) (CommonUtil.getScreenWidth() * 0.9f);
        cardHeight = cardWidth + PixelUtils.dp2px(45);
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new CardHolder(mInflater.inflate(R.layout.fragment_card_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {

        GankBean gankBean = beanList.get(position);

        Glide.with(context)
                .load(gankBean.getUrl())
                .into(holder.showImage);
        holder.titleText.setText(gankBean.getDesc());
        holder.timeText.setText(gankBean.getPublishedAt());
        holder.viewsText.setText(0 + "浏览");
        holder.commentText.setText(0 + "评论");
        holder.sizeText.setText(0 + "张");
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
        ImageView showImage;
        TextView titleText;
        TextView timeText;
        TextView viewsText;
        TextView commentText;
        TextView sizeText;
        public ImageView praiseImage;
        public ImageView passImage;
        public ImageView reportImage;

        public CardHolder(View itemView) {
            super(itemView);
            itemView.getLayoutParams().width = cardWidth;
            itemView.getLayoutParams().height = cardHeight;
            showImage = (ImageView) itemView.findViewById(R.id.fragment_card_recycler_item_showImage);
            titleText = (TextView) itemView.findViewById(R.id.fragment_card_recycler_item_titleText);
            timeText = (TextView) itemView.findViewById(R.id.fragment_card_recycler_item_timeText);
            viewsText = (TextView) itemView.findViewById(R.id.fragment_card_recycler_item_viewsText);
            commentText = (TextView) itemView.findViewById(R.id.fragment_card_recycler_item_commentText);
            sizeText = (TextView) itemView.findViewById(R.id.fragment_card_recycler_item_sizeText);
            praiseImage = (ImageView) itemView.findViewById(R.id.fragment_card_recycler_item_praiseImage);
            passImage = (ImageView) itemView.findViewById(R.id.fragment_card_recycler_item_passImage);
            reportImage = (ImageView) itemView.findViewById(R.id.fragment_card_recycler_item_reportImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCardListener != null) {
                        onCardListener.onCardClick(beanList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnCardListener{
        void onCardClick(GankBean gankBean);
    }

    private OnCardListener onCardListener;

    public void setOnCardListener(OnCardListener listener) {
        this.onCardListener = listener;
    }

}



