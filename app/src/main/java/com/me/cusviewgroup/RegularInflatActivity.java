package com.me.cusviewgroup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegularInflatActivity extends AppCompatActivity {

	private String[] mVals = new String[] { "Hello", "Android", "Weclome Hi ", "Button",
        "TextView", "Hello", "Android", "Weclome", "Button ImageView", "TextView",
        "Hello world", "Android", "Weclome Hello", "Button Text", "TextView" };
 
	private FlowLayoutOne mFlowLayout;
 
	@Override
        protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

        // setContentView(R.layout.activity_regular); // too bad too simple
		setContentView(R.layout.activity_regular_two);
		mFlowLayout = (FlowLayoutOne) findViewById(R.id.flowlayout);
		initData();
	}
 
	public void initData() {
		// for (int i = 0; i < mVals.length; i++)
		// {
		// Button btn = new Button(this);
		//
		// MarginLayoutParams lp = new MarginLayoutParams(
		// MarginLayoutParams.WRAP_CONTENT,
		// MarginLayoutParams.WRAP_CONTENT);
		//
		// btn.setText(mVals[i]);
		// mFlowLayout.addView(btn, lp);
		// }
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < mVals.length; i++) {
			TextView tv = (TextView) mInflater.inflate(R.layout.flow_item, mFlowLayout, false);
			tv.setText(mVals[i]);
			mFlowLayout.addView(tv);
		}
	}

	@Override
        protected void onResume() {
		Log.e("TAG","onResume");
		super.onResume();
	}
}
