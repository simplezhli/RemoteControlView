package com.weilu.customview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.weilu.customview.bean.DraggableInfo;
import com.weilu.customview.fragment.DemoFragment;
import com.weilu.customview.fragment.DemoFragment1;
import com.weilu.customview.widget.RemoteControlView;

public class MainActivity extends AppCompatActivity {

    private RemoteControlView remoteControlView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = this.findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = this.findViewById(R.id.viewpagertab);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("功能键", DemoFragment.class));
        pages.add(FragmentPagerItem.of("音量键", DemoFragment1.class));
        pages.add(FragmentPagerItem.of("播放键", DemoFragment.class));
        pages.add(FragmentPagerItem.of("数字键", DemoFragment.class));

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        remoteControlView = this.findViewById(R.id.rcv);
    }
    
    public void setDragInfo(DraggableInfo mButton){
        remoteControlView.setDragInfo(mButton);
    }
}
