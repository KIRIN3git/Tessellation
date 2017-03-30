package com.example.shinji.tessellation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn = (Button)findViewById(R.id.game_start);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				// メイン画面の起動
				startActivity(intent);
			}
		});
	}
}
