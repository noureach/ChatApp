package com.noureach.chatapp.Fragments;

import com.noureach.chatapp.Notifications.MyResponse;
import com.noureach.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key=AAAAIQ3vadQ:APA91bFo5CnEIdemMaNFOUPjekCQuotpIuN0Mw2HbhN5HYXOoFiqS9RV4LDYJmBaKSwa8jE-GrEu42DSiXdItMj_uM7zBQnqBtyY5dMBGpURHXfi1R14WRs2NiuZqHi5QQK5lTWf99bU",

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
