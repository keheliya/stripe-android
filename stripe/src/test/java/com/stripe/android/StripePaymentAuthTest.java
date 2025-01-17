package com.stripe.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import androidx.test.core.app.ApplicationProvider;

import com.stripe.android.model.PaymentIntentFixtures;
import com.stripe.android.model.PaymentIntentParams;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class StripePaymentAuthTest {

    private Context mContext;

    @Mock private Activity mActivity;
    @Mock private PaymentAuthenticationController mPaymentAuthenticationController;
    @Mock private ApiResultCallback<PaymentAuthResult> mCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void startPaymentAuth_withConfirmParams_shouldConfirmAndAuth() {
        final Stripe stripe = createStripe();
        final PaymentIntentParams paymentIntentParams =
                PaymentIntentParams.createConfirmPaymentIntentWithPaymentMethodId(
                        "pm_card_threeDSecure2Required",
                        "client_secret",
                        "yourapp://post-authentication-return-url");
        stripe.startPaymentAuth(mActivity, paymentIntentParams);
        verify(mPaymentAuthenticationController).startConfirmAndAuth(eq(stripe), eq(mActivity),
                eq(paymentIntentParams), eq(ApiKeyFixtures.FAKE_PUBLISHABLE_KEY));
    }

    @Test
    public void onPaymentAuthResult_whenShouldHandleResultIsTrue_shouldCallHandleResult() {
        final Intent data = new Intent();
        when(mPaymentAuthenticationController.shouldHandleResult(
                PaymentAuthenticationController.REQUEST_CODE, Activity.RESULT_OK, data))
                .thenReturn(true);
        final Stripe stripe = createStripe();
        stripe.onPaymentAuthResult(PaymentAuthenticationController.REQUEST_CODE, Activity.RESULT_OK,
                data, mCallback);

        verify(mPaymentAuthenticationController).handleResult(stripe, data,
                ApiKeyFixtures.FAKE_PUBLISHABLE_KEY, mCallback);
    }

    @Test
    public void startPaymentAuth_withConfirmedPaymentIntent_shouldAuth() {
        final Stripe stripe = createStripe();
        stripe.startPaymentAuth(mActivity, PaymentIntentFixtures.PI_REQUIRES_3DS2);
        verify(mPaymentAuthenticationController).startAuth(
                eq(mActivity),
                eq(PaymentIntentFixtures.PI_REQUIRES_3DS2),
                eq(ApiKeyFixtures.FAKE_PUBLISHABLE_KEY)
        );
    }

    @NonNull
    private Stripe createStripe() {
        return new Stripe(
                new StripeApiHandler(
                        mContext,
                        new RequestExecutor(),
                        false),
                new StripeNetworkUtils(mContext),
                mPaymentAuthenticationController,
                ApiKeyFixtures.FAKE_PUBLISHABLE_KEY
        );
    }
}
