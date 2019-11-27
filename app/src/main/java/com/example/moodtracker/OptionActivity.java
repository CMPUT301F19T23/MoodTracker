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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This interface allows the user to view the image
 * for the reason of an event
 *
 * @author xuhf0429
 */
public class OptionActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_PHOTO = 1001;
    private EditText etName;
    private ImageView ivImage;

    private String image = ""; //set image as empty since no image selected yet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        etName = (EditText) findViewById(R.id.idName);
        ivImage = (ImageView) findViewById(R.id.idImage);

        ((TextView) findViewById(R.id.idOption)).setOnClickListener(new View.OnClickListener() {
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

                //switch to the image shops.
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
            }
        });

        //save the created event
        ((TextView) findViewById(R.id.idSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getEditableText().toString();
                if (!name.isEmpty()) {
                    String[] names = name.split(" ");
                    if (names.length > 3) {
                        Toast.makeText(OptionActivity.this, "word count is more than 20", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.length() > 20) {
                        Toast.makeText(OptionActivity.this, "char length is more than 20", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //save stated reasons and selected images.
                Intent intent = new Intent();
                intent.putExtra("reason", etName.getEditableText().toString());
                intent.putExtra("image", image);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PHOTO && resultCode == RESULT_OK) {//select available images from the image shops
            /*
            //zip picture
            ArrayList<String> images = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

            image = images.get(0);
            Glide.with(OptionActivity.this.getApplicationContext()).load(images.get(0)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivImage);
            */
            showPic(resultCode, data);
        }
    }

    //handle images
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                ivImage.setImageBitmap(bitmap);
            }
        }
    };

    // call the android photoshops, select an image
    private void showPic(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    //select an image, thus the cursor has one record only
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            final String path = cursor.getString(cursor.getColumnIndex("_data"));//获取相册路径字段
                            image = path;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
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
            Log.d("OptionActivity", "give up on selecting images");
        }
    }

}
