package com.whatsapp.numbers;

public class Config {

    //your admin panel url
    public static final String ADMIN_PANEL_URL = "http://tasawarchadhar.com/movies/";

    //your api key which obtained from admin panel
    public static final String API_KEY = "cda11ibravqGNYUe1x9VEsDLX7Fd6RyP504TWutQjwZkIfzJcO";


    //layout customization
    public static final boolean ENABLE_TAB_LAYOUT = true;
    public static final boolean ENABLE_SINGLE_ROW_COLUMN = false;
    public static final boolean FORCE_PLAYER_TO_LANDSCAPE = false;
    public static final boolean ENABLE_VIEW_COUNT = false;
    public static final boolean ENABLE_DATE_DISPLAY = false;
    public static final boolean DISPLAY_DATE_AS_TIME_AGO = false;

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = false;

    //load more for next list videos
    public static final int LOAD_MORE = 10;

    //splash screen duration in millisecond
    public static final int SPLASH_TIME = 8000;

    public static int FbAdCount = 0;
    public static int FbAdInterval;

    public static String firbaseRootChild = "adsActivity";
    public static boolean IsAppUpdated = true;
    public static String  pakageNname = "";

    //Ads Configuration
    //set true to enable or set false to disable
    public static  boolean ENABLE_ADMOB_BANNER_ADS = false;
    public static  boolean ENABLE_ADMOB_INTERSTITIAL_ADS = false;
    public static  int ADMOB_INTERSTITIAL_ADS_INTERVAL = 2;
    public static  boolean ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO = false;


    public static boolean isFbBanner = false, isFbInterstitial = false;
    public static String adMobBannerId, adMobInterstitialId, adMobPublisherId, fbBannerId, fbInterstitialId;



}