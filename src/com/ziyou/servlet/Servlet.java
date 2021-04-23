package com.ziyou.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.encryption.gm.sm9.MasterPublicKey;
import com.encryption.gm.sm9.SM9Curve;
import com.encryption.gm.sm9.SM9Utils;
import com.encryption.utils.Hex;
import com.sun.javafx.geom.Curve;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.encryption.gm.sm9.SM9Curve;

public class Servlet extends HttpServlet {
	
    String  P_A;
    String  g_c;
    String  c1;
    String  r_i;
    
    static BigInteger r_i_new ;
	private static BigInteger c1_new;
    static CurveElement P_A_new ;
//    MasterPublicKey masterPublickey = new MasterPublicKey();
    
    public static BigInteger getC1_new() {
		return c1_new;
	}
	
    public static CurveElement getP_A_new() {
	return P_A_new;
}
    
    public static BigInteger getR_i() {
		return r_i_new;
	}
    
	SM9Curve sm9Curve = new SM9Curve();
	SM9Utils sm9Utils = new SM9Utils();

	    /**
	     * Constructor of the object.
	     */ 
	    public Servlet() { 
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
	   

	    	
	        response.setContentType("text/html;charset=utf-8"); 
	        request.setCharacterEncoding("utf-8"); 
	        response.setCharacterEncoding("utf-8"); 
	        PrintWriter out = response.getWriter(); 

	        P_A = request.getParameter("P_A");
	        g_c = request.getParameter("g_c");
	        c1 = request.getParameter("c1");
//	        String username = request.getParameter("username");  //传过来的内容是：password=123&username=lili
//	        //这个直接在服务器上有打印

	        byte [] byteArray = Base64.getDecoder().decode(g_c);
	         P_A_new =  (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(P_A.replaceAll("\\s*", "")));
	         Element g_c_new = sm9Curve.sm9Pairing.getGT().newElementFromBytes(byteArray);
	         
	        
	        c1_new = new BigInteger(c1,16);

//	         r_i_new = new BigInteger(r_i.replaceAll("\\s*", "")).mod(sm9Curve.N);
            r_i_new = SM9Utils.genRandom(sm9Curve.random, sm9Curve.N);
            Element g_i_new = g_c_new.duplicate().pow(r_i_new);


            String base64Str = Base64.getEncoder().encodeToString(g_i_new.toBytes());
//            System.out.println("g_i_new:"+sm9Utils.toHexString(g_i_new.toBytes())); 
//            System.out.println("g_i_sendlast:"+ base64Str);
            out.write(base64Str);
	        
	        
//	        if(username.equals("456")&&pswd.equals("123")){ 
//	            //表示服务器端返回的结果 
//	            out.print("login is success!!!!"); 
//	        }else{ 
//	            out.print("login is fail!!!"); 
//	        } 
	       
	        out.flush(); 
	        out.close(); 
	    } 
	   
	    /**
	     * Initialization of the servlet. <br>
	     *
	     * @throws ServletException if an error occurs
	     */ 
	    public void init() throws ServletException { 
	        // Put your code here 
	    	System.out.println("连接没问题"); 
	    } 
	    
	   
//	} 
	    
	
}
