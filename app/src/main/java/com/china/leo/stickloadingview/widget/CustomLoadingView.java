package com.china.leo.stickloadingview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.china.leo.stickloadingview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * leo linxiaotao1993@vip.qq.com
 * Created on 16-8-24 下午9:58
 */
public class CustomLoadingView extends View
{
    private Paint mPaint;
    /** 当前状态 */
    private int mCurrentStatus;
    /** 中心圆的半径 */
    private float mCircleRadius;
    /** 小圆的半径 */
    private float mSmallRadius;
    private float mCenterDistance;
    /** 圆之间的起始距离 */
    private int mStartDistance;
    /** 第一个小圆和中心圆最远距离 */
    private float mMaxDistance;
    /** 是否需要绘制Path曲线 */
    private boolean mIsPath;
    /** 当前绘制的Path */
    private Path mPath;
    /** 绘制Path的左边距离 */
    private float mPathDistance;
    /** 绘制Path的最小距离 */
    private float mMinPathDistance;
    /** 两个小圆之间的距离 */
    private float mDoubleDistance;
    /** 当前所需要绘制的小圆 */
    private int mCurrentCount = CIRCLE_COUNT;
    /** View的宽 */
    private int mWidth;
    /** View的高 */
    private int mHeight;
    /** 保存的动画列表 */
    private List<Animator> mAnimatorList;
    /** 画布移动值 */
    private float mTranslateValue;
    /** 是否开始translate动画 */
    private boolean mIsTranslate;
    /** 当前绘制步数 */
    private int mCurrentStep = STEP_ONE;
    /** 动画持续时间 */
    private int mDuration = DURATION_DEFAULT;
    /** 大圆抖动动画时间 */
    private int mTranslateDuration = TRANSLATE_DEFALT;

    /** 默认动画时间 */
    private static final int DURATION_DEFAULT = 1250;
    private static final int TRANSLATE_DEFALT = 250;
    /** 绘制第一步 */
    private static final int STEP_ONE = 0;
    /** 绘制第二步 */
    private static final int STEP_TWO = 1;
    /** 绘制第三步 */
    private static final int STEP_THREE = 2;
    /** 绘制第四步 */
    private static final int STEP_FOUR = 3;
    /** 贝塞尔曲线改变值 */
    private static final int PATH_CHANGE_VAL = 5;
    /** 大圆大小改变值 */
    private static final int CIRCLE_VAL = 15;
    /** 小圆的数量 */
    private static final int CIRCLE_COUNT = 3;
    /** 正常状态 */
    private static final int STATUS_NORNAL = 0;
    /** loading状态 */
    private static final int STATUS_LOADING = 1;
    /** 通过贝塞尔曲线绘制圆 */
    private static final float CIRCLE_VALUE = 0.551915024494f;
    /** 线条颜色 */
    private final static int[] COLORS = new int[]{0xFF7ECBDA, 0xFFE6A92C, 0xFFF0A0A5, 0xFF5ABA94};

    public CustomLoadingView(Context context)
    {
        this(context, null);
    }

