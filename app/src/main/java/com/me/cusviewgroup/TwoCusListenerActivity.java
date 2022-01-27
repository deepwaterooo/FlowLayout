package com.me.cusviewgroup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.FlowLayout;
import java.util.ArrayList;
import java.util.List;

// 这里适配器有bug，可以参考前面 scrollview动态添加，和点击监听，曲线解决这个bug
// 可以放在recyclerview中，也可以用relative layout动态添加
public class TwoCusListenerActivity extends AppCompatActivity {

    private FlowLayout flow1, flow2;
    private FlowLayoutAdapter mMyLabelAdapter, mHotLabelAdapter;
    private List<String> MyLabelLists, HotLabelLists;

    private static int TAG_REQUESTCODE = 0x101;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_flowgroupviwe);
        initView();
        initData();
    }

    private void initView() {
        final RecyclerView recyclerView1 = (RecyclerView)findViewById(flow1);
        final RecyclerView recyclerView2 = (RecyclerView)findViewById(flow2);

        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        recyclerView.addItemDecoration(new SpaceItemDecoration(dp2px(10)));
        recyclerView.setLayoutManager(flowLayoutManager);
        list = DataConfig.getItems();
        recyclerView.setAdapter(flowAdapter = new FlowAdapter(list));
        
    }

    private void initData() {
        String[] date = getResources().getStringArray(R.array.tags);
        HotLabelLists = new ArrayList<>();
        for (int i = 0; i < date.length; i++) {
            HotLabelLists.add(date[i]);
        }
        mHotLabelAdapter = new FlowLayoutAdapter(this, HotLabelLists);
//        flow2.setAdapter(mHotLabelAdapter);
//        flow2.setItemClickListener(new TagCloudLayoutItemOnClick(1));

        MyLabelLists = new ArrayList<>();
        mMyLabelAdapter = new FlowLayoutAdapter(this, MyLabelLists);
//        flow1.setAdapter(mMyLabelAdapter);
//        flow1.setItemClickListener(new TagCloudLayoutItemOnClick(0));

        String labels = String.valueOf(getIntent().getStringExtra("labels"));
        if (!TextUtils.isEmpty(labels) && labels.length() > 0
            && !labels.equals("null")) {
            String[] temp = labels.split(",");
            for (int i = 0; i < temp.length; i++) {
                MyLabelLists.add(temp[i]);
            }
            ChangeMyLabels();
        }

    }

     // 刷新我的标签数据
    private void ChangeMyLabels() {
        flow1.setVisibility(MyLabelLists.size() > 0 ? View.VISIBLE
                                   : View.GONE);
        mMyLabelAdapter.notifyDataSetChanged();
    }

    //  // 标签的点击事件
    //  // @author lijuan
    // class TagCloudLayoutItemOnClick implements FlowLayout.TagItemClickListener {
    //     int index;

    //     public TagCloudLayoutItemOnClick(int index) {
    //         this.index = index;
    //     }

    //     @Override
    //         public void itemClick(int position) {
    //         switch (index) {
    //         case 0:
    //             MyLabelLists.remove(MyLabelLists.get(position));
    //             ChangeMyLabels();
    //             break;
    //         case 1:
    //             if (MyLabelLists.size() < 5) {
    //                 if (HotLabelLists.get(position).equals("自定义")) {
    //                     startActivityForResult(
    //                         new Intent(TwoCusListenerActivity.this,
    //                                    AddTagActivity.class),
    //                         TAG_REQUESTCODE);
    //                 } else {
    //                     Boolean isExits = isExist(MyLabelLists,
    //                                               HotLabelLists.get(position));
    //                     if (isExits) {
    //                         Toast.makeText(TwoCusListenerActivity.this, "此标签已经添加啦", Toast.LENGTH_LONG).show();
    //                         return;
    //                     }
    //                     MyLabelLists.add(HotLabelLists.get(position));
    //                     ChangeMyLabels();
    //                 }
    //             } else {
    //                 Toast.makeText(TwoCusListenerActivity.this, "最多只能添加5个标签", Toast.LENGTH_LONG).show();
    //             }
    //             break;
    //         default:
    //             break;
    //         }
    //     }
    // }
    //  // * 将数组里面的字符串遍历一遍，看是否存在相同标签
    //  // @param str
    //  // @param compareStr
    //  // @return
    // public static Boolean isExist(List<String> str, String compareStr) {
    //     Boolean isExist = false;//默认沒有相同标签
    //     for (int i = 0; i < str.size(); i++) {
    //         if (compareStr.equals(str.get(i))) {
    //             isExist = true;
    //         }
    //     }
    //     return isExist;
    // }
    //  // * 回传数据
    // @Override
    //     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //     if (TAG_REQUESTCODE == requestCode) {
    //         if (resultCode == AddTagActivity.TAG_RESULTCODE) {
    //             String label = data.getStringExtra("tags");
    //             MyLabelLists.add(label);
    //             ChangeMyLabels();
    //         }
    //     }
    // }
}

