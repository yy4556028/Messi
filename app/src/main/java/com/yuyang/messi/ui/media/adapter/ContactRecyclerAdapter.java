package com.yuyang.messi.ui.media.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ContactBean;

import java.util.List;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, SectionIndexer {

    private Context mContext;
    private List<ContactBean> beanList;
    public int headerCount;//XRecyclerView 搭配 sticky会有问题

    public ContactRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<ContactBean> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_contact_item, parent, false));
            }
            default:
                return new EmptyHolder(CommonUtil.getEmptyViewForListView(mContext));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (0 == getItemViewType(position)) {
            MyHolder myHolder = (MyHolder) holder;
            ContactBean contactBean = beanList.get(position);

            myHolder.quickContactBadge.assignContactUri(ContactsContract.Contacts.getLookupUri(contactBean.getContactId(), contactBean.getLookUpKey()));
            Bitmap bitmap = contactBean.getPhotoBitmap();
            if (bitmap == null) {
                myHolder.quickContactBadge.setImageResource(R.drawable.avatar);
            } else {
                myHolder.quickContactBadge.setImageBitmap(contactBean.getPhotoBitmap());
            }

            myHolder.nameText.setText(contactBean.getDisplayName());

            int section = getSectionForPosition(position + headerCount);
            if (position == getPositionForSection(section) - headerCount) {
                myHolder.dividerLine.setVisibility(View.GONE);
            } else {
                myHolder.dividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (beanList == null || beanList.size() == 0) {
            return 0;
        }
        return beanList.size();
    }

    @Override
    public int getItemRealCount() {
        if (beanList == null || beanList.size() == 0) {
            return headerCount;
        }
        return beanList.size() + headerCount;
    }

    @Override
    public int getItemViewType(int position) {
        return (beanList == null || beanList.size() == 0) ? 1 : 0;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private View dividerLine;
        private QuickContactBadge quickContactBadge;
        private TextView nameText;
        private ImageView phoneImage;
        private GestureDetectorCompat gestureDetector;

        MyHolder(View itemView) {
            super(itemView);
            dividerLine = itemView.findViewById(R.id.activity_contact_item_line);
            quickContactBadge = itemView.findViewById(R.id.activity_contact_item_avatarImage);
            nameText = itemView.findViewById(R.id.activity_contact_item_nameText);
            phoneImage = itemView.findViewById(R.id.activity_contact_item_phoneImage);
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition() - headerCount;
                    if (listener != null) {
                        listener.onItemPhoneClick(beanList.get(index));
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition() - headerCount;
                    if (listener != null) {
                        listener.onItemClick(beanList.get(index));
                    }
                }
            });
            quickContactBadge.post(new Runnable() {
                @Override
                public void run() {
                    quickContactBadge.setPivotX(quickContactBadge.getWidth() / 2);
                    quickContactBadge.setPivotY(quickContactBadge.getHeight());
                }
            });
            gestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    quickContactBadge.performClick();
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    ObjectAnimator.ofFloat(quickContactBadge, "rotation", 0, 5, -5, 5, -5, 0)
                            .setDuration(1000)
                            .start();
                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1);
                    return super.onDoubleTap(e);
                }
            });
            quickContactBadge.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        EmptyHolder(View view) {
            super(view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ContactBean contactBean);

        void onItemPhoneClick(ContactBean contactBean);
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 以下三个方法为实现接口实现的方法，目的为了获取首字母
     **/
    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类列的索引号获取该序列的首个位置
     **/
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = beanList.get(i).getSortKey();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i + headerCount;
            }
        }
        return -1;
    }

    /**
     * 根据该项的位置，获取所在分类组的索引号
     **/
    @Override
    public int getSectionForPosition(int position) {
        return beanList.get(position - headerCount).getSortKey().toUpperCase().charAt(0);
    }

    /****************** 以下方法为有关吸顶效果的部分 **************/

    /**
     * 去除列表的头部部分，确保返回的是真实的 position
     **/
    @Override
    public long getHeaderId(int position) {
        if (position < headerCount) {
            return -1;
        } else {
            int section = getSectionForPosition(position);
            return getSectionForPosition(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_contact_item_header, viewGroup, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(beanList.get(position - headerCount).getSortKey().toUpperCase().substring(0, 1));
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}






