package com.example.mediacodecserver;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class ScreenCapture {
	private ViewTask mViewTask;
	private Bitmap screenshot;
	public Bitmap tempBitmap;

	public void screenshot(Activity av) throws Exception {
		mViewTask = new ViewTask(av);
		mViewTask.setDrawingCacheEnabled(true);
		screenshot = mViewTask.getDrawingCache();
		tempBitmap = screenshot.copy(Config.RGB_565, true);
		mViewTask.setDrawingCacheEnabled(false);
	}

}
