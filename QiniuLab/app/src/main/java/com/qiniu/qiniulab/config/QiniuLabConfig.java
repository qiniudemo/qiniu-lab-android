package com.qiniu.qiniulab.config;

public class QiniuLabConfig {
    public final static String LOG_TAG = "QiniuLab";
    public final static String REMOTE_SERVICE_SERVER = "https://qiniulab.pdex-service.com/upload";

    //quick start
    public final static String QUICK_START_IMAGE_DEMO_PATH = "/api/quick_start/simple_image_example_token.php";
    public final static String QUICK_START_VIDEO_DEMO_PATH = "/api/quick_start/simple_video_example_token.php";

    // simple upload
    public final static String SIMPLE_UPLOAD_WITHOUT_KEY_PATH = "/api/simple_upload/without_key_upload_token.php";
    public final static String SIMPLE_UPLOAD_WITH_KEY_PATH = "/api/simple_upload/with_key_upload_token.php";
    public final static String SIMPLE_UPLOAD_USE_SAVE_KEY_PATH = "/api/simple_upload/use_save_key_upload_token.php";
    public final static String SIMPLE_UPLOAD_USE_SAVE_KEY_FROM_XPARAM_PATH = "/api/simple_upload/use_save_key_from_xparam_upload_token.php";
    public final static String SIMPLE_UPLOAD_USE_RETURN_BODY_PATH = "/api/simple_upload/use_return_body_upload_token.php";
    public final static String SIMPLE_UPLOAD_OVERWRITE_EXISTING_FILE_PATH = "/api/simple_upload/overwrite_existing_file_upload_token.php";
    public final static String SIMPLE_UPLOAD_USE_FSIZE_LIMIT_PATH = "/api/simple_upload/use_fsize_limit_upload_token.php";
    public final static String SIMPLE_UPLOAD_USE_MIME_LIMIT_PATH = "/api/simple_upload/use_mime_limit_upload_token.php";
    public final static String SIMPLE_UPLOAD_WITH_MIMETYPE_PATH = "/api/simple_upload/with_mimetype_upload_token.php";
    public final static String SIMPLE_UPLOAD_ENABLE_CRC32_CHECK_PATH = "/api/simple_upload/enable_crc32_check_upload_token.php";
    public final static String SIMPLE_UPLOAD_USE_ENDUSER_PATH = "/api/simple_upload/use_enduser_upload_token.php";

    // resumable upload
    public final static String RESUMABLE_UPLOAD_WITHOUT_KEY_PATH = "/api/resumable_upload/without_key_upload_token.php";
    public final static String RESUMABLE_UPLOAD_WITH_KEY_PATH = "/api/resumable_upload/with_key_upload_token.php";

    // callback upload
    public final static String CALLBACK_UPLOAD_WITH_KEY_IN_URL_FORMAT_PATH = "/api/callback_upload/with_key_in_url_format_upload_token.php";
    public final static String CALLBACK_UPLOAD_WITH_KEY_IN_JSON_FORMAT_PATH = "/api/callback_upload/with_key_in_json_format_upload_token.php";

    public final static String PUBLIC_IMAGE_VIEW_LIST_PATH = "/api/image_view/public_image_view_list.php";
    public final static String PUBLIC_VIDEO_PLAY_LIST_PATH = "/api/video_play/public_video_play_list.php";
    public final static String QUERY_PFOP_RESULT_PATH = "/service/query_pfop_result.php";

    public static String makeUrl(String remoteServer, String reqPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(remoteServer);
        sb.append(reqPath);
        return sb.toString();
    }
}
