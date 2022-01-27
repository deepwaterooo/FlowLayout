package com.me.cusviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyViewGroup1 extends ViewGroup {
    private String TAG = "MyViewGroup1";

    public MyViewGroup1(Context context) {
        super(context);
    }
    public MyViewGroup1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyViewGroup1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int lineWidth;//每一行的宽
    private int viewGroupWidth;//ViewGroup的宽
    private int lineHeight;
    private int viewGroupHeight;
    private List<List<View>> views = new ArrayList<>();
    private List<View> lineViews = new ArrayList<>();//每一行的View
    private List<Integer> heights = new ArrayList<>();//记录每行的高度

    private void init() {
        views.clear();
        lineViews.clear();
        lineWidth = 0;
        lineHeight = 0;
        viewGroupWidth = 0;
        heights.clear();
        viewGroupHeight = 0;
    }

    @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //初步测量自身 主要是为了初始化 一些东西避免后续报错
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
        // 计算子View限制信息
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //测量子View宽高
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            //计算每行的View主要是为了测量父View宽高
            int childWidth = childView.getMeasuredWidth();
            int childheight = childView.getMeasuredHeight();
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

            if (lineWidth + childWidth + lp.leftMargin + lp.rightMargin > widthSize - getPaddingRight() - getPaddingLeft()) {
                //换行的时候清空记录每行的list，并把它加入两层list的views
                views.add(lineViews);
                lineViews = new ArrayList<>();
                viewGroupWidth = Math.max(viewGroupWidth, lineWidth);
                viewGroupHeight += lineHeight;
                heights.add(lineHeight);
                lineHeight = 0;
                lineWidth = 0;
            }
            // 不换行
            lineViews.add(childView);
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childheight + lp.topMargin + lp.bottomMargin);

            if (i == childCount - 1) {
                viewGroupWidth = Math.max(viewGroupWidth, lineWidth);
                viewGroupHeight += lineHeight;
                heights.add(lineHeight);
                views.add(lineViews);
            }
        }
        //测量自身尺寸
        int width = 0;
        int height = 0;
        switch (widthMode) {
        case MeasureSpec.EXACTLY:
            width = widthSize;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.UNSPECIFIED:
            width = viewGroupWidth;
            break;
        default:
            break;
        }

        switch (heightMode) {
        case MeasureSpec.EXACTLY:
            height = heightSize;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.UNSPECIFIED:

            height = viewGroupHeight;

            break;
        default:
            break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = 0;
        for (int i = 0; i < views.size(); i++) {
            List<View> lineViews = views.get(i);
            lineHeight = heights.get(i);//行高
            // 遍历当前行
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                // 该child的LayoutParams
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                //上一个child的LayoutParams
                MarginLayoutParams lastLp = null;
                View lastChild = null;
                if (j != 0) {
                    lastChild = lineViews.get(j - 1);
                    lastLp = (MarginLayoutParams) lastChild.getLayoutParams();
                }

                if (j == 0) {
                    left = lp.leftMargin;
                } else {
                    //上一次的左边+上个的宽+上一个的右边距+这一个的左边距
                    left = left + lastChild.getMeasuredWidth() + lastLp.rightMargin + lp.leftMargin;
                }
                Log.d(TAG, "第" + i + "行第" + j + "个 left:" + left + ",right:" + (left + child.getMeasuredWidth()) + ",top:" + (top + lp.topMargin )+ ",buttom:" + (top + lp.topMargin + child.getMeasuredHeight()));
                child.layout(left, top + lp.topMargin, left + child.getMeasuredWidth(), top + lp.topMargin + child.getMeasuredHeight());

            }
            top += lineHeight;
            left = 0;
        }
    }

    @Override
        public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyLayoutParams(getContext(), attrs);
    }
    @Override
        protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new MyLayoutParams(lp);
    }
    @Override
        protected LayoutParams generateDefaultLayoutParams() {
        return new MyLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public static class MyLayoutParams extends MarginLayoutParams {
        public MyLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
        public MyLayoutParams(int width, int height) {
            super(width, height);
        }
        public MyLayoutParams(LayoutParams lp) {
            super(lp);
        }
    }
}
