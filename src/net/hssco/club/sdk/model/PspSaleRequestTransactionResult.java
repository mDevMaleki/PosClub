package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for /api/psp/sale.
 */
public class PspSaleRequestTransactionResult {

    @SerializedName("ResponsStatus")
    private String responsStatus;

    @SerializedName("SpOutputMessage")
    private String spOutputMessage;

    @SerializedName("NewAmount")
    private long newAmount;

    @SerializedName("ThirdSaleTitle")
    private String thirdSaleTitle;

    @SerializedName("DeviceId")
    private long deviceId;

    @SerializedName("ClubId")
    private Long clubId;

    @SerializedName("BranchId")
    private Long branchId;

    @SerializedName("DiscountAmount")
    private long discountAmount;

    @SerializedName("PspAmount")
    private long pspAmount;

    @SerializedName("ClubAmount")
    private long clubAmount;

    @SerializedName("ReciverAmount")
    private long reciverAmount;

    @SerializedName("ClubUsedAmount")
    private long clubUsedAmount;

    @SerializedName("ReciverUsedAmount")
    private long reciverUsedAmount;

    @SerializedName("CustomerOnlineAmount")
    private long customerOnlineAmount;

    @SerializedName("ClubDiscountPercent")
    private float clubDiscountPercent;

    @SerializedName("LocalPercent")
    private float localPercent;

    @SerializedName("AgentScore")
    private int agentScore;

    @SerializedName("EventMerchantPoint")
    private int eventMerchantPoint;

    @SerializedName("EventCustomerPoint")
    private int eventCustomerPoint;

    @SerializedName("PointInReciver")
    private int pointInReciver;

    @SerializedName("VerifyCode")
    private String verifyCode;

    public PspSaleRequestTransactionResult() {
    }

    public String getResponsStatus() {
        return responsStatus;
    }

    public void setResponsStatus(String responsStatus) {
        this.responsStatus = responsStatus;
    }

    public String getSpOutputMessage() {
        return spOutputMessage;
    }

    public void setSpOutputMessage(String spOutputMessage) {
        this.spOutputMessage = spOutputMessage;
    }

    public long getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(long newAmount) {
        this.newAmount = newAmount;
    }

    public String getThirdSaleTitle() {
        return thirdSaleTitle;
    }

    public void setThirdSaleTitle(String thirdSaleTitle) {
        this.thirdSaleTitle = thirdSaleTitle;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public long getPspAmount() {
        return pspAmount;
    }

    public void setPspAmount(long pspAmount) {
        this.pspAmount = pspAmount;
    }

    public long getClubAmount() {
        return clubAmount;
    }

    public void setClubAmount(long clubAmount) {
        this.clubAmount = clubAmount;
    }

    public long getReciverAmount() {
        return reciverAmount;
    }

    public void setReciverAmount(long reciverAmount) {
        this.reciverAmount = reciverAmount;
    }

    public long getClubUsedAmount() {
        return clubUsedAmount;
    }

    public void setClubUsedAmount(long clubUsedAmount) {
        this.clubUsedAmount = clubUsedAmount;
    }

    public long getReciverUsedAmount() {
        return reciverUsedAmount;
    }

    public void setReciverUsedAmount(long reciverUsedAmount) {
        this.reciverUsedAmount = reciverUsedAmount;
    }

    public long getCustomerOnlineAmount() {
        return customerOnlineAmount;
    }

    public void setCustomerOnlineAmount(long customerOnlineAmount) {
        this.customerOnlineAmount = customerOnlineAmount;
    }

    public float getClubDiscountPercent() {
        return clubDiscountPercent;
    }

    public void setClubDiscountPercent(float clubDiscountPercent) {
        this.clubDiscountPercent = clubDiscountPercent;
    }

    public float getLocalPercent() {
        return localPercent;
    }

    public void setLocalPercent(float localPercent) {
        this.localPercent = localPercent;
    }

    public int getAgentScore() {
        return agentScore;
    }

    public void setAgentScore(int agentScore) {
        this.agentScore = agentScore;
    }

    public int getEventMerchantPoint() {
        return eventMerchantPoint;
    }

    public void setEventMerchantPoint(int eventMerchantPoint) {
        this.eventMerchantPoint = eventMerchantPoint;
    }

    public int getEventCustomerPoint() {
        return eventCustomerPoint;
    }

    public void setEventCustomerPoint(int eventCustomerPoint) {
        this.eventCustomerPoint = eventCustomerPoint;
    }

    public int getPointInReciver() {
        return pointInReciver;
    }

    public void setPointInReciver(int pointInReciver) {
        this.pointInReciver = pointInReciver;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
