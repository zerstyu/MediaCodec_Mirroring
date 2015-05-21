package com.example.mediacodecserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import com.example.mediacodecmirroring.R;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	// Class
	private ViewTask mViewTask;
	private ViewUpdateThread mViewUpdateThread;
	private IpAddress mIpAddress;
	private VideoEncoderCore mVideoEncoderCore;
	private ConvertFormat mConvertFormat;
	private ScreenCapture mScreenCapture;
	private StopEncoder mStopEncoder;

	// Widget
	private TextView infoip;
	private Button SendButton, StopButton;

	// Variable
	static long temp = 0;
	static int flag1 = 0;
	static int width = 0, height = 0;

	public int check = 0;
	public int flag = 0;
	public byte[] tempArray = null;
	
	private String filesize = null;
	
	public ServerSocket serverSocket;
	public DataOutputStream dos;

	final Handler myhandler = new Handler();

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

		infoip = (TextView) findViewById(R.id.infoip);

		SendButton = (Button) findViewById(R.id.sendbutton);
		SendButton.setOnClickListener(SendButtonOnClickListener);

		StopButton = (Button) findViewById(R.id.stopbutton);
		StopButton.setOnClickListener(StopButtonOnClickListener);

		mIpAddress = new IpAddress();
		infoip.setText(mIpAddress.getIpAddress());

		mConvertFormat = new ConvertFormat();
		mScreenCapture = new ScreenCapture();
		mStopEncoder = new StopEncoder();

		mViewTask = new ViewTask(this);
		addContentView(mViewTask, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));

		VideoEncoderSet();

		Timer myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				UpdateGUI();
			}
		}, 0, 150);
	}

	private void UpdateGUI() {
		myhandler.post(myRunnable);
	}

	final Runnable myRunnable = new Runnable() {
		public void run() {
			ScreenCapture();

			if (check == 1) {
				tempArray = mConvertFormat.getNV21(width, height,
						mScreenCapture.tempBitmap);

				offerDataToEncoder();

				if (VideoEncoderCore.b != null) {
					SendDataToClient();
				}
			}

			if (flag == 0) {
				mViewUpdateThread = new ViewUpdateThread();
				mViewUpdateThread.start();
				flag = 1;
			}

			mViewTask.postInvalidate();

		}
	};

	OnClickListener SendButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			check = 1;
			VideoEncoderSet();
			flag1 = 1;
		}
	};

	OnClickListener StopButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			mStopEncoder.EncoderStop();
			check = 2;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void VideoEncoderSet() {
		try {
			mVideoEncoderCore = new VideoEncoderCore(width, height, 128000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ScreenCapture() {
		try {
			mScreenCapture.screenshot(MainActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void offerDataToEncoder() {
		mVideoEncoderCore.drainEncoder(false);
		mVideoEncoderCore.offer(tempArray);
	}

	public void SendDataToClient() {
		try {
			filesize = String.valueOf(VideoEncoderCore.b.length);
			String header = "0000000000".substring(0, 10 - filesize.length())
					+ filesize;

			dos.write(header.getBytes());
			dos.write(VideoEncoderCore.b);

			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
