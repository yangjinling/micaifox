package com.micai.fox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micai.fox.R;
import com.micai.fox.resultbean.MyZhongchouPiluResultBean;
import com.micai.fox.util.LogUtil;

import java.util.List;

/**
 * Created by lq on 2018/1/8.
 */

/*众筹详情---披露*/
public class MyZhongchouPiLuAdapter extends MyBaseAdapter<MyZhongchouPiluResultBean.ExecDatasBean.BetInfoBean> {
    private List<MyZhongchouPiluResultBean.ExecDatasBean.BetInfoBean> mList;
    private Context context;

    public MyZhongchouPiLuAdapter(List<MyZhongchouPiluResultBean.ExecDatasBean.BetInfoBean> list, Context context, int resId) {
        super(list, context, resId);
        this.mList = list;
        this.context = context;
    }

    @Override
    public void setData(ViewHolder viewHolder, int position) {
        LogUtil.e("YJL", "" + mList.size());
        MyZhongchouPiluResultBean.ExecDatasBean.BetInfoBean infoBean = mList.get(position);
        LinearLayout ll_parent = ((LinearLayout) viewHolder.findViewById(R.id.pilu_ll));
        TextView pilu_tv_touzhu = ((TextView) viewHolder.findViewById(R.id.pilu_tv_touzhu));
        TextView pilu_tv_status = ((TextView) viewHolder.findViewById(R.id.pilu_tv_status));
        TextView pilu_tv_touzhu_money = ((TextView) viewHolder.findViewById(R.id.pilu_tv_touzhu_money));
        TextView pilu_tv_yingshou_money = ((TextView) viewHolder.findViewById(R.id.pilu_tv_yingshou_money));
        pilu_tv_touzhu.setText("" + infoBean.getBetnum() + "注 " + infoBean.getSeqnum() + "场 " + infoBean.getSeqnum() + "串1 方案" + infoBean.getMultiple() + "倍");
        pilu_tv_touzhu_money.setText("" + infoBean.getBetAmount());
        pilu_tv_yingshou_money.setText("" + infoBean.getRevenueAmount());
        List<MyZhongchouPiluResultBean.ExecDatasBean.BetInfoBean.MatchBean> matchBeanList = infoBean.getMatch();
        LogUtil.e("YJL", "size" + matchBeanList.size());
        switch (infoBean.getStatus()) {
            case 4:
                //在售
                pilu_tv_status.setText("待开奖");
                break;
            case 5:
                //已开奖
                pilu_tv_status.setText("已开奖");
                break;
        }
        for (MyZhongchouPiluResultBean.ExecDatasBean.BetInfoBean.MatchBean matchBean : matchBeanList) {
            View view = LayoutInflater.from(context).inflate(R.layout.pilu, null);
            ll_parent.addView(view);
            ((TextView) view.findViewById(R.id.pilu_tv_country1)).setText("" + matchBean.getHomeTeamName() + " ");
            TextView pilu_tv_score = ((TextView) view.findViewById(R.id.pilu_tv_score));
            if (infoBean.getStatus() == 6) {
//                pilu_tv_score.setText(""+matchBean.);
            } else {
                pilu_tv_score.setText("VS");
            }
            ((TextView) view.findViewById(R.id.pilu_tv_country2)).setText(" " + matchBean.getGuestTeamName());
            ((TextView) view.findViewById(R.id.pilu_tv_country2)).setText(" " + matchBean.getGuestTeamName());
            LinearLayout rang_ll = ((LinearLayout) view.findViewById(R.id.pilu_ll_rang));
            View pilu_view_rang = ((View) view.findViewById(R.id.pilu_view_rang));
            TextView tv_wanfa1 = ((TextView) view.findViewById(R.id.pilu_content_wanfa1));
            TextView tv_wanfa1_result = ((TextView) view.findViewById(R.id.pilu_content_wanfa1_result));
            TextView pilu_content_touzhu1 = ((TextView) view.findViewById(R.id.pilu_content_touzhu1));
            TextView pilu_content_touzhu2 = ((TextView) view.findViewById(R.id.pilu_content_touzhu2));
            TextView pilu_content_touzhu3 = ((TextView) view.findViewById(R.id.pilu_content_touzhu3));
            TextView tv_wanfa2 = ((TextView) view.findViewById(R.id.pilu_content_wanfa2));
            TextView tv_wanfa2_result = ((TextView) view.findViewById(R.id.pilu_content_wanfa2_result));
            TextView pilu_content_rang_touzhu1 = ((TextView) view.findViewById(R.id.pilu_content_rang_touzhu1));
            TextView pilu_content_rang_touzhu2 = ((TextView) view.findViewById(R.id.pilu_content_rang_touzhu2));
            TextView pilu_content_rang_touzhu3 = ((TextView) view.findViewById(R.id.pilu_content_rang_touzhu3));
            if (matchBean.getSelections().contains("R")) {
                if (matchBean.getSelections().contains("3") || matchBean.getSelections().contains("1") || matchBean.getSelections().contains("0")) {
                    //让球+胜负平
                    rang_ll.setVisibility(View.VISIBLE);
                    pilu_view_rang.setVisibility(View.VISIBLE);
                    tv_wanfa1.setText("胜负平");
                    tv_wanfa2.setText("让球(" + matchBean.getHandicap() + ")");
                    if (matchBean.getSelections().contains("3R")) {
                        pilu_content_rang_touzhu1.setVisibility(View.VISIBLE);
                        pilu_content_rang_touzhu1.setText("让球胜");
                    }
                    if (matchBean.getSelections().contains("0R")) {
                        pilu_content_rang_touzhu2.setVisibility(View.VISIBLE);
                        pilu_content_rang_touzhu2.setText("让球平");
                    }
                    if (matchBean.getSelections().contains("1R")) {
                        pilu_content_rang_touzhu3.setVisibility(View.VISIBLE);
                        pilu_content_rang_touzhu3.setText("让球负");
                    }
                    if (matchBean.getSelections().contains("3")) {
                        pilu_content_touzhu1.setVisibility(View.VISIBLE);
                        pilu_content_touzhu1.setText("主胜");
                    }

                    if (matchBean.getSelections().contains("0")) {
                        pilu_content_touzhu2.setVisibility(View.VISIBLE);
                        pilu_content_touzhu2.setText("平");
                    }
                    if (matchBean.getSelections().contains("1")) {
                        pilu_content_touzhu3.setVisibility(View.VISIBLE);
                        pilu_content_touzhu3.setText("主负");
                    }
                } else {
                    //让球
                    tv_wanfa1.setText("让球(" + matchBean.getHandicap() + ")");
                    if (matchBean.getSelections().contains("3R")) {
                        pilu_content_touzhu1.setVisibility(View.VISIBLE);
                        pilu_content_touzhu1.setText("让球胜");
                    }
                    if (matchBean.getSelections().contains("0R")) {
                        pilu_content_touzhu2.setVisibility(View.VISIBLE);
                        pilu_content_touzhu2.setText("让球平");
                    }
                    if (matchBean.getSelections().contains("1R")) {
                        pilu_content_touzhu3.setVisibility(View.VISIBLE);
                        pilu_content_touzhu3.setText("让球负");
                    }

                }
            } else {
                //胜负平
                tv_wanfa1.setText("胜负平");
                if (matchBean.getSelections().contains("3")) {
                    pilu_content_touzhu1.setVisibility(View.VISIBLE);
                    pilu_content_touzhu1.setText("主胜");
                }

                if (matchBean.getSelections().contains("0")) {
                    pilu_content_touzhu2.setVisibility(View.VISIBLE);
                    pilu_content_touzhu2.setText("平");
                }
                if (matchBean.getSelections().contains("1")) {
                    pilu_content_touzhu3.setVisibility(View.VISIBLE);
                    pilu_content_touzhu3.setText("主负");
                }
            }
        }
    }
}