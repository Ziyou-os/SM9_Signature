package com.encryption.gm.sm9;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.encryption.utils.Hex;
import com.example.sig_system.MainActivity;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;
import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveField;
import it.unisa.dia.gas.plaf.jpbc.util.Arrays;

/**
 * SM9 algorithm.
 *
 * Created by Ziyou on 2021/4/16.
 */
public class SM9 {
    protected SM9Curve mCurve;

    public SM9(SM9Curve curve){
        mCurve = curve;
    }



    public SM9Curve getCurve() {
        return mCurve;
    }

    public CurveElement getP_A() { return P_A; }

    public BigInteger getC1() {
        return c1;
    }

    public Element getG_c() {
        return g_c;
    }

    public BigInteger getH() {
        return h;
    }


    public CurveElement getS_i() {
        return s_i;
    }

    CurveElement P_A,s_i;
    Element g_c;
    BigInteger c1,c2;
    BigInteger h,r_m;
    SM9Curve sm9Curve = new SM9Curve();

    /**
     * SM9 sign_division.
     *
     * @param masterPublicKey signed master public key
     * @param privateKey signed private key
     *
     *
     */


    //用来得到一些参数，来第一次传给服务器；
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sign_division(MasterPublicKey masterPublicKey, PrivateKey privateKey)
    {


        c1 = SM9Utils.genRandom(mCurve.random, mCurve.N);
        c2 = SM9Utils.genRandom(mCurve.random, mCurve.N);



        BigInteger cn=(c1.add(c2)).modInverse(mCurve.N).mod(mCurve.N);

//        CurveElement P_A =  PrivateKey.d.duplicate().mul(cn);
        P_A =  privateKey.getD().duplicate().mul(cn);

        //Step1 : g = e(P1, Ppub-s)
        Element g = mCurve.pairing(mCurve.P1, masterPublicKey.Q);
        g_c = g.duplicate().pow(cn);

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sign_tem(String content, byte[] data)
    {
        boolean l;
        do {

            byte [] byteArray = Base64.getDecoder().decode(content);
            //g_i是乘法循环群中的元素，要用getGT
            Element g_i = sm9Curve.sm9Pairing.getGT().newElementFromBytes(byteArray);
//            Element g_i = g_c.duplicate().pow(r_i);
            r_m = SM9Utils.genRandom(mCurve.random, mCurve.N);
            Element g_m = g_c.duplicate().pow(r_m);

            //Step3 : calculate w=g^r
            Element w = g_m.duplicate().mul(g_i);

            //Step4 : calculate h=H2(M||w,N)
            h = SM9Utils.H2(data, w, mCurve.N).mod(mCurve.N);

            //step_add:check w whether equal with g^h
            l = w.isEqual(g_i.duplicate().mul(g_m).pow(h));

        } while(l);

    }

    public ResultSignature sign_end(String S_i){

        BigInteger tem2 = r_m.subtract(h.multiply(c2)).mod(mCurve.N);
        Log.e("s_i_String",S_i);
//         s_i = P_A.duplicate().mul(tem) ;
        CurveElement s_i_new = (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(S_i.replaceAll("\\s*", "")));
        CurveElement s_m = P_A.duplicate().mul(tem2) ;


//        CurveElement hash_G_1 = (CurveElement) sm9Curve.sm9Pairing.getG1().newElementFromBytes(Hex.decode(content.replaceAll("\\s*", "")));
        CurveElement s = s_i_new.duplicate().add(s_m);

        //Step7 : signature=(h,s)
        return new ResultSignature(h, s);
    }
    /**
     * SM9 verify.
     *
     * @param masterPublicKey signed master public key
     * @param id signed private key
     * @param data source data
     * @param signature SM9 signature value for source data
     * @return true present verify success.
     */
    public boolean verify(MasterPublicKey masterPublicKey, String id, byte[] data, ResultSignature signature)
    {
        // Step1 : check if h in the range [1, N-1]
        if(!SM9Utils.isBetween(signature.h, mCurve.N))
            return false;

        // Step2 : check if S is on G1
        if(!signature.s.isValid())
            return false;

        // Step3 : g = e(P1, Ppub-s)
        Element g = mCurve.pairing(mCurve.P1, masterPublicKey.Q);

        // Step4 : calculate t=g^h
        Element t = g.pow(signature.h);

        // Step5 : calculate h1=H1(IDA||hid,N)
        BigInteger h1 = SM9Utils.H1(id, SM9Curve.HID_SIGN, mCurve.N);

        // Step6 : P=[h1]P2+Ppubs
        CurveElement p = mCurve.P2.duplicate().mul(h1).add(masterPublicKey.Q);

        // Step7 : u=e(S,P)
        Element u = mCurve.pairing(signature.s, p);

        // Step8 : w=u*t
        Element w2 = u.mul(t);

        // Step9 : h2=H2(M||w,N)
        BigInteger h2 = SM9Utils.H2(data, w2, mCurve.N);

        return h2.equals(signature.h);
    }

}
