package com.cjz.wasif.lifesaverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DriverAmbulanceType extends AppCompatActivity {

   private RadioButton Ambulance,ad_ambulance,bike;

   String Service;

   String driver_id;
   FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ambulance_type);
        auth=FirebaseAuth.getInstance();
        driver_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference driverdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(driver_id);

        Ambulance=(RadioButton)findViewById(R.id.Simple_ambulance);
        ad_ambulance=(RadioButton)findViewById(R.id.advance_ambulance);
        bike=(RadioButton)findViewById(R.id.bike);

Ambulance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(Ambulance.isChecked()){


            Service="Simple Ambulance";

            Map driver_Service= new HashMap();
            driver_Service.put("Service",Service);
            driverdatabase.updateChildren(driver_Service);

        }
    }
});


ad_ambulance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(ad_ambulance.isChecked()){
            Service="Advance Ambulance";

            Map driver_Service= new HashMap();
            driver_Service.put("Service",Service);
            driverdatabase.updateChildren(driver_Service);


        }
    }
});

bike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(bike.isChecked()){

            Service="bike";

            Map driver_Service= new HashMap();
            driver_Service.put("Service",Service);
            driverdatabase.updateChildren(driver_Service);

        }

    }
});


        Service="Simple Ambulance";

        Map driver_Service= new HashMap();
        driver_Service.put("Service",Service);
        driverdatabase.updateChildren(driver_Service);




    }
}
