package com.qgnix.main.ixxo.dot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;


import com.qgnix.main.ixxo.view.Animation;

import java.util.Random;
import java.util.Vector;

public class DotAnimFW extends Dot {

	Animation mFWAnim = null;

	public DotAnimFW(Context context, int color, int endX, int endY) {
		super(context, color, endX, endY);
		// TODO Auto-generated constructor stub
		new loadAnim().start();
	}

	class loadAnim extends Thread {

		public void run() {
			Random random = new Random();
			switch (random.nextInt(5)) {
			case 0:
				mFWAnim = new Animation(mContext, new int[] { },
						false);
				break;

			case 1:
				mFWAnim = new Animation(mContext,
						new int[] {  }, false);
				break;

			case 2:
				mFWAnim = new Animation(mContext, new int[] {
						}, false);
				break;

			case 3:
				mFWAnim = new Animation(mContext,
						new int[] {}, false);
				break;

			case 4:
				mFWAnim = new Animation(mContext,
						new int[] { }, false);

			default:
				mFWAnim = new Animation(mContext,
						new int[] { }, false);
				break;
			}

		}
	}

	@Override
	public LittleDot[] initBlast() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LittleDot[] blast() {
		// TODO Auto-generated method stub
		return null;
	}

	public void myPaint(Canvas canvas, Vector<Dot> lList) {
		Paint mPaint = new Paint();
		mPaint.setColor(color);
		if (state == 1) {
			if (mFireAnim != null) {
				mFireAnim.DrawAnimation(canvas, null, x, y);
			}

		}
		if (state == 2) {
			state = 3;

		} else if (state == 3) {
			if (mFWAnim != null) {
				if (mFWAnim.ismIsend() == false) {
					mFWAnim.DrawAnimation(canvas, null, x, y);
				} else {
					circle = 100;
				}
			}
		} else if (state == 4) {
			synchronized (lList) {
				lList.remove(this);
			}
		}

	}

}
