package com.example.mediacodecserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataRecvThread extends Thread {
	DataInputStream dis = null;

	public DataRecvThread(InputStream in) {
		dis = new DataInputStream(in);
	}

	public void run() {
		try {
			byte[] buffer = new byte[10];
			dis.read(buffer, 0, 1);

			String header = new String(buffer, 0, 1);

			int bodysize = 0;
			try {
				bodysize = Integer.parseInt(header);
				if (bodysize == 1) {
					if (ViewTask.flag == 0)
						ViewTask.flag = 1;
					else
						ViewTask.flag = 0;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				dis.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}