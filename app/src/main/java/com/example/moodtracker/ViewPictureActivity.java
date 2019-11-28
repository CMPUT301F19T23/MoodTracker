package com.example.moodtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewPictureActivity extends AppCompatActivity {
    ImageView ivImage;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                ivImage.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        final String image = this.getIntent().getStringExtra("image");
        ivImage = (ImageView) findViewById(R.id.idImage);

        //Glide.with(ViewPictureActivity.this.getApplicationContext()).load(image).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivImage);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(image);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        }).start();
    }

}
