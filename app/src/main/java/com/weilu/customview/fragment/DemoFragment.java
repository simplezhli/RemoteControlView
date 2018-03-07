package com.weilu.customview.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.weilu.customview.MainActivity;
import com.weilu.customview.R;
import com.weilu.customview.adapter.MyAdapter;
import com.weilu.customview.bean.DraggableInfo;
import com.weilu.customview.utils.Tools;
import java.util.ArrayList;
import java.util.List;

public class DemoFragment extends Fragment{

    private int[] pic1 = new int[]{R.mipmap.svg_new_close, R.mipmap.svg_new_home, R.mipmap.offered_exit,
            R.mipmap.svg_new_back, R.mipmap.svg_new_setting, R.mipmap.svg_new_source, 0,
            R.mipmap.offered_menu, R.mipmap.offered_out, R.mipmap.offered_mute};
    
    private int[] pic2 = new int[]{R.mipmap.offered_play, R.mipmap.offered_stop, R.mipmap.offered_pause,
            R.mipmap.offered_pause2, R.mipmap.offered_previous, R.mipmap.offered_next, R.mipmap.offered_backward,
            R.mipmap.offered_forward, R.mipmap.offered_height, R.mipmap.offered_width};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mRecyclerView.setHasFixedSize(true);
        
        int position = FragmentPagerItem.getPosition(getArguments());
        final List<DraggableInfo> mData = new ArrayList<>();
        
        if (position == 0){
            for (int i = 0; i < pic1.length; i++){
                if (pic1[i] == 0){
                    mData.add(new DraggableInfo("Text", 0, i, 1));
                }else {
                    mData.add(new DraggableInfo("", pic1[i], i, 0));
                }
            }
        }else if (position == 2){
            for (int i = 0; i < pic2.length; i++){
                mData.add(new DraggableInfo("", pic2[i], i, 0));
            }
        }else if (position == 3){
            for (int i = 0; i < 10; i++){
                mData.add(new DraggableInfo(String.valueOf(i), 0, i, 1));
            }
        }
        final MyAdapter mAdapter = new MyAdapter(getActivity(), mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setTag(mAdapter.getItem(position));
                Tools.startDrag(view);
                ((MainActivity) getActivity()).setDragInfo(mAdapter.getItem(position));
                return false;
            }
        });
    }
    
}
