package com.wiperswitchview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Sweets on 2016/7/2.
 * 自定义滑动按钮
 */
public class WiperSwitch extends View {
    private Bitmap selectedBitmap;//滑动选中开关的背景
    private Bitmap uncheckedBitmap;//滑动未选中开关的背景
    private Bitmap switchBitmap;//开关的背景
    private Bitmap slidBitmap;//滑动块的图片对象
    private int MAX_Left;//left的最大值
    private int slidLeft ;//滑块left的值，默认为0
    private boolean isOpen = false;//记录滑动开关的状态，默认为关闭，false
    private OnCheckChangeListener mOnCheckChangeListener;//点击事件回调接口对象
    private int startX;

    private int moveX;//记录手指在控件上，x轴的移动
    private boolean isClick ;//记录当前是否是点击事件

    //在xml里面使用样式，创建该控件时，调用这个构造方法
    public WiperSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    //在xml里面引用该控件时，就调用该构造方法
    public WiperSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        String namespace = "http://schemas.android.com/apk/res-auto";
        isOpen = attrs.getAttributeBooleanValue(namespace , "isOpen", false);
        if (isOpen) {
            slidLeft = MAX_Left;
        }else{
            slidLeft = 0;
        }

        int slidBitmapId = attrs.getAttributeResourceValue(namespace, "slidBitmap", -1);
        if (slidBitmapId > -1) {
            slidBitmap = BitmapFactory.decodeResource(getResources(), slidBitmapId);
        }
        int selectedBitmapId = attrs.getAttributeResourceValue(namespace, "selectedImage", -1);
        if (slidBitmapId > -1) {
            selectedBitmap = BitmapFactory.decodeResource(getResources(), selectedBitmapId);
        }
        int uncheckedBitmapId = attrs.getAttributeResourceValue(namespace, "uncheckedImage", -1);
        if (slidBitmapId > -1) {
            uncheckedBitmap = BitmapFactory.decodeResource(getResources(), uncheckedBitmapId);
        }

        init();
        invalidate();
    }
    //当该控件，使用代码创建的时间，调用该构造方法
    public WiperSwitch(Context context) {
        super(context);
        init();
    }
    //初始化方法
    private void init() {

        if (isOpen)
            switchBitmap = selectedBitmap;
        else
            switchBitmap = uncheckedBitmap;

        MAX_Left = (int) (switchBitmap.getWidth() - (slidBitmap.getWidth()*1.25f));

        if (isOpen){
            slidLeft = MAX_Left;
        }else{
            slidLeft = 0;
        }


        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isClick) {//点击事件
                    if (isOpen) {//如果开则关闭
                        System.out.println("关闭");
                        isOpen = false;
                        slidLeft = 0;
                        switchBitmap = uncheckedBitmap;
                    }else{//如果关则打开
                        System.out.println("打开");
                        isOpen = true;
                        slidLeft = MAX_Left;
                        switchBitmap = selectedBitmap;
                    }
                    //重新调用ondraw方法
                    invalidate();//强制view进行重绘操作，重新调用ondraw方法
                    //					isOpen = !isOpen;

                    if (mOnCheckChangeListener != null) {
                        mOnCheckChangeListener.onCheckChanged(WiperSwitch.this, isOpen);
                    }

                }
            }


        });
    }
    //测量方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        //		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(switchBitmap.getWidth(), switchBitmap.getHeight());

    }

    //绘制方法
    /**
     * Canvas 画布：把控件绘制在画布，才能显示到屏幕上
     */
    @Override
    protected void onDraw(Canvas canvas) {

        //		canvas.drawRect(0, 0, 200, 200, paint );
        canvas.drawBitmap(switchBitmap, 0, 0, null);
        canvas.drawBitmap(slidBitmap, slidLeft, 0, null);
    }

    public void setOnCheckChangeListener(OnCheckChangeListener listener){
        this.mOnCheckChangeListener = listener;
    }

    //滑动开关 的点击事件回调接口
    public interface OnCheckChangeListener{
        public void onCheckChanged(View v,boolean isOpen);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1、记录手指按下的起始点
                startX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //2、记录手指移动后的结束点
                int endX = (int) event.getX();
                //3、计算出移动间距
                int diffX = endX -startX;

                moveX = moveX + Math.abs(diffX);

                //4、更新slidleft的值
                slidLeft += diffX;
                //			slidLeft = slidLeft + diffX;

                if (slidLeft < 0) {//设置左边界
                    slidLeft = 0;
                }
                if (slidLeft > MAX_Left) {//设置右边界
                    slidLeft = MAX_Left;
                }

                //5、重绘控件
                invalidate();
                //6、更新起始点
                startX = endX;
                break;
            case MotionEvent.ACTION_UP:

                if (moveX > 5) {//手指移动的间距大于5个像素，就认为是一个滑动事件
                    isClick = false;
                }else{//手指移动的间距小于5个像素，就认为是一个点击事件
                    isClick = true;
                }

                moveX = 0;

                if (!isClick) {//如果是滑动事件
                    //计算出中心线
                    int center = MAX_Left/2;
                    if (slidLeft > center) {//大于中心线的话，就显示为打开
                        isOpen = true;
                        slidLeft = MAX_Left;
                        switchBitmap = selectedBitmap;
                    }else{//小于中心线的话，就显示为关闭
                        isOpen = false;
                        slidLeft = 0;
                        switchBitmap = uncheckedBitmap;
                    }
                    invalidate();
                    //手指抬起后，将状态值返回给主界面
                    if (mOnCheckChangeListener != null) {
                        mOnCheckChangeListener.onCheckChanged(WiperSwitch.this, isOpen);
                    }
                }

                invalidate();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);//自己去消费触摸事件
    }

}
