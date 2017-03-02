package com.hm.flowlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23 0023.
 */

public class FlowLayoutViewGroup extends ViewGroup {

    public FlowLayoutViewGroup(Context context) {
        this(context, null);
    }

    public FlowLayoutViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayoutViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int childCount = getChildCount();

        int width = 0;
        int height = 0;


        int lineWidth = 0; //记录每一行的行宽
        int lineHeight = 0;   //记录每一行的行高
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
            if (lineWidth + childWidth > sizeWidth) { //如果某个子控件的宽度加上累加的宽度大于控件的宽度时 需要换行
                width = Math.max(width, childWidth);
                lineWidth = childWidth; //重置行宽
                //累加当前高度
                height += lineHeight;
                lineHeight = childHeight; //重置行高
            } else { //不需要换行
                lineHeight = Math.max(childHeight, lineHeight); //高度重置
                lineWidth += childWidth; //累加行宽
            }

            if (i == childCount - 1) { //如果在没换行的情况下 需要将新累加的行宽和上次记录的行宽作比较 以便于确定最后的行宽
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }

        Log.e("TAG","width = "+width +"height = "+height);

        //如果不是精确值 则需要使用计算出的行宽
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
    /*
    * 存储所有的View，按行记录
    */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth();//控件的宽度
        List<View> lineView = new ArrayList<>();
        if (changed) {
            int childCount = getChildCount();
            int lineWidth = 0;
            int lineHeight = 0;
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int childHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
                if (lineWidth + childWidth > width){ //需要换行
                    //记录这一行所有的子view和这一行的最大高度
                    mAllViews.add(lineView);
                    mLineHeight.add(lineHeight);

                    //重置行宽 重新初始化行view
                    lineWidth = 0;
                    lineView = new ArrayList<>();

                }
                /**
                 * 不需要换行则累加
                 */
                lineWidth += childWidth; //累加行宽
                lineView.add(childView); //将子View添加进行view
                lineHeight = Math.max(childHeight,lineHeight);
                if (i == getChildCount() -1){ //最后一行

                }
            }
            //记录最后一行的子view 和最后一行的行高
            mAllViews.add(lineView);
            mLineHeight.add(lineHeight);

            int left = 0;
            int top = 0;
            // 得到总行数
            int lineNums = mAllViews.size();

            for(int i = 0;i < lineNums ; i ++){
                lineView = mAllViews.get(i);
                lineHeight = mLineHeight.get(i);
                for (int j = 0;j < lineView.size();j ++){
                    View childView = lineView.get(j);
                    if (childView.getVisibility() == GONE){continue;}
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    //计算childView的l,t,r,t
                    int lc = left + lp.leftMargin;
                    int tc = top + lp.topMargin;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();
                    childView.layout(lc,tc,rc,bc);

                    left += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                }
                left = 0;
                top += lineHeight;
            }

        }
    }
}
