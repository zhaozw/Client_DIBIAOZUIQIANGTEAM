package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.pages.regist.RegisterFirstFragment;
import com.dgut.collegemarket.fragment.pages.regist.RegisterSecondFragment;
import com.dgut.collegemarket.fragment.pages.regist.RegisterThirdFragment;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegistersActivity extends Activity {

    private String APPKEY = "1a8522e867d05";
    private String APPSECURITY = "119df776ef1a4fbf570645a33bf93103";

    RegisterFirstFragment registerFirstFragment = new RegisterFirstFragment();
    RegisterSecondFragment registerSecondFragment = new RegisterSecondFragment();
    RegisterThirdFragment registerThirdFragment = new RegisterThirdFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registers);

        SMSSDK.initSDK(RegistersActivity.this,APPKEY,APPSECURITY);
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goStep3();
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        boolean smart = (Boolean)data;
                        if(smart) {
                            //通过智能验证
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegistersActivity.this,"通过智能验证",Toast.LENGTH_SHORT).show();
                                }
                            });
                            goStep3();
                        } else {
                            //依然走短信验证

                        }
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegistersActivity.this,"输入不正确",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        registerFirstFragment.setOnGoNextListener(new RegisterFirstFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep2();
            }
        });
        registerFirstFragment.setOnGoBackListener(new RegisterFirstFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                backOut();
            }
        });
        registerSecondFragment.setOnGoNextListener(new RegisterSecondFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep3();
            }
        });
        registerSecondFragment.setOnGoBackListener(new RegisterSecondFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                goBackStep1();
            }
        });
        registerThirdFragment.setOnGoNextListener(new RegisterThirdFragment.OnGoNextListener(){
            @Override
            public void onGoNext() {
                goStep4();
            }
        });
        registerThirdFragment.setOnGoBackListener(new RegisterThirdFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                goBackStep2();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.container, registerFirstFragment).commit();
    }

    void backOut(){
        finish();
        overridePendingTransition(R.anim.none,R.anim.slide_out_bottom);
    }

    void goBackStep1(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left)
                .replace(R.id.container, registerFirstFragment)
                .commit();
    }
    void goStep2(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right)
                .replace(R.id.container, registerSecondFragment)
                .addToBackStack(null)
                .commit();
    }
    void goBackStep2(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left)
                .replace(R.id.container, registerSecondFragment)
                .commit();
    }
    void goStep3(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right)
                .replace(R.id.container, registerThirdFragment)
                .addToBackStack(null)
                .commit();
    }
    void goStep4(){
        Intent itnt = new Intent(RegistersActivity.this,RegisterSuccessActivity.class);
        startActivity(itnt);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.none);
    }


}
