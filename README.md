# SM9_Signature
毕设做的一个移动终端用的SM9数字签名协同生成系统，传统的数字签名通常是在客户端上完成，这里加入了服务端，实现协同交互生成数字签名的功能。

The Bachelor’s Thesis of ‘A collaborative generation of digital signature scheme based on SM9 encryption algorithm’. On the basis of the original SM9 digital signature, added a private key split storage module (there are mobile terminal and server side respectively), which is more secure, as well as the collaborative generation module.
------
master放的是客户端代码，master2放的是服务端的

"master" is the codes about the server，"master2" is the codes about client

![界面图](https://github.com/Ziyou-os/SM9_Signature/blob/master/View.jpg)

总共实际上就只有2个Button，一个生成数字签名，实现了客户端和服务端交互计算生成数字签名，另一个验签用来验证协同生成的数字签名的正确性与否，并把它显示在下面的TextVIew上，。

先挂一个界面图，具体的功能之后论文写完后再来补充！（求轻喷）

先补充一个这两天用到的

In fact, there are only two 'Button' in total, one of which generates the digital signature, realizing the interactive calculation and generation of the digital signature between the client and the server, and the other one is used to verify the correctness of the co-generated digital signature and display it on the following 'TextVIew'.



## 网络编程中，一些数据格式的转换：| Data Format Conversion in Network Programming：
  
由于系统需要做到客户端和服务端协同生成数字签名，因此未免会涉及一些密码学中特殊格式交换的问题，如CurveElement，Element，BigInteger...... 在查资料的过程中，有看到博主使用setFromHash函数，对Hash了解不多，所以没有这样做，换了一种间接方式，把格式转换成byte以及String来实现：

Because the system needs to generate digital signatures collaboratively with the client and server, it may involve some special data format exchange problems in cryptography, such as CurveElement, Element, BigInteger......When searching information on websites, I saw that some bloggers used the 'setFromHash function' but I did not know much about Hash function. Instead, I changed the format into byte and String in an indirect way:

客户端通用的作法是将它们转换成字节数组，再转换成String字符串传输，服务端相应的做这个逆过程.

The common practice on the client side is to convert them to byte arrays and then to String transfers, and the server does the reverse process accordingly.

1，CurveElement： CurveElement在算法中一般是G1群，如P_A；
  
  1，CurveElement： The CurveElement in the algorithm is generally G1 group, such as P_A
```Java
//The client transmits data
String P_A = SM9Utils.toHexString(SM9Utils.G1ElementToBytes(sm9.getP_A()));

//The receiver receives data
String P_A = request.getParameter("P_A");
CurveElement P_A_new =  (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(P_A));//Here, decode function is decoding to byte arrays
```

2，Element： Element在算法中一般是GT群，如g_c； 【NOTE：在传g_c的时候，由于它的数据量比较大，有碰到数据丢失的问题。调试中有和小伙伴讨论到这个，他用的是字节byte传输没出错，后来有想到可以借用base64编码来传输，base64传输结果的数据量更大；】

 2，Element： The Element in this algorithm is generally GT groups, such as g_c;
 【NOTE：When transferring 'g_C', there is a problem of data loss due to its large amount of data. During the debugging proccess, I discussed this problem with my friends, and he used byte transmission without any error. Thus, I thought that I could use Base64 encoding to transmit, because the data volume of base64 transmission result is larger；】
```Java
//The client transmits data
byte[] g_c_byte = sm9.getG_c().toBytes();
String base64Str = Base64.getEncoder().encodeToString(g_c_byte);

//The receiver receives data
String g_c = request.getParameter("g_c");
byte [] byteArray = Base64.getDecoder().decode(g_c);
Element g_c_new = sm9Curve.sm9Pairing.getGT().newElementFromBytes(byteArray);

//the base64 encoding decoding mode
String base64Str = Base64.getEncoder().encodeToString(g_c_byte);//byte2String
byte [] byteArray = Base64.getDecoder().decode(g_c);//String2byte
```

3，BigInteger： BigInteger在算法中貌似用的更多，如c1，h...

  3，BigInteger： BigInteger seems to be used more in algorithms，such as c1，h...
```Java
//The client transmits data
String c1 = SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(sm9.getC1()));
String h = SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(sm9.getH()));

//The receiver receives data
String c1 = request.getParameter("c1");
BigInteger c1_new = new BigInteger(c1,16);
String h = request.getParameter("h");
BigInteger h_new =new BigInteger(h,16);
```

NOTE：一般传输都转换到了16进制字符串，但实际上应该可以直接转换成base64统一格式来传输，封装一个Base64Utils类，这样代码或许会看上去短一点...

NOTE：Normally the transfer is converted to a hexadecimal string, but it should be possible to transfer directly to the Base64 unified format, encapsulating a Base64Utils class, which might make the code look shorter...
