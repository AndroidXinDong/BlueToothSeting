package com.usr.usrsimplebleassistent.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.usr.usrsimplebleassistent.Utils.DensityUtils;

/**
 * create by XinDong
 * on
 * at
 **/
public class DragFloatBtn extends FloatingActionButton {
    //移动后的xy坐标
    private float mLastRawx;
    private float mLastRawy;
    private boolean isDrug = false;
    private int mRootMeasuredWidth;
    private int padding;
    private float distanceX,distanceY;

    public DragFloatBtn(Context context) {
        super(context);
        init();
    }

    public DragFloatBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        padding = DensityUtils.dp2px(getContext(), 15);
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //当前手指的坐标
        float mRawx = ev.getRawX();
        float mRawy = ev.getRawY();

        ViewGroup mViewGroup = (ViewGroup) getParent();

        switch (ev.getAction()) {
            //手指按下
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                isDrug = false;
                //记录按下的位置
                mLastRawx = mRawx;
                mLastRawy = mRawy;
                if (mViewGroup != null) {
                    int[] location = new int[2];
                    mViewGroup.getLocationInWindow(location);
                    //获取父布局的宽度
                    mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                }

                break;
            //手指滑动
            case MotionEvent.ACTION_MOVE:
                if (mViewGroup != null) {
                    int[] location = new int[2];
                    mViewGroup.getLocationInWindow(location);
                    //获取父布局的高度
                    int mRootMeasuredHeight = mViewGroup.getMeasuredHeight();
                    mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                    //获取父布局顶点的坐标
                    int rootTopy = location[1];
                    if (mRawx >= 0 && mRawx <= mRootMeasuredWidth && mRawy >= rootTopy && mRawy <= (mRootMeasuredHeight + rootTopy)) {
                        //手指X轴滑动距离
                        float differenceValuex = mRawx - mLastRawx;
                        //手指Y轴滑动距离
                        float differenceValuey = mRawy - mLastRawy;
                        //判断是否为拖动操作
                        if (!isDrug) {
                            isDrug = !(Math.sqrt(differenceValuex * differenceValuex + differenceValuey * differenceValuey) < 2);
                        }
                        //获取手指按下的距离与控件本身X轴的距离
                        float ownx = getX();
                        //获取手指按下的距离与控件本身Y轴的距离
                        float owny = getY();
                        //理论中X轴拖动的距离
                        float endx = ownx + differenceValuex;
                        //理论中Y轴拖动的距离
                        float endy = owny + differenceValuey;
                        //X轴可以拖动的最大距离
                        float maxx = mRootMeasuredWidth - getWidth();
                        //Y轴可以拖动的最大距离
                        float maxy = mRootMeasuredHeight - getHeight();
                        //X轴边界限制
                        endx = endx < 0 ? 0 : endx > maxx ? maxx : endx;
                        //Y轴边界限制
                        endy = endy < 0 ? 0 : endy > maxy ? maxy : endy;
                        //开始移动
                        setX(endx);
                        setY(endy);
                         distanceX = ev.getRawX() - mLastRawx;
                         distanceY = ev.getRawY() - mLastRawy;
                        //记录位置
                        mLastRawx = mRawx;
                        mLastRawy = mRawy;
                    }
                }
                break;
            //手指离开
            case MotionEvent.ACTION_UP:
                //恢复按压效果
                setPressed(false);
                if (!isDrug || (Math.abs(distanceX)<=3&&Math.abs(distanceY)<=3)){
                    performClick();
                }
                float center = mRootMeasuredWidth / 2;
                //自动贴边
                if (mLastRawx <= center) {
                    //向左贴边
                    DragFloatBtn.this.animate()
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(500)
//                            .x(padding)
                            .start();
                } else {
                    //向右贴边
                    DragFloatBtn.this.animate()
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(500)
//                            .x(mRootMeasuredWidth - getWidth() - padding)
                            .start();
                }
                break;
        }
        //是否拦截事件
        return isDrug ? isDrug : super.onTouchEvent(ev);
    }

}
