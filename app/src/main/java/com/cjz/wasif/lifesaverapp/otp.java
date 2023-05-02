package com.cjz.wasif.lifesaverapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {

    private String DriverContactNumberl;
    private EditText otp_code;

    private Button verifyy;
    int randomNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otp_code=(EditText)findViewById(R.id.otpcode);
        verifyy=(Button)findViewById(R.id.verify);
        DriverContactNumberl=getIntent().getStringExtra("DriverContact");
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(DriverContactNumberl!=null){

            try {
                // Construct data
                String apiKey = "apikey=" + "lvp5dOLuagM-F1reYTHl6fossKYk2ozbH428inEybW";
                Random random= new Random();

                randomNumber=random.nextInt(999999);



                String message = "&message=" + "Hello welcome in L I F E  SAVER APP. Please inter the otp and continue your registration." +
                        "Thanks"
                        +"And please Don't share the otp code with any one"+"Your OTP CODE: "+randomNumber;
                String sender = "&sender=" + "L I F E SAVER";
                String numbers = "&numbers=" + DriverContactNumberl;

                // Send data
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
                String data = apiKey + numbers + message + sender;
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.getOutputStream().write(data.getBytes("UTF-8"));
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    stringBuffer.append(line);
                }
                rd.close();

                Toast.makeText(this, "OTP send Successfully", Toast.LENGTH_SHORT).show();

               // return stringBuffer.toString();
            } catch (Exception e) {
               // System.out.println("Error SMS "+e);
              //  return "Error "+e;
                Toast.makeText(this, "ERROR SMS"+e, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "ERROR"+e, Toast.LENGTH_SHORT).show();
            }



        }

        verifyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(otp_code.getText().toString())){

                    Toast.makeText(otp.this, "please entered the otp code", Toast.LENGTH_SHORT).show();
                }

                else {

                    if(randomNumber==Integer.valueOf(otp_code.getText().toString())){

                        Toast.makeText(otp.this, "You number is Successfully Registered", Toast.LENGTH_SHORT).show();

                        String driverRegisterd_number=DriverContactNumberl;
                        Boolean flag=true;
                        Intent intent = new Intent(otp.this, MainActivity.class);
                        intent.putExtra("DriverContact", driverRegisterd_number);
                        intent.putExtra("flags",flag);

                        startActivity(intent);


                    }
                    else {

                        Toast.makeText(otp.this, "You otp is not Valid Please retry...Thanks", Toast.LENGTH_LONG).show();
                    }

                }





            }
        });



    }

}