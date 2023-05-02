package com.cjz.wasif.lifesaverapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjz.wasif.lifesaverapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button btnReg, btnSign;
    RelativeLayout rootLayout;
    private  int backpress=0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    FirebaseAuth auth;
    Button forget_pass;

    FirebaseDatabase db;

    DatabaseReference users;
    ProgressDialog loadingBar;
    String first_Pass, Second_pass;

    Boolean ch=false;
    TextView Riderapp;







    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());


        setContentView(R.layout.activity_main);



        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");




        FirebaseUser firebaseUser=auth.getCurrentUser();

        if(firebaseUser!= null && firebaseUser.isEmailVerified()){
            startActivity(new Intent(MainActivity.this, Home.class));
        }

        //button handels
        btnReg = (Button) findViewById(R.id.RegisterBtn);
        btnSign = (Button) findViewById(R.id.SignInBtn);



        //handle for Progress bar
        loadingBar = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        //handle for layout
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        //event listner
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedToInternet()){
                showRegisterDialog();
              //  startActivity(new Intent(MainActivity.this,numberRegistration.class));

            }else
                {
                    Toast.makeText(MainActivity.this, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedToInternet()){
                showloginDialog();
            }else{

                    Toast.makeText(MainActivity.this, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        String driver_number_verified = getIntent().getStringExtra("DriverContact");
        final Boolean flag=getIntent().getBooleanExtra("flags",false);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnectedToInternet()){




              if(flag==true){
                  showRegisterDialog();

            }
            else{

                  startActivity(new Intent(MainActivity.this,numberRegistration.class));

              }


            }
            else {

                    Toast.makeText(MainActivity.this, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    //registeration dialog
    private void showRegisterDialog() {

        if(isConnectedToInternet()) {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("REGESTER");
            dialog.setMessage("Please use the email to register");
            LayoutInflater inflater = LayoutInflater.from(this);

            View register_layout = inflater.inflate(R.layout.layout_registration, null);


            final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
            final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPass);
            final MaterialEditText edtPasswordCon = register_layout.findViewById(R.id.edtPassConfirm);
            final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);


            dialog.setView(register_layout);


            //set button POSITIVE
            if (isConnectedToInternet()){
                dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                        //check Validation
                        if (TextUtils.isEmpty(edtName.getText().toString())) {

                            // Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Please enter the Name", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (TextUtils.isEmpty(edtEmail.getText().toString())) {

                            // Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Please enter the Email", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (TextUtils.isEmpty(edtPassword.getText().toString())) {

                            //Snackbar.make(rootLayout, "Please enter Password", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(edtPassword.getText().toString())) {

                            //Snackbar.make(rootLayout, "Please enter the Password", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Please enter the Password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (edtPassword.getText().toString().length() < 6) {

                            // Snackbar.make(rootLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Password Length short", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(edtPasswordCon.getText().toString())) {

                            //Snackbar.make(rootLayout, "Please enter the Password", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Please enter the Confirm Password", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        first_Pass = edtPassword.getText().toString();
                        Second_pass = edtPasswordCon.getText().toString();

                        if (!first_Pass.equals(Second_pass)) {

                            Toast.makeText(MainActivity.this, "Password and Confirm Password Don't match ", Toast.LENGTH_SHORT).show();
                            return;

                        } else {


                            loadingBar.setTitle("Please wait :");

                            loadingBar.setMessage("While system is performing processing on your data...");
                            loadingBar.show();
                        }


                        //Register New User


                        auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(final AuthResult authResult) {
                                //save user to data base
                                User user = new User();
                                user.setEmail(edtEmail.getText().toString());
                                user.setName(edtName.getText().toString());

                                String driver_number_verified = getIntent().getStringExtra("DriverContact");
                                user.setPhone(driver_number_verified);
                                user.setPassword(edtPassword.getText().toString());


                                //Use UI-id to key

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Snackbar.make(rootLayout, "Register success fully!!!!", Snackbar.LENGTH_SHORT).show();


                                                //email verification
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            loadingBar.dismiss();
                                                            Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(MainActivity.this, RegistrationCompleted.class));
                                                        } else {
                                                            loadingBar.dismiss();
                                                            Toast.makeText(MainActivity.this, "Email is not Valid for Registration", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();

                                                loadingBar.dismiss();
                                                Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        loadingBar.dismiss();

                                        // Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                        Toast.makeText(MainActivity.this, "You Already Registered or Use different Email! or Use correct Formate of Email: Example@gmail.com", Toast.LENGTH_LONG).show();


                                    }
                                });


                        //set Negative button for dialog


                    }
                });


        }
        else{

                Toast.makeText(this, "No Internet Connection!!!!", Toast.LENGTH_SHORT).show();
            }

            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();


                }
            });


            dialog.show();
        }


    }


    //make  login dialog

  public void  showloginDialog(){


      AlertDialog.Builder dialog = new AlertDialog.Builder(this);

      dialog.setTitle("SIGN IN");
      dialog.setMessage("Please use the email to Sign In");
      LayoutInflater inflater = LayoutInflater.from(this);
      View login_layout = inflater.inflate(R.layout.layout_login, null);


      final MaterialEditText edtEmail = login_layout.findViewById(R.id.edtEmail);
      final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPass);


      TextView forget_pass=(TextView) login_layout.findViewById(R.id.forgetPassword_customer);
// forget button

      forget_pass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(MainActivity.this, ForgotPasswordCustomer.class));
          }
      });






      dialog.setView(login_layout);



    //set button POSITIVE
    dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {


        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();


            //check Validation
            if (TextUtils.isEmpty(edtEmail.getText().toString())) {


                // Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();

                Toast.makeText(MainActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }


            if (TextUtils.isEmpty(edtPassword.getText().toString())) {

                //  Snackbar.make(rootLayout, "Please enter the Password", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (edtPassword.getText().toString().length() < 6) {

                // Snackbar.make(rootLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                return;

            }





            else {


                loadingBar.setTitle("Please wait :");

                loadingBar.setMessage("While system is performing processing on your data...");
                loadingBar.show();

                //Querry For selecting the Only driver data from database

                final String email_check = edtEmail.getText().toString();
                Query email_Querry = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(email_check);
                email_Querry.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Toast.makeText(MainActivity.this, "Hello Driver", Toast.LENGTH_SHORT).show();

                            auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    loadingBar.dismiss();
                                    String user_id = auth.getCurrentUser().getUid();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();


                                    if (firebaseUser != null && firebaseUser.isEmailVerified()) {

                                        startActivity(new Intent(MainActivity.this, Home.class));

                                        Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, Home.class));


                                    } else {


                                        finish();
                                        Toast.makeText(MainActivity.this, "Your Account is not Verified! Check your Email For Verification..", Toast.LENGTH_LONG).show();


                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    loadingBar.dismiss();
                                    // Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            Toast.makeText(MainActivity.this, "You are not Driver!", Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Data base ERRORS
                    }
                });


                //Login


            }
        }

    });




      dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

              loadingBar.dismiss();
              dialog.dismiss();



          }
      });


      dialog.show();

  }


    public void onBackPressed(){
        backpress = (backpress + 1);
        if(backpress==1) {
            Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
        }

          else{
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
                moveTaskToBack(true);


        }
    }



    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }








}



