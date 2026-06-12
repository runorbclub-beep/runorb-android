#MVP框架:
 1:base目录是mvp框架基类
 2:service 是封装retrofit+rxjava包以及与服务器通信接口
 3:mvp目录是一个mvp模式简单代码实现
 
#MVVM框架(使用Jetpack):
注：适合电商类数据展示类项目


#本项目介绍
1:不采用任何框架，项目比较小，更方便，去除RN模块

密钥库类型: jks
密钥库提供方: SUN

您的密钥库包含 1 个条目

别名: key0
创建日期: 2020-12-24
条目类型: PrivateKeyEntry
证书链长度: 1
证书[1]:
所有者: CN=megabuyer, OU=megabuyer, O=megabuyer, L=shenzhen, ST=guangdong, C=CN
发布者: CN=megabuyer, OU=megabuyer, O=megabuyer, L=shenzhen, ST=guangdong, C=CN
序列号: 4125e51a
有效期为 Thu Dec 24 18:19:37 CST 2020 至 Fri Dec 01 18:19:37 CST 2119
证书指纹:
         MD5:  73:FC:5C:03:0A:C0:CA:26:A4:81:BE:62:8F:8A:31:FE
         SHA1: B6:BF:93:9C:C7:BE:8A:99:C8:65:49:91:75:5F:42:3E:7B:5E:FD:1A
         SHA256: 41:DC:9F:B4:26:E7:1F:CD:D3:8D:7F:A7:D6:1E:96:DD:88:C3:00:54:39:6E:A6:34:8B:C7:5A:8B:2E:8A:DA:CF


华为应用市场
pepk.jar
请上传包含私钥和公钥证书的 ZIP 文件
java -jar pepk.jar --keystore release.jks --alias key0 --output=output.zip --encryptionkey=034200041E224EE22B45D19B23DB91BA9F52DE0A06513E03A5821409B34976FDEED6E0A47DBA48CC249DD93734A6C5D9A0F43461F9E140F278A5D2860846C2CF5D2C3C02 --include-cert

腾讯市场
jarsigner -verbose -keystore release.jks -signedjar sined.apk tap_unsign.apk key0