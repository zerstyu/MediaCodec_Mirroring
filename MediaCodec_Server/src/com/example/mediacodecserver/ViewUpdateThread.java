package com.example.mediacodecserver;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ViewUpdateThread extends Thread {
	final int SocketServerPORT = 8260;

	int count = 0;

	public ServerSocket serverSocket;
	private MainActivity mMainActivity = new MainActivity();

	InputStream inputStream;
	OutputStream outputStream;
	DataRecvThread mrecvThread;
	FileInputStream fis;

	String filesize = null;

	public void run() {
		try {
			if (serverSocket == null) {
				serverSocket = new ServerSocket();
				serverSocket.setReuseAddress(true);
				serverSocket.bind(new InetSocketAddress(SocketServerPORT));
			}

			while (true) {
				Socket socket = serverSocket.accept();
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();

				mMainActivity.check = 1;

				mMainActivity.dos = new DataOutputStream(outputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}