package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

public class PspSaleResponse {
    @SerializedName("isSuccess") private boolean isSuccess;
    @SerializedName("message") private String message;
    @SerializedName("data") private PspSaleRequestTransactionResult data;

    public boolean isSuccess() { return isSuccess; }
    public String getMessage() { return message; }
    public PspSaleRequestTransactionResult getData() { return data; }
}