    public CustomLoadingView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CustomLoadingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        drawCircle(canvas, mCurrentStep);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /** 开始 */
    public void start()
    {
        if (mCurrentStatus == STATUS_NORNAL)
        {
            mCurrentStatus = STATUS_LOADING;
            circleOneAnim();
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

    /** 当前loading状态 */
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
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.loading_color));
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        if (mPath == null)
        {
            mPath = new Path();
        }
    }

    /** 初始化参数 */
    private void initData()
    {
        if (mAnimatorList == null)
        {
            mAnimatorList = new ArrayList<>();
        } else
        {
            mAnimatorList.clear();
        }
        mIsTranslate = false;
        mTranslateValue = 0;
        mTranslateDuration = TRANSLATE_DEFALT;
        mDuration = DURATION_DEFAULT;
        mCurrentStep = STEP_ONE;
        mCurrentCount = CIRCLE_COUNT;
        mIsPath = false;
        mCurrentStatus = STATUS_NORNAL;
        mCircleRadius = dp2px(getContext(), 20);
        mSmallRadius = mCircleRadius / 2;
        mStartDistance = dp2px(getContext(), 20);
        mMaxDistance = (mStartDistance + 2 * mSmallRadius) * mCurrentCount + mSmallRadius;
        mCenterDistance = mMaxDistance;
        mMinPathDistance = (mCircleRadius + mStartDistance + mSmallRadius) / 4 * 3;
        mPathDistance = 0;
        mDoubleDistance = mSmallRadius * 2 + mStartDistance;
    }

    /** 取消所有动画 */
    private void cancelAnim()
    {
        if (!mAnimatorList.isEmpty())
        {
            for (Animator animator : mAnimatorList)
            {
                animator.cancel();
            }

            mAnimatorList.clear();
        }
    }

    /** 中心的圆,需要绘制1个大圆,3个小圆 */
    private void drawCircle(Canvas canvas, int currentStep)
    {
        float endX = 0f;
        float endY = 0f;
        float startX = 0f;
        float startY = 0f;

        //绘制大圆
        drawCirclePath(canvas, mPathDistance);

        switch (currentStep)
        {
            case STEP_ONE:
                endX = mWidth / 2 - mCenterDistance;
                endY = mHeight / 2;
                for (int i = 0; i < mCurrentCount; i++)
                {
                    canvas.drawCircle(endX + (i * mDoubleDistance), endY, mSmallRadius, mPaint);
                }
                break;
            case STEP_TWO:
                startX = mWidth / 2 + mCenterDistance;
                startY = mHeight / 2;
                for (int i = 0; i < mCurrentCount; i++)
                {
                    canvas.drawCircle(startX - i * mDoubleDistance, startY, mSmallRadius, mPaint);
                }
                break;
            case STEP_THREE:
                endX = mWidth / 2 + mCenterDistance;
                endY = mHeight / 2;
                for (int i = 0; i < mCurrentCount; i++)
                {
                    canvas.drawCircle(endX - (i * mDoubleDistance), endY, mSmallRadius, mPaint);
                }
                break;
            case STEP_FOUR:
                startX = mWidth / 2 - mCenterDistance;
                startY = mHeight / 2;
                for (int i = 0; i < mCurrentCount; i++)
                {
                    canvas.drawCircle(startX + i * mDoubleDistance, startY, mSmallRadius, mPaint);
                }
                break;
        }
    }

    /** 通过贝塞尔曲线画圆 */
    private void drawCirclePath(Canvas canvas, float distance)
    {
        float m = mCircleRadius * CIRCLE_VALUE;

        CirclePoint p1 = new CirclePoint(mWidth / 2, mHeight / 2 - mCircleRadius);
        CirclePoint p2 = new CirclePoint(mWidth / 2 + m, mHeight / 2 - mCircleRadius);
        CirclePoint p3 = new CirclePoint(mWidth / 2 + mCircleRadius, mHeight / 2 - m);
        CirclePoint p4 = new CirclePoint(mWidth / 2 + mCircleRadius, mHeight / 2);

        CirclePoint p5 = new CirclePoint(mWidth / 2 + mCircleRadius, mHeight / 2 + m);
        CirclePoint p6 = new CirclePoint(mWidth / 2 + m, mHeight / 2 + mCircleRadius);
        CirclePoint p7 = new CirclePoint(mWidth / 2, mHeight / 2 + mCircleRadius);

        CirclePoint p8 = new CirclePoint(mWidth / 2 - m, mHeight / 2 + mCircleRadius);
        CirclePoint p9 = new CirclePoint(mWidth / 2 - mCircleRadius, mHeight / 2 + m);
        CirclePoint p10 = new CirclePoint(mWidth / 2 - mCircleRadius, mHeight / 2);

        CirclePoint p11 = new CirclePoint(mWidth / 2 - mCircleRadius, mHeight / 2 - m);
        CirclePoint p12 = new CirclePoint(mWidth / 2 - m, mHeight / 2 - mCircleRadius);


        if (mIsPath)
        {
            if (distance > 0)
            {
                p1.y += PATH_CHANGE_VAL;
                p2.y = p1.y;
                p3.x += distance;
                p4.x = p3.x;
                p5.x = p3.x;
                p6.y -= PATH_CHANGE_VAL;
                p7.y = p6.y;
                p8.y = p6.y;
                p9.x -= PATH_CHANGE_VAL / 2;
                p10.x = p9.x;
                p11.x = p9.x;
                p12.y = p1.y;

            } else
            {
                p1.y += PATH_CHANGE_VAL;
                p2.y = p1.y;
                p3.x += PATH_CHANGE_VAL / 2;
                p4.x = p3.x;
                p5.x = p3.x;
                p6.y -= PATH_CHANGE_VAL;
                p7.y = p6.y;
                p8.y = p6.y;
                p9.x += distance;
                p10.x = p9.x;
                p11.x = p9.x;
                p12.y = p1.y;
            }
        } else
        {
            p1.x += mTranslateValue;
            p7.x += mTranslateValue;
            if (mTranslateValue > 0)
            {
                p2.x += mTranslateValue;
                p3.x += mTranslateValue;
                p4.x += mTranslateValue;
                p5.x += mTranslateValue;
                p6.x += mTranslateValue;

                p8.x += mTranslateValue / 6;
                p9.x += mTranslateValue / 6;
                p10.x += mTranslateValue / 6;
                p11.x += mTranslateValue / 6;
                p12.x += mTranslateValue / 6;

            } else
            {
                p2.x += mTranslateValue / 5;
                p3.x += mTranslateValue / 5;
                p4.x += mTranslateValue / 5;
                p5.x += mTranslateValue / 5;
                p6.x += mTranslateValue / 5;

                p8.x += mTranslateValue;
                p9.x += mTranslateValue;
                p10.x += mTranslateValue;
                p11.x += mTranslateValue;
                p12.x += mTranslateValue;
            }


        }

        mPath.reset();
        mPath.moveTo(p1.x, p1.y);
        mPath.cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);
        mPath.cubicTo(p5.x, p5.y, p6.x, p6.y, p7.x, p7.y);
        mPath.cubicTo(p8.x, p8.y, p9.x, p9.y, p10.x, p10.y);
        mPath.cubicTo(p11.x, p11.y, p12.x, p2.y, p1.x, p1.y);
        canvas.drawPath(mPath, mPaint);
    }

    /** dp==>px */
    private int dp2px(Context context, float dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
//        Logger.d(scale);
        return (int) (dp * scale + 0.5f);
    }

    /** 第一个动画 */
    private void circleOneAnim()
    {
        ValueAnimator translationAnim = ValueAnimator.ofFloat(mMaxDistance, 0f);
        translationAnim.setInterpolator(new AccelerateInterpolator());
        translationAnim.setDuration(mDuration);
        translationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
//                Logger.d(mCenterDistance);
                mCenterDistance = (float) animation.getAnimatedValue();
                if (mCenterDistance <= mMaxDistance - mDoubleDistance && mCurrentCount == CIRCLE_COUNT)
                {
                    mCurrentCount--;
                    mCircleRadius += CIRCLE_VAL;
                } else if (mCenterDistance <= mMaxDistance - 2 * mDoubleDistance && mCurrentCount == CIRCLE_COUNT - 1)
                {
                    mCurrentCount--;
//                    mCircleRadius += CIRCLE_VAL;
                } else if (mCenterDistance <= mMaxDistance - 3 * mDoubleDistance && mCurrentCount == CIRCLE_COUNT - 2)
                {
                    mCurrentCount--;
//                    mCircleRadius += CIRCLE_VAL;
                }

                calculatePath(mCurrentStep);
//                Logger.d(mCenterDistance);
                invalidate();
            }
        });
        translationAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
