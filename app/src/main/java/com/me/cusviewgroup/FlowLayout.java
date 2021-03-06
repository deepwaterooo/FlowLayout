package com.me.cusviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";

    private List<Rect> mChildrenPositionList = new ArrayList<>();   // 记录各子 View 的位置
    private int mMaxLines = Integer.MAX_VALUE;      // 最多显示的行数，默认无限制
    private int mVisibleItemCount;       // 可见的 item 数

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 清除之前的位置
        mChildrenPositionList.clear();
        // 测量所有子元素（这样 child.getMeasuredXXX 才能获取到值）
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int[] a = helper(widthSize);
        int measuredHeight = 0;
        // EXACTLY 模式：对应指定大小和 match_parent
        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize;
        }
        // AT_MOST 模式，对应 wrap_content
        else if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = a[0];
        }
        int measuredWidth = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = a[1];
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * 在 wrap_content 情况下，得到布局的测量高度和测量宽度
     * 返回值是一个有两个元素的数组 a，a[0] 代表测量高度，a[1] 代表测量宽度
     */
    private int[] helper(int widthSize) {
        boolean isOneRow = true;    // 是否是单行
        int width = getPaddingLeft();   // 记录当前行已有的宽度
        int height = getPaddingTop();   // 记录当前行已有的高度
        int maxHeight = 0;      // 记录当前行的最大高度
        int currLine = 1;       // 记录当前行数

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 获取当前子元素的 margin
            LayoutParams params = child.getLayoutParams();
            MarginLayoutParams mlp;
            if (params instanceof MarginLayoutParams) {
                mlp = (MarginLayoutParams) params;
            } else {
                mlp = new MarginLayoutParams(params);
            }
            // 记录子元素所占宽度和高度
            int childWidth = mlp.leftMargin + child.getMeasuredWidth() + mlp.rightMargin;
            int childHeight = mlp.topMargin + child.getMeasuredHeight() + mlp.bottomMargin;
            maxHeight = Math.max(maxHeight, childHeight);

            // 判断是否要换行
            if (width + childWidth + getPaddingRight() > widthSize) {
                // 加上该行的最大高度
                height += maxHeight;
                // 重置 width 和 maxHeight
                width = getPaddingLeft();
                maxHeight = childHeight;
                isOneRow = false;
                currLine++;
                if (currLine > mMaxLines) {
                    break;
                }
            }
            // 存储该子元素的位置，在 onLayout 时设置
            Rect rect = new Rect(width + mlp.leftMargin,
                                 height + mlp.topMargin,
                                 width + childWidth - mlp.rightMargin,
                                 height + childHeight - mlp.bottomMargin);
            mChildrenPositionList.add(rect);

            // 加上该子元素的宽度
            width += childWidth;
        }

        int[] res = new int[2];
        res[0] = height + maxHeight + getPaddingBottom();
        res[1] = isOneRow? width + getPaddingRight() : widthSize;

        return res;
    }

    @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 布置子 View 的位置
        int n = Math.min(getChildCount(), mChildrenPositionList.size());
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            Rect rect = mChildrenPositionList.get(i);
            child.layout(rect.left, rect.top, rect.right, rect.bottom);
        }
        mVisibleItemCount = n;
    }

    /**
     * 设置 Adapter
     */
    public void setAdapter(Adapter adapter) {
        // 移除之前的视图
        removeAllViews();
        // 添加 item
        int n = adapter.getItemCount();
        for (int i = 0; i < n; i++) {
            ViewHolder holder = adapter.onCreateViewHolder(this);
            adapter.onBindViewHolder(holder, i);
            View child = holder.itemView;
            addView(child);
        }
    }

    /**
     * 设置最多显示的行数
     */
    public void setMaxLines(int maxLines) {
        mMaxLines = maxLines;
    }

    /**
     * 获取显示的 item 数
     */
    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public abstract static class Adapter<VH extends ViewHolder> {

        public abstract VH onCreateViewHolder(ViewGroup parent);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();

    }

    public abstract static class ViewHolder {
        public final View itemView;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }
}


//     public class FlowLayout extends ViewGroup {
//     /**
//      * 每个item纵向间距
//      */
//     private int mVerticalSpacing;
//     /**
//      * 每个item横向间距
//      */
//     private int mHorizontalSpacing;
//     private BaseAdapter mAdapter;
//     private TagItemClickListener mListener;
//     private DataChangeObserver mObserver;

//     private int mVisibleItemCount;       // 可见的 item 数
    
