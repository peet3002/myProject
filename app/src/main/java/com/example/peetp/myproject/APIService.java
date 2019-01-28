package com.example.peetp.myproject;


import com.example.peetp.myproject.notifications.MyResponse;
import com.example.peetp.myproject.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAADbWLzIs:APA91bEzBiUDFvnLFXYY0SsCzMUu-8bcM7aBX9_DnYGPccKaGssdqyMML9bGODOndsCibQKAaRbrioOf_XM_nOe45Y7D9jBNBwb1FwV4ZZ8Wbhq8l0tYLTP6VT8HWjE-jddERkAlAvjw"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
