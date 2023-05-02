package com.cjz.wasif.lifesaverapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class editNumberRegistration extends AppCompatActivity {

    private Button contact_Register;
    private EditText driver_contact;
    private String driver_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_number_registration);

        contact_Register=(Button)findViewById(R.id.contactRegister);
        driver_contact=(EditText)findViewById(R.id.contact_d);


        contact_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(driver_contact.getText().toString())){

                    Toast.makeText(editNumberRegistration.this, "please entered the contact", Toast.LENGTH_SHORT).show();
                    return;

                }
                if(driver_contact.getText().toString().length()>12){
                    Toast.makeText(editNumberRegistration.this, "Phone number length is long ", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(driver_contact.getText().toString().length()<12){
                    Toast.makeText(editNumberRegistration.this, "Phone number length is short ", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(driver_contact.getText().toString()!=null){

                    String driver_contact_check_number="+"+driver_contact.getText().toString();
                    Query driver_contact_check= FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(driver_contact_check_number);

                    driver_contact_check.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0){

                                Toast.makeText(editNumberRegistration.this, "Contact is Already Registered Please use different number!", Toast.LENGTH_LONG).show();
                            }

                            else{

                                driver_id=getIntent().getStringExtra("driver_id");

                                String Driver_Contact_number="+"+driver_contact.getText().toString();

                                Intent intent = new Intent(editNumberRegistration.this, otpedit.class);
                                intent.putExtra("DriverContact", Driver_Contact_number);
                                intent.putExtra("Driverid",driver_id);

                                startActivity(intent);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }






            }
        });


    }
}
