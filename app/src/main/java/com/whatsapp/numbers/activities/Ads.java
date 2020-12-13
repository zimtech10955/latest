package com.whatsapp.numbers.activities;

import android.content.Context;
import android.util.Log;

import com.whatsapp.numbers.Config;
import com.facebook.ads.InterstitialAd;

public class Ads {
    public static InterstitialAd fbInterstitial;


    public static void loadFacebookInterstitial(Context context){
        fbInterstitial = new com.facebook.ads.InterstitialAd(context, Config.fbInterstitialId);
        Log.d("fbInterstitial","fb interstitial loaded");
        // load the ad
        fbInterstitial.loadAd();
    }

    public static void showFacebookInterstitialAds(){
        if(Config.isFbInterstitial){
            if(fbInterstitial.isAdLoaded() && !fbInterstitial.isAdInvalidated()){
                /*if(fbCounter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL){
                    fbCounter = 1;*/
                    fbInterstitial.show();
                    Log.d("fbInterstitial","fb interstitial show");

                /*}else{
                    fbCounter++;
                }*/
            }
        }
    }
}
