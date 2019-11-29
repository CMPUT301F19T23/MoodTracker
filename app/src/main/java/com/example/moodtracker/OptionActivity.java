package com.example.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OptionActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_PHOTO = 1001;
    private EditText nameField;
    private ImageView ivImage;

    private String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        nameField = findViewById(R.id.name_field);
        ivImage = findViewById(R.id.idImage);

         findViewById(R.id.option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                MultiImageSelector.create()
                        .showCamera(false)
                        //.count(IMAGE_SIZE - originImages.size() + 1)
                        .count(1)
                        .multi()
                        .start(OptionActivity.this, REQUEST_IMAGE_PHOTO);
                        */

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
            }
        });

        findViewById(R.id.idSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getEditableText().toString().trim();
                if (!name.isEmpty()) {
                    String[] names = name.split(" ");
                    if (names.length > 3) {
                        Toast.makeText(OptionActivity.this, "word count is more than 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.length() > 20) {
                        Toast.makeText(OptionActivity.this, "there are more than 20 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("reason", nameField.getEditableText().toString());
                intent.putExtra("image", image);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PHOTO && resultCode == RESULT_OK) {//从相册选择完图片
            /*
            //压缩图片
            ArrayList<String> images = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

            image = images.get(0);
            Glide.with(OptionActivity.this.getApplicationContext()).load(images.get(0)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivImage);
            */
            showPic(resultCode, data);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                ivImage.setImageBitmap(bitmap);
            }
        }
    };

    // 调用android自带图库，显示选中的图片
    private void showPic(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    //选择的就只是一张图片，所以cursor只有一条记录
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            final String path = cursor.getString(cursor.getColumnIndex("_data"));//获取相册路径字段
                            image = path;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //Log.v(TAG, "打开相册获取的图片sd卡路径:" + path);
                                    Bitmap bitmap = BitmapFactory.decodeFile(path);

                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = bitmap;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }
                    }
                }
            }
        } else {
            Log.d("OptionActivity", "放弃从相册选择");
        }
    }

}
