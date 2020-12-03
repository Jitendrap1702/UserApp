package com.example.userapp.fcmsender.legacy;

public interface OnMsgSentListener {
    void onSuccess(String response);
    void onFailure(String error);
}