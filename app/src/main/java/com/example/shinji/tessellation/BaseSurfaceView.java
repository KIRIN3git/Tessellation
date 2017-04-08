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
	final static int SQUARE_AB_NUM = SQUARE_NUM * 2;
	// 色の塗りつぶし確認
	int color_check[][];

	// ひとつ前の塗りつぶし座標
	int before_check_a = -1;
	int before_check_b = -1;

	// 矩形の一辺の長さ
//	final static int SQUARE_LENGTH = 100;
	final static int SQUARE_LENGTH = 50;
	// 移動マーカーの半径
//	final static int DIRECTION_RADIUS = 80;
	final static int DIRECTION_RADIUS = 40;
	// プレイヤーの半径
//	final static int PLAYER_RADIUS = 40;
	final static int PLAYER_RADIUS = 20;
	// プレイヤーのスピード
//	final static int PLAYER_SPEED = 10;
	final static int PLAYER_SPEED = 5;

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

		color_check = new int[SQUARE_NUM*2+1][SQUARE_NUM*2+1];
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

		// ポイントが閉じたかのフラグ
		boolean close_flg;

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

						//color_check[i][j] == 1 &&
						// 枠内に中心点が入ったら
						if( color_check[i+SQUARE_NUM][j+SQUARE_NUM] == 1 || ( ( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x < center_x
								&& center_x < ( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x
								&& ( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y < center_y
								&& center_y < ( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y ) ){

							paint.setColor(Color.argb(255, 255, 0, 0));
							paint.setStrokeWidth(8);
							paint.setStyle(Paint.Style.FILL);
							canvas.drawRect(
									( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
									( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
									( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
									( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
									paint);

							// 未マークポジションなら
							if( color_check[i+SQUARE_NUM][j+SQUARE_NUM] != 1 ){
								// 色を記録
								color_check[i+SQUARE_NUM][j+SQUARE_NUM] = 1;

								CheckClose(i+SQUARE_NUM,j+SQUARE_NUM,canvas);


								before_check_a = i+SQUARE_NUM;
								before_check_b = j+SQUARE_NUM;

							}




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

	public void CheckClose(int a,int b,Canvas canvas){

		boolean tojita_flg = false;
		// 前の塗りつぶしがなければエラー
		if( before_check_a == -1 || before_check_b == -1 ) return;




		Log.w( "DEBUG_DATA2", "a " + a );
		Log.w( "DEBUG_DATA2", "b " + b );
		Log.w( "DEBUG_DATA2", "before_check_a " + before_check_a );
		Log.w( "DEBUG_DATA2", "before_check_b " + before_check_b );

		// PLAYARが上端じゃなくて、上のマスがBEFOREじゃなく埋まっていたら、閉じた可能性あり
		if( a != 0 ){
			Log.w( "DEBUG_DATA2", "color_check[a-1][b] " + color_check[a-1][b] );
			if( color_check[a-1][b] == 1 && !( a-1 == before_check_a && b == before_check_b ) ){
				tojita_flg = true;

				Log.w( "DEBUG_DATA2", "TRUEEEEEEEEEE1"  );
			}
		}
		if( b != 0 ){
			Log.w( "DEBUG_DATA2", "color_check[a][b-1] " + color_check[a][b-1] );
			if( color_check[a][b-1] == 1 && !( a == before_check_a && b-1 == before_check_b ) ){
				tojita_flg = true;

				Log.w( "DEBUG_DATA2", "TRUEEEEEEEEEE2"  );
			}
		}
		if( a != SQUARE_AB_NUM ){
			Log.w( "DEBUG_DATA2", "color_check[a+1][b] " + color_check[a+1][b] );
			if( color_check[a+1][b] == 1 && !( a+1 == before_check_a && b == before_check_b ) ){
				tojita_flg = true;

				Log.w( "DEBUG_DATA2", "TRUEEEEEEEEEE3"  );
			}
		}
		if( b != SQUARE_AB_NUM ){
			Log.w( "DEBUG_DATA2", "color_check[a][b+1] " + color_check[a][b+1] );
			if( color_check[a][b+1] == 1 && !( a == before_check_a && b+1 == before_check_b ) ){
				tojita_flg = true;

				Log.w( "DEBUG_DATA2", "TRUEEEEEEEEEE4"  );
			}
		}

		if(!tojita_flg) return;

		boolean kanzenni_tojita = false;
		// 左が開いていたら、閉じているか確認
		if( a != 0 && color_check[a-1][b] == 0 ) {
			color_check[a-1][b] = 2;
			Log.w( "DEBUG_DATA333", "I AM 2 a" + a  );
			Log.w( "DEBUG_DATA333", "I AM 2 b" + b  );
			// 完全に閉じているかチェック、閉じてる範囲を3に書き換え
			kanzenni_tojita = ZanteiCheck(a-1,b);

			Log.w( "DEBUG_DATA", "kanzenni_tojita　" + kanzenni_tojita);
			SetColorDesu(kanzenni_tojita,canvas);




		}

	}


	public void SetColorDesu(boolean mode,Canvas canvas){

		int i;
		int j;

		Log.w( "DEBUG_DATA", "");

		Paint paint = new Paint();

		for( i = -SQUARE_NUM; i <= SQUARE_NUM; i++ ){
			for( j = -SQUARE_NUM; j <= SQUARE_NUM; j++ ){
Log.w( "DEBUG_DATA", "color_check[i+SQUARE_NUM][j+SQUARE_NUM] " + color_check[i+SQUARE_NUM][j+SQUARE_NUM] );
				if( color_check[i+SQUARE_NUM][j+SQUARE_NUM] == 3 ){
					if( mode == true){
						Log.w( "DEBUG_DATA33", "i " + i);
						Log.w( "DEBUG_DATA33", "j " + j);

						paint.setColor(Color.argb(255, 255, 0, 0));
						Log.w( "DEBUG_DATA33", "aaaaaaaaaaaaaaaaaa1");
						paint.setStrokeWidth(8);
						Log.w( "DEBUG_DATA33", "aaaaaaaaaaaaaaaaaa2");
						paint.setStyle(Paint.Style.FILL);
						Log.w( "DEBUG_DATA33", "aaaaaaaaaaaaaaaaaa3 center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x " + (center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x);
						canvas.drawRect(
								( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
								( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
								( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x,
								( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * j) + move_y,
								paint);

						Log.w( "DEBUG_DATA33", "aaaaaaaaaaaaaaaaaa4");

					}
					// 0に戻しておく
					else{
						color_check[i+SQUARE_NUM][j+SQUARE_NUM] = 0;
					}
				}

			}
		}
	}


	public boolean ZanteiCheck(int check_a,int check_b){
		Log.w( "DEBUG_DATA3", "ZanteiCheck");
		boolean roop_flg = true;
		boolean kanzenni_tojiteru = true;
		boolean kensaku_taisyou_mada_ari_flg = false;
		boolean dassyutu_flg = false;
		int a,b;
		while(roop_flg){
			roop_flg = false;
			Log.w( "DEBUG_DATA3", "ROOP");
			for( a = 0; a <= SQUARE_AB_NUM; a++ ) {
				for (b = 0; b <= SQUARE_AB_NUM; b++) {
					Log.w( "DEBUG_DATA3", "a " + a);
					Log.w( "DEBUG_DATA3", "b " + b);
					if( color_check[a][b] == 2 ){
						roop_flg = true;
						Log.w( "DEBUG_DATA3", "TOOTTAAAA");
						kensaku_taisyou_mada_ari_flg = true;
						// 検索対象の左が0番だったら検索対象に追加
						if( a != 0 ){
							Log.w( "DEBUG_DATA3", "1");
							if( color_check[a-1][b] == 0 ) color_check[a-1][b] = 2;
						}
						if( b != 0 ){
							Log.w( "DEBUG_DATA3", "2");
							if( color_check[a][b-1] == 0 ) color_check[a][b-1] = 2;
						}
						if( a != SQUARE_AB_NUM ){
							Log.w( "DEBUG_DATA3", "3");
							if( color_check[a+1][b] == 0 ) color_check[a+1][b] = 2;
						}
						if( b != SQUARE_AB_NUM ){
							Log.w( "DEBUG_DATA3", "4");
							if( color_check[a][b+1] == 0 ) color_check[a][b+1] = 2;
						}
						// チェック済み
						Log.w( "DEBUG_DATA3", "I AM 3 a" + a);
						Log.w( "DEBUG_DATA3", "I AM 3 b" + b);
						color_check[a][b] = 3;
						Log.w( "DEBUG_DATA3", "aaa1");

						// 検索対象が画面端に来たら、囲まれていない
						if( a == 0 || b == 0 || a == SQUARE_AB_NUM || b == SQUARE_AB_NUM ){
							Log.w( "DEBUG_DATA3", "ERRRRRRRRRRRRRRRRRRRRRR");
							roop_flg = false;
							kanzenni_tojiteru = false;
							dassyutu_flg = true;
							break;
						}
						Log.w( "DEBUG_DATA3", "aaa2");
					}
				}
				if(dassyutu_flg) break;
			}
			Log.w( "DEBUG_DATA3", "aaa3");
			// もう検索対象なし、囲まれていた
			if(kanzenni_tojiteru && kensaku_taisyou_mada_ari_flg == false){
				Log.w( "DEBUG_DATA3", "OKKKKKKKKKKKKKKKKKKKKKKKK");
				roop_flg = false;
			}
			Log.w( "DEBUG_DATA3", "aaa4");
		}

		return kanzenni_tojiteru;
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

