#简介

该项目是七牛Android SDK的使用方法演示项目，使用 Android Studio 开发，可以下载到本地，直接导入作为参考。

该项目对应的服务端在[https://github.com/qiniudemo/qiniu-api-server](https://github.com/qiniudemo/qiniu-api-server/tree/master/php-v6)，可以搭建一个简单的PHP环境即可使用。

#体验版本
[下载 QiniuLab v1.0](http://devtools.qiniu.com/qiniulab-v1.0.apk)

#上传模型

![上传模型](programming-model.png)

标准的上传流程如下：

1. 客户端用户登录到APP的账号系统里面
2. 客户端上传文件之前，需要向业务服务器申请七牛的上传凭证，这个凭证由业务服务器使用七牛提供的服务端SDK生成
3. 客户端使用七牛提供的客户端SDK，调用上传方法上传文件，上传方法中必须有上传凭证和文件内容（由于七牛允许大小为0的文件，所以文件上传之前，建议检查文件大小。
如果业务不允许文件大小为0，那么需要自行检测下）。
4. 客户端文件上传到七牛之后，可选的操作是七牛回调业务服务器，（即七牛把文件相关的信息发送POST请求到上传策略里面指定的回调地址）。
5. 业务服务器回复七牛的回调请求，给出JSON格式的回复内容（必须是JSON格式的回复），这个回复内容将被七牛转发给客户端。
