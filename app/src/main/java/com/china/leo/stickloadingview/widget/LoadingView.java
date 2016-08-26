package com.path.leo.myapplication.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * leo linxiaotao1993@vip.qq.com
 * Created on 16-8-23 下午5:47
 */
public class LoadingView extends View
{
    private Paint mPaint;
    /** 线条长度 */
    private int mLineLength = dp2px(getContext(), 40);
    /** 线条当前长度 */
    private float mCurrentLength;
    /** 当前width,height */
    private int mWidth = 0;
    private int mHeight = 0;
    /** 起始角度 */
    private int mCanvasAngle = 60;
    /** 绘制圆的半径 */
    private int mCireleRadius;
    /** 绘制圆的距离 */
    private float mCircleY;
    /** 线条颜色 */
    private int[] mColors = new int[]{0xB07ECBDA, 0xB0E6A92C, 0xB0D6014D, 0xB05ABA94};
    /** 当前步骤 */
    private int mCurrentStep = STEP_ONE;
    /** 当前状态 */
    private int mCurrentStatus = STATUS_NORNAL;
    /** 执行的动画集合 */
    private List<Animator> mAnimatorList;

    //静止状态
    private static final int STATUS_NORNAL = 0;
    //loading状态
    private static final int STATUS_LOADING = 1;

    private static final int STEP_COUNT = 4;
    /** 第一步 */
    private static final int STEP_ONE = 0;
    /** 第二步 */
    private static final int STEP_TWO = 1;
    /** 第三步 */
    private static final int STEP_THREE = 2;
    /** 第四步 */
    private static final int STEP_FOUR = 3;
    /** 每次旋转角度 */
    private static final int ROTATE_ANGLE = 90;
    /** 默认起始旋转角度 */
    private static final int ROTATE_DEFALUT = 60;
    /** 默认动画执行时间 */
    private static final int DURATION_DEFALUT = 500;


    public LoadingView(Context context)
    {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        Logger.d("w = %d,h = %d,oldw = %d,oldh = %d", w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        drawLine(canvas, mCurrentStep % STEP_COUNT);
        super.onDraw(canvas);
    }

    /** 开始 */
    public void start()
    {
        if (mCurrentStatus == STATUS_NORNAL)
        {
            mCurrentStatus = STATUS_LOADING;
            cancelAnim();
            startAnimOne();
        }
    }

    /** 停止 */
    public void stop()
    {
        if (mCurrentStatus == STATUS_LOADING)
        {
            mCurrentStatus = STATUS_NORNAL;
            cancelAnim();
            initData();
        }
    }

    /** 清理 */
    public void clean()
    {
        cancelAnim();
    }

    /** 是否已经开始 */
    public boolean isStart()
    {
        return mCurrentStatus == STATUS_LOADING;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // private method
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** 初始化 */
    private void init()
    {
        initView();
        initData();
    }

    /** 初始化View */
    private void initView()
    {
        if (mPaint == null)
        {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(48f);
        }
    }

    /** 初始化参数 */
    private void initData()
    {
        mCircleY = 0;
        mCurrentStep = STEP_ONE;
        mCurrentStatus = STATUS_NORNAL;
        mAnimatorList = new ArrayList<>();
        mCanvasAngle = ROTATE_DEFALUT;
        mLineLength = dp2px(getContext(), 50);
        mCurrentLength = mLineLength;
        mCireleRadius = mLineLength / 5;
    }

    /** 取消动画 */
    private void cancelAnim()
    {
        if (!mAnimatorList.isEmpty())
        {
            for (Animator animator : mAnimatorList)
            {
                if (animator.isRunning())
                    animator.cancel();
            }
            mAnimatorList.clear();
        }
    }

    /** 绘制线条 */
    private void drawLine(Canvas canvas, int step)
    {
        float startX = mWidth / 2 - mLineLength / 2f;
        float startY = mHeight / 2 - mLineLength;
        float endX = startX;
        float endY = mHeight / 2 + mLineLength;
        switch (step)
        {
            case STEP_ONE:
                for (int i = 0; i < mColors.length; i++)
                {
                    mPaint.setColor(mColors[i]);
                    canvas.rotate(mCanvasAngle + ROTATE_ANGLE * i, mWidth / 2, mHeight / 2);
                    canvas.drawLine(startX, mHeight / 2 - mCurrentLength, endX, endY, mPaint);
                    canvas.rotate(-(mCanvasAngle + ROTATE_ANGLE * i), mWidth / 2, mHeight / 2);
                }
                break;
            case STEP_TWO:
                for (int i = 0; i < mColors.length; i++)
                {
                    mPaint.setColor(mColors[i]);
                    canvas.rotate(mCanvasAngle + ROTATE_ANGLE * i, mWidth / 2, mHeight / 2);
                    canvas.drawCircle(endX, endY, mCireleRadius, mPaint);
                    canvas.rotate(-(mCanvasAngle + ROTATE_ANGLE * i), mWidth / 2, mHeight / 2);
                }
                break;
            case STEP_THREE:
                for (int i = 0; i < mColors.length; i++)
                {
                    mPaint.setColor(mColors[i]);
                    canvas.rotate(mCanvasAngle + ROTATE_ANGLE * i, mWidth / 2, mHeight / 2);
                    canvas.drawCircle(startX, mHeight / 2 + mCircleY, mCireleRadius, mPaint);
                    canvas.rotate(-(mCanvasAngle + ROTATE_ANGLE * i), mWidth / 2, mHeight / 2);
                }
                break;
            case STEP_FOUR:
                for (int i = 0; i < mColors.length; i++)
                {
                    mPaint.setColor(mColors[i]);
                    canvas.rotate(mCanvasAngle + ROTATE_ANGLE * i, mWidth / 2, mHeight / 2);
                    canvas.drawLine(startX, endY, endX, mHeight / 2 + mCurrentLength, mPaint);
                    canvas.rotate(-(mCanvasAngle + ROTATE_ANGLE * i), mWidth / 2, mHeight / 2);
                }
                break;
        }
    }

    /** 启动动画第一步 */
    private void startAnimOne()
    {
        ValueAnimator canvasRotateAnim = ValueAnimator.ofInt(ROTATE_DEFALUT + 0, ROTATE_DEFALUT + 360);
        canvasRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCanvasAngle = (int) animation.getAnimatedValue();
            }
        });

