# SM9_Signature
毕设做的一个移动终端用的SM9数字签名协同生成系统，在原有SM9数字签名基础上加了私钥分割存储（分别存在移动端和服务器端，这样更安全），以及数字签名协同生成的功能。
------

![界面图](https://github.com/Ziyou-os/SM9_Signature/blob/master/View.jpg)

总共实际上就只有2个Button，一个生成数字签名，实现了客户端和服务端交互计算生成数字签名，另一个验签用来验证协同生成的数字签名的正确性与否，并把它显示在下面的TextVIew上，。

先挂一个界面图，具体的功能之后论文写完后再来补充！（求轻喷）

先补充一个这两天用到的
##网络编程中，一些数据格式的转换：
  由于系统需要做到客户端和服务端协同生成数字签名，因此未免会涉及一些密码学中特殊格式交换的问题，如CurveElement，Element，BigInteger......
  在查资料的过程中，有看到博主使用setFromHash函数，对Hash了解不多，所以没有这样做，换了一种间接方式，把格式转换成byte以及String来实现：
  
  客户端通用的作法是将它们转换成字节数组，再转换成String字符串传输，服务端相应的做这个逆过程.
  1，CurveElement： CurveElement在算法中一般是G1群，如P_A；
```Java
//客户端传送数据
String P_A = SM9Utils.toHexString(SM9Utils.G1ElementToBytes(sm9.getP_A()));

//服务端接收数据
String P_A = request.getParameter("P_A");
CurveElement P_A_new =  (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(P_A));//这里decode就是解码成byte数组
```

 2，Element： Element在算法中一般是GT群，如g_c；
 【NOTE：在传g_c的时候，由于它的数据量比较大，有碰到数据丢失的问题。调试中有和小伙伴讨论到这个，他用的是字节byte传输没出错，后来有想到可以借用base64编码来传输，base64传输结果的数据量更大；】
```Java
//客户端传送数据
byte[] g_c_byte = sm9.getG_c().toBytes();
String base64Str = Base64.getEncoder().encodeToString(g_c_byte);

//服务端接收数据
String g_c = request.getParameter("g_c");
byte [] byteArray = Base64.getDecoder().decode(g_c);
Element g_c_new = sm9Curve.sm9Pairing.getGT().newElementFromBytes(byteArray);

//关于base64的编码解码方式
String base64Str = Base64.getEncoder().encodeToString(g_c_byte);//byte2String
byte [] byteArray = Base64.getDecoder().decode(g_c);//String2byte
```

  3，BigInteger： BigInteger在算法中貌似用的更多，如c1，h...
```Java
//客户端传送数据
String c1 = SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(sm9.getC1()));
String h = SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(sm9.getH()));

//服务端接收数据
String c1 = request.getParameter("c1");
BigInteger c1_new = new BigInteger(c1,16);
String h = request.getParameter("h");
BigInteger h_new =new BigInteger(h,16);
```

NOTE：一般传输都转换到了16进制字符串，但实际上应该可以直接转换成base64统一格式来传输，封装一个Base64Utils类，这样代码或许会看上去短一点...
