package com.example.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.DB.MoodWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddActivity extends AppCompatActivity {

    private CheckBox cb;

    int s1 = -1, s2 = -1;
    private Spinner moodSpinner, situationSpinner;
    private List<String> moodList = new ArrayList<String>();
    private List<String> situationList = new ArrayList<String>();
    private MyAdapter<String> moodAdapter, situationAdapter;

    private EditText nameField;
    private TextView dateField, timeField;

    private Calendar cal = null;

    private String reason = "";
    private String image = "";
    private String email;
    private MoodWriter moodWriter;

    private int failCount = 0;

    // for map get current location
    private double latitude;
    private double longitude;
    private Location mlocation;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.EXTRA_USER);
        moodWriter =  ViewModelProviders.of(this).get(MoodWriter.class);
        moodWriter.init(email);

        cal = Calendar.getInstance();

        cb = findViewById(R.id.idAttach);

        nameField = findViewById(R.id.name_field);
        dateField = findViewById(R.id.date_field);
        timeField = findViewById(R.id.time_field);

        dateField.setText(MoodEvent.dayFormat.format(cal.getTime()));
        timeField.setText(MoodEvent.timeFormat.format(cal.getTime()));
        
        initSpinnerData();

        moodSpinner = findViewById(R.id.mood_spinner);
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    s1 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        situationSpinner = findViewById(R.id.situation_spinner);
        situationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    s2 = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 声明一个ArrayAdapter用于存放简单数据
        moodAdapter = new MyAdapter<>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                moodList);
        // 把定义好的Adapter设定到spinner中
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(moodList.size() - 1, true);

        // 声明一个ArrayAdapter用于存放简单数据
        situationAdapter = new MyAdapter<>(
                AddActivity.this, android.R.layout.simple_spinner_item,
                situationList);
        // 把定义好的Adapter设定到spinner中
        situationSpinner.setAdapter(situationAdapter);
        situationSpinner.setSelection(situationList.size() - 1, true);

        findViewById(R.id.option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, OptionActivity.class);
                startActivityForResult(intent, 1001);
            }
        });

        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

        moodWriter.getSuccess().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                Boolean b = (Boolean)o;
                if(b.booleanValue()){
                    finish();
                }else{
                    if(failCount >= 1){
                        // a bit janky, but have to do because false is returned on create
                        Toast.makeText(AddActivity.this, "Couldn't save mood. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    ++failCount;
                }
            }
        });


        //to retrieve a LocationManager for controlling location updates.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()){
                    getLocation();

                    Toast.makeText(AddActivity.this, "Latitude: " +latitude
                            + ", Longitude "+longitude, Toast.LENGTH_SHORT).show();
                    Log.d("Latitude", latitude+"");
                    Log.d("Longitude", longitude+"");
                }
            }

        });

    }

    private void getLocation() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mlocation = location;
                latitude = mlocation.getLatitude();
                longitude = mlocation.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
                if (checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddActivity.this, "Need GPS Permission!", Toast.LENGTH_SHORT).show();
                    return;
                }
                latitude = locationManager.getLastKnownLocation(s).getLongitude();
                longitude = locationManager.getLastKnownLocation(s).getLatitude();
            }

            @Override
            public void onProviderDisabled(String s) {
                return;
            }
        };
        getCurrentLocation();
    }

    public void getCurrentLocation() {
        // if no permission
        if (checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(AddActivity.this, "Need GPS Permission!", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: check network permission
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();
        Log.d("getCurrent_latitude", latitude+"");
        Log.d("getCurrent_longitude", longitude+"");

        // get the location every 2 seconds
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, locationListener);
    }


    class MyAdapter<T> extends ArrayAdapter {
        public MyAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            int i = super.getCount();
            return i > 0 ? i - 1 : i;
        }
    }

    private void initSpinnerData() {
        // this way, list dynamically grows as we add emotions
        for(int i = 0; i < MoodEvent.MOOD_DATA.length; ++i){
            moodList.add(MoodEvent.MOOD_DATA[i].getEmotion());
        }
        moodList.add("select a mood");

        // this way, list dynamically grows as we add situations
        for(int i = 0; i >= 0; ++i){
            String s = MoodEvent.intToSituation(i);
            if(!s.equals("Error")){
                situationList.add(s);
            }else{break;}
        }
        situationList.add("select a social situation");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            reason = data.getStringExtra("reason");
            image = data.getStringExtra("image");
        }
    }

    private void onConfirm() {
        boolean attach = false;
        if (cb.isChecked()) {
            attach = true;
        }else{
            latitude = 0;
            longitude = 0;
        }

        String name = nameField.getEditableText().toString();
        if (name.isEmpty()) {
            Toast.makeText(AddActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s1 == -1) {
            Toast.makeText(AddActivity.this, "please choose a mood", Toast.LENGTH_SHORT).show();
            return;
        }
        if (s2 == -1) {
            Toast.makeText(AddActivity.this, "please choose a social situation", Toast.LENGTH_SHORT).show();
            return;
        }

        moodWriter.createAndWriteMood(name, cal, situationList.get(s2), moodList.get(s1), reason, image, latitude, longitude);
    }

}
