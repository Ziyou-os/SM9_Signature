package com.encryption.test;

import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.encryption.Main;
import com.encryption.gm.sm9.KGC;
import com.encryption.gm.sm9.MasterKeyPair;
import com.encryption.gm.sm9.PrivateKey;
import com.encryption.gm.sm9.PrivateKeyType;
import com.encryption.gm.sm9.ResultSignatureDivision;
import com.encryption.gm.sm9.SM9;
import com.encryption.gm.sm9.SM9Curve;
import com.encryption.gm.sm9.ResultSignature;
import com.example.sig_system.R;

/**
 * SM9 test.
 *
 * Created by Ziyou on 2021/4/16.
 */
public final class SM9Test {
    public SM9Test() {

    }


    /**
     * 这里做了私钥分割处理.
     *
     */


    MasterKeyPair signMasterKeyPair;
    String msg = "Chinese IBS standard";
    String id_A = "Alice";




    public void test_sm9_division(KGC kgc, SM9 sm9) throws Exception {
        Main.showMsg("\n----------------------------------------------------------------------\n");
        Main.showMsg("SM9签名测试\n");


        //生成签名主密钥对

        signMasterKeyPair = kgc.genSignMasterKeyPair();
        Main.showMsg("签名主私钥 ks:");
        Main.showMsg(signMasterKeyPair.getPrivateKey().toString());
        Main.showMsg("签名主公钥 Ppub-s:");
        Main.showMsg(signMasterKeyPair.getPublicKey().toString());


        //显示ID信息
        Main.showMsg("实体A的标识IDA:");
        Main.showMsg(id_A);

        //生成签名私钥
        PrivateKey signPrivateKey = kgc.genPrivateKey(signMasterKeyPair.getPrivateKey(), id_A, PrivateKeyType.KEY_SIGN);
        Main.showMsg("签名私钥 ds_A:");
        Main.showMsg(signPrivateKey.toString());


        //签名
        Main.showMsg("签名步骤中的相关值:");

        Main.showMsg("待签名消息 M:");
        Main.showMsg(msg);

        sm9.sign_division(signMasterKeyPair.getPublicKey(), signPrivateKey);
//        Main.showMsg(division.toString());
//        return division;
    }

    /**
     * 这里对两份私钥作了数字签名协同生成，还有验签.
     *
     */

//    public void test_sm9_sign2(String content){
//        SM9 sm9 = new SM9(sm9Curve);
//
//        sm9.sign_tem(content, msg.getBytes());
//
//    }

    public String test_sm9_sign3(SM9 sm9,String content_h){


        ResultSignature signature = sm9.sign_end(content_h);
        Main.showMsg("消息M的签名为(h,s):");
        Main.showMsg(signature.toString());

        //验签
        if (sm9.verify(signMasterKeyPair.getPublicKey(), id_A, msg.getBytes(), signature)) {
            Main.showMsg("verify OK");
        }
        else
            Main.showMsg("verify failed");
        return  signature.toString();

    }
}
