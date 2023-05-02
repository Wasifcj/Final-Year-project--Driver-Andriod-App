package com.cjz.wasif.lifesaverapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, RoutingListener {


    private GoogleMap mMap;
    MaterialAnimatedSwitch location_switch;
    private Boolean IsLoggedOut=false;

    private Boolean keep_app_working= false;

    GoogleApiClient mGoogleApiClient;
    Location mlastlocation;
    LocationRequest mlocationRequest;
    FirebaseAuth auth;
    FirebaseAuth newauth;
    String CustomerID;


    FirebaseDatabase db;
    FirebaseUser current_user;

    DatabaseReference users;
    // SupportMapFragment mapFragment;

    private String cusromer_id = "";

    boolean CloudNotification = false;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};


    //Customer Card view

    private CardView Customer_cardview;
    private TextView CustomerName,CustomerContact;
    private CircleImageView CustomerImage;

    //End ride
    private Button endride;

    //calculations for Emergency-bike ride

    private Button start_counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

  // handel for bike Start counter
String Servicee="bike";
  start_counter=(Button)findViewById(R.id.counter_start);
  String currentFirebaseUser = FirebaseAuth.getInstance().getUid() ;
 // final DatabaseReference usersss=FirebaseDatabase.getInstance().getReference("Users");
        Query queryyy=FirebaseDatabase.getInstance().getReference("Users").orderByChild("Service").equalTo(Servicee);

queryyy.keepSynced(true);
queryyy.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if(dataSnapshot.getChildrenCount()>0){

    start_counter.setVisibility(View.VISIBLE);

}
else {
    start_counter.setVisibility(View.INVISIBLE);
}
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});



//Customer cardView handel

        Customer_cardview=(CardView)findViewById(R.id.customer_cardView);
        CustomerImage=(CircleImageView)findViewById(R.id.Customer_profile_image);
        CustomerName=(TextView)findViewById(R.id.Customer_name);
        CustomerContact=(TextView)findViewById(R.id.Customer_contact);
