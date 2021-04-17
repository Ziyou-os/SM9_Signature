package com.encryption.gm.sm9;

/*

SM9 private key division
Date:2021.4.15
 */

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import it.unisa.dia.gas.plaf.jpbc.field.curve.CurveElement;

public class ResultSignatureDivision {
    BigInteger h;
    CurveElement s_i;
    CurveElement s_m;

    public ResultSignatureDivision(BigInteger h, CurveElement s_i,CurveElement s_m)
    {
        this.h = h;
        this.s_i = s_i;
        this.s_m = s_m;
    }

    public CurveElement getS_i() {
        return s_i;
    }


        public static ResultSignatureDivision fromByteArray(SM9Curve curve, byte[] data) {
        byte[] bh = Arrays.copyOfRange(data, 0, SM9CurveParameters.nBits/8);
        byte[] bs = Arrays.copyOfRange(data, SM9CurveParameters.nBits/8, data.length);

        CurveElement e = curve.G1.newElement();
        e.setFromBytes(bs);
        return new ResultSignatureDivision(new BigInteger(1, bh), e,e);
    }

    public byte[] toByteArray()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] temp = SM9Utils.BigIntegerToBytes(h, SM9CurveParameters.nBits/8);
        bos.write(temp, 0, temp.length);
        temp = s_i.toBytes();
        bos.write(temp, 0, temp.length);
        return bos.toByteArray();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("sm9 signature:");
        sb.append(SM9Utils.NEW_LINE);
        sb.append("h:");
        sb.append(SM9Utils.NEW_LINE);
        sb.append(SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(h)));
        sb.append("s_i:");
        sb.append(SM9Utils.NEW_LINE);
        sb.append(SM9Utils.toHexString(SM9Utils.G1ElementToBytes(s_i)));
        sb.append(SM9Utils.NEW_LINE);
        sb.append("s_m:");
        sb.append(SM9Utils.NEW_LINE);
        sb.append(SM9Utils.toHexString(SM9Utils.G1ElementToBytes(s_m)));
        sb.append(SM9Utils.NEW_LINE);
        return sb.toString();
    }
}
