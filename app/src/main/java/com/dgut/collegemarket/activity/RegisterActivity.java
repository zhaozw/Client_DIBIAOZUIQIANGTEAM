package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.PictrueInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.MD5;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {

    SimpleTextInputCellFragment account;
    SimpleTextInputCellFragment password;
    SimpleTextInputCellFragment passwordRepeat;
    SimpleTextInputCellFragment name;
    SimpleTextInputCellFragment email;

    PictrueInputCellFragment pictrue;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        account = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_account);
        password = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_password);
        passwordRepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_password2);
        pictrue = (PictrueInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_pictrue);
        name = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_name);
        email = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_email);
        register = (Button) findViewById(R.id.register);

        account.setInputType(InputType.TYPE_CLASS_NUMBER);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }

    /**
     * 提交注册信息
     */
    private void submit() {

        if (!isInputCorrect()) {
            return;
        }
        String accountS = account.getText();
        String passwordS = password.getText();
        String passwordRepeatS = passwordRepeat.getText();
        String emailS = email.getText();
        String nameS = name.getText();

        if (!passwordS.equals(passwordRepeatS)) {
            passwordRepeat.setEidtError("密码不一致");
            passwordRepeat.setClickable();
            return;
        }
        passwordS = MD5.getMD5(passwordS);

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", accountS)
                .addFormDataPart("passwordHash", passwordS)
                .addFormDataPart("email", emailS)
                .addFormDataPart("name", nameS);
        if (pictrue.getPngData() != null)
            multipartBuilder.addFormDataPart("avatar", "avatarName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , pictrue.getPngData()));

        final Request request = Server.requestBuilderWithApi("user/register")
                .post(multipartBuilder.build())
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("注册中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册失败")
                                .setMessage("原因：" + e.getLocalizedMessage())
                                .setCancelable(true)
                                .setNegativeButton("好", null)
                                .show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    final String result = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!request.equals(""))
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("注册成功")
                                        .setCancelable(true)
                                        .setNegativeButton("马上登陆", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        })
                                        .show();
                            else
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("注册失败")
                                        .setMessage("用户名已存在")
                                        .setCancelable(true)
                                        .setNegativeButton("重新注册", null)
                                        .show();
                        }
                    });

                }
            }
        });

    }

    /**
     * 判断用户输入
     * @return
     */
    private boolean isInputCorrect() {
        if (account.getText().equals("")) {
            account.setLayoutError("用户名不能为空");
            password.getText();//清除上一次密码为空的提示
            return false;
        } else if(!(account.getText().toString().length()>=8 && account.getText().toString().length()<=13)){
            account.setLayoutError("用户名输入格式不正确，请输入8-13个数字");
            return false;
        }

        if (password.getText().equals("")) {
            password.setLayoutError("密码不能为空");
            return false;
        }else if(!(password.getText().length()>=6 && password.getText().length()<=13)){
            password.setLayoutError("密码输入格式不正确，请输入6-13个字符");
            return false;
        }

        if (passwordRepeat.getText().equals("")) {
            passwordRepeat.setLayoutError("重复密码不能为空");
            return false;
        }else if(!(passwordRepeat.getText().length()>=6 && passwordRepeat.getText().length()<=13)){
            passwordRepeat.setLayoutError("重复密码输入格式不正确，请输入6-13个字符");
            return false;
        }

        if (email.getText().equals("")) {
            email.setLayoutError("邮箱地址不能为空");
            return false;
        }else if(!isEmail(email.getText())){
            email.setLayoutError("邮箱格式不正确");
            return false;
        }

        if (name.getText().equals(email.getText())) {
            name.setLayoutError("昵称不能为空");
            return false;
        }else if (name.getText().length()>10){
            name.setLayoutError("昵称长度超出范围");
            return false;
        }

        return true;

    }

    /**
     * 返回
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 判断邮箱格式
     * @param email
     * @return
     */
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    @Override
    protected void onResume() {
        super.onResume();

        account.setHintText("请输入账号(8-13位数字)");
        password.setHintText("请输入密码(6-13位字符)");
        password.setIsPassword(true);
        passwordRepeat.setHintText("请再输入一次密码(6-13位字符)");
        passwordRepeat.setIsPassword(true);
//        pictrue.setHintText("更改图片");
//        pictrue.setLableText("选择头像");
        email.setHintText("邮箱地址");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        name.setHintText("昵称");
    }

}
