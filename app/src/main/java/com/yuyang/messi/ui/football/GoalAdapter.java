package com.yuyang.messi.ui.football;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.bean.football.FootballGoalBean;
import com.yuyang.messi.utils.FootballUtil;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.MyHolder> {

    private LayoutInflater mInflater;

    private List<FootballGoalBean> goalBeanList;

    public GoalAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<FootballGoalBean> goalBeanList) {
        this.goalBeanList = goalBeanList;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.fragment_football_goal_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        FootballGoalBean bean = goalBeanList.get(position);

        FootballUtil.displayTeamIconByTeamName(bean.getC3(), holder.teamIcon);
        holder.rankText.setText(bean.getC1());
        holder.playerText.setText(bean.getC2());
        holder.teamName.setText(bean.getC3());
        holder.goalText.setText(bean.getC4());
        holder.penaltyText.setText(bean.getC5());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return goalBeanList == null ? 0 : goalBeanList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        private TextView rankText;
        private TextView playerText;
        private ImageView teamIcon;
        private TextView teamName;
        private TextView goalText;
        private TextView penaltyText;

        public MyHolder(View itemView) {
            super(itemView);
            rankText = (TextView) itemView.findViewById(R.id.fragment_football_goal_recycler_item_rank);
            playerText = (TextView) itemView.findViewById(R.id.fragment_football_goal_recycler_item_player);
            teamIcon = (ImageView) itemView.findViewById(R.id.fragment_football_goal_recycler_item_teamIcon);
            teamName = (TextView) itemView.findViewById(R.id.fragment_football_goal_recycler_item_teamName);
            goalText = (TextView) itemView.findViewById(R.id.fragment_football_goal_recycler_item_goal);
            penaltyText = (TextView) itemView.findViewById(R.id.fragment_football_goal_recycler_item_penalty);
        }
    }

}



