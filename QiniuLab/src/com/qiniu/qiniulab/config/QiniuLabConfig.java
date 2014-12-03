package com.qiniu.qiniulab.config;

public class QiniuLabConfig {
	public final static byte[] EMPTY_BODY = new byte[0];
	public final static String REMOTE_SERVICE_SERVER = "http://192.168.0.125/~jemy/qiniu-lab-php";

	public final static String SIMPLE_UPLOAD_WITHOUT_KEY_PATH = "/demos/api/simple_upload_without_key_upload_token.php";

	public final static String PUBLIC_VIDEO_PLAY_LIST_PATH = "/demos/api/public_video_play_list.php";

	public static String makeUrl(String remoteServer, String reqPath) {
		StringBuilder sb = new StringBuilder();
		sb.append(remoteServer);
		sb.append(reqPath);
		return sb.toString();
	}
}
