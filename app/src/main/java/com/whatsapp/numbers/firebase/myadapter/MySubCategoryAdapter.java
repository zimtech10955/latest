package com.whatsapp.numbers.firebase.myadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.ads.MyFacebook;
import com.whatsapp.numbers.firebase.GirlDetailActivity;
import com.whatsapp.numbers.firebase.model.Girl;
import com.whatsapp.numbers.utils.MyConstant;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MySubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Object> list;
    private String  adPosition = "3";
    private static final int ITEM_DATA_TYPE = 1;
    private static final int ITEM_AD_TYPE = 2;
    private String mNative = null;

    private InterstitialAd mInterstitial;

    public MySubCategoryAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
        loadFromFirebase();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_DATA_TYPE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.firebase_category_item_row, parent, false);
            return new MyCategoryViewHolder(v);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fb_native_ad, parent, false);
            return new AdViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_DATA_TYPE){
           MyCategoryViewHolder viewHolder = (MyCategoryViewHolder) holder;
           Girl myCategory = (Girl) list.get(position);
           viewHolder.bindName(myCategory.getName());
           viewHolder.bindImage(myCategory.getImage());

        }else {

            AdViewHolder adViewHolder = (AdViewHolder) holder;
            NativeAd nativeAd = (NativeAd) list.get(position);
            adViewHolder.loadFbNativeAd(nativeAd);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return list.get(position) instanceof Girl ? ITEM_DATA_TYPE : ITEM_AD_TYPE;

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        void bindName(String name){
            TextView mName = itemView.findViewById(R.id.item_row_categoryName);
            TextView mSubTitle = itemView.findViewById(R.id.item_row_categorySubtitle);
            mName.setText(name);
            mSubTitle.setText("Hello , I am " + name + " !");
        }
        void bindImage(String image){
            ImageView mImage = itemView.findViewById(R.id.item_row_categoryImage);
            Glide.with(context).load(image).into(mImage);
        }

        @Override
        public void onClick(View v) {
            Girl girl = (Girl) list.get(getAdapterPosition());
            Intent intent = new Intent(context , GirlDetailActivity.class);
            intent.putExtra(MyConstant.CATEGORY_NAME , girl.getName());
            intent.putExtra(MyConstant.CATEGORY_IMAGE , girl.getImage());
            intent.putExtra(MyConstant.AGE , girl.getAge());
            intent.putExtra(MyConstant.COUNTRY , girl.getCountry());
           // intent.putExtra(MyConstant.NUMBER , girl.getNumber());

            context.startActivity(intent);
            if (mInterstitial != null && mInterstitial.isAdLoaded()){
                MyFacebook.facebookShowInterstitialWithoutIntent(mInterstitial);
            }
        }
    }
    public class AdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);

        }
        private void loadFbNativeAd(final NativeAd nativeAd) {
            try {

                mLayout = itemView.findViewById(R.id.fb_native_ad);
                NativeAdListener nativeAdListener = new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {
                        // Native ad finished downloading all assets
                        // Log.e(TAG, "Native ad finished downloading all assets.");
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        Toast.makeText(context, "" + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        // Native ad failed to load
                        //Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        try {
                            nativeAd.unregisterView();
                            mLayout.removeAllViews();
                            View adView = NativeAdView.render(context, nativeAd);
                            mLayout.addView(adView, new LinearLayout.LayoutParams(MATCH_PARENT, 650));

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Native ad clicked
                        //Log.d(TAG, "Native ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Native ad impression
                        //Log.d(TAG, "Native ad impression logged!");
                    }
                };

                // Request an ad
                nativeAd.loadAd(
                        nativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener)
                                .build());
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String banner = (String) snapshot.child(MyConstant.BANNER).getValue();
                String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();
                mNative = (String) snapshot.child(MyConstant.NATIVE).getValue();
                adPosition = (String) snapshot.child(MyConstant.POSITION).getValue();
                loadFbInterstitial(interstitial);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFbInterstitial(String id){
        mInterstitial = new InterstitialAd(context , id);
        mInterstitial.loadAd();
    }

}
