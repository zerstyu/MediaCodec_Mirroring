# MediaCodec_Mirroring
This is : 
* Mirroring using MediaCodec.
* It operates in the background. So, it does not stop to interrupt.

※ interrupt ? call, message, home button and so on.

Server
* 1. get Bitmap using getDrawingCache(). 
* 2. Bitmap to Byte[]
* 3. Byte[] to Encoder
* 4. get Byte[] (encoded data) from Encoder
* 5. Transfer Byte[] to Client

Client
* 1. Receive data from Server
* 2. Received data to Byte[]
* 3. Byte[] to Decoder
* 4. Decoder to Surface
* 5. Rendering with the Surface

However :
* It's not implemented layout.
* It's not encoded with the surface. So, need to RGB2YUV and NV21->NV12. Results in a low performance. 

# Feature
* Application Mirroring
* H.264 Encoding, Decoding
* It's not used MediaExtractor.

# Requirements
* Requires minimum API 16 (Android 4.1)


# Android API
* MediaCodec : http://developer.android.com/reference/android/media/MediaCodec.html

# Reference
* Encoder : http://bigflake.com/mediacodec/
 
# Image
![alt tag](https://github.com/zerstyu/MediaCodec_Mirroring/blob/master/mediacodec.PNG)
