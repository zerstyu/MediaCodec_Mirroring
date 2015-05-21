package com.example.mediacodec_client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class MyClientTask extends AsyncTask<Void, Void, Void>{
	// Class
	private MainActivity mMainActivity;
	private VideoDecoderThread mVideoDecoderThread;
	
	// Variable
	private int dstPort;
	private String dstAddress;
	private String response = "";
	
	private InputStream inputStream;
	private DataInputStream dis;
	private String header;

	MyClientTask(String addr, int port) {
		dstAddress = addr;
		dstPort = port;
		
		mMainActivity = new MainActivity();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		Socket socket = null;

		while (true) {
			try {
				long time = System.currentTimeMillis();
				if ((time - mMainActivity.temp) != 0)
					mMainActivity.fps = 1000 / (time - mMainActivity.temp);

				mMainActivity.temp = time;

				socket = new Socket(dstAddress, dstPort);
				inputStream = socket.getInputStream();

				byte[] buf = new byte[10];

				dis = new DataInputStream(inputStream);
				dis.read(buf, 0, 10);
				header = new String(buf, 0, 10);
				
				mMainActivity.bodysize = 0;
				try {
					mMainActivity.bodysize = Integer.parseInt(header);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					dis.close();
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				byte[] buffer = new byte[1024];
				int read_length = 0;

				while (read_length < mMainActivity.bodysize) {
					int rsize;
					rsize = dis.read(buffer);
					bos.write(buffer, 0, rsize);
					read_length += rsize;
				}

				mMainActivity.resBytes = bos.toByteArray();

				VideoDecoderThread.flag1 = 0;

				if (mMainActivity.flag == 0) {
					mVideoDecoderThread.start();
					mMainActivity.flag = 1;
				}

				bos.close();
				dis.close();
				socket.close();
			} catch (IOException e) {
				Log.e("Socket", "disconnected", e);
				break;
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mMainActivity.textResponse.setText(response);
		super.onPostExecute(result);
	}
}