//                Logger.d("动画结束");
                if (mCurrentStatus == STATUS_LOADING && mCurrentStep == STEP_ONE)
                {
                    mCurrentStep = STEP_TWO;
                    circleTwoAnim();
                }
                super.onAnimationEnd(animation);
            }
        });
        if (mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(translationAnim);
            translationAnim.start();
        }
    }

    /** 第二个动画 */
    private void circleTwoAnim()
    {
        ValueAnimator translationAnim = ValueAnimator.ofFloat(0f, mMaxDistance);
        translationAnim.setDuration(mDuration);
        translationAnim.setInterpolator(new DecelerateInterpolator());
        translationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCenterDistance = (float) animation.getAnimatedValue();
//                Logger.d(mCenterDistance);
                if (mCenterDistance >= (mCircleRadius - mSmallRadius) && mCurrentCount == 0)
                {
                    mCircleRadius -= CIRCLE_VAL;
                    mCurrentCount++;
                } else if (mCenterDistance >= (mCircleRadius + mDoubleDistance - mSmallRadius) && mCurrentCount == 1)
                {
                    mCurrentCount++;
//                    mCircleRadius -= CIRCLE_VAL;
                } else if (mCenterDistance >= (mCircleRadius + mDoubleDistance * 2 - mSmallRadius) && mCurrentCount == 2)
                {
                    mCurrentCount++;
//                    mCircleRadius -= CIRCLE_VAL;
//                    translateAnim();
                }

                calculatePath(mCurrentStep);

                if (mCurrentCount == CIRCLE_COUNT && !mIsPath && !mIsTranslate)
                {
                    translateAnim(mCurrentStep, 0f, 30f, 0f);
                }

                invalidate();
            }
        });

        if (mCurrentStatus == STATUS_LOADING && mCurrentStep == STEP_TWO)
        {
            mAnimatorList.add(translationAnim);
            translationAnim.start();
        }
    }

    /** 第三个动画 */
    private void circleThreeAnim()
    {
        ValueAnimator translationAnim = ValueAnimator.ofFloat(mMaxDistance, 0f);
        translationAnim.setDuration(mDuration);
        translationAnim.setInterpolator(new AccelerateInterpolator());
        translationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCenterDistance = (float) animation.getAnimatedValue();
                if (mCenterDistance <= mMaxDistance - mDoubleDistance && mCurrentCount == CIRCLE_COUNT)
                {
                    mCurrentCount--;
                    mCircleRadius += CIRCLE_VAL;
                } else if (mCenterDistance <= mMaxDistance - 2 * mDoubleDistance && mCurrentCount == CIRCLE_COUNT - 1)
                {
                    mCurrentCount--;
//                    mCircleRadius += CIRCLE_VAL;
                } else if (mCenterDistance <= mMaxDistance - 3 * mDoubleDistance && mCurrentCount == CIRCLE_COUNT - 2)
                {
                    mCurrentCount--;
//                    mCircleRadius += CIRCLE_VAL;
                }
                calculatePath(mCurrentStep);
//                Logger.d(mCenterDistance);
                invalidate();
            }
        });
        translationAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mCurrentStep == STEP_THREE && mCurrentStatus == STATUS_LOADING)
                {
                    mCurrentStep = STEP_FOUR;
                    circleFourAnim();
                }
                super.onAnimationEnd(animation);
            }
        });

        if (mCurrentStep == STEP_THREE && mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(translationAnim);
            translationAnim.start();
        }
    }

    /** 第四个动画 */
    private void circleFourAnim()
    {
        ValueAnimator translationAnim = ValueAnimator.ofFloat(0f, mMaxDistance);
        translationAnim.setDuration(mDuration);
        translationAnim.setInterpolator(new DecelerateInterpolator());
        translationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCenterDistance = (float) animation.getAnimatedValue();
                if (mCenterDistance >= (mCircleRadius - mSmallRadius) && mCurrentCount == 0)
                {
                    mCircleRadius -= CIRCLE_VAL;
                    mCurrentCount++;
                } else if (mCenterDistance >= (mCircleRadius + mDoubleDistance - mSmallRadius) && mCurrentCount == 1)
                {
                    mCurrentCount++;
//                    mCircleRadius -= CIRCLE_VAL;
                } else if (mCenterDistance >= (mCircleRadius + mDoubleDistance * 2 - mSmallRadius) && mCurrentCount == 2)
                {
                    mCurrentCount++;
//                    mCircleRadius -= CIRCLE_VAL;
                }

                calculatePath(mCurrentStep);
                if (mCurrentCount == CIRCLE_COUNT && !mIsPath && !mIsTranslate)
                {
                    translateAnim(mCurrentStep, 0f, -30f, 0f);
                }
                invalidate();
            }
        });
        if (mCurrentStep == STEP_FOUR && mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(translationAnim);
            translationAnim.start();
        }
    }

    /** 大圆抖动动画 */
    private void translateAnim(final int step, float... valus)
    {
        final ValueAnimator roteAnim = ValueAnimator.ofFloat(valus);
        roteAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mTranslateValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        roteAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mIsTranslate = true;
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mIsTranslate = false;
                if (step == STEP_TWO)
                {
                    mCurrentStep = STEP_THREE;
                    circleThreeAnim();
                } else if (step == STEP_FOUR)
                {
                    mCurrentStep = STEP_ONE;
                    circleOneAnim();
                }
                mTranslateValue = 0f;
                super.onAnimationEnd(animation);
            }
        });
        roteAnim.setDuration(mTranslateDuration);
        roteAnim.setInterpolator(new LinearInterpolator());
        roteAnim.start();
        if (mCurrentStatus == STATUS_LOADING)
        {
            mAnimatorList.add(roteAnim);
            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    roteAnim.start();
                }
            }, 250);
        }
    }

    /** 计算path */
    private void calculatePath(int step)
    {
        mIsPath = false;
        float distance = Math.abs(mCenterDistance - (mCurrentCount - 1) * mDoubleDistance);
        if (mCurrentCount > 0)
        {
            switch (step)
            {
                case STEP_ONE:
                    if (distance < (mMinPathDistance - mCenterDistance / 8))
                    {
                        mPaint.setColor(COLORS[step]);
                        mIsPath = true;
                        mPathDistance = -distance;
//                        Logger.d("min = %f,disatnce = %f",mMinDxistance,mPathDistance);
                    }
                    break;
                case STEP_TWO:
                    if (distance < mMinPathDistance)
                    {
                        mPaint.setColor(COLORS[step]);
                        mIsPath = true;
                        mPathDistance = distance;
                    }
                    break;
                case STEP_THREE:
                    if (distance < (mMinPathDistance - mCenterDistance / 8))
                    {
                        mPaint.setColor(COLORS[step]);
                        mIsPath = true;
                        mPathDistance = distance;
                    }
                    break;
                case STEP_FOUR:
                    if (distance < mMinPathDistance)
                    {
                        mPaint.setColor(COLORS[step]);
                        mIsPath = true;
                        mPathDistance = -distance;
                    }
            }
        }
    }

    private class CirclePoint
    {
        float x;
        float y;

        public CirclePoint(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
