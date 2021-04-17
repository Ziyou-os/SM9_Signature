package com.example.sig_system;

import androidx.appcompat.app.AppCompatActivity;

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
import com.encryption.gm.sm9.ResultSignatureDivision;
import com.encryption.gm.sm9.SM9;
import com.encryption.gm.sm9.SM9Curve;
import com.encryption.gm.sm9.SM9Utils;
import com.encryption.test.SM9Test;
import com.encryption.utils.Hex;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveField;

public class MainActivity extends AppCompatActivity {

    private String content; // 声明一个输入文本内容的编辑框对象
    private Button button; // 声明一个发表按钮对象
    private Handler handler; // 声明一个Handler对象
    private String result = ""; // 声明一个代表显示内容的字符串
    private TextView resultTV; // 声明一个显示结果的文本框对象
    private Button but_rec;

    SM9Curve sm9Curve = new SM9Curve();
    ResultSignatureDivision division;

    KGC kgc = new KGC(sm9Curve);
    SM9 sm9 = new SM9(sm9Curve);

    public String getContent() {
        return content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resultTV = (TextView) findViewById(R.id.result); // 获取显示结果的TextView组件

        button = (Button) findViewById(R.id.button); // 获取“发表”按钮组件
        but_rec = (Button) findViewById(R.id.button_receive);
        SM9Test sm9Test = new SM9Test();
        Main.showMsg(sm9Curve.toString());
        // 为按钮添加单击事件监听器
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    division = sm9Test.test_sm9_division(kgc,sm9);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                content = SM9Utils.toHexString(SM9Utils.G1ElementToBytes(division.getS_i()));


//                Log.e("content1",content);

                SM9Curve sm9Curve = new SM9Curve();




                // 创建一个新线程，用于从网络上获取文件
                new Thread(new Runnable() {
                    public void run() {
                        send();
                        Message m = handler.obtainMessage(); // 获取一个Message
                        handler.sendMessage(m); // 发送消息
                    }
                }).start(); // 开启线程

                Toast.makeText(MainActivity.this, "数据发送成功",
                        Toast.LENGTH_SHORT).show();

                sm9.s_itoZero();

            }
        });

        but_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm9Test.test_sm9_sign2(division,sm9,result);
                resultTV.setText(result); // 显示获得的结果
                Toast.makeText(MainActivity.this, "verify OK!",
                        Toast.LENGTH_SHORT).show();

            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (result != null) {
//                    resultTV.setText(result); // 显示获得的结果
//                    content.setText(""); // 清空内容编辑框
//                    nickname.setText(""); // 清空昵称编辑框
                }
                super.handleMessage(msg);
            }
        };
    }

    public void send() {
        String target = "http://192.168.31.80:8081/blog/dealPost.jsp";	//要提交的目标地址
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


            String param = "content="
                    + URLEncoder.encode(content, "utf-8");	//连接要提交的数据


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
}