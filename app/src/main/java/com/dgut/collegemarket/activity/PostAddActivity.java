package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.PictrueInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.AnimationEffec;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAddActivity extends Activity {

    ImageView truckImg,back;
    SimpleTextInputCellFragment fragmentTitle = new SimpleTextInputCellFragment();
    SimpleTextInputCellFragment fragmentContent = new SimpleTextInputCellFragment();
    PictrueInputCellFragment fragmentPictrue = new PictrueInputCellFragment();
    SimpleTextInputCellFragment fragmentPrice = new SimpleTextInputCellFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);

        truckImg = (ImageView) findViewById(R.id.truck);
        fragmentTitle = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_title);
        fragmentContent = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_content);
        fragmentPictrue = (PictrueInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_pictrue);
        fragmentPrice = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_price);
        back = (ImageView) findViewById(R.id.iv_back);

        fragmentTitle.setHintText("标题");
        fragmentContent.setHintText("内容");
        fragmentContent.setLinesAndLength(5,200);
        fragmentPictrue.setHintText("添加照片");
        fragmentPrice.setHintText("悬赏金额");


        truckImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationEffec.setTransAniToRight(truckImg, 0, 700, 0, 0, 1500);
                submit();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 发布帖子
     */
    public void submit(){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .addFormDataPart("title",fragmentTitle.getText())
                .addFormDataPart("content",fragmentContent.getText())
                .addFormDataPart("reward",fragmentPrice.getText());

        if (fragmentPictrue.getPngData() != null)
            requestBody.addFormDataPart("albums", "albumsName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , fragmentPictrue.getPngData()));
        Request request = Server.requestBuilderWithApi("post/addpost")
                .post(requestBody.build())
                .build();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发贴中");
        progressDialog.setCancelable(false);
        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PostAddActivity.this).setMessage("服务器异常").show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PostAddActivity.this).setMessage("发帖成功").show();
                        finish();
                    }
                });
            }
        });
    }
}
