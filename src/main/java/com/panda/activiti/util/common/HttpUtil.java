package com.panda.activiti.util.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lao on 2015/9/29.
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    // 默认连接超时时间: 30s
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 20;
    // 默认读取超时时间: 30s
    private static final int DEFAULT_READ_TIMEOUT = 1000 * 20;
    // 默认编码
    private static final String DEFAULT_ENCODE = "UTF-8";

    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    /**
     * get 请求
     * @param url
     * @param params params可以为null
     * @return
     * 需要判断返回值是否为null或者""
     */
    public static String get(String url,Map<String,String> params) {
        String result = "";
        BufferedReader in = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    uriBuilder.addParameter(entry.getKey(),entry.getValue());
                }
            }
            httpclient = HttpClients.createDefault();
            HttpGet request = new HttpGet(uriBuilder.build());
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();
            request.setConfig(config);
            response = httpclient.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            logger.error("GET请求失败",e);
        } catch (URISyntaxException e) {
            logger.error("URL语法错误", e);
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {}
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public static String get(String url) {
        return get(url,null);
    }

    /**
     * get 请求
     * @param url
     * @param params params可以为null
     * @return
     * 需要判断返回值是否为null或者""
     */
    public static String asyncGet(String url,Map<String,String> params) {
        String result = "";
        BufferedReader in = null;
        Future<HttpResponse> future = null;
        CloseableHttpAsyncClient httpclient = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    uriBuilder.addParameter(entry.getKey(),entry.getValue());
                }
            }
            httpclient = HttpAsyncClients.createDefault();
            httpclient.start();
            HttpGet request = new HttpGet(uriBuilder.build());
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();
            request.setConfig(config);
            future = httpclient.execute(request,null);
            in = new BufferedReader(new InputStreamReader(future.get().getEntity().getContent()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            logger.error("GET请求失败",e);
        } catch (URISyntaxException e) {
            logger.error("URL语法错误", e);
        } catch (InterruptedException e) {
            logger.error("GET请求失败", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * get 请求
     * @param url
     * @param params params可以为null
     * @return
     * 需要判断返回值是否为null或者""
     */
    public static void asyncGetforCallback(String url,Map<String,String> params,RequestHandler handler) {
        CloseableHttpAsyncClient httpclient = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    uriBuilder.addParameter(entry.getKey(),entry.getValue());
                }
            }
            httpclient = HttpAsyncClients.createDefault();
            httpclient.start();
            HttpGet request = new HttpGet(uriBuilder.build());
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();
            request.setConfig(config);
            httpclient.execute(request,handler);
        } catch (URISyntaxException e) {
            logger.error("URL语法错误", e);
        }
    }

    public static String asyncGet(String url) {
        return asyncGet(url,null);
    }

    /**
     * post 请求
     * @param url
     * @param params
     * @return
     */
    public static String post(String url,Map<String,String> params) {
        String result = "";
        BufferedReader in = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            List<NameValuePair> nvps = new ArrayList<>();
            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpclient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();
            request.setConfig(config);
            request.setEntity(new UrlEncodedFormEntity(nvps,DEFAULT_ENCODE));
            response = httpclient.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            logger.error("POST请求失败", e);
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {}
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public static String post(String url) {
        return post(url,null);
    }

    /**
     * post 请求
     * @param url
     * @param params
     * @return
     */
    public static String asyncPost(String url,Map<String,String> params) {
        String result = "";
        BufferedReader in = null;
        Future<HttpResponse> future = null;
        CloseableHttpAsyncClient httpclient = null;
        try {
            List<NameValuePair> nvps = new ArrayList<>();
            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpclient = HttpAsyncClients.createDefault();
            httpclient.start();
            HttpPost request = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();
            request.setConfig(config);
            request.setEntity(new UrlEncodedFormEntity(nvps,DEFAULT_ENCODE));
            future = httpclient.execute(request,null);
            in = new BufferedReader(new InputStreamReader(future.get().getEntity().getContent()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("POST请求失败",e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public static String asyncPost(String url) {
        return asyncPost(url,null);
    }

    /**
     * post 上传文件
     * @param url
     * @param params
     * @param files
     * @return
     */
    public static String uploadFiles(String url,Map<String,String> params,Map<String,File> files) {
        if(files == null || files.size() == 0){
            logger.error("files is not blank");
            return null;
        }
        String result = "";
        BufferedReader in = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //解决中文乱码
            ContentType contentType = ContentType.create(DEFAULT_CONTENT_TYPE, DEFAULT_ENCODE);

            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    StringBody stringBody = new StringBody(entry.getValue(),contentType);
                    builder.addPart(entry.getKey(), stringBody);
                }
            }

            for(Map.Entry<String,File> entry:files.entrySet()){
                builder.addBinaryBody(entry.getKey(),entry.getValue());
            }

            httpclient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();
            request.setConfig(config);
            request.setEntity(builder.build());
            response = httpclient.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            logger.error("文件上传失败",e);
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {}
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * 文件下载
     * @param url
     * @param params
     * @param file
     * @return
     */
    public static void downloadFile(String url,Map<String,String> params,File file) {
//        String result = "";
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        try {
            List<NameValuePair> nvps = new ArrayList<>();
            if(params != null){
                for(Map.Entry<String,String> entry:params.entrySet()){
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpclient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                    .setSocketTimeout(60 * 1000 * 10).build();
            request.setConfig(config);
            request.setEntity(new UrlEncodedFormEntity(nvps,DEFAULT_ENCODE));
            response = httpclient.execute(request);
            in = new BufferedInputStream(response.getEntity().getContent());
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[1024];
            int b = 0;
            while ((b = in.read(buff)) != -1) {
                out.write(buff,0,b);
            }
            out.flush();
        } catch (IOException e) {
            logger.error("文件下载失败",e);
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {}
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        String commonUrl = "http://localhost:8888/test/testJson.htm";
        Map<String,String> params = new HashMap<>();
        params.put("id", "小明");
//        System.out.println(HttpUtils.get(commonUrl,params));
//        System.out.println(HttpUtils.post(commonUrl,params));
//        System.out.println(HttpUtils.asyncGet(commonUrl,params));
//        System.out.println(HttpUtils.asyncPost(commonUrl,params));
        HttpUtil.asyncGetforCallback(commonUrl, params, new RequestHandler() {
            @Override
            public void success(String result) {
                System.out.println(result);
            }
        });
        System.out.println("下一步");

//        String downloadUrl = "http://localhost:8888/test/downloadOne.htm";
//        HttpUtils.downloadFile(downloadUrl,null,new File("D:\\d.txt"));

//        String uploadUrl = "http://localhost:8888/test/singleUpload.htm";
//        Map<String,String> params = new HashMap<>();
//        params.put("comment", "说明");
//        Map<String,File> files = new HashMap<>();
//        files.put("file", new File("D:\\test.txt"));
//        System.out.println(HttpUtils.uploadFiles(uploadUrl, params, files));
    }
}

abstract class RequestHandler implements FutureCallback<HttpResponse>{
    @Override
    public void completed(HttpResponse httpResponse) {
        BufferedReader in = null;
        try {
            String result = "";
            in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            while((line=in.readLine()) != null){
                result += line;
            }
            success(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {}
            }
        }

    }

    @Override
    public void failed(Exception e) {

    }

    @Override
    public void cancelled() {

    }

    public abstract void success(String result);
}

