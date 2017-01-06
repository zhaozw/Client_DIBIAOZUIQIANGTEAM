package com.dgut.collegemarket.fragment.pages.regist;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2017/1/5.
 */

public class RegisterSecondFragment extends Fragment{

    View view;
    Activity activity;

    ImageView ivBack;
    EditText etCode;
    TextView tvSend;
    Button btnNext;
    TextView tvTime;
    TextView tvTeap;

    private int recLen =60;
    Timer timer;
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    tvTime.setText(""+recLen);
                    if(recLen < 0){
                        timer.cancel();
                        recLen = 60;
                        tvTime.setVisibility(View.GONE);
                        tvSend.setEnabled(true);
                    }
            }
        }
    };

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            recLen--;
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.activity_regist_second,null);
            activity = getActivity();
            init();
        }
        return view;
    }

    void init(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("regist", Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString("phone","");
        final String country = sharedPreferences.getString("country","");

        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        etCode = (EditText) view.findViewById(R.id.et_code);
        tvSend = (TextView) view.findViewById(R.id.tv_recover_send);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTeap = (TextView) view.findViewById(R.id.tv_teap);

        tvTeap.setText("我们已经给你的手机号码"+country+"-"+phone+"发送了一条验证短信。");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSSDK.getVerificationCode(phone,country);
                tvSend.setEnabled(false);
                timer = new Timer();
                timer.schedule(task, 1000, 1000);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSSDK.submitVerificationCode(country,phone,etCode.getText().toString());
            }
        });
        tvSend.setEnabled(false);
        timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }

    public static interface OnGoNextListener{
        void onGoNext();
    }

    OnGoNextListener onGoNextListener;

    public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
        this.onGoNextListener = onGoNextListener;
    }

    void goNext(){
        if(onGoNextListener!=null){
            onGoNextListener.onGoNext();
        }
    }
    public static interface OnGoBackListener{
        void onGoBack();
    }
    OnGoBackListener onGoBackListener;
    public void setOnGoBackListener(OnGoBackListener onGoBackListener){
        this.onGoBackListener = onGoBackListener;
    }
    void goBack(){
        if(onGoBackListener!=null){
            onGoBackListener.onGoBack();
        }
    }
}
