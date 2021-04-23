package com.example.sig_system;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.encryption.Main;
import com.encryption.gm.sm9.KGC;

import com.encryption.gm.sm9.SM9;
import com.encryption.gm.sm9.SM9Curve;
import com.encryption.gm.sm9.SM9Utils;
import com.encryption.test.SM9Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveField;

public class MainActivity extends AppCompatActivity {

    //    private String content; // 声明一个输入文本内容的编辑框对象
    private String P_A;
    private String  c1;
    private String base64Str;
    byte[] g_c_byte;
    private Handler handler; // 声明一个Handler对象
    private Handler handler2; // 声明一个Handler对象
    private String result = ""; // 声明一个代表显示内容的字符串
    private TextView resultTV; // 声明一个显示结果的文本框对象
    private Button but_rec;
    private Button but_end;
    private String result2 = "";
    String msg = "Chinese IBS standard";

    SM9Curve sm9Curve = new SM9Curve();

    KGC kgc = new KGC(sm9Curve);
    SM9 sm9 = new SM9(sm9Curve);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        button = (Button) findViewById(R.id.button); // 获取“发表”按钮组件
        but_rec = (Button) findViewById(R.id.button_receive);
        but_end = (Button)findViewById(R.id.button_end);
        SM9Test sm9Test = new SM9Test();
        resultTV = (TextView) findViewById(R.id.result);

        try {
            sm9Test.test_sm9_division(kgc,sm9);
        } catch (Exception e) {
            e.printStackTrace();
        }

        P_A = SM9Utils.toHexString(SM9Utils.G1ElementToBytes(sm9.getP_A()));
        g_c_byte = sm9.getG_c().toBytes();
        c1 = SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(sm9.getC1())).replaceAll("\\s*", "");

        // 创建一个新线程，用于从网络上获取文件
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                send();
                Message m = handler.obtainMessage(); // 获取一个Message
                handler.sendMessage(m); // 发送消息
            }
        }).start(); // 开启线程
//
        Toast.makeText(MainActivity.this, "初始化已完成",
                Toast.LENGTH_SHORT).show();





        but_rec.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                sm9.sign_tem(result, msg.getBytes());

                // 创建一个新线程，用于从网络上获取文件
                new Thread(new Runnable() {
                    public void run() {
                        send2();
                        Message m1 = handler2.obtainMessage(); // 获取一个Message
                        handler2.sendMessage(m1); // 发送消息
                    }
                }).start(); // 开启线程


            }
        });


        but_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result_end = sm9Test.test_sm9_sign3(sm9,result2);
                resultTV.setText("生成数字签名：\n" + result_end); // 显示获得的结果
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void send() {
        String target = "http://192.168.31.80:8081/ServletTest/Servlet";	//要提交的目标地址
        URL url;
        try {
            url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection(); // 创建一个HTTP连接
            urlConn.setRequestMethod("POST"); // 指定使用POST请求方式
            urlConn.setDoInput(true); // 向连接中写入数据
            urlConn.setDoOutput(true); // 从连接中读取数据
            urlConn.setUseCaches(false); // 禁止缓存
            urlConn.setInstanceFollowRedirects(true);	//自动执行HTTP重定向
            urlConn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded"); // 设置内容类型
            DataOutputStream out = new DataOutputStream(
                    urlConn.getOutputStream()); // 获取输出流


            base64Str = Base64.getEncoder().encodeToString(g_c_byte);

            String param = "P_A=" + URLEncoder.encode(P_A, "utf-8")+ "&g_c=" +
                    URLEncoder.encode(base64Str, "utf-8")+ "&c1=" +
                    URLEncoder.encode(c1, "utf-8");	//连接要提交的数据

            out.writeBytes(param);//将要传递的数据写入数据输出流
            out.flush();	//输出缓存
            out.close();	//关闭数据输出流
            // 判断是否响应成功
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(
                        urlConn.getInputStream()); // 获得读取的内容
                BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
                String inputLine = null;
                while ((inputLine = buffer.readLine()) != null){
                    result += inputLine;
                }
                in.close();	//关闭字符输入流
            }
            urlConn.disconnect();	//断开连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void send2() {
        String target = "http://192.168.31.80:8081/ServletTest/Servlet2";	//要提交的目标地址
        URL url;
        try {
            url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection(); // 创建一个HTTP连接
            urlConn.setRequestMethod("POST"); // 指定使用POST请求方式
            urlConn.setDoInput(true); // 向连接中写入数据
            urlConn.setDoOutput(true); // 从连接中读取数据
            urlConn.setUseCaches(false); // 禁止缓存
            urlConn.setInstanceFollowRedirects(true);	//自动执行HTTP重定向
            urlConn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded"); // 设置内容类型
            DataOutputStream out = new DataOutputStream(
                    urlConn.getOutputStream()); // 获取输出流

            String h = SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(sm9.getH())).replaceAll("\\s*", "");
            Log.e("h_Main", h);
            String param2 = "h=" + URLEncoder.encode(h, "utf-8");	//连接要提交的数据
            Log.e("param2", param2);

            out.writeBytes(param2);//将要传递的数据写入数据输出流
            out.flush();	//输出缓存
            out.close();	//关闭数据输出流
            // 判断是否响应成功
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(
                        urlConn.getInputStream()); // 获得读取的内容
                BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
                String inputLine = null;
                while ((inputLine = buffer.readLine()) != null){
                    result2 += inputLine;
                }
                in.close();	//关闭字符输入流
            }
            urlConn.disconnect();	//断开连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}