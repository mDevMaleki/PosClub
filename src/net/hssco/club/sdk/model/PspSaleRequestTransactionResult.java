package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

public class PspSaleRequestTransactionResult {

    @SerializedName("responsStatus") private String responsStatus;
    @SerializedName("spOutputMessage") private String spOutputMessage;
    @SerializedName("newAmount") private long newAmount;
    @SerializedName("thirdSaleTitle") private String thirdSaleTitle;
    @SerializedName("deviceId") private long deviceId;
    @SerializedName("clubId") private Long clubId;
    @SerializedName("branchId") private Long branchId;
    @SerializedName("discountAmount") private long discountAmount;
    @SerializedName("pspAmount") private long pspAmount;
    @SerializedName("clubAmount") private long clubAmount;
    @SerializedName("reciverAmount") private long reciverAmount;
    @SerializedName("clubUsedAmount") private long clubUsedAmount;
    @SerializedName("reciverUsedAmount") private long reciverUsedAmount;
    @SerializedName("customerOnlineAmount") private long customerOnlineAmount;
    @SerializedName("clubDiscountPercent") private float clubDiscountPercent;
    @SerializedName("localPercent") private float localPercent;
    @SerializedName("agentScore") private int agentScore;
    @SerializedName("eventMerchantPoint") private int eventMerchantPoint;
    @SerializedName("eventCustomerPoint") private int eventCustomerPoint;
    @SerializedName("pointInReciver") private int pointInReciver;
    @SerializedName("verifyCode") private String verifyCode;

    public String getResponsStatus() { return responsStatus; }
    public String getSpOutputMessage() { return spOutputMessage; }
    public long getNewAmount() { return newAmount; }
    public String getThirdSaleTitle() { return thirdSaleTitle; }
    public long getDeviceId() { return deviceId; }
    public Long getClubId() { return clubId; }
    public Long getBranchId() { return branchId; }
    public long getDiscountAmount() { return discountAmount; }
    public long getPspAmount() { return pspAmount; }
    public long getClubAmount() { return clubAmount; }
    public long getReciverAmount() { return reciverAmount; }
    public long getClubUsedAmount() { return clubUsedAmount; }
    public long getReciverUsedAmount() { return reciverUsedAmount; }
    public long getCustomerOnlineAmount() { return customerOnlineAmount; }
    public float getClubDiscountPercent() { return clubDiscountPercent; }
    public float getLocalPercent() { return localPercent; }
    public int getAgentScore() { return agentScore; }
    public int getEventMerchantPoint() { return eventMerchantPoint; }
    public int getEventCustomerPoint() { return eventCustomerPoint; }
    public int getPointInReciver() { return pointInReciver; }
    public String getVerifyCode() { return verifyCode; }
}
