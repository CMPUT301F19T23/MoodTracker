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

/**
 * This is the activity for an user to
 * have option to enter textual reason when adding
 * an event, or use a photograph to represent the reason.
 */

public class OptionActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_PHOTO = 1001; //set the image size
    private EditText nameField; //set a reason field to be editable
    private ImageView ivImage; //set an image view

    private String image = ""; //set image as null

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        nameField = findViewById(R.id.name_field);
        ivImage = findViewById(R.id.idImage);

        //click on the option button
        findViewById(R.id.option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch to the media where to optionally select image
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                nameField.setText(intent.getStringExtra("reason"));
                startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
            }
        });

        //clicks on save button
        findViewById(R.id.idSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getEditableText().toString().trim();
                if (!name.isEmpty()) {
                    //check if the reason exceeds 3 words or more than 20 characters
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

                //confirm the correct reason or image
                Intent intent = new Intent();
                intent.putExtra("reason", nameField.getEditableText().toString());
                intent.putExtra("image", image);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    /**
     * set the photo requestCode, resultCode, and the data of the stored image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PHOTO && resultCode == RESULT_OK) {//select the image from the shop

            //zip image
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

    /**
     * call android photo store, show the selected image
     * @param resultCode
     * @param data
     */
    private void showPic(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) { //if the code of the image is correct
            if (data != null) { //get stored data from the database
                Uri uri = data.getData();
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null); //cursor gets the record
                    //selected an image, cursor only has one record
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            final String path = cursor.getString(cursor.getColumnIndex("_data"));//cursor gets the record
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
        } else {//if url is null, then cursor gets no data
            Log.d("OptionActivity", "give up selection");
        }
    }

}
