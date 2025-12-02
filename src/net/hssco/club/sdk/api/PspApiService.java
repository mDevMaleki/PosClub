package net.hssco.club.sdk.api;

import net.hssco.club.sdk.model.GetBalanceRequestTransactionCommand;
import net.hssco.club.sdk.model.GetBalanceRequestTransactionResult;
import net.hssco.club.sdk.model.LocalRequestClubCardChargeCommand;
import net.hssco.club.sdk.model.LocalRequestClubCardChargeResult;
import net.hssco.club.sdk.model.PspSaleRequestTransactionCommand;
import net.hssco.club.sdk.model.PspSaleRequestTransactionResult;
import net.hssco.club.sdk.model.PspVerifySaleRequestTransactionCommand;
import net.hssco.club.sdk.model.PspVerifySaleRequestTransactionResult;
import net.hssco.club.sdk.model.VerifyLocalRequestClubCardChargeCommand;
import net.hssco.club.sdk.model.VerifyLocalRequestClubCardChargResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Retrofit interface for the POS/Club PSP API.
 */
public interface PspApiService {

    @Headers("Content-Type: application/json")
    @POST("/api/android/psp/balance")
    Call<GetBalanceRequestTransactionResult> getBalance(@Body GetBalanceRequestTransactionCommand request);

    @Headers("Content-Type: application/json")
    @POST("/api/android/psp/sale")
    Call<PspSaleRequestTransactionResult> sale(@Body PspSaleRequestTransactionCommand request);

    @Headers("Content-Type: application/json")
    @POST("/api/android/psp/sale/verify")
    Call<PspVerifySaleRequestTransactionResult> verifySale(@Body PspVerifySaleRequestTransactionCommand request);

    @Headers("Content-Type: application/json")
    @POST("/api/android/psp/club/charge")
    Call<LocalRequestClubCardChargeResult> chargeClubCard(@Body LocalRequestClubCardChargeCommand request);

    @Headers("Content-Type: application/json")
    @POST("/api/android/psp/club/charge/verify")
    Call<VerifyLocalRequestClubCardChargResult> verifyCharge(@Body VerifyLocalRequestClubCardChargeCommand request);
}