//     public FlowLayout(Context context) {
//         this(context, null);
//     }
//     public FlowLayout(Context context, AttributeSet attrs) {
//         this(context, attrs, 0);
//     }
//     public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
//         super(context, attrs, defStyle);
//         /**
//          * 获得我们所定义的自定义样式属性
//          */
//         TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyle, 0);
//         for (int i = 0; i < a.getIndexCount(); i++) {
//             int attr = a.getIndex(i);
//             switch (attr) {
//             case R.styleable.FlowLayout_verticalSpacing:
//                 mVerticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, 5);
//                 break;
//             case R.styleable.FlowLayout_horizontalSpacing:
//                 mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, 10);
//                 break;
//             }
//         }
//         a.recycle();
//     }

//     /**
//      * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
//      */
//     @Override
//         protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//         /**
//          * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
//          */
//         int heighMode = MeasureSpec.getMode(heightMeasureSpec);
//         int heighSize = MeasureSpec.getSize(heightMeasureSpec);
//         int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//         /**
//          * 高
//          */
//         int height = 0;
//         /**
//          * 每一行的高度，累加至height
//          */
//         int lineHeight = 0;
//         /**
//          * 在warp_content情况下，记录当前childView的左边的一个位置
//          */
//         int childLeft = getPaddingLeft();
//         /**
//          * 在warp_content情况下，记录当前childView的上边的一个位置
//          */
//         int childTop = getPaddingTop();
//         // getChildCount得到子view的数目,遍历循环出每个子View
//         for (int i = 0; i < getChildCount(); i++) {
//             //拿到index上的子view
//             View childView = getChildAt(i);
//             // 测量每一个child的宽和高
//             measureChild(childView, widthMeasureSpec, heightMeasureSpec);
//             //当前子空间实际占据的高度
//             int childHeight = childView.getMeasuredHeight();
//             //当前子空间实际占据的宽度
//             int childWidth = childView.getMeasuredWidth();
//             lineHeight = Math.max(childHeight, lineHeight);// 取最大值
//             //如果加入当前childView，超出最大宽度，则将目前最大宽度给width，类加height 然后开启新行
//             if (childWidth + childLeft + getPaddingRight() > widthSize) {
//                 childLeft = getPaddingLeft();// 重新开启新行，开始记录childLeft
//                 childTop += mVerticalSpacing + childHeight;// 叠加当前的高度
//                 lineHeight = childHeight;// 开启记录下一行的高度
//             }else{
//                 //否则累加当前childView的宽度
//                 childLeft += childWidth + mHorizontalSpacing;
//             }
//         }
//         height += childTop + lineHeight + getPaddingBottom();
//         setMeasuredDimension(widthSize, heighMode == MeasureSpec.EXACTLY ? heighSize : height);
//     }

//     @Override
//         protected void onLayout(boolean changed, int l, int t, int r, int b) {
//         int width = r - l;
//         int childLeft = getPaddingLeft();
//         int childTop = getPaddingTop();
//         int lineHeight = 0;
//         //遍历所有childView根据其宽和高，计算子控件应该出现的位置
//         for (int i = 0; i < getChildCount(); i++) {
//             final View childView = getChildAt(i);
//             if (childView.getVisibility() == View.GONE) {
//                 continue;
//             }
//             int childWidth = childView.getMeasuredWidth();
//             int childHeight = childView.getMeasuredHeight();
//             lineHeight = Math.max(childHeight, lineHeight);
//             // 如果已经需要换行
//             if (childLeft + childWidth + getPaddingRight() > width) {
//                 childLeft = getPaddingLeft();
//                 childTop += mVerticalSpacing + lineHeight;
//                 lineHeight = childHeight;
//             }
//             childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
//             childLeft += childWidth + mHorizontalSpacing;
//         }
//     }

//     private void drawLayout() {
//         if (mAdapter == null || mAdapter.getCount() == 0) {
//             return;
//         }
//         removeAllViews();
//         for (int i = 0; i < mAdapter.getCount(); i++) {
//             View view = mAdapter.getView(i, null, null);
//             final int position = i;
//             view.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                         public void onClick(View v) {
//                         if (mListener != null) {
//                             mListener.itemClick(position);
//                         }
//                     }
//                 });
//             addView(view);
//         }
//     }

//     // public void setAdapter(BaseAdapter adapter) {
//     //     if (mAdapter == null) {
//     //         mAdapter = adapter;
//     //         if (mObserver == null) {
//     //             mObserver = new DataChangeObserver();
//     //             mAdapter.registerDataSetObserver(mObserver);
//     //         }
//     //         drawLayout();
//     //     }
//     // }
//     // public void setItemClickListener(TagItemClickListener mListener) {
//     //     this.mListener = mListener;
//     // }
//     // public interface TagItemClickListener { // 定义了接口方法
//     //     void itemClick(int position);
//     // }
//     // class DataChangeObserver extends DataSetObserver {
//     //     @Override
//     //         public void onChanged() {
//     //         drawLayout();
//     //     }
//     //     @Override
//     //         public void onInvalidated() {
//     //         super.onInvalidated();
//     //     }
//     // }
// }