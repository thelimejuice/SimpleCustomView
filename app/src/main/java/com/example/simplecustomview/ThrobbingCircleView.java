package com.example.simplecustomview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ThrobbingCircleView extends View {

    private static final String PROPERTY_RADIUS = "propertyRadius";
    private static final int RATIO_FOR_ANIMATION = 8;
    private static final int MIN_MEASURE_FOR_ANIMATION = 3;
    private static final int SHADOW_RADIUS = 5;
    private static final int DURATION_FOR_ANIMATION = 300;
    private static final int DEFAULT_RADIUS = 80;

    private Paint paint = new Paint();
    private int radius;
    private int color;
    private ValueAnimator animator;
    private int minRadius;
    private int maxRadius;
    private int measureForAnimation;


    public ThrobbingCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setValueAnimator();
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ThrobbingCircleView, 0, 0);
        radius = typedArray.getInteger(R.styleable.ThrobbingCircleView_radius, DEFAULT_RADIUS);
        minRadius = radius;
        measureForAnimation = radius / RATIO_FOR_ANIMATION;
        measureForAnimation = measureForAnimation <= MIN_MEASURE_FOR_ANIMATION ? MIN_MEASURE_FOR_ANIMATION : measureForAnimation;

        maxRadius = minRadius + measureForAnimation;
        color = typedArray.getColor(R.styleable.ThrobbingCircleView_circleColor, Color.BLACK);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredValue = maxRadius * 2;
        setMeasuredDimension(measuredValue, measuredValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(color);
        paint.setShadowLayer(SHADOW_RADIUS, 0, 0, Color.GRAY);

        canvas.drawCircle(maxRadius, maxRadius, radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                animator.start();
                break;
            case MotionEvent.ACTION_UP:
                animator.cancel();
                radius = minRadius;
                invalidate();
                break;
        }

        return true;
    }

    private void setValueAnimator() {
        PropertyValuesHolder propertyRadius = PropertyValuesHolder.ofInt(PROPERTY_RADIUS, minRadius, maxRadius);

        ValueAnimator animator = new ValueAnimator();
        animator.setValues(propertyRadius);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(DURATION_FOR_ANIMATION);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (int) animation.getAnimatedValue(PROPERTY_RADIUS);
                invalidate();
            }
        });
        this.animator = animator;
    }
}
