package com.cloud.runball.service;

import android.text.TextUtils;

import com.cloud.runball.BuildConfig;
import com.cloud.runball.basecomm.service.gson.BaseConverterFactory;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;

import org.apache.http.conn.scheme.HostNameResolver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @author ns467
 */
public class WristBallRetrofitHelper {
  private static final int DEFAULT_TIME_OUT = 10;
  private static final int DEFAULT_READ_TIME_OUT = 10;
  private String baseServerUrl = "https://api.hisport.cloud";
  private static WristBallRetrofitHelper apiRetrofit;
  private Retrofit retrofit;
  private OkHttpClient client;
  private WristBallServer wristBallServer;

  private Interceptor tokenInterceptor = new Interceptor() {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request()
          .newBuilder()
          .addHeader("token", token)
          .addHeader("language", language)
          .build();
      return chain.proceed(request);
    }
  };

  /**
   * 请求访问quest
   * response拦截器
   */
  private Interceptor interceptor = new Interceptor() {
    /**
     * 拦截器，用来切换url和添加header
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      HttpUrl baseURL = null;
      if(Constant.getServerType() == Constant.CHINA_SERVER_TYPE){
        baseURL = HttpUrl.parse(BuildConfig.CHINA_SERVER_URL);
      }else{
        baseURL = HttpUrl.parse(BuildConfig.NATION_SERVER_URL);
      }

      //获取当前的url
      HttpUrl oldUrl = request.url();
      //重建新的HttpUrl，需要重新设置的url部分
      HttpUrl newHttpUrl = oldUrl.newBuilder()
          .scheme(baseURL.scheme())
          .host(baseURL.host())
          .port(baseURL.port())
          .build();

      request = request.newBuilder().url(newHttpUrl).addHeader("content-type", "application/json").build();
      //设置测试token
      if (!TextUtils.isEmpty(token)) {
        request = request.newBuilder()
            .url(newHttpUrl)
            .addHeader("token", token)
            //.addHeader("token", "ceca7c8e46ca0b3dec539e94dc9d8349")
            .addHeader("language",language)
            .build();
      } else {
        request = request.newBuilder().url(newHttpUrl).addHeader("token", "").addHeader("language",language).build();
      }

      //return chain.proceed(request);

      //long startTime = System.currentTimeMillis();
      Response response = chain.proceed(request);
      //long endTime = System.currentTimeMillis();
      //long duration = endTime - startTime;
      MediaType mediaType = response.body().contentType();
      String content = response.body().string();

      //AppLogger.e("----------Request Start----------------");
      AppLogger.d("| " + getRequestInfo(request) +"|"+ request.headers().toString()+"   请求url=" + request.url());
      AppLogger.d( "| Response:" + content);
      //AppLogger.d( "----------Request End:" + duration + "毫秒----------");
      //Logger.v("请求body数据 " + getRequestInfo(request) + "   请求url=" + request.url() + ";token=" + request.header("token").toString() + ";返回结果:" + content);
      return response.newBuilder()
          .body(ResponseBody.create(mediaType, content))
          .build();
    }
  };

  /**
   * 打印请求消息
   *
   * @param request 请求的对象
   */
  private String getRequestInfo(Request request) {
    String str = "";
    if (request == null) {
      return str;
    }
    RequestBody requestBody = request.body();
    if (requestBody == null) {
      return str;
    }
    try {
      Buffer bufferedSink = new Buffer();
      requestBody.writeTo(bufferedSink);
      str = bufferedSink.readString(StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return str;
  }

  public WristBallRetrofitHelper() {
    client = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        //.addInterceptor(new HttpHeaderInterceptor())
        //.addNetworkInterceptor(new HttpCacheInterceptor())
        // .sslSocketFactory(SslContextFactory.getSSLSocketFactoryForTwoWay())  // https认证 如果要使用https且为自定义证书 可以去掉这两行注释，并自行配制证书。
        // .hostnameVerifier((hostname, session) -> true)
        //.sslSocketFactory(SslContextFactory.getSSLSocketFactoryForTwoWay())
        .build();

    //本身不支持切换baseUrl,每切换一个url就创建一个实例本身不符合单例逻辑，因此可以在拦截器里处理url
    retrofit = new Retrofit.Builder()
        .baseUrl(baseServerUrl)
        //添加自定义的解析器(请求，返回)
        .addConverterFactory(BaseConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        //支持RxJava2
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build();
    wristBallServer = retrofit.create(WristBallServer.class);
  }

  public static WristBallRetrofitHelper getInstance() {
    if (apiRetrofit == null) {
      synchronized (Object.class) {
        if (apiRetrofit == null) {
          apiRetrofit = new WristBallRetrofitHelper();
        }
      }
    }
    return apiRetrofit;
  }

  public WristBallServer getWristBallService() {
    return wristBallServer;
  }
  String token;
  String language="";
  public void updateToken(String token) {
    this.token = token;
  }

  public String getToken(){
    return this.token;
  }

  public void removeToken() {
    this.token = null;
  }

  public void updateLanguage(String language) {
    if(language.startsWith(ZH)) {
      this.language = "zh-CN";
    }else if(language.startsWith(EN)) {
      this.language = "en-US";
    }else if(language.startsWith(JA)) {
      this.language = "ja";
    }else{
      this.language = "en-US";
    }
  }

  static final String ZH = "zh";
  static final String EN = "en";
  static final String JA = "ja";
}
