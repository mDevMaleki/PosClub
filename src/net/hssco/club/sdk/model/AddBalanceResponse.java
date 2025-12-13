package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response wrapper for add balance API.
 */
public class AddBalanceResponse {

    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}
