package com.qgnix.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;

public class BigImageView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private final Rect mRect;
    private final BitmapFactory.Options mOption;
    private final GestureDetector mGestureDetector;
    private final Scroller mScroller;
    private int mImageWidth;
    private int mImageHeight;
    private BitmapRegionDecoder mDecoder;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;
    private Bitmap mBitmap;

    public BigImageView(Context context) {
        this(context, null);
    }

    public BigImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //第一步：BigView展示区域；
        mRect = new Rect();
        //需要内存复用
        mOption = new BitmapFactory.Options();
        // 手势识别
        mGestureDetector = new GestureDetector(context, this);
        // 滚动类
        mScroller = new Scroller(context);
        setOnTouchListener(this);
    }

    //第二步，获取图片宽高，其他配置
    public void setImage(byte[] data) {
        //只读取图片，不加载到内存中，获取到图片的宽高,
        mOption.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, mOption);
        mImageWidth = mOption.outWidth;
        mImageHeight = mOption.outHeight;
        //开始复用
        mOption.inMutable = true;
        //设置颜色格式
        mOption.inPreferredConfig = Bitmap.Config.RGB_565;
        //设置回来，第四步需要加载到内存
        mOption.inJustDecodeBounds = false;
        //创建一个解码器
        try {
            mDecoder = BitmapRegionDecoder.newInstance(data, 0, data.length, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    //第三步，开始计算显示区域
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        //确定图片区域的大小
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;
        mScale = mViewWidth / (float) mImageWidth;
        mRect.bottom = (int) (mViewHeight / mScale);
    }

    //第四步，画出具体内容
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            //解码器非空判断
            if (mDecoder == null) {
                return;
            }
            //复用上一个bitmap的内存，1：复用的图片的尺寸要一致，
            mOption.inBitmap = mBitmap;
            //指定解码区域
            mBitmap = mDecoder.decodeRegion(mRect, mOption);
            //利用矩阵缩放
            @SuppressLint("DrawAllocation")
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            canvas.drawBitmap(mBitmap, matrix, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //第五步，处理手势
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    //第六部，按下
    @Override
    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        // true 继续接受后续事件
        return true;
    }

    //第七步，出来滑动事件

    /**
     * @param e1        按下
     * @param e2        当前
     * @param distanceX x距离
     * @param distanceY y距离
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //滑动时要改变显示区域；
        mRect.offset(0, (int) distanceY);
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight - (int) (mViewHeight / mScale);
        }
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight / mScale);
        }
        invalidate();//失效重新绘制
        return false;
    }

    //第八步，处理惯性问题

    /**
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY 速度，向上和向下不通
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.fling(0, mRect.top, 0, (int) -velocityY,
                0, 0, 0, mImageHeight - (int) (mViewHeight / mScale));
        return false;
    }

    //第九步，处理惯性计算结果
    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) {
            return;
        }
        //返回true 惯性滑动还没有结束
        if (mScroller.computeScrollOffset()) {
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top + (int) (mViewHeight / mScale);
            invalidate();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}