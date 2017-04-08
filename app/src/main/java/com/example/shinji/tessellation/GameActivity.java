package com.example.shinji.tessellation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shinji on 2017/04/06.
 */

public class GameActivity extends AppCompatActivity{
	BaseSurfaceView surfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		surfaceView = new BaseSurfaceView(this);
		setContentView(surfaceView);
	}
}

