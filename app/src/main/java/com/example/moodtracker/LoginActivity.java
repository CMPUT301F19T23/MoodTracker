package com.example.moodtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moodtracker.bean.ResUtil;
import com.example.moodtracker.bean.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * This activity is the login activity, which allows the user to login using a given username and password.
 * @author xuhf0429
 */

public class LoginActivity extends AppCompatActivity {

    private boolean bPermission = false;
    private EditText etUsername;
    private EditText etPassword;

    public static final String EXTRA_USERPATH = "com.example.moodtracker.USERPATH"; // Filepath to get to the Users database
    private String userPathStr = "Users/";
    public static final String EXTRA_USER = "com.example.moodtracker.USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        request_permission();

        etUsername = (EditText) findViewById(R.id.idUsername);
        etPassword = (EditText) findViewById(R.id.idPassword);

        FirebaseFirestore db;
        final String TAG = "UserReg";
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");



        collectionReference.addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                ResUtil.listUser.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getData().get("password")));
                    String userName = doc.getId();
                    String userPassword = (String) doc.getData().get("password");
                    ResUtil.listUser.add(new User(userName, userPassword));
                }

            }
        });






        ((TextView) findViewById(R.id.idLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getEditableText().toString();
                String password = etPassword.getEditableText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "username or password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean bFind = false;
                for (int i = 0; i < ResUtil.listUser.size(); i++) {
                    if (ResUtil.listUser.get(i).getUsername().equals(username) && ResUtil.listUser.get(i).getPassword().equals(password)) {
                        Intent intent = new Intent(LoginActivity.this, MoodActivity.class);
                        startActivity(intent);
                        bFind = true;
                        break;
                    }
                }

                if (bFind == false) {
                    Toast.makeText(LoginActivity.this, "username or password is error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((TextView) findViewById(R.id.idCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(EXTRA_USERPATH, userPathStr);
                startActivity(intent);
            }
        });
    }

    private void request_permission() {
        // 拍照及文件权限申请
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 权限还没有授予，进行申请
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 3000);
        } else {
            bPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
        switch (requestCode) {
            case 3000:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        bPermission = false;
                        return;
                    }
                    bPermission = true;
                }
                break;
            default:
                break;
        }
    }

}