        ValueAnimator lineHeightAnim = ValueAnimator.ofFloat(mLineLength, -mLineLength);
        lineHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCurrentLength = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(canvasRotateAnim).with(lineHeightAnim);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.setDuration(DURATION_DEFALUT);
        animatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                Logger.d("第一个动画结束");
                if (mCurrentStatus == STATUS_LOADING)
                {
                    startAnimTwo();
                    mCurrentStep++;
                }
                super.onAnimationEnd(animation);
            }
        });

        if (mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(animatorSet);
            animatorSet.start();
        }
    }

    /** 启动动画第二步 */
    private void startAnimTwo()
    {
        ValueAnimator canvasRotateAnim = ValueAnimator.ofInt(mCanvasAngle, mCanvasAngle + 180);
        canvasRotateAnim.setInterpolator(new LinearInterpolator());
        canvasRotateAnim.setDuration(DURATION_DEFALUT);
        canvasRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCanvasAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        canvasRotateAnim.addListener(new AnimatorListenerAdapter()
        {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                Logger.d("第二个动画结束");
                if (mCurrentStatus == STATUS_LOADING)
                {
                    mCurrentStep++;
                    startAnimThree();
                }
                super.onAnimationEnd(animation);
            }
        });
        if (mCurrentStatus == STATUS_LOADING)
        {
            canvasRotateAnim.start();
            mAnimatorList.add(canvasRotateAnim);
        }
    }

    /** 启动动画第三步 */
    private void startAnimThree()
    {
        ValueAnimator canvasRotateAnim = ValueAnimator.ofInt(mCanvasAngle + 90, mCanvasAngle + 180);
        canvasRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCanvasAngle = (int) animation.getAnimatedValue();
            }
        });

        ValueAnimator circleAnim = ValueAnimator.ofFloat(mLineLength, mLineLength / 4, mLineLength);
        circleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCircleY = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(canvasRotateAnim).with(circleAnim);
        animatorSet.setDuration(DURATION_DEFALUT * 2);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                Logger.d("第三个动画结束");
                if (mCurrentStatus == STATUS_LOADING)
                {
                    mCurrentStep++;
                    startAnimFour();
                }
                super.onAnimationEnd(animation);
            }
        });
        if (mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(animatorSet);
            animatorSet.start();
        }
    }

    /** 启动动画第四步 */
    private void startAnimFour()
    {
        ValueAnimator lineAnim = ValueAnimator.ofFloat(mLineLength, -mLineLength);
        lineAnim.setDuration(DURATION_DEFALUT);
        lineAnim.setInterpolator(new LinearInterpolator());
        lineAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCurrentLength = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        lineAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                Logger.d("第四个动画结束");
                if (mCurrentStatus == STATUS_LOADING)
                {
                    mCurrentStep++;
                    startAnimOne();
                }
                super.onAnimationEnd(animation);
            }
        });

        if (mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(lineAnim);
            lineAnim.start();
        }
    }

    /** dp==>px */
    private int dp2px(Context context, float dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
