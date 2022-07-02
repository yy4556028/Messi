package com.yuyang.messi.kotlinui.category;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.AlignSelf;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.card.MaterialCardView;
import com.yuyang.messi.R;
import com.yuyang.messi.room.entity.ModuleEntity;
import com.yuyang.messi.utils.ColorUtil;

import java.util.List;

/**
 * @see FlexboxLayout
 */
public class CategoryAdapter extends BaseQuickAdapter<ModuleEntity, BaseViewHolder> {

    public CategoryAdapter(@Nullable List<ModuleEntity> data) {
        super(R.layout.fragment_category_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ModuleEntity moduleEntity) {
        ViewGroup.LayoutParams lp = helper.itemView.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            /*
             * layout_flexGrow 属性定义项目的放大比例，默认为0，
             * 即如果存在剩余空间，也不放大。其实就是 LinearLayout 中的weight属性，
             * 如果所有项目的layout_flexGrow 属性都为1，则它们将等分剩余空间。
             * 如果一个项目的layout_flexGrow 属性为2，其他项目都为1，则前者占据的剩余空间将比其他项多一倍。
             */
            flexboxLp.setFlexGrow(0f);
            /*
             * layout_flexShrink 属性定义了项目的缩小比例，默认为1，
             * 即如果空间不足，该项目将缩小。
             * 如果所有项目的 layout_flexShrink 属性都为1，当空间不足时，都将等比例缩小。
             * 如果一个项目的flex-shrink属性为0，其他项目都为1，则空间不足时，前者不缩小。
             * 负值对该属性无效
             */
            flexboxLp.setFlexShrink(1f);
            /*
              layout_alignSelf 属性允许单个子元素有与其他子元素不一样的对齐方式，可覆盖 alignItems 属性。
              默认值为auto，表示继承父元素的alignItems 属性，
              如果没有父元素，则等同于stretch
             */
            flexboxLp.setAlignSelf(AlignSelf.AUTO);
            /*
             * layout_flexBasisPercent 属性定义了在分配多余空间之前，子元素占据的主轴空间的百分比。
             * 它的默认值为auto，即子元素的本来大小
             */
            flexboxLp.setFlexBasisPercent(-1f);
        }

        int color = ColorUtil.getRandomColor();

        helper.setText(R.id.fragment_category_item_textView, moduleEntity.getName())
        .setTextColor(R.id.fragment_category_item_textView, color);

        MaterialCardView cardView = helper.getView(R.id.fragment_category_item_cardView);
        cardView.setCardBackgroundColor(ColorUtil.getReverseColor(color));
    }

}
