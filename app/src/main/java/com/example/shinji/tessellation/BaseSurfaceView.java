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

	// 四角の縦、横の数
	final static int SQUARE_NUM = 21;
	// 色の塗りつぶし確認
	int square_color[][];

	// スクリーンの大きさ
	int screen_width, screen_height;

	// 現在タッチしている位置
	int now_touch_x = 0,now_touch_y = 0;

	// セーブしたタッチ位置
	int save_touch_x = 0,save_touch_y = 0;

	// Canvas 中心点
	float center_x = 0.0f;
	float center_y = 0.0f;

	// 全体の移動位置
	int move_x = 0,move_y = 0;

	// Base図形RGB
	final static int BASE_R = 188;
	final static int BASE_G = 189;
	final static int BASE_B = 194;

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
	final static int PLAYER_SPEED = 10;

	// プレイヤーの色
	final static int PLAYER_R = 44;
	final static int PLAYER_G = 45;
	final static int PLAYER_B = 21;

	// ひとつ前の塗りつぶし座標
	int before_fill_i = -1;
	int before_fill_j = -1;


	// 現在タッチ中かのフラグ
	boolean touch_flg = false;

	SurfaceHolder surfaceHolder;
	Thread thread;



	public BaseSurfaceView(Context context){
		super(context);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		square_color = new int[SQUARE_NUM][SQUARE_NUM];
	}

	@Override public void run() {

		// グリッドの位置
		int i,j;

		// セーブ位置と指示器の差分
		int indicatorDiff[] = {0,0};

		// 指示器のXY位置
		int indicatorXY[] = {0,0};

		// キャンバスを設定
		Canvas canvas;

		// ペイントを設定
		Paint paint = new Paint();
		Paint bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE);

		while(thread != null){
			try{

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
				for( i = 0; i < SQUARE_NUM; i++ ){
					for( j = 0; j < SQUARE_NUM; j++ ){

						paint.setColor(Color.argb(255, BASE_R, BASE_G, BASE_B));
						paint.setStrokeWidth(8);
						paint.setStyle(Paint.Style.STROKE);

						// 縦横に10個ずつ
						// (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)
						canvas.drawRect(
								( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x,
								( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y,
								( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x,
								( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y,
								paint);


						// すでにペイント済み、枠内に中心点が入ったら
						if( square_color[i][j] == 1
								|| ( ( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x < center_x
								&& center_x < ( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x
								&& ( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y < center_y
								&& center_y < ( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y ) ){

							// 色を塗る
							paint.setColor(Color.argb(255, 255, 0, 0));
							paint.setStrokeWidth(8);
							paint.setStyle(Paint.Style.FILL);
							canvas.drawRect(
									( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x,
									( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y,
									( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x,
									( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y,
									paint);

							// 新規塗りだったら
							if( square_color[i][j] != 1 ){
								// 色を記録
								square_color[i][j] = 1;

								// 囲まれていたら色を塗る
								CheckCloseAndFill(i,j,canvas);

								before_fill_i = i;
								before_fill_j = j;

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

				// 描画
				surfaceHolder.unlockCanvasAndPost(canvas);

			} catch(Exception e){}
		}
	}

	public void CheckCloseAndFill(int i,int j,Canvas canvas){

		boolean tabun_close_flg = false;
		boolean kakujituni_close_flg = false;

		// 前の塗りつぶしがなければエラー
		if( before_fill_i == -1 || before_fill_j == -1 ) return;

		Log.w( "CheckCloseAndFill", "i " + i );
		Log.w( "CheckCloseAndFill", "j " + j );
		Log.w( "CheckCloseAndFill", "before_fill_i " + before_fill_i );
		Log.w( "CheckCloseAndFill", "before_fill_j " + before_fill_j );

		// PLAYARが上端じゃなくて、上のマスがBEFOREじゃなく埋まっていたら、閉じた可能性あり
		if( i != 0 ){
			Log.w( "CheckCloseAndFill", "square_color[i-1][j] " + square_color[i-1][j] );
			if( square_color[i-1][j] == 1 && !( i-1 == before_fill_i && j == before_fill_j ) ){
				tabun_close_flg = true;

				Log.w( "CheckCloseAndFill", "TRUEEEEEEEEEE1"  );
			}
			Log.w( "CheckCloseAndFill", "square_color[i-1][j] " + square_color[i-1][j] );
		}
		if( j != 0 ){
			Log.w( "CheckCloseAndFill", "square_color[i][j-1] " + square_color[i][j-1] );
			if( square_color[i][j-1] == 1 && !( i == before_fill_i && j-1 == before_fill_j ) ){
				tabun_close_flg = true;

				Log.w( "CheckCloseAndFill", "TRUEEEEEEEEEE2"  );
			}
			Log.w( "CheckCloseAndFill", "square_color[i][j-1] " + square_color[i][j-1] );
		}
		if( i != SQUARE_NUM - 1 ){
			Log.w( "CheckCloseAndFill", "square_color[i+1][j] " + square_color[i+1][j] );
			if( square_color[i+1][j] == 1 && !( i+1 == before_fill_i && j == before_fill_j ) ){
				tabun_close_flg = true;

				Log.w( "CheckCloseAndFill", "TRUEEEEEEEEEE3"  );
			}
			Log.w( "CheckCloseAndFill", "square_color[i+1][j] " + square_color[i+1][j] );
		}
		if( j != SQUARE_NUM - 1 ){
			Log.w( "CheckCloseAndFill", "square_color[i][j+1] " + square_color[i][j+1] );
			if( square_color[i][j+1] == 1 && !( i == before_fill_i && j+1 == before_fill_j ) ){
				tabun_close_flg = true;

				Log.w( "CheckCloseAndFill", "TRUEEEEEEEEEE4"  );
			}
			Log.w( "CheckCloseAndFill", "square_color[i][j+1] " + square_color[i][j+1] );
		}

		Log.w( "CheckCloseAndFill", "ssssssssssssssssssss1 " );

		if(!tabun_close_flg) return;

		Log.w( "CheckCloseAndFill", "ssssssssssssssssssss2 " );

		// 左が開いていたら、閉じているか確認
		if( i != 0 && square_color[i-1][j] == 0 ) {
			square_color[i-1][j] = 2;
			Log.w( "CheckCloseAndFill", "I AM 2 i" + i  );
			Log.w( "CheckCloseAndFill", "I AM 2 j" + j  );
			// 完全に閉じているかチェック、閉じてる範囲を3に書き換え
			kakujituni_close_flg = CheckCloseComp(i-1,j);

			Log.w( "CheckCloseAndFill", "kanzenni_tojita　" + kakujituni_close_flg);
			// 閉じられているところを塗る
			FillClose(kakujituni_close_flg,canvas);
		}


	}

	// 完全に閉じらているか確認し、３番をセットする
	public boolean CheckCloseComp(int check_i,int check_j){
		Log.w( "CheckCloseComp", "CheckCloseFull");
		// ループフラグ
		boolean roop_flg = true;
		// 停止フラグ
		boolean stop_flg = true;
		// コンプリートフラグ
		boolean comp_flg = false;
		// 検索対象データが１つでもあったフラグ
		boolean data_flg = false;


		int i,j;

		while(roop_flg){
			Log.w( "CheckCloseComp", "ROOP");

			data_flg = false;
			for( i = 0; i < SQUARE_NUM; i++ ) {
				for (j = 0; j < SQUARE_NUM; j++) {
					Log.w( "CheckCloseComp", "i " + i);
					Log.w( "CheckCloseComp", "j " + j);
					if( square_color[i][j] == 2 ){
						//１個でも2があれば、再検索するよ
						comp_flg = true;
						data_flg = true;

						Log.w( "CheckCloseComp", "TOOTTAAAA");

						// 検索対象の左が0番だったら検索対象に追加
						if( i != 0 ){
							Log.w( "CheckCloseComp", "1");
							if( square_color[i-1][j] == 0 ) square_color[i-1][j] = 2;
						}
						if( j != 0 ){
							Log.w( "CheckCloseComp", "2");
							if( square_color[i][j-1] == 0 ) square_color[i][j-1] = 2;
						}
						if( i != ( SQUARE_NUM - 1 ) ){
							Log.w( "CheckCloseComp", "3");
							if( square_color[i+1][j] == 0 ) square_color[i+1][j] = 2;
						}
						if( j != ( SQUARE_NUM - 1 ) ){
							Log.w( "CheckCloseComp", "4");
							if( square_color[i][j+1] == 0 ) square_color[i][j+1] = 2;
						}
						// チェック済み
						Log.w( "CheckCloseComp", "I AM 3 i" + i);
						Log.w( "CheckCloseComp", "I AM 3 j" + j);
						square_color[i][j] = 3;
						Log.w( "CheckCloseComp", "aaa1");

						// 検索対象が画面端に来たら、囲まれていない
						if( i == 0 || j == 0 || i == ( SQUARE_NUM - 1 ) || j == ( SQUARE_NUM - 1 ) ){
							Log.w( "CheckCloseComp", "ERRRRRRRRRRRRRRRRRRRRRR");
							roop_flg = false;
							stop_flg = false;
							comp_flg = false;

							break;
						}
						Log.w( "CheckCloseComp", "aaa2");
					}
				}
				if(!stop_flg) break;
			}
			Log.w( "CheckCloseComp", "aaa3");

			if( !data_flg || !stop_flg ){
				roop_flg = false;
			}

		}

		return comp_flg;
	}

	//mode 0:閉じられていない、1:閉じられている
	public void FillClose(boolean mode,Canvas canvas){

		int i,j;

		Log.w( "FillClose", "");

		Paint paint = new Paint();

		for( i = 0; i < SQUARE_NUM; i++ ){
			for( j = 0; j < SQUARE_NUM; j++ ){
				if( square_color[i][j] == 3 ){
					if( mode == true){
						Log.w( "FillClose", "i " + i);
						Log.w( "FillClose", "j " + j);

						paint.setColor(Color.argb(255, 255, 0, 0));
						Log.w( "FillClose", "aaaaaaaaaaaaaaaaaa1");
						paint.setStrokeWidth(8);
						Log.w( "FillClose", "aaaaaaaaaaaaaaaaaa2");
						paint.setStyle(Paint.Style.FILL);
						Log.w( "FillClose", "aaaaaaaaaaaaaaaaaa3 center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x " + (center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * i) + move_x);
						canvas.drawRect(
								( center_x - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x,
								( center_y - (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y,
								( center_x + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( i - ( SQUARE_NUM / 2 ) ) ) + move_x,
								( center_y + (SQUARE_LENGTH / 2) ) + (SQUARE_LENGTH * ( j - ( SQUARE_NUM / 2 ) ) ) + move_y,
								paint);

						// 色を記録
						square_color[i][j] = 1;

						Log.w( "FillClose", "aaaaaaaaaaaaaaaaaa4");

					}
					// 0に戻しておく
					else{
						square_color[i][j] = 0;
					}
				}
			}
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

