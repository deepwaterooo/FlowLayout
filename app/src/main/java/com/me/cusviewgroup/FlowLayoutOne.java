package com.me.cusviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayoutOne extends ViewGroup {

    /** 当前行已用的宽度，由子View宽度加上横向间隔 */
    private int mUsedWidth = 0;
    /** 代表一行 */
    private Line mLine = null;
    /** 默认间隔 */
    public static final int DEFAULT_SPACING = 20;
    /** 横向间隔 */
    private int mHorizontalSpacing = DEFAULT_SPACING;
    /** 纵向间隔 */
    private int mVerticalSpacing = DEFAULT_SPACING;
    /** 代表行的集合 */
    private final List<Line> mLines = new ArrayList<Line>();
    /** 默认的最大的行数 */
    private int mMaxLinesCount = Integer.MAX_VALUE;
    /** 是否让子view充满该行 */
    private boolean fillLine = false;
    public FlowLayoutOne(Context context) {
        super(context);
    }

    public FlowLayoutOne(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**是否填充满改行，如果是false，右边有空间*/
    public void setFillLine(boolean fill) {
        fillLine = fill;
    }
    // 设置两个view水平间距
    public void setHorizontalSpacing(int spacing) {
        if (mHorizontalSpacing != spacing) {
            mHorizontalSpacing = spacing;
            requestLayout();
        }
    }
    /** 设置纵向间距*/
    public void setVerticalSpacing(int spacing) {
        if (mVerticalSpacing != spacing) {
            mVerticalSpacing = spacing;
            requestLayout();
        }
    }
    @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 1获取with height 以及mode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec)
            - getPaddingBottom() - getPaddingTop();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec)
            - getPaddingLeft() - getPaddingRight();
        restoreLine();
        int count = getChildCount();
        // 2 测量子View
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int widthSpec = MeasureSpec.makeMeasureSpec(widthSize,
                                                        widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
                                                        : widthMode);
            int heightSpec = MeasureSpec.makeMeasureSpec(heightSize,
                                                         heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
                                                         : heightMode);
            child.measure(widthSpec, heightSpec);
            if (mLine == null) {
                mLine = new Line();
            }
            // 将childview 添加到每一行中
            int childWidth = child.getMeasuredWidth();
            // 当前行已经占用的宽度
            mUsedWidth += childWidth;
            if (mUsedWidth < widthSize) {
                // 当前行还没有达到上限，那么该child就添加进这一行
                mLine.addView(child);
                mUsedWidth += mHorizontalSpacing; // 添加上两个子View之间水平方向的间隔
            } else {
                // 说明长度超出了当前的最大宽度
                if (mLine.getViewCount() == 0) {
                    // 表示当前行中还没有元素，添加的第一个元素 长度就超过了最大宽度，那么也要把该child 添加进去保证有数据
                    mLine.addView(child);
                    // 同时换行
                } else {
                    // 表示当前行中已经有元素，那么换一行，添加进去
                    newLine();
                    mLine.addView(child);
                    // 改变已使用的宽度
                    mUsedWidth += mHorizontalSpacing + childWidth;
                }
            }
        }
        // 前面只有换行的时候才将Line 添加到lines 集合中，这里要判断一下最后一行，将最后一行也添加进去
        if (mLine != null && mLine.getViewCount() > 0
            && !mLines.contains(mLine)) {
            // 表示有数据
            mLines.add(mLine);
        }
        // 设置测量的宽高setMeasuredDimension
        int totoalHeight = 0;
        for (int i = 0; i < mLines.size(); i++) {
            totoalHeight += mLines.get(i).mHeight;// N行的高度
        }
        // 加上 行间距
        totoalHeight += (mLines.size() - 1) * mVerticalSpacing;
        // 加上padding
        totoalHeight += getPaddingBottom() + getPaddingTop();
        // 设置FlowLayout的宽度值 高度值 宽度就是默认的宽度，高度是总的高度
        int measuredHeight = resolveSize(totoalHeight, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                             measuredHeight);
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 主要是调用child.layout
        int count = mLines.size();
        int left = getPaddingLeft();
        int top = getPaddingTop();

        for (int i = 0; i < count; i++) {
            Line line = mLines.get(i);
            line.layout(left, top);
            top += mVerticalSpacing + line.mHeight;
        }
    }

    /** 还原所有数据 */
    private void restoreLine() {
        mLines.clear();
        mLine = new Line();
        mUsedWidth = 0;
    }

    /** 新增加一行 */
    private boolean newLine() {
        mLines.add(mLine);
        if (mLines.size() < mMaxLinesCount) {
            mLine = new Line();
            mUsedWidth = 0;
            return true;
        }
        return false;
    }

    /***
     * 代表着一行，封装了一行所占高度，该行子View的集合，以及所有View的宽度总和
     *
     * @author Administrator
     *
     */
    public class Line {
        int mWidth = 0;// 该行中所有的子View累加的宽度
        int mHeight = 0;// 该行中所有的子View中高度最高的那个子View的高度

        List<View> views = new ArrayList<View>();// 存放一行中的View

        public void addView(View child) {
            views.add(child);
            mWidth += child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            mHeight = mHeight < childHeight ? childHeight : mHeight;// 高度等于一行中最高的View
        }

        /***
         * layout 子view
         * @param l
         * @param t
         */
        public void layout(int l, int t) {
            int left = l;
            int top = t;
            // 父布局的宽度
            int totoalWidth = getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight();
            // 当前line 中view的个数
            int count = getViewCount();
            // 剩余空间平分给每个View
            int spaceLast = totoalWidth - mWidth - (count - 1)
                * mHorizontalSpacing;
            int averageWidth = spaceLast / count;
            // 平分的宽度
            // int splitSpacing = (int) (spaceLast / count + 0.5);
            for (int i = 0; i < count; i++) {
                View child = views.get(i);
                int childHeight = child.getMeasuredHeight();
                int childWidth = child.getMeasuredWidth();

                if (fillLine) {// 要充满该行
                    childWidth += averageWidth;
                    child.getLayoutParams().width = childWidth;
                    // 改变了原来的宽高，重新测量一次
                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        childWidth, MeasureSpec.EXACTLY);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        childHeight, MeasureSpec.EXACTLY);
                    // 再次测量
                    child.measure(widthMeasureSpec, heightMeasureSpec);

                }
                // 布局View
                child.layout(left, top, left + childWidth, top + childHeight);
                left += childWidth + mHorizontalSpacing; // 为下一个View的left赋值
            }
        }
        /**
         * 该行中view的个数
         * @return
         */
        public int getViewCount() {
            return views.size();
        }
    }
}

