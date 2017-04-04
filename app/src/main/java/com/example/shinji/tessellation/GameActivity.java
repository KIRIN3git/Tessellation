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


public class GameActivity extends AppCompatActivity{

	private static final int StrokeWidth = 100;
	private static final int StrokeHarfWidth = 50;

	// Canvas 中心点
	private float center_x = 0.0f;
	private float center_y = 0.0f;

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
		// オブジェクトの移動位置
		int move_x = 0,move_y = 0;

		// 現在タッチしている位置
		int now_touch_x = 0,now_touch_y = 0;

		// タッチした位置をセーブ
		int save_touch_x = 0,save_touch_y = 0;

		// 最低限必要な移動範囲
		int minimum_range = 10;

		// 現在タッチ中かのフラグ
		boolean touch_flg = false;



		// オブジェクトの移動速度
		int speed = 4;

		public MainView(Context context) {
			super(context);
			paint = new Paint();
		}

		@Override
		protected void onDraw(Canvas canvas) {

			int i,j;
			int xy[] = {0,0};

			// 背景
			canvas.drawColor(Color.argb(0, 0, 0, 0));

			// Canvas 中心点
			center_x = canvas.getWidth()/2;
			center_y = canvas.getHeight()/2;

			Log.w( "DEBUG_DATA", "canvas.getWidth() " + String.valueOf(canvas.getWidth()));
			Log.w( "DEBUG_DATA", "canvas.getHeight() " + String.valueOf(canvas.getHeight()));

			// 矩形
			if(fill_x != -999 && fill_y != -999){
				paint.setColor(Color.argb(255, r, g, b));
				paint.setStrokeWidth(4);
				paint.setStyle(Paint.Style.FILL);
				i = fill_x;
				j = fill_y;
				canvas.drawRect(
						center_x - StrokeHarfWidth + (StrokeWidth * i) + move_x,
						center_y - StrokeHarfWidth + (StrokeWidth * j) + move_y,
						center_x + StrokeHarfWidth + (StrokeWidth * i) + move_x,
						center_y + StrokeHarfWidth + (StrokeWidth * j) + move_y,
						paint);

			}

			paint.setColor(Color.argb(255, 0, 0, 255));
			paint.setStrokeWidth(4);
			paint.setStyle(Paint.Style.STROKE);

			// 基本グリッド
			for(i = -20; i <= 20; i++){
				for(j = -20; j <= 20; j++){
					// 縦横に10個ずつ
					// (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)
					canvas.drawRect(
							center_x - StrokeHarfWidth + (StrokeWidth * i) + move_x,
							center_y - StrokeHarfWidth + (StrokeWidth * j) + move_y,
							center_x + StrokeHarfWidth + (StrokeWidth * i) + move_x,
							center_y + StrokeHarfWidth + (StrokeWidth * j) + move_y,
							paint);
				}
			}

			if(touch_flg){
				// セーブタップ位置に〇を表示
				paint.setColor(Color.argb(120, 188, 200, 219)); // 水浅葱
				paint.setStrokeWidth(20);
				paint.setStyle(Paint.Style.STROKE);
				paint.setAntiAlias(true);
				canvas.drawCircle(save_touch_x, save_touch_y, 80, paint);


				// セーブタップ位置を中心にタップ〇移動範囲を表示
				paint.setColor(Color.argb(120, 188, 200, 219)); // 水浅葱
				paint.setStrokeWidth(20);
				paint.setStyle(Paint.Style.STROKE);
				paint.setAntiAlias(true);
				canvas.drawCircle(save_touch_x, save_touch_y, 240, paint);


				// 移動方向に〇を表示
				paint.setColor(Color.argb(120, 235, 121, 136)); // 水浅葱
				paint.setStrokeWidth(20);
				paint.setStyle(Paint.Style.STROKE);
				paint.setAntiAlias(true);

				naviXY(save_touch_x,save_touch_y,now_touch_x,now_touch_y,xy);


				canvas.drawCircle(xy[0], xy[1], 80, paint);
//				canvas.drawCircle(save_touch_x + 80, save_touch_y, 80, paint);


				Log.w( "DEBUG_DATA", "CENTER save_touch_x " + save_touch_x );
				Log.w( "DEBUG_DATA", "CENTER save_touch_y " + save_touch_y );
				Log.w( "DEBUG_DATA", "CENTER xy[0] " + xy[0] );
				Log.w( "DEBUG_DATA", "CENTER xy[1] " + xy[1] );
			}
		}
		public void naviXY(int save_touch_x,int save_touch_y,int now_touch_x,int now_touch_y,int[] xy){
			boolean positive_x = true,positive_y = true;
			double sa_x = 0,sa_y = 0;
			double ratio = 0;

			if( save_touch_x > now_touch_x ){
				positive_x = false;
			}
			if( save_touch_y > now_touch_y ){
				positive_y = false;
			}

			sa_x = abs(save_touch_x - now_touch_x);
			sa_y = abs(save_touch_y - now_touch_y);


			Log.w( "DEBUG_DATA", "save_touch_x " + save_touch_x  );
			Log.w( "DEBUG_DATA", "save_touch_y " + save_touch_y  );
			Log.w( "DEBUG_DATA", "now_touch_x " + now_touch_x  );
			Log.w( "DEBUG_DATA", "now_touch_y " + now_touch_y  );

			Log.w( "DEBUG_DATA", "sa_x " + sa_x  );
			Log.w( "DEBUG_DATA", "sa_y " + sa_y  );

			ratio = sqrt( pow(160,2) / ( pow(sa_x,2) + pow(sa_y,2) ) );

			Log.w( "DEBUG_DATA", "pow(160,2) " + pow(160,2)  );
			Log.w( "DEBUG_DATA", "pow(sa_x,2) " + pow(sa_x,2) );
			Log.w( "DEBUG_DATA", "pow(sa_y,2) " + pow(sa_y,2) );
			Log.w( "DEBUG_DATA", "ratio " + ratio  );

			if( positive_x ) xy[0] = save_touch_x + (int)round(sa_x * ratio);
			else xy[0] = save_touch_x - (int)round(sa_x * ratio);

			if( positive_y ) xy[1] = save_touch_y + (int)round(sa_y * ratio);
			else xy[1] = save_touch_y - (int)round(sa_y * ratio);

			Log.w( "DEBUG_DATA", "(int)round(sa_x * ratio) " + (int)round(sa_x * ratio)  );
			Log.w( "DEBUG_DATA", "(int)round(sa_y * ratio) " + (int)round(sa_y * ratio)  );

			Log.w( "DEBUG_DATA", "xy[0] " + xy[0]  );
			Log.w( "DEBUG_DATA", "xy[1] " + xy[1]  );

			Log.w( "DEBUG_DATA", "結果 " + ( pow(xy[0],2) + pow(xy[1],2) )  );

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

					Log.w( "DEBUG_DATA","DOWN" );
					break;
				case MotionEvent.ACTION_UP:

					touch_flg = false;

					break;
			}

			Log.w( "DEBUG_DATA", "tauch x " + now_touch_x );
			Log.w( "DEBUG_DATA", "tauch y " + now_touch_y );

			move_x+=speed;
			move_y+=speed;

			// 再描画の指示
			invalidate();

			return true;
		}
	}
}