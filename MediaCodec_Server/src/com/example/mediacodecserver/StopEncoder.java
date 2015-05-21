package com.example.mediacodecserver;

public class StopEncoder {
	private VideoEncoderCore mVideoEncoderCore;

	public void EncoderStop() {
		mVideoEncoderCore.drainEncoder(true);
		mVideoEncoderCore.release();
	}

}
