package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

public class GetBalanceResponse {
    @SerializedName("isSuccess") private boolean isSuccess;
    @SerializedName("message") private String message;
    @SerializedName("data") private GetBalanceRequestTransactionResult data;

    public boolean isSuccess() { return isSuccess; }
    public String getMessage() { return message; }
    public GetBalanceRequestTransactionResult getData() { return data; }
}
