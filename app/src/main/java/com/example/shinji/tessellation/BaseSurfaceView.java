package com.example.shinji.tessellation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

/**
 * Created by shinji on 2017/04/06.
 */

public class BaseSurfaceView extends SurfaceView implements  Runnable,SurfaceHolder.Callback{

	int r = 0,g = 0,b = 255;
	int fill_x = -999,fill_y = -999;

	// 矩形の上下左右の数（総数は SQUARE_NUM * 2 + 1の2乗）
	final static int SQUARE_NUM = 5;

	// 矩形の一辺の長さ
	final static int SQUARE_LENGTH = 100;

	// 移動マーカーの半径
	final static int DIRECTION_RADIUS = 80;

	// プレイヤーの半径
	final static int PLAYER_RADIUS = 40;

	// プレイヤーの半径
	final static int PLAYER_SPEED = 10;

	// プレイヤーの色
	final static int PLAYER_R = 44;
	final static int PLAYER_G = 45;
	final static int PLAYER_B = 21;

	// Base図形RGB
	final static int BASE_R = 188;
	final static int BASE_G = 189;
	final static int BASE_B = 194;

//	final static int SQUARE_NUM = 3;
//
//	// 矩形の一辺の長さ
//	final static int SQUARE_LENGTH = 50;
//
//	// 移動マーカーの半径
//	final static int DIRECTION_RADIUS = 40;

	// Canvas 中心点
	float center_x = 0.0f;
	float center_y = 0.0f;

	// オブジェクトの移動位置
	int move_x = 0,move_y = 0;

	// 現在タッチしている位置
	int now_touch_x = 0,now_touch_y = 0;

	// セーブしたタッチ位置
	int save_touch_x = 0,save_touch_y = 0;

	// 初回表示フラグ
	boolean initial_flg = true;
	// 現在タッチ中かのフラグ
	boolean touch_flg = false;
	// オブジェクトの移動速度
	int speed = 4;
	Paint paint;

	static final long FPS = 20;
	static final long FRAME_TIME = 1000 / FPS;
	static final int BALL_R = 30;
	SurfaceHolder surfaceHolder;
	Thread thread;
	int cx = BALL_R, cy = BALL_R;
	int screen_width, screen_height;