// public class TwoCusListenerActivity extends AppCompatActivity {
// LayoutInflater mInflater;
//     // @InjectView(R.id.id_flowlayout1)
//     FlowLayout idFlowlayout1;
//     // @InjectView(R.id.id_flowlayout2)
//     FlowLayout idFlowlayout2;
//     private String[] mVals = new String[]
//     {"Do", "one thing", "at a time", "and do well.", "Never", "forget",
//      "to say", "thanks.", "Keep on", "going ", "never give up."};
//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_two_flowgroupviwe);
//         // ButterKnife.inject(this);
//         mInflater = LayoutInflater.from(TwoCusListenerActivity.this);
// // bug: java.lang.ClassCastException: com.me.cusviewgroup.FlowLayout cannot be cast to com.google.android.material.internal.FlowLayout
//         idFlowlayout1 = (FlowLayout)findViewById(R.id.id_flowlayout1);
//         idFlowlayout1 = (FlowLayout)findViewById(R.id.id_flowlayout2);
//         initFlowlayout2();
//     }

//     public void initFlowlayout2() {
//         for (int i = 0; i < mVals.length; i++) {
//             final RelativeLayout rl2 = (RelativeLayout) mInflater.inflate(R.layout.flow_item, idFlowlayout2, false);
//             TextView tv2 = (TextView) rl2.findViewById(R.id.tv);
//             tv2.setText(mVals[i]);
//             rl2.setTag(i);
//             idFlowlayout2.addView(rl2);
//             rl2.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                         public void onClick(View v) {
//                         int i = (int) v.getTag();
//                         addViewToFlowlayout1(i);
// //                        rl2.setBackgroundResource(R.drawable.flow_layout_disable_bg);
//                         rl2.setClickable(false);
//                     }
//                 });
//         }
//     }
//     public void addViewToFlowlayout1(int i){
//         RelativeLayout rl1 = (RelativeLayout) mInflater.inflate(R.layout.flow_item, idFlowlayout1, false);
// //        ImageView iv = (ImageView) rl1.findViewById(R.id.iv);
//         TextView iv = (TextView) rl1.findViewById(R.id.iv);
//         iv.setVisibility(View.VISIBLE);
//         TextView tv1 = (TextView) rl1.findViewById(R.id.tv);
//         tv1.setText(mVals[i]);
//         rl1.setTag(i);
//         idFlowlayout1.addView(rl1);
//         rl1.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                     public void onClick(View v) {
//                     int i = (int) v.getTag();
//                     idFlowlayout1.removeView(v);
//                     View view = idFlowlayout2.getChildAt(i);
//                     view.setClickable(true);
// //                    view.setBackgroundResource(R.drawable.flow_layout_bg);
//                 }
//             });
//     }
// }
