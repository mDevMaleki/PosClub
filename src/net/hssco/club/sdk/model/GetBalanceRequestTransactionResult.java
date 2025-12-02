package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

public class GetBalanceRequestTransactionResult {

    @SerializedName("spOutputMessage") private String spOutputMessage;
    @SerializedName("responsStatus") private String responsStatus;
    @SerializedName("amount") private long amount;
    @SerializedName("basicAmount") private long basicAmount;
    @SerializedName("showClubSaleMenu") private boolean showClubSaleMenu;
    @SerializedName("showReciverSaleMenu") private boolean showReciverSaleMenu;
    @SerializedName("cardId") private Long cardId;
    @SerializedName("deviceId") private Long deviceId;
    @SerializedName("clubId") private Long clubId;
    @SerializedName("branchId") private Long branchId;

    // Getters
    public String getSpOutputMessage() { return spOutputMessage; }
    public String getResponsStatus() { return responsStatus; }
    public long getAmount() { return amount; }
    public long getBasicAmount() { return basicAmount; }
    public boolean isShowClubSaleMenu() { return showClubSaleMenu; }
    public boolean isShowReciverSaleMenu() { return showReciverSaleMenu; }
    public Long getCardId() { return cardId; }
    public Long getDeviceId() { return deviceId; }
    public Long getClubId() { return clubId; }
    public Long getBranchId() { return branchId; }
}
