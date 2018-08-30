package com.aditya.myapplication;


import java.io.IOException;

import java.util.Calendar;

import java.util.List;
import java.util.Locale;



import android.app.DatePickerDialog;

import android.app.TimePickerDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private FusedLocationProviderClient client;
    private GoogleMap mMap;
    Switch sw1;
    String str1, str2;
    private TextView  tv_time,tv_1,tv_2,tv_date;
    TextView vNo,vYes;
    boolean flag;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int PLACE_PICKER_REQUEST = 1;
    ImageView iv1,iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        tv_1 = (TextView)findViewById( R.id.tv_1 );
        tv_2 = (TextView)findViewById( R.id.tv_2 );
        tv_date = (TextView) findViewById( R.id.tv_date );
        tv_time = (TextView) findViewById( R.id.tv_time );
        sw1 = (Switch)findViewById( R.id.simpleSwitch );
        iv1 = (ImageView)findViewById( R.id.iv1 );
        iv2 = (ImageView)findViewById( R.id.iv2 );
        vNo =(TextView)findViewById( R.id.vNo );
        vYes =(TextView)findViewById( R.id.vYes );

        requestPermission();

        client = LocationServices.getFusedLocationProviderClient( this );

        // FOR GETTING CURRENT LOCATION

        tv_1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placepickerfunc();
                flag = true;
            }
        } );

        //FOR GETTING DESTINATION LOCATION
        tv_2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placepickerfunc();
                flag = false;
            }
        } );
// GET CALENDER DISPLAY
        iv1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                tv_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        } );

        // GET TIME DISPLAY

        iv2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                tv_time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        } );

// CURRENT LAT LONG
                if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                    return;
                }
                client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            //textView.setText(String.valueOf( x ));
                            ConvLatLong( lat, lng );
                        }

                    }
                });
                // SWITCH FOR YES/NO FLEXIBLE TIMINGS

                sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (sw1.isChecked()) {
                            str2 = sw1.getTextOn().toString();
                            vYes.setVisibility( View.GONE);
                            vNo.setVisibility( View.VISIBLE);

                            Toast.makeText(getApplicationContext(), "Switch2 - " + str2, Toast.LENGTH_SHORT).show();
                        } else {

                            vNo.setVisibility( View.GONE);
                            vYes.setVisibility( View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Switch2 - " + str2, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void placepickerfunc() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult( builder.build( MainActivity.this ), PLACE_PICKER_REQUEST );
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }



protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PLACE_PICKER_REQUEST) {
        if (resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace( data, this );

            LatLng  latLng = place.getLatLng();
            String toastMsg = String.format("Place: %s", place.getAddress());

            String lati = String.format( String.valueOf( latLng.latitude ) );
            String longi = String.format( String.valueOf( latLng.longitude ) );
            if (flag) {
               // tv_1.setText( lati + "----"+ longi );
                tv_1.setText( toastMsg );

            } else {
                //tv_2.setText( lati + "----"+ longi );
                tv_2.setText( toastMsg );
            }
        }

    }
}


    private void ConvLatLong(double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        tv_1.setText(address + " " +city );
       // Log.e("Address",""+address+" "+city+" "+state+" "+country);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}