package com.whatsapp.numbers.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.ads.NativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.firebase.model.Girl;
import com.whatsapp.numbers.firebase.model.MyCategory;
import com.whatsapp.numbers.firebase.myadapter.MySubCategoryAdapter;
import com.whatsapp.numbers.utils.MyConstant;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DatabaseReference mRef;
    private List<Object> list = new ArrayList<>();
    private String adPosition = "2";
    private int counter = 0;
    private String nativeId = "0";
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);


        init();
        setupRecyclerView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(getIntent().getStringExtra(MyConstant.CATEGORY_NAME));
            }
        },2000);

        loadFromFirebase();
    }

    private void loadFbBanner(String id) {

        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(SubCategoryActivity.this, id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

    }
    private void init(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);
    }
    private NativeAd getNative(String id){
        return new NativeAd(this , id);
    }
    private void setupRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
    }
    private void loadData(String category){
        mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.SUB_CATEGORY).child(category);
        mRef.keepSynced(true);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProgressBar.setVisibility(View.GONE);
                if (list.isEmpty()){
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String name = (String) postSnapshot.child(MyConstant.CATEGORY_NAME).getValue();
                        String image = (String) postSnapshot.child(MyConstant.CATEGORY_IMAGE).getValue();
                        String age = (String) postSnapshot.child(MyConstant.AGE).getValue();
                        String country = (String) postSnapshot.child(MyConstant.COUNTRY).getValue();

                        //  String number = (String) postSnapshot.child(MyConstant.NUMBER).getValue();


                        if (counter % Integer.parseInt(adPosition) == 0 && counter != 0){
                            list.add(getNative(nativeId));
                        }
                        list.add(new Girl(name , image , age , country));

                        counter++;

                    }
                }

                MySubCategoryAdapter adapter = new MySubCategoryAdapter(SubCategoryActivity.this , list);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String banner = (String) snapshot.child(MyConstant.BANNER).getValue();
                String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();

                adPosition = (String) snapshot.child(MyConstant.POSITION).getValue();
                nativeId = (String) snapshot.child(MyConstant.NATIVE).getValue();

                loadFbBanner(banner);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}