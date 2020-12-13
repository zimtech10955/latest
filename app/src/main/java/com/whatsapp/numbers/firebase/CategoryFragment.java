package com.whatsapp.numbers.firebase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.ads.NativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.firebase.model.MyCategory;
import com.whatsapp.numbers.firebase.myadapter.MyCategoryAdapter;
import com.whatsapp.numbers.utils.MyConstant;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference mRef;
    private List<Object> list = new ArrayList<>();
    private View view;
    private String adPosition = "2";
    private int counter = 0;
    private String nativeId = "0";
    private ProgressBar mProgressBar;
    public CategoryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recent, container, false);


        init();
        setupRecyclerView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();

            }
        },2000);

        loadFromFirebase();
        return view;

    }

    private void loadFbBanner(String id) {

        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getContext(), id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

    }
    private void init(){
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mProgressBar = view.findViewById(R.id.progressBar);
    }
    private void setupRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
    }
    private NativeAd getNative(String id){
        return new NativeAd(getContext() , id);
    }
    private void loadData(){

        mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.CATEGORY);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProgressBar.setVisibility(View.GONE);
                if (list.isEmpty()){
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                        String name = (String) postSnapshot.child(MyConstant.CATEGORY_NAME).getValue();
                        String image = (String) postSnapshot.child(MyConstant.CATEGORY_IMAGE).getValue();
                        if (counter % Integer.parseInt(adPosition) == 0 && counter != 0){
                            list.add(getNative(nativeId));
                        }
                        list.add(new MyCategory(name , image));

                        counter++;
                    }

                    MyCategoryAdapter adapter = new MyCategoryAdapter(getContext() , list);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.keepSynced(true);
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