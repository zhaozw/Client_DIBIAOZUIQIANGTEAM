package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.LoginActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.fragment.widgets.InfoListFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/21.
 */

public class UserInfoActivity extends Activity{

    User user;
    TextView tvTitle,tvExit;
    AvatarView userAvatar ;
    RelativeLayout rlAvatar,rlUpdatePassword;

    InfoListFragment fragmentUserName = new InfoListFragment();
    InfoListFragment fragmentUserEmail = new InfoListFragment();
    InfoListFragment fragmentUserXp= new InfoListFragment();
    InfoListFragment fragmentUsercoins = new InfoListFragment();
    InfoListFragment fragmentUserCreatedate = new InfoListFragment();

    Button btnLoginOut;

    final int REQUESTCODE_CAMERA = 1;
    final int REQUESTCODE_ALBUM = 2;
    final int REQUESTCODE_CUTTING=3;

    Uri uri;
    String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);

        tvTitle = (TextView) findViewById(R.id.tv_user_title);
        tvExit = (TextView) findViewById(R.id.tv_exit);
        rlUpdatePassword = (RelativeLayout) findViewById(R.id.rl_change_password);
        userAvatar = (AvatarView) findViewById(R.id.av_user_avatar);
        rlAvatar = (RelativeLayout) findViewById(R.id.fragment_avatar);
        fragmentUserName = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_name);
        fragmentUserEmail = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_email);
        fragmentUserXp = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_xp);
        fragmentUsercoins = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_coins);
        fragmentUserCreatedate = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_createdate);
        btnLoginOut = (Button) findViewById(R.id.btn_login_out);

        tvTitle.setText("个人信息");
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rlAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onImageViewClicked();
            }
        });
        InfoListFragment.InfoOnClickListener infoOnClickListener = new InfoListFragment.InfoOnClickListener() {
            @Override
            public void onclick() {
                Intent itnt = new Intent(UserInfoActivity.this, UpdateUserNameActivity.class);
                startActivity(itnt);
                overridePendingTransition(R.anim.slide_in_left,R.anim.none);
            }
        };
        fragmentUserName.setInfoOnClickListener(infoOnClickListener);

        rlUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(UserInfoActivity.this,UpdatePasswordActivity.class);
                startActivity(itnt);
            }
        });
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("auto",false);
                editor.commit();
                CurrentUserInfo.online = false;
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            user = (User) getIntent().getSerializableExtra("user");
            userAvatar.load(user);
            fragmentUserName.setTvUserAttribute("昵称");
            fragmentUserName.setTvUserContent(user.getName());
            fragmentUserEmail.setTvUserAttribute("邮箱");
            fragmentUserEmail.setTvUserContent(user.getEmail());
            fragmentUserXp.setTvUserAttribute("总经验");
            fragmentUserXp.setTvUserContent(user.getXp()+"");
            fragmentUsercoins.setTvUserAttribute("金币");
            fragmentUsercoins.setTvUserContent(user.getCoin()+"");
            fragmentUserCreatedate.setTvUserAttribute("创建时间");
            fragmentUserCreatedate.setTvUserContent(DateFormat.format("yyyy-MM-dd hh:ss",user.getCreateDate()).toString());
        }catch (Exception e){
            Toast.makeText(UserInfoActivity.this,"获取用户信息失败，请检查网络设置",Toast.LENGTH_SHORT).show();
        }
    }

    void onImageViewClicked() {
        String[] items = {
                "拍照",
                "相册"
        };
        new AlertDialog.Builder(UserInfoActivity.this).setTitle("选择照片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                pickFromAlbum();
                                break;
                            default:
                        }
                    }
                }).setNegativeButton("取消", null).show();

    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = Uri.fromFile(getNewFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_CAMERA);
    }

    /**
     * 获取本地图片
     */
    private void pickFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_ALBUM);
    }

    Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUESTCODE_CAMERA) {
            startPhotoZoom(uri);
        } else if (requestCode == REQUESTCODE_ALBUM) {
            uri = Uri.fromFile(getNewFile());
            uri = data.getData();
            url = selectImage(UserInfoActivity.this,uri);
            startPhotoZoom(uri);
        }else if(requestCode == REQUESTCODE_CUTTING){
            try {
                bitmap =  BitmapFactory.decodeFile(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateAvatar();
        }
    }
    /**
     * 裁剪图片方法实现
     * @param uris
     */
    public void startPhotoZoom(Uri uris) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uris, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public  String selectImage(Context context,Uri selectedImage){
        if(selectedImage!=null){
            String uriStr=selectedImage.toString();
            String path=uriStr.substring(10,uriStr.length());
            if(path.startsWith("com.sec.android.gallery3d")){
                return null;
            }
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /**
     * 获取图片数据流
     * @return
     */
    public byte[] getPngData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap!=null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        else {
            return null;
        }
        byte[] datas = baos.toByteArray();

        return datas;
    }

    /**
     * 创建新文件
     * @return
     */
    public File getNewFile()  {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        String sdcardPath=Environment.getExternalStorageDirectory().getPath();
        File imgPath=new File(sdcardPath+"/img/");
        if(!imgPath.exists()){
            imgPath.mkdirs();
        }
        url = imgPath.getPath()+"/"+user.getId()+".png";
        File userImgFile=new File(imgPath.getPath()+"/"+user.getId()+".png");
        if(!userImgFile.exists()){
            try {
                userImgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userImgFile;
    }
    /**
     * 上传头像
     */
    public void updateAvatar(){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(getPngData()!=null){
            multipartBuilder.addFormDataPart("avatar", "avatarName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , getPngData()));
        }
        final Request request = Server.requestBuilderWithApi("user/update/avatar")
                .post(multipartBuilder.build())
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("上传中");
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
                        new AlertDialog.Builder(UserInfoActivity.this)
                                .setTitle("上传失败")
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
                            Toast.makeText(UserInfoActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            userAvatar.load(user);
                        }
                    });
                }
            }
        });
    }
}