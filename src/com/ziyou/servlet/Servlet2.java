package com.ziyou.servlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.encryption.gm.sm9.SM9Curve;
import com.encryption.gm.sm9.SM9Utils;
import com.encryption.utils.Hex;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;

public class Servlet2 extends HttpServlet {

	
	SM9Curve sm9Curve = new SM9Curve();
	Servlet ser1 = new Servlet();
	
    /**
     * Constructor of the object.
     */ 
    public Servlet2() { 
        super(); 
    } 
   
    /**
     * Destruction of the servlet. <br>
     */ 
    public void destroy() { 
        super.destroy(); // Just puts "destroy" string in log 
        // Put your code here 
    } 
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { 
   
        this.doPost(request, response); 
       
    } 
   
    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */ 
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { 
   
    	String h;
    	
        response.setContentType("text/html;charset=utf-8"); 
        request.setCharacterEncoding("utf-8"); 
        response.setCharacterEncoding("utf-8"); 
        PrintWriter out = response.getWriter(); 
        
        h = request.getParameter("h");
//        h = request.getParameter("r_i");
        
        BigInteger h_new =new BigInteger(h,16);
//        CurveElement s_i = (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(h.replaceAll("\\s*", "")));

//        String username = request.getParameter("username");  //传过来的内容是：password=123&username=lili
//        //这个直接在服务器上有打印

        
        BigInteger tem = Servlet.getR_i().subtract(h_new.multiply(Servlet.getC1_new())).mod(sm9Curve.N);

//        System.out.println("tem:"+tem.toString()); 
//        CurveElement s_i = (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(S_i.replaceAll("\\s*", "")));;
        CurveElement s_i = Servlet.getP_A_new().duplicate().mul(tem) ;
        
//        System.out.println("r_i:"+SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(Servlet.getR_i())).replaceAll("\\s*", "")); 
//        System.out.println("h_new:"+SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(h_new))); 
//        System.out.println("h_new_replace:"+SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(h_new)).replaceAll("\\s*", "")); 
//        out.write(SM9Utils.toHexString(SM9Utils.G1ElementToBytes(s_i)));
//        out.write(SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(Servlet.getR_i())).replaceAll("\\s*", ""));
        out.write(SM9Utils.toHexString(SM9Utils.G1ElementToBytes(s_i)).replaceAll("\\s*", ""));
//        System.out.println("s_i:"+SM9Utils.toHexString(SM9Utils.G1ElementToBytes(s_i)).replaceAll("\\s*", ""));
       
        out.flush(); 
        out.close(); 
    }
    
    public void init() throws ServletException { 
        // Put your code here 
    	System.out.println("连接2没问题"); 
    } 
}
