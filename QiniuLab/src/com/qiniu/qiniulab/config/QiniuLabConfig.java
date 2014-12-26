package com.qiniu.qiniulab.config;

public class QiniuLabConfig {
	public final static byte[] EMPTY_BODY = new byte[0];
	public final static String REMOTE_SERVICE_SERVER = "http://localtunnel.qiniu.io:9090";

	// simple upload
	public final static String SIMPLE_UPLOAD_WITHOUT_KEY_PATH = "/demos/api/simple_upload_without_key_upload_token.php";
	public final static String SIMPLE_UPLOAD_WITH_KEY_PATH = "/demos/api/simple_upload_with_key_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_SAVE_KEY_PATH = "/demos/api/simple_upload_use_save_key_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_SAVE_KEY_FROM_XPARAM_PATH = "/demos/api/simple_upload_use_save_key_from_xparam_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_RETURN_BODY_PATH = "/demos/api/simple_upload_use_return_body_upload_token.php";
	public final static String SIMPLE_UPLOAD_OVERWRITE_EXISTING_FILE_PATH = "/demos/api/simple_upload_overwrite_existing_file_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_FSIZE_LIMIT_PATH = "/demos/api/simple_upload_use_fsize_limit_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_MIME_LIMIT_PATH = "/demos/api/simple_upload_use_mime_limit_upload_token.php";
	public final static String SIMPLE_UPLOAD_WITH_MIMETYPE_PATH = "/demos/api/simple_upload_with_mimetype_upload_token.php";
	public final static String SIMPLE_UPLOAD_ENABLE_CRC32_CHECK_PATH = "/demos/api/simple_upload_enable_crc32_check_upload_token.php";
	public final static String SIMPLE_UPLOAD_USE_ENDUSER_PATH = "/demos/api/simple_upload_use_enduser_upload_token.php";

	// resumable upload
	public final static String RESUMABLE_UPLOAD_WITHOUT_KEY_PATH = "/demos/api/resumable_upload_without_key_upload_token.php";
	public final static String RESUMABLE_UPLOAD_WITH_KEY_PATH = "/demos/api/resumable_upload_with_key_upload_token.php";

	public final static String PUBLIC_VIDEO_PLAY_LIST_PATH = "/demos/api/public_video_play_list.php";

	public static String makeUrl(String remoteServer, String reqPath) {
		StringBuilder sb = new StringBuilder();
		sb.append(remoteServer);
		sb.append(reqPath);
		return sb.toString();
	}
}
