package com.cjz.wasif.lifesaverapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjz.wasif.lifesaverapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.sql.Driver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileDriver extends AppCompatActivity {

    private Button back,Confirm;
    private EditText Driver_contact_edit,Driver_Name_edit;
    private FirebaseAuth auth;
    private DatabaseReference mDriverDataBase;
    private String user_id;
    private String driver_name;
    private String driver_contact;
    private CircleImageView Driver_profile;
    private Uri resultUri;
    private String Driver_profile_image;
    private TextView display_driver_contact;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_driver);

        back=(Button)findViewById(R.id.back_id);
        Confirm=(Button)findViewById(R.id.confirm_id);
        display_driver_contact=(TextView)findViewById(R.id.driver_contact_id);

       // Driver_contact_edit=(EditText)findViewById(R.id.driver_contact_id);
        Driver_Name_edit=(EditText)findViewById(R.id.driver_name_id);
//       profile image of driver
        Driver_profile=(CircleImageView)findViewById(R.id.profile_image);
        Driver_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });






        auth=FirebaseAuth.getInstance();
        user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDriverDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        GetDriverInfo();


        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateDriveData();

                finish();
                return;

            }
        });


        display_driver_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditProfileDriver.this,editNumberRegistration.class);
               intent.putExtra("driver_id",user_id);
               startActivity(intent);
            }
        });


    }

    private void UpdateDriveData() {

        driver_name=Driver_Name_edit.getText().toString();
//        driver_contact=Driver_contact_edit.getText().toString();

        Map DriverInfo=new HashMap();
        DriverInfo.put("name",driver_name);

           // DriverInfo.put("phone",driver_contact);



        mDriverDataBase.updateChildren(DriverInfo);
        //store image on database
        if(resultUri!=null){

            final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("profile_images").child(user_id);
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //smaller size of Image
            ByteArrayOutputStream baos= new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);

            byte [] data= baos.toByteArray();

            UploadTask uploadTask=filepath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mDriverDataBase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
        finish();


    }
    private void GetDriverInfo() {

       mDriverDataBase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                   Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                   if(map.get("name")!=null) {
                       driver_name = map.get("name").toString();
                       Driver_Name_edit.setText(driver_name);
                   }
                   if(map.get("phone")!=null) {
                       driver_contact = map.get("phone").toString();
                     //  Driver_contact_edit.setText(driver_contact);
                       display_driver_contact.setText(driver_contact);

                   }

                   if(map.get("profileImageUrl")!=null) {
                       Driver_profile_image = map.get("profileImageUrl").toString();
                       Glide.with(getApplicationContext()).load(Driver_profile_image).into(Driver_profile);

                   }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode== Activity.RESULT_OK );
        {
            if(data!=null){
            final Uri imageUri= data.getData();
            resultUri=imageUri;
            Driver_profile.setImageURI(resultUri);
            }
            else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
