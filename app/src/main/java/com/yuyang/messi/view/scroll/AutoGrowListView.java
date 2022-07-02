package com.yuyang.messi.view.scroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

public class AutoGrowListView extends ListView implements OnScrollListener {

	private static final int SCROLL_MSG = 123;

	private View[] itemsInWholePage;

	private float minRate = 0.3f;
	int bigItemHeight = 0;
	int smallItemHeight = 0;

	private Handler handler = new MHandler(this);

	static class MHandler extends Handler {

		WeakReference<ListView> contextView;

		public MHandler(ListView listView) {
			contextView = new WeakReference<ListView>(listView);
		}

		@Override
		public void handleMessage(Message msg) {
			ListView listView = contextView.get();
			switch (msg.what) {
			case SCROLL_MSG:
				listView.smoothScrollBy(msg.arg1, 300);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	public AutoGrowListView(Context context) {
		this(context, null);
	}

	public AutoGrowListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOnScrollListener(this);
		setDivider(null);
	}

	public void setBigItemHeight(int bigItemHeight) {
		this.bigItemHeight = bigItemHeight;
	}

	public void setSmallItemHeight(int smallItemHeight) {
		this.smallItemHeight = smallItemHeight;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		switch (scrollState) {
		// 当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；
		// 由于用户的操作，屏幕产生惯性滑动时为2
		case OnScrollListener.SCROLL_STATE_FLING:
			// this.smoothScrollBy(0, 0);
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (getFirstVisiblePosition() < getHeaderViewsCount() || getChildCount() < 2)
				return;

			View firstChild = getChildAt(0);
			View secondChild = getChildAt(1);

			if (getCount() - getFooterViewsCount() <= getPositionForView(secondChild))
				return;
			View shouldBeFirstView = null;
			int offset = 0;

			if (getChildCount() > 1 && firstChild != null) {
				// int firstVisibleHeight = firstChild.getBottom();
				// shouldBeFirstView = firstVisibleHeight > smallItemHeight ?
				// firstChild : secondChild;
				shouldBeFirstView = secondChild.getHeight() < (bigItemHeight + smallItemHeight) / 2 ? firstChild : secondChild;
				offset = secondChild.getHeight() < (bigItemHeight + smallItemHeight) / 2 ? firstChild.getTop() : secondChild.getBottom() - bigItemHeight;
			} else {
				shouldBeFirstView = firstChild;
			}

			if (shouldBeFirstView != null) {
				if (shouldBeFirstView.getTop() != 0) {
					Message message = handler.obtainMessage(SCROLL_MSG);
					message.arg1 = offset;
					message.sendToTarget();
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// if (radio == 0)
		// radio = (maxAlpha - minAlpha) / (bigItemHeight - smallItemHeight);

		int headViewCount = getHeaderViewsCount();
		int footViewCount = getFooterViewsCount();

		int firstVisibleListItemPosition = firstVisibleItem < headViewCount ? headViewCount - firstVisibleItem : 0;
		int firstVisibleFooterPosition = firstVisibleItem < totalItemCount - footViewCount ? totalItemCount - footViewCount - firstVisibleItem : 0;

		if (itemsInWholePage != null)
			itemsInWholePage = null;

		itemsInWholePage = new View[visibleItemCount];

		for (int i = 0; i < itemsInWholePage.length; i++) {
			itemsInWholePage[i] = view.getChildAt(i);
		}

		if (itemsInWholePage.length > firstVisibleListItemPosition) {
			int newHeight = bigItemHeight + itemsInWholePage[0].getTop() * (bigItemHeight - smallItemHeight) / smallItemHeight;
			if (firstVisibleListItemPosition == 0) {
				if (newHeight > bigItemHeight)
					newHeight = bigItemHeight;
				if (Math.abs(itemsInWholePage[0].getTop()) >= smallItemHeight)
					newHeight = smallItemHeight;
			} else {
				newHeight = bigItemHeight;
			}

			if (itemsInWholePage[firstVisibleListItemPosition].getHeight() != newHeight && firstVisibleListItemPosition < firstVisibleFooterPosition) {

				itemsInWholePage[firstVisibleListItemPosition].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, newHeight));

				float rate = ((1 - minRate) * newHeight + bigItemHeight * minRate - smallItemHeight) / (bigItemHeight - smallItemHeight);
				
				setColorFilter(itemsInWholePage[firstVisibleListItemPosition], rate, false);
			}

			if (itemsInWholePage.length > firstVisibleListItemPosition + 1)
				if (itemsInWholePage[firstVisibleListItemPosition + 1] != null) {
					newHeight = smallItemHeight - itemsInWholePage[0].getTop() * (bigItemHeight - smallItemHeight) / smallItemHeight;

					if (firstVisibleListItemPosition == 0) {
						if (newHeight > bigItemHeight)
							newHeight = bigItemHeight;
					} else {
						newHeight = smallItemHeight;
					}

					if (firstVisibleListItemPosition + 1 < firstVisibleFooterPosition) {
						if (itemsInWholePage[firstVisibleListItemPosition + 1].getHeight() != newHeight)
							itemsInWholePage[firstVisibleListItemPosition + 1].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, newHeight));

						float rate = ((1 - minRate) * newHeight + bigItemHeight * minRate - smallItemHeight) / (bigItemHeight - smallItemHeight);
						
						if (itemsInWholePage[firstVisibleListItemPosition + 1].getHeight() == newHeight)
							setColorFilter(itemsInWholePage[firstVisibleListItemPosition + 1], rate, true);
						else
							setColorFilter(itemsInWholePage[firstVisibleListItemPosition + 1], rate, false);
					}
				}

			for (int i = firstVisibleListItemPosition + 2; i < itemsInWholePage.length; i++) {
				if (i < firstVisibleFooterPosition) {
					if (itemsInWholePage[i].getHeight() != smallItemHeight)
						itemsInWholePage[i].setLayoutParams((new LayoutParams(LayoutParams.MATCH_PARENT, smallItemHeight)));

					setColorFilter(itemsInWholePage[i], minRate, true);
				}
			}
		}
	}
	
	@SuppressLint("NewApi")
	private void setColorFilter (View view, float rate, boolean needJudgeNull) {
		ImageView iv;
		
		if (view instanceof ImageView) {
			iv = (ImageView) view;
			
			if (!needJudgeNull || iv.getColorFilter() == null) {
				ColorMatrix colorMatrix = new ColorMatrix(new float[] { 
						rate, 0, 0, 0, 0, 
						0, rate, 0, 0, 0, 
						0, 0, rate, 0, 0, 
						0, 0, 0, 1, 0, 
				});
				
				iv.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
			}
		} else if (view instanceof RelativeLayout) {
			RelativeLayout rl = (RelativeLayout) view;
			
			for (int j = 0; j < rl.getChildCount(); j++) {
				if (rl.getChildAt(j) instanceof ImageView) {
					iv = (ImageView) rl.getChildAt(j);

					if (!needJudgeNull || iv.getColorFilter() == null) {
						ColorMatrix colorMatrix = new ColorMatrix(new float[] { 
								rate, 0, 0, 0, 0, 
								0, rate, 0, 0, 0, 
								0, 0, rate, 0, 0, 
								0, 0, 0, 1, 0, 
						});
						
						iv.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
					}
				}
			}
		} else{}
	}
}
