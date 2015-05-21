package com.example.mediacodecserver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class ViewTask extends View {

	static int flag = 0;

	public ViewTask(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ViewTask(MainActivity context) {
		// TODO Auto-generated constructor stub
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);

	}

}