// public class FlowGroupView extends ViewGroup {
//     private int childHorizontalSpace = 0;
//     private int childVerticalSpace = 0;
//     private String TAG = "TAG";

//     // 储存所有的view 按行记录
//     private List<List<View>> mAllViews = new ArrayList<List<View>>();
//     // 记录每一行的高度
//     private List<Integer> mLineHeight = new ArrayList<Integer>();

//     public FlowGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
//         super(context, attrs, defStyleAttr);
//     }
//     public FlowGroupView(Context context, AttributeSet attrs) {
//         super(context, attrs);
//         // TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.FlowGroupView);
//         // if (attrArray != null) {
//         //     childHorizontalSpace = attrArray.getDimensionPixelSize(R.styleable.FlowGroupView_horizontalSpace, 12);
//         //     childVerticalSpace = attrArray.getDimensionPixelSize(R.styleable.FlowGroupView_verticalSpace, 12);
//         //     Log.e(getClass().getName(),"HorizontalSpace:"+childHorizontalSpace+"|VerticalSpace："+childVerticalSpace);
//         //     attrArray.recycle();
//         // }
//         // //此视图是否自行绘制
//         // setWillNotDraw(false);
//     }
//     public FlowGroupView(Context context) {
//         super(context);
//     }

//     // 所有childView的位置的布局
//     @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
//         // 当前行的最大高度  
//         int lineHeight = 0;  
//         // 存储每一行所有的childView  
//         List<View> lineViews = new ArrayList<View>();  
//         int left = 0;  
//         int top = 0;  
//         // 得到总行数  
//         int lineNums = mAllViews.size();  
//         for (int i = 0; i < lineNums; i++)   {  
//             // 每一行的所有的views  
//             lineViews = mAllViews.get(i);     // List<View> 
//             // 当前行的最大高度  
//             lineHeight = mLineHeight.get(i);  // 这个全局变量在 onMeasure()方法中已经填充了每行的高度
//             Log.d(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
//             Log.d(TAG, "第" + i + "行， ：" + lineHeight);  
//             // 遍历当前行所有的View  
//             for (int j = 0; j < lineViews.size(); j++)   {  
//                 View child = lineViews.get(j);  
//                 if (child.getVisibility() == View.GONE) // 如果不可见，就不。。。    
//                     continue;  
//                 MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();  
//                 // 计算 childView 的 left,top,right,bottom  
//                 int lc = left + lp.leftMargin; // left cur
//                 int tc = top + lp.topMargin;   // top cur
//                 // 子视图会调用getMeasuredWidth()和getMeasuredHeight()方法获取到 measure 过程得到的 mMeasuredWidth 和 mMeasuredHeight，作为自己的 width 和 height
//                 int rc = lc + child.getMeasuredWidth();   // right
//                 int bc = tc + child.getMeasuredHeight();  // bottom
//                 child.layout(lc, tc, rc, bc); // 调用每一个子视图的layout(l, t, r, b)函数，来确定每个子视图在父视图中的位置
//                 left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;  
//             }  
//             left = 0;  
//             top += lineHeight; // lineHeight
//         }  
//         Log.v(TAG, "onLayout   mAllViews.size() -- > " + mAllViews.size() + "   mLineHeight.size() -- > "+ mLineHeight.size());
//     }
//     @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//         // 置空 view 容器 和 lineHeight 容器  重新赋值
//         mAllViews.clear();  
//         mLineHeight.clear();  
//         // 得到上级容器为其推荐的宽高和计算模式
//         int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
//         int specHeighMode = MeasureSpec.getMode(heightMeasureSpec);
//         int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
//         int specHeighSize = MeasureSpec.getSize(heightMeasureSpec);
//         // 计算出所有的 child 的 宽和高
//         measureChildren(specWidthSize, specHeighSize); // 是系统自己的方法
//         // 记录如果是 warp_content 是设置的宽和高
//         int width = 0;
//         int height = 0;
//         // 得到子view的个数
//         int cCount = getChildCount();
//         /** 
//          // 记录每一行的宽度，width不断取最大宽度 
//          */  
//         int lineWidth = 0;  
//         /** 
//          // 每一行的高度，累加至height 
//          */  
//         int lineHeight = 0;  
//         // 存储每一行所有的childView  
//         List<View> lineViews = new ArrayList<View>();  
//         for (int i = 0; i < cCount; i++) {
//             // 得到每个子View
//             View child = getChildAt(i);
//             // 测量每个子View的宽高
//             measureChild(child, widthMeasureSpec, heightMeasureSpec);
//             // 当前子view的lp
//             MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
//             // 子view的宽和高
//             int cWidth = 0;
//             int cheight = 0;
//             // 当前子 view 实际占的宽
//             cWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
//             // 当前子View 实际占的高
//             cheight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
//             // 需要换行
//             if (lineWidth + cWidth > specWidthSize){
//                 width = Math.max(lineWidth, cWidth); // (现累加行宽，与当前控件的宽度)取最大值
//                 lineWidth = cWidth;   // 开启新行的时候重新累加width
//                 height += lineHeight; // 开启新行时累加（上一行的） height
//                 lineHeight = cheight; // 记录当前行的高度
//                 mAllViews.add(lineViews);
//                 mLineHeight.add(lineHeight);  
//                 lineViews = new ArrayList<View>();
//                 // 换行的时候把该 view 放进 集合里
//                 lineViews.add(child);// 这个  view(child) 是当前行的第一个view
//                 Log.v(TAG, "onl  mAllViews.size()  --  > " + mAllViews.size());
//             } else {
//                 // 不需要换行
//                 lineWidth += cWidth; // 宽度继续累加 
//                 height = Math.max(lineHeight, cheight);// 高度取最大值
//                 // 不需要换行时 把子View add 进集合
//                 lineViews.add(child);
//             }
//             if (i == cCount - 1) {
//                 // 如果是最后一个view 
//                 width = Math.max(lineWidth, cWidth); // 最后一行在没有换行的情况下，它的宽度还没有作全局处理
//                 height += lineHeight; // 累加最后一行的高度
//             }
//         }
//         // 循环结束后 把最后一行内容add进集合中
//         mLineHeight.add(lineHeight); // 记录最后一行
//         mAllViews.add(lineViews);
//         // MeasureSpec.EXACTLY 表示设置了精确的值
//         // 如果 mode 是 MeasureSpec.EXACTLY 时候，则不是 warp_content 用计算来的值，否则则用上级布局分给它的值
//         setMeasuredDimension(specWidthMode == MeasureSpec.EXACTLY ? specWidthSize : width,
//                              specHeighMode == MeasureSpec.EXACTLY ? specHeighSize : height);
//         Log.v(TAG, "onLayout  onMeasure   mAllViews.size() -- > " + mAllViews.size() + "   mLineHeight.size() -- > "+ mLineHeight.size());
//     }

//     // 这个一定要设置，否则会报强转错误
//     // 设置它支持 marginLayoutParams
//     @Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
//         return new MarginLayoutParams(getContext(), attrs);
//     }
// }
