package com.dgut.collegemarket.fragment.widgets;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.myprofile.userInfo.UpdateUserNameActivity;

/**
 * Created by Administrator on 2016/12/21.
 */

public class InfoListFragment extends Fragment{

    TextView tvUserAttribute,tvUserContent;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_userinfo_list,null);
        tvUserAttribute = (TextView) view.findViewById(R.id.tv_user_attribute);
        tvUserContent = (TextView) view.findViewById(R.id.tv_user_content);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick();
            }
        });
        return view;
    }

    public void setTvUserAttribute(String text){
        tvUserAttribute.setText(text);
    }

    public void setTvUserContent(String text){
        tvUserContent.setText(text);
    }

    public interface InfoOnClickListener{
         void onclick();
    }
    InfoOnClickListener infoOnClickListener ;
    public void setInfoOnClickListener(InfoOnClickListener infoOnClickListener){
        this.infoOnClickListener =infoOnClickListener;
    }

    void onItemClick(){
        if(infoOnClickListener!=null){
            infoOnClickListener.onclick();
        }

    }
}
