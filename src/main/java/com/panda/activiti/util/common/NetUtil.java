package com.panda.activiti.util.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lao on 2015/9/29.
 */
public class NetUtil {

    private static Logger       logger                     = LoggerFactory.getLogger(NetUtil.class);

    // 默认连接超时时间: 30s
    private static final int    DEFAULT_CONNECTION_TIMEOUT = 1000 * 20;
    // 默认读取超时时间: 30s
    private static final int    DEFAULT_READ_TIMEOUT       = 1000 * 20;
    // 默认编码
    private static final String DEFAULT_ENCODE             = "UTF-8";
    // 文件上传默认key
    // private static final String FILE_KEY = "file";s

    /**
     * post请求
     *
     * @param url 请求地址
     * @param params 请求参数 a=1&b=1格式 无参数时params设为null
     * @param encoding 编码
     * @param connectionTimeout 连接超时时间
     * @param readTimeout 响应超时时间
     * @return
     * @throws IOException
     */
    public static String post(String url, String params, String encoding, int connectionTimeout, int readTimeout) {
        String result = "";
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), encoding));
            out.print(params);
            out.flush();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding), 8 * 1024);
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            logger.error("POST请求失败", e);
        } finally {
            boolean success = true;
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    success = false;
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    success = false;
                }
            }
            if (!success) {
                return null;
            }
        }
        return result;
    }

    public static String post(String url, String params) {
        return post(url, params, DEFAULT_ENCODE, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    public static String post(String url, Map<String, Object> params) {
        return post(url, _transfer(params), DEFAULT_ENCODE, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     * @param file
     */
    public static void downloadFile(String url, String params, File file) {
        BufferedInputStream in = null;
        PrintWriter pw = null;
        BufferedOutputStream out = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(1000 * 60 * 10); // 10分钟
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            pw = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), DEFAULT_ENCODE));
            pw.print(params);
            pw.flush();

            in = new BufferedInputStream(connection.getInputStream());
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[1024]; // 1kb的写
            int b = 0;
            while ((b = in.read(buff)) != -1) {
                out.write(buff, 0, b);
            }
            out.flush();
        } catch (IOException e) {
            file.delete();
            logger.error("下载文件失败", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void downloadFile(String url, String params, String filePath) {
        downloadFile(url, params, new File(filePath));
    }

    public static String uploadFiles(String url, Map<String, String> params, Map<String, File> files) {
        String result = "";
        String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
        OutputStream out = null;
        DataInputStream in = null;
        BufferedReader reader = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            out = new DataOutputStream(conn.getOutputStream());
            // text
            if (params != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getValue() == null) {
                        continue;
                    } else {
                        sb.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                        sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                        sb.append(entry.getValue());
                    }
                }
                out.write(sb.toString().getBytes());
            }

            // file
            if (files != null) {
                for (Map.Entry<String, File> entry : files.entrySet()) {
                    if (entry.getValue() == null) {
                        continue;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        // MagicMatch match = new Magic().getMagicMatch(entry.getValue(), false,true);
                        sb.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                        sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\""
                                  + entry.getValue().getName() + "\"\r\n");
                        // sb.append("Content-Type:" + match.getMimeType() + "\r\n\r\n");
                        sb.append("Content-Type:" + "text/plain" + "\r\n\r\n");
                        out.write(sb.toString().getBytes());

                        in = new DataInputStream(new FileInputStream(entry.getValue()));
                        int b = 0;
                        byte[] buff = new byte[1024];
                        while ((b = in.read(buff)) != -1) {
                            out.write(buff, 0, b);
                        }
                    }
                }
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();

            // 读取返回数据
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("上传文件失败", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 内部方法 拼接参数
     *
     * @param params
     * @return
     */
    private static String _transfer(Map<String, Object> params) {
        StringBuilder joinParams = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            joinParams.append("&").append(entry.getKey()).append("=" + entry.getValue());
        }
        return joinParams.toString().substring(1);
    }

    /**
     * get请求
     *
     * @param url 请求地址
     * @param params 请求参数 a=1&b=1格式
     * @param encoding 编码
     * @param connectionTimeout 连接超时时间
     * @param readTimeout 响应超时时间
     * @return
     */
    public static String get(String url, String params, String encoding, int connectionTimeout, int readTimeout) {
        String result = "";
        BufferedReader in = null;

        if (params != null && !params.equals("")) {
            url = url + "?" + params;
        }

        try {
            URLConnection connection = new URL(url).openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            // 建立实际的连接
            connection.connect();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding), 8 * 1024);
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("GET请求失败", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    public static String get(String url, String params) {
        return get(url, params, DEFAULT_ENCODE, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    public static String get(String url, Map<String, Object> params) {
        return get(url, _transfer(params), DEFAULT_ENCODE, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * @param url http://www.a.com?a=1&b=1
     * @return
     */
    public static String get(String url) {
        return get(url, null, DEFAULT_ENCODE, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    public static void main(String[] args) {
        // String commonUrl = "http://localhost:8888/test/testJson.htm";
        // System.out.println(NetUtils.get(commonUrl,"id=小明"));
        // System.out.println(NetUtils.post(commonUrl,"id=小明"));

        // String downloadUrl = "http://localhost:8888/test/downloadOne.htm";
        // NetUtils.downloadFile(downloadUrl,null,new File("D:\\d.txt"));

        String uploadUrl = "http://localhost:8888/test/singleUpload.htm";
        Map<String, String> params = new HashMap<>();
        params.put("comment", "说明");
        Map<String, File> files = new HashMap<>();
        files.put("file", new File("D:\\test.txt"));
        System.out.println(NetUtil.uploadFiles(uploadUrl, params, files));
    }
}
