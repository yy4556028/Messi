package com.yuyang.messi.ui.football;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.bean.football.FootballScoreBean;
import com.yuyang.messi.utils.FootballUtil;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.MyHolder> {

    private LayoutInflater mInflater;
    private Context context;

    private List<FootballScoreBean> scoreBeanList;

    public ScoreAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<FootballScoreBean> scoreBeanList) {
        this.scoreBeanList = scoreBeanList;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.fragment_football_score_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        FootballScoreBean bean = scoreBeanList.get(position);

        FootballUtil.displayTeamIconByTeamName(bean.getC2(), holder.teamIcon);
        holder.rankText.setText(bean.getC1());
        holder.teamName.setText(bean.getC2());
        holder.numText.setText(bean.getC3());
        holder.winText.setText(bean.getC41());
        holder.tieText.setText(bean.getC42());
        holder.loseText.setText(bean.getC43());
        holder.goalDiffText.setText(bean.getC5());
        holder.scoreText.setText(bean.getC6());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return scoreBeanList == null ? 0 : scoreBeanList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        private TextView rankText;
        private ImageView teamIcon;
        private TextView teamName;
        private TextView numText;
        private TextView winText;
        private TextView tieText;
        private TextView loseText;
        private TextView goalDiffText;
        private TextView scoreText;

        public MyHolder(View itemView) {
            super(itemView);
            rankText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_rank);
            teamIcon = (ImageView) itemView.findViewById(R.id.fragment_football_score_recycler_item_teamIcon);
            teamName = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_teamName);
            numText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_num);
            winText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_win);
            tieText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_tie);
            loseText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_lose);
            goalDiffText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_goalDiff);
            scoreText = (TextView) itemView.findViewById(R.id.fragment_football_score_recycler_item_score);
        }
    }

}



