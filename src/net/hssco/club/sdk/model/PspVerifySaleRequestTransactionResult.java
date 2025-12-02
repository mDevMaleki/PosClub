package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

public class PspVerifySaleRequestTransactionResult {

    @SerializedName("responsStatus") private String responsStatus;

    public String getResponsStatus() { return responsStatus; }
    public void setResponsStatus(String responsStatus) { this.responsStatus = responsStatus; }
}
