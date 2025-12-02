package net.hssco.club.sdk;

import net.hssco.club.sdk.api.PspApiService;
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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Example usage of the PSP Retrofit SDK.
 */
public class PspSdkUsageExample {

    public void demoCalls() {
        PspApiClient client = PspApiClient.create("https://your-posclub-host/");
        PspApiService service = client.getApiService();

        GetBalanceRequestTransactionCommand balanceRequest = new GetBalanceRequestTransactionCommand(
                1L,
                100,
                "INIT",
                "Balance",
                "20240101",
                "101500",
                "TERM001",
                "123456",
                "1111222233334444",
                "1234",
                "custom balance request"
        );

        service.getBalance(balanceRequest).enqueue(new Callback<GetBalanceRequestTransactionResult>() {
            @Override
            public void onResponse(Call<GetBalanceRequestTransactionResult> call,
                                   Response<GetBalanceRequestTransactionResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetBalanceRequestTransactionResult result = response.body();
                    // Handle balance result
                }
            }

            @Override
            public void onFailure(Call<GetBalanceRequestTransactionResult> call, Throwable t) {
                // Handle error
            }
        });

        PspSaleRequestTransactionCommand saleRequest = new PspSaleRequestTransactionCommand(
                2L,
                100,
                "INIT",
                "Sale",
                "20240101",
                "101600",
                50000L,
                "REF12345",
                "TERM001",
                "654321",
                "5555666677778888",
                "VALID",
                "1234",
                "ANDROID",
                "0000",
                "123",
                "POS sale",
                "sale payload"
        );

        try {
            Response<PspSaleRequestTransactionResult> saleResponse = service.sale(saleRequest).execute();
            if (saleResponse.isSuccessful()) {
                PspSaleRequestTransactionResult saleResult = saleResponse.body();
                // Use saleResult
            }
        } catch (Exception exception) {
            // Handle exception
        }

        PspVerifySaleRequestTransactionCommand verifySaleRequest = new PspVerifySaleRequestTransactionCommand(
                3L,
                100,
                "TERM001",
                "654321"
        );

        service.verifySale(verifySaleRequest).enqueue(new Callback<PspVerifySaleRequestTransactionResult>() {
            @Override
            public void onResponse(Call<PspVerifySaleRequestTransactionResult> call,
                                   Response<PspVerifySaleRequestTransactionResult> response) {
                // Inspect response
            }

            @Override
            public void onFailure(Call<PspVerifySaleRequestTransactionResult> call, Throwable t) {
                // Handle failure
            }
        });

        LocalRequestClubCardChargeCommand chargeCommand = new LocalRequestClubCardChargeCommand(
                4L,
                100,
                "MERCHANT_PIN",
                "INIT",
                "Charge",
                "20240101",
                "101700",
                75000L,
                "REF6789",
                "TERM001",
                "777888",
                "9999000011112222",
                "VALID",
                "1234",
                "ANDROID",
                "0000",
                "123",
                "Club charge",
                "charge payload",
                "CARD123456789"
        );

        service.chargeClubCard(chargeCommand).enqueue(new Callback<LocalRequestClubCardChargeResult>() {
            @Override
            public void onResponse(Call<LocalRequestClubCardChargeResult> call,
                                   Response<LocalRequestClubCardChargeResult> response) {
                // Handle charge response
            }

            @Override
            public void onFailure(Call<LocalRequestClubCardChargeResult> call, Throwable t) {
                // Handle failure
            }
        });

        VerifyLocalRequestClubCardChargeCommand verifyChargeCommand = new VerifyLocalRequestClubCardChargeCommand(
                5L,
                100,
                "TERM001",
                "777888"
        );

        service.verifyCharge(verifyChargeCommand).enqueue(new Callback<VerifyLocalRequestClubCardChargResult>() {
            @Override
            public void onResponse(Call<VerifyLocalRequestClubCardChargResult> call,
                                   Response<VerifyLocalRequestClubCardChargResult> response) {
                // Handle verification response
            }

            @Override
            public void onFailure(Call<VerifyLocalRequestClubCardChargResult> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
