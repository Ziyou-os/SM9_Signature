package com.encryption.gm.sm9;

import java.math.BigInteger;

/**
 * SM9 master private key.
 *
 * A master private key is a big random integer between [1, N-1].
 *
 * Created by yaoyuan on 2019/4/13.
 */
public class MasterPrivateKey {
    BigInteger d;

    public MasterPrivateKey(BigInteger d) {
        this.d = d;
    }

    public static MasterPrivateKey fromByteArray(byte[] source) {
        BigInteger d = new BigInteger(1, source);
        return new MasterPrivateKey(d);
    }

    public byte[] toByteArray() {
        return SM9Utils.BigIntegerToBytes(d, SM9CurveParameters.nBits/8);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("sm9 master private key:");
        sb.append(SM9Utils.NEW_LINE);
        sb.append(SM9Utils.toHexString(SM9Utils.BigIntegerToBytes(d, SM9CurveParameters.nBits/8)));

        return sb.toString();
    }
}
