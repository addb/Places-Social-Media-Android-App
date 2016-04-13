/*
 * Copyright (c) 2016 Krumbs Inc.
 * All rights reserved.
 *
 */
/* **************************************************
**

This is the starter file given by Krumbs Team to get started with using Krumbs SDK

******************************************************/
 
 package io.krumbs.sdk.starter;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;

import java.net.URL;

import io.krumbs.sdk.KIntentPanelConfiguration;
import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.KrumbsUser;
import io.krumbs.sdk.data.model.Media;
import io.krumbs.sdk.krumbscapture.KMediaUploadListener;


public class StarterApplication extends Application {
    public static final String KRUMBS_SDK_APPLICATION_ID = "io.krumbs.sdk.APPLICATION_ID";
    public static final String KRUMBS_SDK_CLIENT_KEY = "io.krumbs.sdk.CLIENT_KEY";
    public static final String SDK_STARTER_PROJECT_USER_FN = "JohnQ";
    public static final String SDK_STARTER_PROJECT_USER_SN = "Public";

    @Override
    public void onCreate() {
        super.onCreate();

        String appID = getMetadata(getApplicationContext(), KRUMBS_SDK_APPLICATION_ID);
        String clientKey = getMetadata(getApplicationContext(), KRUMBS_SDK_CLIENT_KEY);
        if (appID != null && clientKey != null) {
// SDK usage step 1 - initialize the SDK with your application id and client key
            KrumbsSDK.initialize(getApplicationContext(), appID, clientKey);

// Implement the interface KMediaUploadListener.
// After a Capture completes, the media (photo and audio) is uploaded to the cloud
// KMediaUploadListener will be used to listen for various state of media upload from the SDK.
            KMediaUploadListener kMediaUploadListener = new KMediaUploadListener() {
                // onMediaUpload listens to various status of media upload to the cloud.
                @Override
                public void onMediaUpload(String id, KMediaUploadListener.MediaUploadStatus mediaUploadStatus,
                                          Media.MediaType mediaType, URL mediaUrl) {
                    if (mediaUploadStatus != null) {
                        Log.i("KRUMBS-BROADCAST-RECV", mediaUploadStatus.toString());
                        if (mediaUploadStatus == KMediaUploadListener.MediaUploadStatus.UPLOAD_SUCCESS) {
                            if (mediaType != null && mediaUrl != null) {
                                Log.i("KRUMBS-BROADCAST-RECV", mediaType + ": " + id + ", " + mediaUrl);
                            }
                        }
                    }
                }
            };
            // pass the KMediaUploadListener object to the sdk
            KrumbsSDK.setKMediaUploadListener(this, kMediaUploadListener);

            try {

// SDK usage step 2 - register your customized Intent Panel with the SDK

                // Register the Intent Panel model
                // the emoji image assets will be looked up by name when the KCapture camera is started
                // Make sure to include the images in your resource directory before starting the KCapture
                // Use the 'asset-generator' tool to build your image resources from intent-categories.json
                String assetPath = "IntentResourcesExample";
                KrumbsSDK.registerIntentCategories(assetPath);


// SDK usage step 3 (optional) - add your Intent Panel view customizations

                // Configure the IntentPanel View (colors & fonts)
                KIntentPanelConfiguration defaults = KrumbsSDK.getIntentPanelViewConfigurationDefaults();
                KIntentPanelConfiguration.IntentPanelCategoryTextStyle ts = defaults.getIntentPanelCategoryTextStyle();
                ts.setTextColor(Color.YELLOW);
                KIntentPanelConfiguration newDefaults = new KIntentPanelConfiguration.KIntentPanelConfigurationBuilder()
                        .intentPanelBarColor("#029EE1")
                        .intentPanelTextStyle(ts)
                        .build();

                KrumbsSDK.setIntentPanelViewConfigurationDefaults(newDefaults);

// SDK usage step 3 (optional) - register users so you can associate their ID (email) with created content with Cloud API
                // Register user information (if your app requires login)
                // to improve security on the mediaJSON created.
                String userEmail = DeviceUtils.getPrimaryUserID(getApplicationContext());
                KrumbsSDK.registerUser(new KrumbsUser.KrumbsUserBuilder()
                        .email(userEmail)
                        .firstName(SDK_STARTER_PROJECT_USER_FN)
                        .lastName(SDK_STARTER_PROJECT_USER_SN).build());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
// if we can’t find it in the manifest, just return null
        }
        return null;
    }
}
