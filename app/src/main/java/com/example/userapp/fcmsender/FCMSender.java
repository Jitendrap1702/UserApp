package com.example.userapp.fcmsender;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FCMSender {

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send"
            , KEY_STRING = "key=AAAAFD_snsg:APA91bFOKMxgd1rdgAfSARNrk6aP0-xjvYsxQIt325PlGCnKIWY8GQ01zp0JTbfeO4FTIPdFecxBLD_v1-WwVmI3pT2fsHWUHSgQVFIGBif_LBpk5fYZZdfsloNgjsNSsm_PK3POwLSQ";

    OkHttpClient client = new OkHttpClient();

    public void send(String message, Callback callback){
        RequestBody reqBody = RequestBody.create(MediaType.get("application/json")
                        , message);

        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", KEY_STRING)
                .post(reqBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}

