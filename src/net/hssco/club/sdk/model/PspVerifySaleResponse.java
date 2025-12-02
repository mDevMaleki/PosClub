package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

public class PspVerifySaleResponse {
    @SerializedName("isSuccess") private boolean isSuccess;
    @SerializedName("message") private String message;
    @SerializedName("data") private PspVerifySaleRequestTransactionResult data;

    public boolean isSuccess() { return isSuccess; }
    public String getMessage() { return message; }
    public PspVerifySaleRequestTransactionResult getData() { return data; }
}