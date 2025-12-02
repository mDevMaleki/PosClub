package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for /api/psp/balance.
 */
public class GetBalanceRequestTransactionResult {

    @SerializedName("SpOutputMessage")
    private String spOutputMessage;

    @SerializedName("ResponsStatus")
    private String responsStatus;

    @SerializedName("Amount")
    private long amount;

    @SerializedName("BasicAmount")
    private long basicAmount;

    @SerializedName("ShowClubSaleMenu")
    private boolean showClubSaleMenu;

    @SerializedName("ShowReciverSaleMenu")
    private boolean showReciverSaleMenu;

    @SerializedName("CardId")
    private Long cardId;

    @SerializedName("DeviceId")
    private Long deviceId;

    @SerializedName("ClubId")
    private Long clubId;

    @SerializedName("BranchId")
    private Long branchId;

    public GetBalanceRequestTransactionResult() {
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getBasicAmount() {
        return basicAmount;
    }

    public void setBasicAmount(long basicAmount) {
        this.basicAmount = basicAmount;
    }

    public boolean isShowClubSaleMenu() {
        return showClubSaleMenu;
    }

    public void setShowClubSaleMenu(boolean showClubSaleMenu) {
        this.showClubSaleMenu = showClubSaleMenu;
    }

    public boolean isShowReciverSaleMenu() {
        return showReciverSaleMenu;
    }

    public void setShowReciverSaleMenu(boolean showReciverSaleMenu) {
        this.showReciverSaleMenu = showReciverSaleMenu;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
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
}
