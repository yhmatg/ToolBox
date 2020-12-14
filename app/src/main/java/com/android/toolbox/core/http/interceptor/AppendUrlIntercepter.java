package com.android.toolbox.core.http.interceptor;

import android.util.Log;
import com.android.toolbox.core.DataManager;
import com.android.toolbox.utils.StringUtils;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AppendUrlIntercepter implements Interceptor {
    private String baseUrl;
    public AppendUrlIntercepter(String baseUrl){
        this.baseUrl=baseUrl;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = DataManager.getInstance().getToken();
        String cacheHost= DataManager.getInstance().getHostUrl();

        Request oldRequest = chain.request();
        HttpUrl.Builder builder = oldRequest
                .url()
                .newBuilder();
        //add 20190729 start
        builder.addQueryParameter("token",token);
        //add 20190729 start
        HttpUrl httpUrl = oldRequest.url();
        if (!StringUtils.isEmpty(cacheHost) && !baseUrl.equals(cacheHost)) {
            cacheHost=cacheHost.replaceAll("http(s)?://","");
            String host = httpUrl.host();
            int port = httpUrl.port();
            String[] split = cacheHost.split(":");
            if(split.length>0) {
                String cacheH = split[0];
                String cacheP = split.length==2?split[1]:"80";
                builder=builder.host(cacheH).port(Integer.valueOf(cacheP));
            }
        }
        Log.e("AppendUrlIntercepter","builder.build()===" + builder.build());
        Request newRequest = oldRequest
                .newBuilder()
                //.addHeader("Authorization",token)
                .url(builder.build())
                .build();
        return chain.proceed(newRequest);
    }
}