	public BaseSurfaceView(Context context){
		super(context);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	@Override public void run() {

		int i,j;

		// セーブ位置と指示器の差分
		int indicatorDiff[] = {0,0};

		// 指示器の位置
		int indicatorXY[] = {0,0};


		Canvas canvas = null;
		Paint paint = new Paint();
		Paint bgPaint = new Paint();
// Background bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(Color.WHITE);
// Ball paint.setStyle(Style.FILL);
		paint.setColor(Color.BLUE);
		long loopCount = 0;
		long waitTime = 0;
		long startTime = System.currentTimeMillis();

		while(thread != null){
			try{

				loopCount++;
				canvas = surfaceHolder.lockCanvas();
				canvas.drawRect( 0, 0, screen_width, screen_height, bgPaint);

				// Canvas 中心点
				center_x = canvas.getWidth()/2;
				center_y = canvas.getHeight()/2;

				if( touch_flg ){
					// タップ移動比率xyと指示マーカーのxyを取得
					getIndicatorXY(save_touch_x, save_touch_y, now_touch_x, now_touch_y, indicatorDiff, indicatorXY);
					//Log.w( "DEBUG_DATA", "indicatorDiff[0] " + indicatorDiff[0] );
					//Log.w( "DEBUG_DATA", "indicatorDiff[1] " + indicatorDiff[1] );
					move_x = move_x - (indicatorDiff[0] / PLAYER_SPEED);
					move_y = move_y - (indicatorDiff[1] / PLAYER_SPEED);
					//Log.w( "DEBUG_DATA", "move_x " + move_x );
					//Log.w( "DEBUG_DATA", "move_y " + move_y );
				}


				// 基本グリッド
				for( i = -SQUARE_NUM; i <= SQUARE_NUM; i++ ){
					for( j = -SQUARE_NUM; j <= SQUARE_NUM; j++ ){



						paint.setColor(Color.argb(255, BASE_R, BASE_G, BASE_B));
						paint.setStrokeWidth(8);
						paint.setStyle(Paint.Style.STROKE);

						// 縦横に10個ずつ
						// (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)
						canvas.drawRect(
								( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
								( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
								( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
								( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
								paint);

//						canvas.drawRect(
//								center_x - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i),
//								center_y - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j),
//								center_x + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i),
//								center_y + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j),
//								paint);

						if( ( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x < center_x
								&& center_x < ( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x
								&& ( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y < center_y
								&& center_y < ( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y ){

							paint.setColor(Color.argb(255, 255, 0, 0));
							paint.setStrokeWidth(8);
							paint.setStyle(Paint.Style.FILL);
							canvas.drawRect(
									( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
									( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
									( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
									( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
									paint);

							float a = ( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x;
							float b = ( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y;
							float c = ( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x;
							float d = ( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y;

							Log.w( "DEBUG_DATA", "center_x " + center_x );
							Log.w( "DEBUG_DATA", "(SQUARE_LENGTH / 2) " + (SQUARE_LENGTH / 2) );
							Log.w( "DEBUG_DATA", "(SQUARE_LENGTH * i) " + (SQUARE_LENGTH * i) );
							Log.w( "DEBUG_DATA", "move_x " + move_x );

							Log.w( "DEBUG_DATA", "( center_x - (SQUARE_LENGTH / 2) ) " + ( center_x - (SQUARE_LENGTH / 2) ) );
							Log.w( "DEBUG_DATA", "( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) " + ( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) );
							Log.w( "DEBUG_DATA", "a " + a );
							Log.w( "DEBUG_DATA", "b " + b );
							Log.w( "DEBUG_DATA", "c " + c );
							Log.w( "DEBUG_DATA", "d " + d );
						}

					}
				}

				// 中心円の表示
				paint.setColor(Color.argb(255, 0, 0, 255));
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
// (x1,y1,r,paint) 中心x1座標, 中心y1座標, r半径
				canvas.drawCircle(center_x, center_y, PLAYER_RADIUS, paint);



				if( touch_flg ){
					// セーブタップ位置に〇を表示
					paint.setColor(Color.argb(120, 188, 200, 219)); // 水浅葱
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					paint.setAntiAlias(true);
					canvas.drawCircle(save_touch_x, save_touch_y, DIRECTION_RADIUS, paint);

					// セーブタップ位置を中心にタップ〇移動範囲を表示
					paint.setColor(Color.argb(120, 188, 200, 219)); // 水浅葱
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					paint.setAntiAlias(true);
					canvas.drawCircle(save_touch_x, save_touch_y, DIRECTION_RADIUS * 3, paint);

					// 移動方向に〇を表示
					paint.setColor(Color.argb(120, 235, 121, 136)); // 水浅葱
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					paint.setAntiAlias(true);

					canvas.drawCircle(indicatorXY[0], indicatorXY[1], DIRECTION_RADIUS, paint);

					//Log.w( "DEBUG_DATA", "CENTER save_touch_x " + save_touch_x );
					//Log.w( "DEBUG_DATA", "CENTER save_touch_y " + save_touch_y );
					//Log.w( "DEBUG_DATA", "CENTER direXY[0] " + indicatorXY[0] );
					//Log.w( "DEBUG_DATA", "CENTER direXY[1] " + indicatorXY[1] );
				}

				surfaceHolder.unlockCanvasAndPost(canvas);


//				waitTime = (loopCount * FRAME_TIME) - (System.currentTimeMillis() - startTime);
//				if( waitTime > 0 ){
//					Thread.sleep(waitTime);
//				}
			} catch(Exception e){}
		}
	}

	// タッチイベントを処理するためOverrideする
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// タッチしている位置取得
		now_touch_x = (int) e.getX();
		now_touch_y = (int) e.getY();

		switch(e.getAction()){
			// 触る
			case MotionEvent.ACTION_DOWN:
				r = 255;
				g = 0;
				b = 0;
				fill_x = 3;
				fill_y = 5;

				save_touch_x = now_touch_x;
				save_touch_y = now_touch_y;

				touch_flg = true;

//				paint.setStyle(Paint.Style.FILL);

				//Log.w( "DEBUG_DATA","DOWN" );
				break;
			case MotionEvent.ACTION_UP:

				touch_flg = false;

				break;
		}

		Log.w( "DEBUG_DATA", "tauch x " + now_touch_x );
		Log.w( "DEBUG_DATA", "tauch y " + now_touch_y );
		//move_x += 3;
		//move_y += 3;
		// 再描画の指示
		invalidate();

		return true;

	}

	// 変更時に呼び出される
	@Override public void surfaceChanged( SurfaceHolder holder, int format, int width, int height) {
		screen_width = width;
		screen_height = height;

//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(Color.BLUE);
//		paint.setStyle(Style.FILL);
//
//		Canvas canvas = holder.lockCanvas();
//		canvas.drawColor(Color.BLACK);
//		canvas.drawCircle(100, 200, 50, paint);
//		holder.unlockCanvasAndPost(canvas);

	}
	// 作成時に読みだされる
	// この時点で描画準備はできていて、SurfaceHoderのインスタンスを返却する
	@Override public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		thread.start();
	}
	// 破棄時に呼び出される
	@Override public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
	}


	// 指示マーカーの位置を取得
	public void getIndicatorXY(int save_touch_x,int save_touch_y,int now_touch_x,int now_touch_y,int[] indicatorDiff,int[] indicatorXY){
		// 移動方向の正、負
		boolean positive_x = true,positive_y = true;
		// セーブ位置と現在位置の絶対値差分
		double sa_x,sa_y;
		// 絶対値差分と表示位置の比率
		double ratio;

		// 移動方向の正、負を取得
		if( save_touch_x > now_touch_x ){
			positive_x = false;
		}
		if( save_touch_y > now_touch_y ){
			positive_y = false;
		}

		// セーブ位置と現在位置の絶対値差分を取得
		sa_x = abs(save_touch_x - now_touch_x);
		sa_y = abs(save_touch_y - now_touch_y);

		//Log.w( "DEBUG_DATA", "save_touch_x " + save_touch_x  );
		//Log.w( "DEBUG_DATA", "save_touch_y " + save_touch_y  );
		//Log.w( "DEBUG_DATA", "now_touch_x " + now_touch_x  );
		//Log.w( "DEBUG_DATA", "now_touch_y " + now_touch_y  );

		//Log.w( "DEBUG_DATA", "sa_x " + sa_x  );
		//Log.w( "DEBUG_DATA", "sa_y " + sa_y  );

		// 三平方の定理で絶対値差分と表示位置の比率を取得
		ratio = sqrt( pow(DIRECTION_RADIUS * 2,2) / ( pow(sa_x,2) + pow(sa_y,2) ) );

		//Log.w( "DEBUG_DATA", "pow(160,2) " + pow(DIRECTION_RADIUS * 2,2)  );
		//Log.w( "DEBUG_DATA", "pow(sa_x,2) " + pow(sa_x,2) );
		//Log.w( "DEBUG_DATA", "pow(sa_y,2) " + pow(sa_y,2) );
		//Log.w( "DEBUG_DATA", "ratio " + ratio  );

		// 指示マーカーとセーブ位置の差分を取得（四捨五入のため誤差あり）
		if( positive_x ) indicatorDiff[0] = (int)round(sa_x * ratio);
		else indicatorDiff[0] = - (int)round(sa_x * ratio);
		if( positive_y ) indicatorDiff[1] = (int)round(sa_y * ratio);
		else indicatorDiff[1] = - (int)round(sa_y * ratio);

		// 四捨五入して指示マーカーの位置を取得
		indicatorXY[0] = save_touch_x + indicatorDiff[0];
		indicatorXY[1] = save_touch_y + indicatorDiff[1];

		//Log.w( "DEBUG_DATA", "(int)round(sa_x * ratio) " + (int)round(sa_x * ratio)  );
		//Log.w( "DEBUG_DATA", "(int)round(sa_y * ratio) " + (int)round(sa_y * ratio)  );

		//Log.w( "DEBUG_DATA", "indicatorXY[0] " + indicatorXY[0]  );
		//Log.w( "DEBUG_DATA", "indicatorXY[1] " + indicatorXY[1]  );

		//Log.w( "DEBUG_DATA", "結果 " + ( pow(indicatorXY[0],2) + pow(indicatorXY[1],2) )  );

	}
}

