package com.whatsapp.numbers.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatsapp.numbers.BuildConfig;
import com.whatsapp.numbers.Config;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.firebase.CategoryFragment;
import com.whatsapp.numbers.utils.Constant;
import com.whatsapp.numbers.utils.GDPR;
import com.whatsapp.numbers.utils.HttpTask;
import com.whatsapp.numbers.utils.Tools;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String SELECTED_TAG = "selected_index";
    private static int selectedIndex;
    private final static int COLLAPSING_TOOLBAR = 0;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    BroadcastReceiver broadcastReceiver;
    NavigationView navigationView;
    private long exitTime = 0;
    private AdView adView;
    View view;
    String androidId;
    SharedPreferences preferences;

    DatabaseReference mDatabase;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        view = findViewById(android.R.id.content);


        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        GDPR.updateConsentStatus(this);


        mDatabase= FirebaseDatabase.getInstance().getReference();

        //fetch data from dataBase
       // fetchDataFromDataBase();

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        if (savedInstanceState != null) {
            navigationView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }

        selectedIndex = COLLAPSING_TOOLBAR;
       /* getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new FragmentTabRecent(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                .commit();*/
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView,  new CategoryFragment(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                .commit();

        //loadBannerAd();

        onReceiveNotification();
        handleNotification();

        setupNavigationDrawer(mToolbar);

    }

    /*private void fetchDataFromDataBase() {

        DatabaseReference rootRefrence = FirebaseDatabase.getInstance().getReference().child(Config.firbaseRootChild);
        rootRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Config.IsAppUpdated = Boolean.parseBoolean(dataSnapshot.child("isAppUpdated").getValue().toString());
                Config.pakageNname = String.valueOf(dataSnapshot.child("pakageName").getValue().toString());
                Config.isFbBanner = Boolean.parseBoolean(dataSnapshot.child("isFbBanner").getValue().toString());
                Config.isFbInterstitial = Boolean.parseBoolean(dataSnapshot.child("isFbInterstitial").getValue().toString());
                Config.ENABLE_ADMOB_BANNER_ADS = Boolean.parseBoolean(dataSnapshot.child("isAdmobBanner").getValue().toString());
                Config.ENABLE_ADMOB_INTERSTITIAL_ADS = Boolean.parseBoolean(dataSnapshot.child("isAdmobInterstitial").getValue().toString());
                Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO = Boolean.parseBoolean(dataSnapshot.child("isAdmobInterstitialOnVideoClick").getValue().toString());
                Config.ADMOB_INTERSTITIAL_ADS_INTERVAL = Integer.parseInt(dataSnapshot.child("admobInterstitialAdsInterval").getValue().toString());
                Config.adMobBannerId = String.valueOf(dataSnapshot.child("adMobBannerId").getValue().toString());
                Config.adMobInterstitialId = String.valueOf(dataSnapshot.child("adMobInterstitialId").getValue().toString());
                Config.adMobPublisherId = String.valueOf(dataSnapshot.child("adMobPublisherId").getValue().toString());
                Config.fbBannerId = String.valueOf(dataSnapshot.child("fbBannerId").getValue().toString());
                Config.fbInterstitialId = String.valueOf(dataSnapshot.child("fbInterstitialId").getValue().toString());

                if(Config.IsAppUpdated){
                    showUpdate(Config.pakageNname);
                }

                loadBannerAd(Config.ENABLE_ADMOB_BANNER_ADS);
                loadFbBanner(Config.isFbBanner,Config.fbBannerId);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {


            case R.id.drawer_category:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerView,  new CategoryFragment(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;



            case R.id.drawer_rate:

                final String appName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }

                return true;

            case R.id.drawer_more:

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));

                return true;

            case R.id.drawer_share:

                String app_name = android.text.Html.fromHtml(getResources().getString(R.string.app_name)).toString();
                String share_text = android.text.Html.fromHtml(getResources().getString(R.string.share_text)).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, app_name + "\n\n" + share_text + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;



        }
        return false;
    }

    public void setupNavigationDrawer(Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }




    private void onFailRequest() {
        if (Tools.isConnect(this)) {
            sendRegistrationIdToBackend();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.failed_text), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRegistrationIdToBackend() {

        Log.d("FCM_TOKEN", "Send data to server...");

        String token = preferences.getString("fcm_token", null);
        String appVersion = BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")";
        String osVersion = currentVersion() + " " + Build.VERSION.RELEASE;
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;

        if (token != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("user_android_token", token));
            nameValuePairs.add(new BasicNameValuePair("user_unique_id", androidId));
            nameValuePairs.add(new BasicNameValuePair("user_app_version", appVersion));
            nameValuePairs.add(new BasicNameValuePair("user_os_version", osVersion));
            nameValuePairs.add(new BasicNameValuePair("user_device_model", model));
            nameValuePairs.add(new BasicNameValuePair("user_device_manufacturer", manufacturer));
            new HttpTask(null, MainActivity.this, Config.ADMIN_PANEL_URL + "/token-register.php", nameValuePairs, false).execute();
            Log.d("FCM_TOKEN_VALUE", token + " " + androidId);
        }

    }

    private void updateRegistrationIdToBackend() {

        Log.d("FCM_TOKEN", "Update data to server...");
        String token = preferences.getString("fcm_token", null);
        if (token != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("user_android_token", token));
            nameValuePairs.add(new BasicNameValuePair("user_unique_id", androidId));
            new HttpTask(null, MainActivity.this, Config.ADMIN_PANEL_URL + "/token-update.php", nameValuePairs, false).execute();
            Log.d("FCM_TOKEN_VALUE", token + " " + androidId);
        }

    }

    public static String currentVersion() {
        double release = Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
        String codeName = "Unsupported";
        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
        else if (release < 5) codeName = "Kit Kat";
        else if (release < 6) codeName = "Lollipop";
        else if (release < 7) codeName = "Marshmallow";
        else if (release < 8) codeName = "Nougat";
        else if (release < 9) codeName = "Oreo";
        return codeName;
    }

    public void handleNotification() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String url = intent.getStringExtra("link");
        if (id != null) {
            if (id.equals("0")) {
                if (!url.equals("")) {
                    Intent e = new Intent(Intent.ACTION_VIEW);
                    e.setData(Uri.parse(url));
                    startActivity(e);
                }
                Log.d("FCM_INFO", " id : " + id);
            }
        }
    }

    public void onReceiveNotification() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Constant.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    final String id = intent.getStringExtra("id");
                    final String title = intent.getStringExtra("title");
                    final String message = intent.getStringExtra("message");
                    final String image_url = intent.getStringExtra("image_url");
                    final String url = intent.getStringExtra("link");

                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                    View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setView(mView);

                    final TextView notification_title = mView.findViewById(R.id.title);
                    final TextView notification_message = mView.findViewById(R.id.message);
                    final ImageView notification_image = mView.findViewById(R.id.big_image);

                    if (id != null) {
                        if (id.equals("0")) {
                            if (!url.equals("")) {
                                notification_title.setText(title);
                                notification_message.setText(Html.fromHtml(message));
                                Picasso.with(MainActivity.this)
                                        .load(image_url.replace(" ", "%20"))
                                        .placeholder(R.drawable.ic_thumbnail)
                                        .into(notification_image);
                                alert.setPositiveButton("Open link", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        startActivity(intent);

                                    }
                                });
                                alert.setNegativeButton(getResources().getString(R.string.dialog_dismiss), null);
                            } else {
                                notification_title.setText(title);
                                notification_message.setText(Html.fromHtml(message));
                                Picasso.with(MainActivity.this)
                                        .load(image_url.replace(" ", "%20"))
                                        .placeholder(R.drawable.ic_thumbnail)
                                        .into(notification_image);
                                alert.setPositiveButton(getResources().getString(R.string.dialog_ok), null);
                            }
                        } else {
                            notification_title.setText(title);
                            notification_message.setText(Html.fromHtml(message));
                            Picasso.with(MainActivity.this)
                                    .load(image_url.replace(" ", "%20"))
                                    .placeholder(R.drawable.ic_thumbnail)
                                    .into(notification_image);

                            alert.setPositiveButton(getResources().getString(R.string.dialog_read_more), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alert.setNegativeButton(getResources().getString(R.string.dialog_dismiss), null);
                        }
                        alert.setCancelable(false);
                        alert.show();
                    }

                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constant.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constant.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }


}
