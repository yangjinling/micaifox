package com.micai.fox.fragment;

import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.micai.fox.R;
import com.micai.fox.activity.ExpertsDetailActivity;
import com.micai.fox.activity.ZhongChouDetailActivity;
import com.micai.fox.adapter.MyExpertsListAdapter;

import java.util.ArrayList;

/**
 * Created by lq on 2018/1/9.
 */

public class MyZhongChouFragment extends Fragment {
    private ArrayList<String> data;
    private ListView lv;
    private TextView tv;
    private int kind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_zhongchou, container, false);
        lv = ((ListView) view.findViewById(R.id.mine_zhongchou_lv));
        tv = ((TextView) view.findViewById(R.id.fragment_mine_zhongchou_tv));
        tv.setVisibility(View.VISIBLE);
        kind = getArguments().getInt("KIND", 0);
        switch (kind) {
            case 0:
                tv.setText("全部");
                break;
            case 1:
                tv.setText("待支付");
                break;
            case 2:
                tv.setText("已支付");
                break;
            case 3:
                tv.setText("已兑换");
                break;
        }
        data = getData();
        MyExpertsListAdapter adapter = new MyExpertsListAdapter(data, getContext(), R.layout.item_lv_experts);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ZhongChouDetailActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        String temp = " item";
        for (int i = 0; i < 50; i++) {
            data.add(i + temp);
        }

        return data;
    }
}
