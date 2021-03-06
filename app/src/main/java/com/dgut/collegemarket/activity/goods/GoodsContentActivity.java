package com.dgut.collegemarket.activity.goods;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.MainActivity;
import com.dgut.collegemarket.activity.orders.OrdresCreateActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.util.PxtDipTransform;
import com.squareup.picasso.Picasso;

public class GoodsContentActivity extends AppCompatActivity {
    private static final int RESULT_CREATE_ORDERS = 200;
    ImageView albumsImg;
    ImageView avatarImg;
    public static Goods goods;
    Toolbar toolbar;
    public static User publisher;
    int width, height;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        goods = (Goods) getIntent().getSerializableExtra("goods");
        publisher = goods.getPublishers();
        setContentView(R.layout.activity_goods_content);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        albumsImg = (ImageView) findViewById(R.id.image_albums);
        avatarImg = (ImageView) findViewById(R.id.img_avatar);
        setPopupView();
        fab= (FloatingActionButton) findViewById(R.id.fab_buy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.showAtLocation(findViewById(R.id.goods_content), Gravity.BOTTOM, 0, 0);
                fab.setEnabled(false);

            }
        });
        ViewTreeObserver viewTreeObserver = albumsImg.getViewTreeObserver();
        viewTreeObserver
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        albumsImg.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        width = PxtDipTransform.px2dip(GoodsContentActivity.this, albumsImg.getWidth());
                        height = PxtDipTransform.px2dip(GoodsContentActivity.this, albumsImg.getHeight());
                        Picasso.with(GoodsContentActivity.this).load(Server.serverAddress + goods.getAlbums()).resize(width, height).centerInside().into(albumsImg);
                    }
                });

        Picasso.with(this).load(Server.serverAddress + goods.getPublishers().getAvatar()).resize(50, 50).centerInside().error(R.drawable.unknow_avatar).into(avatarImg);

        initView();


    }


    TextView titleText;
    TextView contentText;


    private void initView() {

        titleText = (TextView) findViewById(R.id.text_title);
        contentText = (TextView) findViewById(R.id.text_content);
        titleText.setText(goods.getTitle());

        contentText.setText(goods.getContent());
    }


    TextView quantityText;
    TextView priceText;
    TextView preQuantityText;
    TextView buyQuantityText;
    PopupWindow mPopupWindow;
    Button settlementBt;
    Button addBt;
    Button deleteBt;

    private void setPopupView() {
        View popupView = getLayoutInflater().inflate(R.layout.layout_popupwindow, null);

        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, PxtDipTransform.dip2px(GoodsContentActivity.this,275), true);

        mPopupWindow.setAnimationStyle(R.style.mystyle);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.goods_bg));
        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();

                    }
                    return true;
                }
                return false;
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                fab.setEnabled(true);
            }
        });
        quantityText = (TextView) popupView.findViewById(R.id.text_quantity);
        priceText = (TextView) popupView.findViewById(R.id.text_price);
        preQuantityText = (TextView) popupView.findViewById(R.id.text_quantity_buy_pre);
        buyQuantityText = (TextView) popupView.findViewById(R.id.text_quantity_buy);
        settlementBt = (Button) popupView.findViewById(R.id.btn_orders_create);
        addBt = (Button) popupView.findViewById(R.id.bt_add);
        deleteBt = (Button) popupView.findViewById(R.id.bt_delete);

        quantityText.setText(goods.getQuantity() + "");
        priceText.setText(goods.getPrice() + "");

        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(preQuantityText.getText().toString());
                if (num < goods.getQuantity())
                    num++;
                preQuantityText.setText(num + "");
                buyQuantityText.setText(num + "");
                settlementBt.setText("结算（" + num * goods.getPrice() + "元）");
            }
        });

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(preQuantityText.getText().toString());
                if (num > 0)
                    num--;
                preQuantityText.setText(num + "");
                buyQuantityText.setText(num + "");
                settlementBt.setText("结算（" + num * goods.getPrice() + "元）");
            }
        });
        settlementBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(preQuantityText.getText().toString());
                if (num > 0) {
                    Intent intent = new Intent(GoodsContentActivity.this, OrdresCreateActivity.class);
                    intent.putExtra("goods", goods);
                    intent.putExtra("quantity", num);
                    startActivityForResult(intent, RESULT_CREATE_ORDERS);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            MainActivity.ordersPage=2;
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
    }


}
