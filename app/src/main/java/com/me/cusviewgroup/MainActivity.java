package com.me.cusviewgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

public class MainActivity extends AppCompatActivity {
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.init(getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.main_container);

        // Check if we have write permission
//        checkoPermission();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.height = SizeUtils.dp2px(67);

        container.addView( // 静态的 
            FunctionViewBuilder.getFunctionView(this
                                                , "RegularInflatActivity"
                                                , "Regularly Inflated"
                                                , new View.OnClickListener() {
                                                    @Override
                                                        public void onClick(View v) {
                                                        startActivity(new Intent(MainActivity.this, RegularInflatActivity.class));
                                                    }
                                                }), lp
            );
        container.addView( // 动态的
            FunctionViewBuilder.getFunctionView(this
                                                , "DynamicInflatActivity"
                                                , "Dynamically Inflated"
                                                , new View.OnClickListener() {
                                                    @Override
                                                        public void onClick(View v) {
                                                        startActivity(new Intent(MainActivity.this, DynamicInflatActivity.class));
                                                    }
                                                }), lp
            );
       container.addView( // 设置单元素点击监听
           FunctionViewBuilder.getFunctionView(this
                                               , "ListenerActivity"
                                               , "Two Custom ViewGroup Listener Activity"
                                               , new View.OnClickListener() {
                                                   @Override
                                                       public void onClick(View v) {
                                                       startActivity(new Intent(MainActivity.this, TwoCusListenerActivity.class));
                                                   }
                                               }), lp
           );
    }
}
