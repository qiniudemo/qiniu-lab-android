package com.qiniu.qiniulab.config;

public class QiniuLabConfig {
	public final static byte[] EMPTY_BODY = new byte[0];
	public final static String REMOTE_SERVICE_SERVER = "http://192.168.100.108/~jemy/qiniu-lab-php";

	public final static String SIMPLE_UPLOAD_WITHOUT_KEY_PATH = "/demos/api/simple_upload_without_key_upload_token.php";
	public final static String SIMPLE_UPLOAD_WITH_KEY_PATH = "/demos/api/simple_upload_with_key_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_SAVE_KEY_PATH = "/demos/api/simple_upload_use_save_key_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_SAVE_KEY_FROM_XPARAM_PATH = "/demos/api/simple_upload_use_save_key_from_xparam_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_RETURN_BODY_PATH = "/demos/api/simple_upload_use_return_body_upload_token.php";
	public final static String PUBLIC_VIDEO_PLAY_LIST_PATH = "/demos/api/public_video_play_list.php";

	public static String makeUrl(String remoteServer, String reqPath) {
		StringBuilder sb = new StringBuilder();
		sb.append(remoteServer);
		sb.append(reqPath);
		return sb.toString();
	}
}
