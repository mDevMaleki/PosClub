package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for /api/android/psp/club/charge.
 */
public class LocalRequestClubCardChargeResult {

    @SerializedName("SpOutputMessage")
    private String spOutputMessage;

    @SerializedName("ResponsStatus")
    private String responsStatus;

    @SerializedName("ChargeAmount")
    private String chargeAmount;

    @SerializedName("AccTableVersion")
    private String accTableVersion;

    public LocalRequestClubCardChargeResult() {
    }

    public String getSpOutputMessage() {
        return spOutputMessage;
    }

    public void setSpOutputMessage(String spOutputMessage) {
        this.spOutputMessage = spOutputMessage;
    }

    public String getResponsStatus() {
        return responsStatus;
    }

    public void setResponsStatus(String responsStatus) {
        this.responsStatus = responsStatus;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getAccTableVersion() {
        return accTableVersion;
    }

    public void setAccTableVersion(String accTableVersion) {
        this.accTableVersion = accTableVersion;
    }
}
