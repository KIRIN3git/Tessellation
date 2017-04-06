package com.example.shinji.tessellation;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;


public class GameSubActivity extends AppCompatActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainView mainView = new MainView(this);
		setContentView(mainView);
	}

	class MainView extends View {
		Paint paint;
		int r = 0,g = 0,b = 255;
		int fill_x = -999,fill_y = -999;

		// 矩形の上下左右の数（総数は SQUARE_NUM * 2 + 1の2乗）
		private static final int SQUARE_NUM = 40;

		// 矩形の一辺の長さ
		private static final int SQUARE_LENGTH = 100;

		// 移動マーカーの半径
		public static final int DIRECTION_RADIUS = 80;

		// Canvas 中心点
		private float center_x = 0.0f;
		private float center_y = 0.0f;

		// オブジェクトの移動位置
		int move_x = 0,move_y = 0;

		// 現在タッチしている位置
		int now_touch_x = 0,now_touch_y = 0;

		// セーブしたタッチ位置
		int save_touch_x = 0,save_touch_y = 0;

		// 最低限必要な移動範囲
		int minimum_range = 10;

		// 初回表示フラグ
		boolean initial_flg = true;
		// 現在タッチ中かのフラグ
		boolean touch_flg = false;

		boolean save_flg = false;

		// オブジェクトの移動速度
		int speed = 4;

		public MainView(Context context) {
			super(context);
			paint = new Paint();
		}

		@Override
		protected void onDraw(Canvas canvas) {

			int i,j;

			// セーブ位置と指示器の差分
			int indicatorDiff[] = {0,0};

			// 指示器の位置
			int indicatorXY[] = {0,0};

			// 背景
			canvas.drawColor(Color.argb(0, 0, 0, 0));

			// Canvas 中心点
			center_x = canvas.getWidth()/2;
			center_y = canvas.getHeight()/2;

			//Log.w( "DEBUG_DATA", "canvas.getWidth() " + String.valueOf(canvas.getWidth()));
			//Log.w( "DEBUG_DATA", "canvas.getHeight() " + String.valueOf(canvas.getHeight()));


				if( touch_flg ){
					// タップ移動比率xyと指示マーカーのxyを取得
					getIndicatorXY(save_touch_x, save_touch_y, now_touch_x, now_touch_y, indicatorDiff, indicatorXY);
					//Log.w( "DEBUG_DATA", "indicatorDiff[0] " + indicatorDiff[0] );
					//Log.w( "DEBUG_DATA", "indicatorDiff[1] " + indicatorDiff[1] );
					move_x = move_x - (indicatorDiff[0] / 5);
					move_y = move_y - (indicatorDiff[1] / 5);
					//Log.w( "DEBUG_DATA", "move_x " + move_x );
					//Log.w( "DEBUG_DATA", "move_y " + move_y );
				}
				canvas.translate(move_x, move_y);


				Log.w( "DEBUG_DATA", "RESTOREEEEEEEEEEEEEEE" );




				// 矩形
				if( fill_x != -999 && fill_y != -999 ){
					paint.setColor(Color.argb(255, r, g, b));
					paint.setStrokeWidth(4);
					paint.setStyle(Paint.Style.FILL);
					i = fill_x;
					j = fill_y;

					//				canvas.drawRect(
					//						center_x - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i) + move_x,
					//						center_y - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j) + move_y,
					//						center_x + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i) + move_x,
					//						center_y + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j) + move_y,
					//						paint);
					canvas.drawRect(
							center_x - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i),
							center_y - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j),
							center_x + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i),
							center_y + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j),
							paint);
				}

				paint.setColor(Color.argb(255, 0, 0, 255));
				paint.setStrokeWidth(4);
				paint.setStyle(Paint.Style.STROKE);

				initial_flg = true;
				// 初回表示
				if( initial_flg == true ){
					initial_flg = false;
					// 基本グリッド
					for( i = -SQUARE_NUM; i <= SQUARE_NUM; i++ ){
						for( j = -SQUARE_NUM; j <= SQUARE_NUM; j++ ){
							// 縦横に10個ずつ
							// (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)
							//					canvas.drawRect(
							//							center_x - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i) + move_x,
							//							center_y - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j) + move_y,
							//							center_x + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i) + move_x,
							//							center_y + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j) + move_y,
							//							paint);
							canvas.drawRect(
									center_x - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i),
									center_y - (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j),
									center_x + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * i),
									center_y + (SQUARE_LENGTH / 2) + (SQUARE_LENGTH * j),
									paint);
						}
					}
				}

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


			Log.w( "DEBUG_DATA", "SAVEEEEEEEEEEEE" );
			save_flg = true;

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

		public boolean onTouchEvent(MotionEvent e){

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

					paint.setStyle(Paint.Style.FILL);

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
	}
}