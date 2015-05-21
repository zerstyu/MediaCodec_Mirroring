package com.example.mediacodec_client;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

public class VideoDecoderThread extends Thread {
	private MainActivity mMainActivity;

	private static final String VIDEO = "video/";
	private static final String MIME_TYPE = "video/avc"; // H.264

	private static final String TAG = "VideoDecoder";

	// private MediaExtractor mExtractor;
	private MediaCodec mDecoder;

	private boolean eosReceived;
	public static int flag = 0, flag1 = 0;
	int count = 0;

	public boolean init(Surface surface) {
		eosReceived = false;
		try {

			MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE,
					mMainActivity.width, mMainActivity.height);
			mDecoder = MediaCodec.createDecoderByType(MIME_TYPE);
			

			try {
				Log.d(TAG, "format : " + format);
				
				/*
				 * 1. MediaFormat : 위에서 설정한 MediaFormat의 설정값(MediaExtractor를 이용할 경우
				 * 해당 MediaFormat 정보를 return 받아올 수 있다) 2. Surface : 디코딩에서만 사용하며 화면을
				 * 그리기 위한 View를 넘겨주면 된다. 3. MediaCrypto : 암호화된 Media Data를 다룰 때 사용하는
				 * 옵션이다. 일반적으로 null로 초기화해서 사용 4. flags : 인코딩시에만 다음의
				 * 값(MediaCodec.CONFIGURE_FLAG_ENCODE)을 초기화하고, 그 외에는 0으로 초기화한다.
				 */
				
				mDecoder.configure(format, surface, null, 0 /* Decoder */);

			} catch (IllegalStateException e) {
				Log.e(TAG, "codec failed configuration. " + e);
				return false;
			}

			mDecoder.start();

			// mExtractor = new MediaExtractor();
			// mExtractor.setDataSource(filePath);

			/*
			 * for (int i = 0; i < mExtractor.getTrackCount(); i++) {
			 * MediaFormat format = mExtractor.getTrackFormat(i);
			 * 
			 * String mime = format.getString(MediaFormat.KEY_MIME); if
			 * (mime.startsWith(VIDEO)) { mExtractor.selectTrack(i); mDecoder =
			 * MediaCodec.createDecoderByType(mime); try { Log.d(TAG,
			 * "format : " + format); mDecoder.configure(format, surface, null,
			 * 0 Decoder );
			 * 
			 * } catch (IllegalStateException e) { Log.e(TAG, "codec '" + mime +
			 * "' failed configuration. " + e); return false; }
			 * 
			 * mDecoder.start(); break; } }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void run() {
		BufferInfo info = new BufferInfo();
		ByteBuffer[] inputBuffers = mDecoder.getInputBuffers();

		// mDecoder.getOutputBuffers();

		boolean isInput = true;
		boolean first = false;
		long startWhen = 0;

		while (!eosReceived) {
			if (flag1 == 0) {

				if (isInput) {
					try {

						int inputIndex = mDecoder.dequeueInputBuffer(10000);
						if (inputIndex >= 0) {
							// fill inputBuffers[inputBufferIndex] with valid
							// data
							ByteBuffer inputBuffer = inputBuffers[inputIndex];
							inputBuffer.clear();
							inputBuffer.put(MainActivity.resBytes);

							mDecoder.queueInputBuffer(inputIndex, 0,
									MainActivity.resBytes.length, count++, 0);

							// int sampleSize =
							// mExtractor.readSampleData(inputBuffer, 0);

							/*
							 * if (mExtractor.advance() && sampleSize > 0) {
							 * mDecoder.queueInputBuffer(inputIndex, 0,
							 * sampleSize, mExtractor.getSampleTime(), 0);
							 * 
							 * } else { Log.d(TAG,
							 * "InputBuffer BUFFER_FLAG_END_OF_STREAM");
							 * mDecoder.queueInputBuffer(inputIndex, 0, 0, 0,
							 * MediaCodec.BUFFER_FLAG_END_OF_STREAM); isInput =
							 * false; }
							 */
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}

				int outIndex = mDecoder.dequeueOutputBuffer(info, 10000);
				switch (outIndex) {
				case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
					// 여기에서는 MediaCodec의 Buffer 상태가 변경될 수 있다. 아래와 같은 코드를 꼭 넣어줘야
					// 한다.
					Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
					mDecoder.getOutputBuffers();
					break;

				case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
					// 여기에서는 MediaMuxer를 이용해서 파일을 출력할 때 사용할 수 있다. 참고로 Andorid
					// 4.3 이상에서는 항상 호출되지만
					// 4.3 이전 버전 (4.2, 4.1) 에서는 호출될 수도 안될 수도 있다. 만약 Mediamuxer를
					// 사용한다면 아래와 같이 하면 된다.
					Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : "
							+ mDecoder.getOutputFormat());
					break;

				case MediaCodec.INFO_TRY_AGAIN_LATER:
					// 여기는 MediaCodec에서 생성한 Buffer가 현재 사용 공간이 없을 경우에 출력.
					// 폰 상황에 따라서 자주 호출될 수 잇다.

					// Log.d(TAG, "INFO_TRY_AGAIN_LATER");
					break;

				default:
					// 여기가 정상적인 경우에 호출되는 경우
					if (!first) {
						startWhen = System.currentTimeMillis();
						first = true;
					}
					try {
						long sleepTime = (info.presentationTimeUs / 1000)
								- (System.currentTimeMillis() - startWhen);
						Log.d(TAG, "info.presentationTimeUs : "
								+ (info.presentationTimeUs / 1000)
								+ " playTime: "
								+ (System.currentTimeMillis() - startWhen)
								+ " sleepTime : " + sleepTime);

						if (sleepTime > 0)
							Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mDecoder.releaseOutputBuffer(outIndex, true /* Surface init */);
					break;
				}

				// All decoded frames have been rendered, we can stop playing
				// now
				if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
					Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
					break;
				}

				flag = 1;
				flag1 = 1;
			}

			mDecoder.stop();
			mDecoder.release();
			// mExtractor.release();
		}
	}

	public void close() {
		eosReceived = true;
	}
}