//end ride button handel

        endride=(Button)findViewById(R.id.end);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        UpdateNavForDriver();

        polylines = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mapFragment.getMapAsync(this);
        }

        startService(new Intent(Home.this, OnAppKilled.class));
        location_switch = (MaterialAnimatedSwitch) findViewById(R.id.location_switch);

        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                if (isChecked) {

                    ConnectDriver();
                    Toast.makeText(Home.this, "You are online", Toast.LENGTH_SHORT).show();




                } else {


                    disconnectDriver();
                    Toast.makeText(Home.this, "You are offline", Toast.LENGTH_SHORT).show();


                }

            }
        });


        getAssignedCustomer();


            endride.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EndRide();

                }
            });







    }

    private void EndRide() {




        String User_ID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer Request");

       // GeoFire geoFire = new GeoFire(ref);
      //  geoFire.removeLocation(User_ID);
        DatabaseReference Ref= FirebaseDatabase.getInstance().getReference("Users").child(User_ID);

        Ref.child("Customer Rider ID").removeValue();
        pickupMarker.remove();


        DatabaseReference customerdatabase=FirebaseDatabase.getInstance().getReference("Customer Request");
         GeoFire geoFire = new GeoFire(customerdatabase);
         geoFire.removeLocation(cusromer_id);

        Customer_cardview.setVisibility(View.GONE);


        DatabaseReference tracking= FirebaseDatabase.getInstance().getReference("Tracking").child(User_ID);
        tracking.removeValue();












    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            IsLoggedOut=true;
            keep_app_working=true;
            disconnectDriver();
            FirebaseAuth.getInstance().signOut();


            startActivity(new Intent(Home.this,MainActivity.class));





            this.finish();
        } else if (id == R.id.nav_Ambulance_type) {

            startActivity(new Intent(Home.this,DriverAmbulanceType.class));


        } else if (id == R.id.edit_profile) {

           startActivity(new Intent(Home.this,EditProfileDriver.class));



        }
        else if (id == R.id.nav_contact) {
            startActivity(new Intent(Home.this,Driver_contact.class));

        }

        else if (id == R.id.nav_share) {

            Intent myintent = new Intent(Intent.ACTION_SEND);
            myintent.setType("text/plain");
            String shareBody= "L I F E SAVER";
            String shareSub=  "L I F E SAVER is basically first Android app in Pakistan which gives the online booking Ambulance and you can also facilitate with first aid at your door step! available on play store soon! ";
            myintent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
            myintent.putExtra(Intent.EXTRA_TEXT,shareSub);
            startActivity(Intent.createChooser(myintent,"Share using"));



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void UpdateNavForDriver(){

        String id;
        newauth=FirebaseAuth.getInstance();
        id=newauth.getUid();
        current_user=newauth.getCurrentUser();
        users=FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        final TextView driver_name_nav=headerView.findViewById(R.id.nav_driver_name);
        TextView driver_email_nav=headerView.findViewById(R.id.nav_email_driver);
        final CircleImageView Driver_image_nav=headerView.findViewById(R.id.nav_driver_image);



        driver_email_nav.setText(current_user.getEmail());


        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null) {
                        String   driver_name = map.get("name").toString();
                        driver_name_nav.setText(driver_name);
                    }

                    if(map.get("profileImageUrl")!=null) {
                        String   Driver_profile_image = map.get("profileImageUrl").toString();
                        Glide.with(getApplicationContext()).load(Driver_profile_image).into(Driver_image_nav);

                    }



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void getAssignedCustomer() {


        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).child("Customer Rider ID");
        final android.support.v7.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    cusromer_id = dataSnapshot.getValue().toString();

                    // getAssignedCustomerPickUpLocation();
                    Notification_builder();

                } else {
                    erasePolylines();
                    cusromer_id = "";
                    if (pickupMarker != null) {

                        pickupMarker.remove();
                    }
                    if (assignedCustomerPickupLocationRefListner != null) {
                        assignedCustomerPickUpLocation.removeEventListener(assignedCustomerPickupLocationRefListner);
                    }
                    Customer_cardview.setVisibility(View.GONE);
                    endride.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void Notification_builder() {
        final Vibrator vibrator;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(20000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(20000);
        }

        dialog.setTitle("AMBULANCE");
        dialog.setMessage("Please Accept Ambulance Emergency Request!");

        dialog.setPositiveButton("accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getAssignedCustomerPickUpLocation();
                getCustomerDataOnCardView();
                vibrator.cancel();

            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }

    private void getCustomerDataOnCardView() {

        endride.setVisibility(View.VISIBLE);
       Customer_cardview.setVisibility(View.VISIBLE);
       auth=FirebaseAuth.getInstance();
       // user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
      DatabaseReference  mCustomerDataBase= FirebaseDatabase.getInstance().getReference().child("Customers").child(cusromer_id);

        mCustomerDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null) {
                      String  customer_name = map.get("name").toString();
                        CustomerName.setText("Customer Name: "+customer_name);
                    }
                    if(map.get("phone")!=null) {
                     String   customer_contact = map.get("phone").toString();
                        CustomerContact.setText("Customer Contact# "+customer_contact);
                    }

                    if(map.get("profileImageUrl")!=null) {
                     String   Customer_profile_image = map.get("profileImageUrl").toString();
                        Glide.with(getApplicationContext()).load(Customer_profile_image).into(CustomerImage);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }






    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickUpLocation;
    private ValueEventListener assignedCustomerPickupLocationRefListner;

    private void getAssignedCustomerPickUpLocation() {


        // String driverID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        assignedCustomerPickUpLocation = FirebaseDatabase.getInstance().getReference().child("Customer Request").child(cusromer_id).child("l");
        assignedCustomerPickupLocationRefListner = assignedCustomerPickUpLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && !cusromer_id.equals("")) {

                    List<Object> map = (List<Object>) dataSnapshot.getValue();

                    double locationLat = 0;
                    double locationLng = 0;

                    if (map.get(0) != null) {

                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1) != null) {

                        locationLng = Double.parseDouble(map.get(1).toString());

                    }

                    LatLng Customer_pickup = new LatLng(locationLat, locationLng);


                    pickupMarker = mMap.addMarker(new MarkerOptions().position(Customer_pickup).title("Pickup Location"));
                    if (pickupMarker != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(Customer_pickup));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        getRouteToMarker(Customer_pickup);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });

    }

    private void getRouteToMarker(LatLng customer_pickup) {

        Routing routing = new Routing.Builder().key("AIzaSyAugx-er0pHQZXRJlMeZnHEN0NDq0X3BnI")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mlastlocation.getLatitude(),mlastlocation.getLongitude()), customer_pickup)
                .build();
        routing.execute();
    }


    private void ConnectDriver()

    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);

    }

    private void disconnectDriver() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        String user_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Driver available");

        GeoFire geoFire= new GeoFire(ref);
        geoFire.removeLocation(user_ID);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onLocationChanged(Location location) {

        if(getApplicationContext()!= null) {


            mlastlocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


            if(pickupMarker==null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));



            }

            // mMap.moveCamera(center);
            //mMap.animateCamera(zoom);


            final String user_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference refavailable = FirebaseDatabase.getInstance().getReference("Driver available");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("Driver Working");

            GeoFire geoFireavailable = new GeoFire(refavailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);





            switch (cusromer_id){

                case "":
                    geoFireWorking.removeLocation(user_ID);
                    geoFireavailable.setLocation(user_ID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;


                default:
                    geoFireavailable.removeLocation(user_ID);
                    geoFireWorking.setLocation(user_ID,new GeoLocation(location.getLatitude(),location.getLongitude()));

                    break;
            }








        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        //    ActivityCompat.requestPermissions(Welcome.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        //   }
        //  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    final  int LOCATION_REQUEST_CODE=1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);


                }
                else {

                    Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                }
                break;

                //if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            }
        }

    }

    //Draw route for driver
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }
    private void erasePolylines(){

        for (Polyline line : polylines){

            line.remove();
        }
        polylines.clear();
    }

    @Override
    protected void onStop() {

        super.onStop();
        if(!IsLoggedOut){
            if(keep_app_working==true){

            disconnectDriver();
        }
         }

    }


}




