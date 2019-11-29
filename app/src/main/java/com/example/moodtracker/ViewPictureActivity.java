package com.example.moodtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity allows user to view his/her
 * image for the reason of an event
 */
public class ViewPictureActivity extends AppCompatActivity {
    ImageView ivImage; //set image view for images

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

        final String image = this.getIntent().getStringExtra("image"); //get image from intent
        ivImage = findViewById(R.id.idImage);

        //store images
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
