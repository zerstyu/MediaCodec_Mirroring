package com.example.mediacodec_client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	// Static
	static long bodysize2 = 0;
	static byte[] resBytes;
	
	// Class
	private VideoDecoderThread mVideoDecoder;

	// Widget
	TextView textResponse, mtextView1;
	EditText editTextAddress, editTextPort;
	Button buttonConnect;
	
	// Variable
	public int bodysize, bodysize1;
	public int width = 0, height = 0;
	public int flag = 0;
	public long temp = 0, fps = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Display display = getWindowManager().getDefaultDisplay();

		width = display.getWidth();
		height = display.getHeight();

		editTextAddress = (EditText) findViewById(R.id.address);
		editTextPort = (EditText) findViewById(R.id.port);
		buttonConnect = (Button) findViewById(R.id.connect);
		textResponse = (TextView) findViewById(R.id.response);

		mtextView1 = (TextView) findViewById(R.id.textView1);

		buttonConnect.setOnClickListener(buttonConnectOnClickListener);

		editTextAddress.setText("172.20.10.4");
		editTextPort.setText("8260");

		SurfaceViewSet();

		mVideoDecoder = new VideoDecoderThread();

		Timer timeTimer = new Timer();
		timeTimer.schedule(timeTimerTask, 30, 700);

	}

	TimerTask timeTimerTask = new TimerTask() {
		public void run() {
			Handler timeHandler = mtextView1.getHandler();
			timeHandler.post(new Runnable() {
				public void run() {
					String str = "#00FF00";
					mtextView1.setTextColor(Color.parseColor(str));
					mtextView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23.5f);
					mtextView1.setText("FPS ==> " + fps);
				}
			});
		}
	};

	OnClickListener buttonConnectOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			MyClientTask myClientTask = new MyClientTask(editTextAddress
					.getText().toString(), Integer.parseInt(editTextPort
					.getText().toString()));
			myClientTask.execute();
		}

	};

	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (mVideoDecoder != null) {
			if (mVideoDecoder.init(holder.getSurface())) {

			} else {
				mVideoDecoder = null;
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mVideoDecoder != null) {
			mVideoDecoder.close();
		}
	}
	
	public void SurfaceViewSet(){
		SurfaceView surfaceView = new SurfaceView(this);
		surfaceView.getHolder().addCallback(this);
		addContentView(surfaceView, new FrameLayout.LayoutParams(921, 1536,
				Gravity.BOTTOM));
	}

}
