package com.zyb.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class HttpURLConnectionDemo
{
    public static void main(String[] args)
        throws Exception
    {
        // ********* URLConnection的对象 *******************************************************************************************
        URL url = new URL("http://localhost:8081/ygg-admin/seller/jsonSellerCode");
        URLConnection urlConnection = url.openConnection();
        // 此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection,
        // 故此处最好将其转化为HttpURLConnection类型的对象,以便用到HttpURLConnection更多的API.如下:
        HttpURLConnection httpUrlConnection = (HttpURLConnection)urlConnection;
        
        // ********* HttpURLConnection对象参数 *******************************************************************************************
        // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
        httpUrlConnection.setDoOutput(true);
        
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        httpUrlConnection.setDoInput(true);
        
        // Post 请求不能使用缓存
        httpUrlConnection.setUseCaches(false);
        
        // 与springMVC注解配合  http://blog.csdn.net/kobejayandy/article/details/12690161
        httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        
        // 设定请求的方法为"POST"，默认是GET
        httpUrlConnection.setRequestMethod("POST");
        
        String cookies =
            "JSESSIONID=DACE9DCE56E284A2935019E50FB0B35B; rememberMe=ngo7OjXXDu/EEOifwvextvcW3WREXfQB7KlrOkw5biqEcR82IZV9R3MLEdASHGfB2J2JQi2i5vCm3L8NIZkIMGT4v12wYJhagbal5M8tVSHAGmuALSeMwyjW/A0rw5YOmMYcOwYDv3XAF4uyI7p3wqxKGRgj9QarSrJuG+zU9SCsWtaMGOymgQr+6Haa6WKPwl8zy9b+Gmpx+s63n1SWKY8XRWFuC3eKlaDUi/rcS2BW7PX7h9Moq7AcYVKhH3XS4fgfhi5BpLTjFXw6D+qCiFbZZoKcDCWxTewxG8o8XQ3Zv7ZHmbuZ9jSPkBXkboEt1YgdfzbOjIkrQ5/rQb2CTAYqdRPdXXO4XyB9PmtOH14d2eRKnzP5JvA8TqED9L9Sd8A6AxRcW1FFuvM6ji2OFJhpSX/v2XXYkoFt7/D1RgdPb2vGA+7HNCzMxqUWnNmoE02wS4csXMLZ6X0GpUdL/KDQEF7VSqquTT+vJfd9mnQKVQnlOK0gKxn70OeT1dhZskDHlE+C2/o47Omf3l95MA==";
        httpUrlConnection.setRequestProperty("Cookie", cookies);
        
        httpUrlConnection.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒）
        httpUrlConnection.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒）
        
        // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
        // connect()函数会根据HttpURLConnection对象的配置值生成http头部信息，因此在调用connect函数之前，就必须把所有的配置准备好
        // HttpURLConnection的connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求 ******
        httpUrlConnection.connect();
        
        // *********  HttpURLConnection连接 *******************************************************************************************
        // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，所以在开发中不调用上述的connect()也可以)。
        OutputStream outStrm = httpUrlConnection.getOutputStream();
        
        // *********  HttpURLConnection写数据与发送数据 *******************************************************************************************
        // 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。   
        //        ObjectOutputStream objOutputStrm = new ObjectOutputStream(outStrm);
        
        // 向对象输出流写出数据，这些数据将存到内存缓冲区中   
        String data = "isBirdex=1&q=笨鸟";
        //        objOutputStrm.writeObject(data);
        outStrm.write(data.getBytes());
        
        // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）   
        //        objOutputStrm.flush();
        outStrm.flush();
        
        // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
        //        objOutputStrm.close();
        outStrm.close();
        
        // 调用HttpURLConnection连接对象的getInputStream()函数, 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
        InputStream inStrm = httpUrlConnection.getInputStream(); // <===注意，实际发送请求的代码段就在这里
        
        // 上边的httpUrlConnection.getInputStream()方法已调用,本次HTTP请求已结束,下边向对象输出流的输出已无意义，
        // 既使对象输出流没有调用close()方法，下边的操作也不会向对象输出流写入任何数据.   
        // 因此，要重新发送数据时需要重新创建连接、重新设参数、重新创建流对象、重新写数据、重新发送数据(至于是否不用重新这些操作需要再研究)
        // objOutputStrm.writeObject(new String("isAvailable=0"));
        // httpUrlConnection.getInputStream();
        
        /**
         *  在http头后面紧跟着的是http请求的正文，正文的内容是通过outputStream流写入的，
         实际上outputStream不是一个网络流，充其量是个字符串流，往里面写入的东西不会立即发送到网络，
         而是存在于内存缓冲区中，待outputStream流关闭时，根据输入的内容生成http正文。
         至此，http请求的东西已经全部准备就绪。在getInputStream()函数调用的时候，就会把准备好的http请求
         正式发送到服务器了，然后返回一个输入流，用于读取服务器对于此次http请求的返回信息。由于http
         请求在getInputStream的时候已经发送出去了（包括http头和正文），因此在getInputStream()函数
         之后对connection对象进行设置（对http头的信息进行修改）或者写入outputStream（对正文进行修改）
         都是没有意义的了，执行这些操作会导致异常的发生
         */
        
        String result = "";
        // 定义BufferedReader输入流来读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(inStrm));
        String line;
        while ((line = in.readLine()) != null)
        {
            result += line;
        }
        System.out.println("返回结果:" + result);
    }
    
    public static String sendPost(String reqUrl, Map<String, String> params, Map<String, String> headers)
        throws IOException
    {
//        for (String key : headers.keySet()) {
//            conn.setRequestProperty(key, headers.get(key));
//        }

        URL url = new URL(reqUrl);

        // 此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection,故此处最好将其转化为HttpURLConnection类型的对象,
        // 以便用到HttpURLConnection更多的API.如下:
        HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();

        // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
        httpUrlConnection.setDoOutput(true);
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        httpUrlConnection.setDoInput(true);
        // Post 请求不能使用缓存
        httpUrlConnection.setUseCaches(false);
        // 与springMVC注解配合  http://blog.csdn.net/kobejayandy/article/details/12690161
        httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");


        StringBuffer buffer = new StringBuffer();
        return null;
    }
}
