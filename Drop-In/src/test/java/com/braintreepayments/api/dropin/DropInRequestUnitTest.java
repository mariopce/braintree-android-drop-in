package com.braintreepayments.api.dropin;

import android.content.Intent;
import android.os.Parcel;

import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Collections;

import static com.braintreepayments.api.test.TestTokenizationKey.TOKENIZATION_KEY;
import static com.braintreepayments.api.test.UnitTestFixturesHelper.stringFromFixture;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DropInRequestUnitTest {

    @Test
    public void includesAllOptions() {
        Cart cart = Cart.newBuilder()
                .setTotalPrice("5.00")
                .build();
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice("10")
                        .setCurrencyCode("USD")
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .build())
                .emailRequired(true);

        PayPalRequest paypalRequest = new PayPalRequest("10")
                .currencyCode("USD");

        Intent intent = new DropInRequest()
                .tokenizationKey(TOKENIZATION_KEY)
                .collectDeviceData(true)
                .amount("1.00")
                .googlePaymentRequest(googlePaymentRequest)
                .disableGooglePayment()
                .paypalRequest(paypalRequest)
                .disablePayPal()
                .disableVenmo()
                .requestThreeDSecureVerification(true)
                .maskCardNumber(true)
                .maskSecurityCode(true)
                .vaultManager(true)
                .showCheckBoxToAllowVaultOverride(true)
                .defaultVaultSetting(true)
                .cardholderNameStatus(CardForm.FIELD_OPTIONAL)
                .getIntent(RuntimeEnvironment.application);

        DropInRequest dropInRequest = intent.getParcelableExtra(DropInRequest.EXTRA_CHECKOUT_REQUEST);

        assertEquals(DropInActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(TOKENIZATION_KEY, dropInRequest.getAuthorization());
        assertTrue(dropInRequest.shouldCollectDeviceData());
        assertEquals("1.00", dropInRequest.getAmount());
        assertEquals("10", dropInRequest.getGooglePaymentRequest().getTransactionInfo().getTotalPrice());
        assertEquals("USD", dropInRequest.getGooglePaymentRequest().getTransactionInfo().getCurrencyCode());
        assertEquals(WalletConstants.TOTAL_PRICE_STATUS_FINAL, dropInRequest.getGooglePaymentRequest().getTransactionInfo().getTotalPriceStatus());
        assertTrue(dropInRequest.getGooglePaymentRequest().isEmailRequired());
        assertFalse(dropInRequest.isGooglePaymentEnabled());
        assertEquals("10", dropInRequest.getPayPalRequest().getAmount());
        assertEquals("USD", dropInRequest.getPayPalRequest().getCurrencyCode());
        assertFalse(dropInRequest.isPayPalEnabled());
        assertFalse(dropInRequest.isVenmoEnabled());
        assertTrue(dropInRequest.shouldRequestThreeDSecureVerification());
        assertTrue(dropInRequest.shouldMaskCardNumber());
        assertTrue(dropInRequest.shouldMaskSecurityCode());
        assertTrue(dropInRequest.isVaultManagerEnabled());
        assertTrue(dropInRequest.getDefaultVaultSetting());
        assertTrue(dropInRequest.isSaveCardCheckBoxShown());
        assertEquals(CardForm.FIELD_OPTIONAL, dropInRequest.getCardholderNameStatus());
    }

    @Test
    public void hasCorrectDefaults() {
        Intent intent = new DropInRequest()
                .getIntent(RuntimeEnvironment.application);

        DropInRequest dropInRequest = intent.getParcelableExtra(DropInRequest.EXTRA_CHECKOUT_REQUEST);

        assertEquals(DropInActivity.class.getName(), intent.getComponent().getClassName());
        assertNull(dropInRequest.getAuthorization());
        assertFalse(dropInRequest.shouldCollectDeviceData());
        assertNull(dropInRequest.getAmount());
        assertTrue(dropInRequest.isGooglePaymentEnabled());
        assertNull(dropInRequest.getPayPalRequest());
        assertTrue(dropInRequest.isPayPalEnabled());
        assertTrue(dropInRequest.isVenmoEnabled());
        assertFalse(dropInRequest.shouldRequestThreeDSecureVerification());
        assertFalse(dropInRequest.shouldMaskCardNumber());
        assertFalse(dropInRequest.shouldMaskSecurityCode());
        assertFalse(dropInRequest.isVaultManagerEnabled());
        assertFalse(dropInRequest.isSaveCardCheckBoxShown());
        assertFalse(dropInRequest.getDefaultVaultSetting());
        assertEquals(CardForm.FIELD_DISABLED, dropInRequest.getCardholderNameStatus());
    }

    @Test
    public void isParcelable() {
        Cart cart = Cart.newBuilder()
                .setTotalPrice("5.00")
                .build();
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice("10")
                        .setCurrencyCode("USD")
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .build())
                .emailRequired(true);

        PayPalRequest paypalRequest = new PayPalRequest("10")
                .currencyCode("USD");

        DropInRequest dropInRequest = new DropInRequest()
                .tokenizationKey(TOKENIZATION_KEY)
                .collectDeviceData(true)
                .amount("1.00")
                .googlePaymentRequest(googlePaymentRequest)
                .disableGooglePayment()
                .paypalRequest(paypalRequest)
                .disablePayPal()
                .disableVenmo()
                .requestThreeDSecureVerification(true)
                .maskCardNumber(true)
                .maskSecurityCode(true)
                .vaultManager(true)
                .defaultVaultSetting(true)
                .showCheckBoxToAllowVaultOverride(true)
                .cardholderNameStatus(CardForm.FIELD_OPTIONAL);

        Parcel parcel = Parcel.obtain();
        dropInRequest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        DropInRequest parceledDropInRequest = DropInRequest.CREATOR.createFromParcel(parcel);

        assertEquals(TOKENIZATION_KEY, parceledDropInRequest.getAuthorization());
        assertTrue(parceledDropInRequest.shouldCollectDeviceData());
        assertEquals("1.00", parceledDropInRequest.getAmount());
        assertEquals("10", dropInRequest.getGooglePaymentRequest().getTransactionInfo().getTotalPrice());
        assertEquals("USD", dropInRequest.getGooglePaymentRequest().getTransactionInfo().getCurrencyCode());
        assertEquals(WalletConstants.TOTAL_PRICE_STATUS_FINAL, dropInRequest.getGooglePaymentRequest().getTransactionInfo().getTotalPriceStatus());
        assertTrue(dropInRequest.getGooglePaymentRequest().isEmailRequired());
        assertEquals("10", dropInRequest.getPayPalRequest().getAmount());
        assertEquals("USD", dropInRequest.getPayPalRequest().getCurrencyCode());
        assertFalse(dropInRequest.isGooglePaymentEnabled());
        assertFalse(parceledDropInRequest.isPayPalEnabled());
        assertFalse(parceledDropInRequest.isVenmoEnabled());
        assertTrue(parceledDropInRequest.shouldRequestThreeDSecureVerification());
        assertTrue(parceledDropInRequest.shouldMaskCardNumber());
        assertTrue(parceledDropInRequest.shouldMaskSecurityCode());
        assertTrue(parceledDropInRequest.isVaultManagerEnabled());
        assertTrue(parceledDropInRequest.getDefaultVaultSetting());
        assertTrue(parceledDropInRequest.isSaveCardCheckBoxShown());
        assertEquals(CardForm.FIELD_OPTIONAL, parceledDropInRequest.getCardholderNameStatus());
    }

    @Test
    public void getIntent_includesClientToken() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(stringFromFixture("client_token.json"));

        assertEquals(stringFromFixture("client_token.json") , dropInRequest.getAuthorization());
    }

    @Test
    public void getIntent_includesTokenizationKey() {
        DropInRequest dropInRequest = new DropInRequest()
                .tokenizationKey(TOKENIZATION_KEY);

        assertEquals(TOKENIZATION_KEY, dropInRequest.getAuthorization());
    }

    @Test
    public void getCardholderNameStatus_includesCardHolderNameStatus() {
        DropInRequest dropInRequest = new DropInRequest()
                .cardholderNameStatus(CardForm.FIELD_REQUIRED);

        assertEquals(CardForm.FIELD_REQUIRED, dropInRequest.getCardholderNameStatus());
    }
}
