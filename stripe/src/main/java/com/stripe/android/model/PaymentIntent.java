package com.stripe.android.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.stripe.android.StripeNetworkUtils;
import com.stripe.android.utils.ObjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.stripe.android.model.StripeJsonUtils.listToJsonArray;
import static com.stripe.android.model.StripeJsonUtils.optBoolean;
import static com.stripe.android.model.StripeJsonUtils.optCurrency;
import static com.stripe.android.model.StripeJsonUtils.optLong;
import static com.stripe.android.model.StripeJsonUtils.optMap;
import static com.stripe.android.model.StripeJsonUtils.optString;
import static com.stripe.android.model.StripeJsonUtils.putArrayIfNotNull;
import static com.stripe.android.model.StripeJsonUtils.putBooleanIfNotNull;
import static com.stripe.android.model.StripeJsonUtils.putLongIfNotNull;
import static com.stripe.android.model.StripeJsonUtils.putMapIfNotNull;
import static com.stripe.android.model.StripeJsonUtils.putStringIfNotNull;

/**
 * A PaymentIntent tracks the process of collecting a payment from your customer.
 *
 * <ul>
 * <li><a href="https://stripe.com/docs/payments/payment-intents">Payment Intents Overview</a></li>
 * <li><a href="https://stripe.com/docs/api/payment_intents">PaymentIntents API</a></li>
 * </ul>
 */
public class PaymentIntent extends StripeJsonModel {
    private static final String VALUE_PAYMENT_INTENT = "payment_intent";

    static final String FIELD_ID = "id";
    static final String FIELD_OBJECT = "object";
    static final String FIELD_AMOUNT = "amount";
    static final String FIELD_CREATED = "created";
    static final String FIELD_CANCELED = "canceled_at";
    static final String FIELD_CAPTURE_METHOD = "capture_method";
    static final String FIELD_CLIENT_SECRET = "client_secret";
    static final String FIELD_CONFIRMATION_METHOD = "confirmation_method";
    static final String FIELD_CURRENCY = "currency";
    static final String FIELD_DESCRIPTION = "description";
    static final String FIELD_LIVEMODE = "livemode";
    static final String FIELD_NEXT_ACTION = "next_action";
    static final String FIELD_PAYMENT_METHOD_TYPES = "payment_method_types";
    static final String FIELD_RECEIPT_EMAIL = "receipt_email";
    static final String FIELD_SOURCE = "source";
    static final String FIELD_STATUS = "status";

    private static final String FIELD_NEXT_ACTION_TYPE = "type";

    @Nullable private final String mId;
    @Nullable private final String mObjectType;
    @NonNull private final List<String> mPaymentMethodTypes;
    @Nullable private final Long mAmount;
    @Nullable private final Long mCanceledAt;
    @Nullable private final String mCaptureMethod;
    @Nullable private final String mClientSecret;
    @Nullable private final String mConfirmationMethod;
    @Nullable private final Long mCreated;
    @Nullable private final String mCurrency;
    @Nullable private final String mDescription;
    @Nullable private final Boolean mLiveMode;
    @Nullable private final Map<String, Object> mNextAction;
    @Nullable private final NextActionType mNextActionType;
    @Nullable private final String mReceiptEmail;
    @Nullable private final String mSource;
    @Nullable private final Status mStatus;

    @Nullable
    public String getId() {
        return mId;
    }

    @NonNull
    public List<String> getPaymentMethodTypes() {
        return mPaymentMethodTypes;
    }

    @Nullable
    public Long getAmount() {
        return mAmount;
    }

    @Nullable
    public Long getCanceledAt() {
        return mCanceledAt;
    }

    @Nullable
    public String getCaptureMethod() {
        return mCaptureMethod;
    }

    @Nullable
    public String getClientSecret() {
        return mClientSecret;
    }

    @Nullable
    public String getConfirmationMethod() {
        return mConfirmationMethod;
    }

    @Nullable
    public Long getCreated() {
        return mCreated;
    }

    @Nullable
    public String getCurrency() {
        return mCurrency;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public Boolean isLiveMode() {
        return mLiveMode;
    }

    public boolean requiresAction() {
        return mStatus == Status.RequiresAction;
    }

    public boolean requiresConfirmation() {
        return mStatus == Status.RequiresConfirmation;
    }

    @Nullable
    public Map<String, Object> getNextAction() {
        return mNextAction;
    }

    @Nullable
    public NextActionType getNextActionType() {
        return mNextActionType;
    }

    @Nullable
    public Uri getRedirectUrl() {
        final RedirectData redirectData = getRedirectData();
        if (redirectData == null) {
            return null;
        }

        return redirectData.url;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public SdkData getStripeSdkData() {
        if (mNextAction == null || NextActionType.UseStripeSdk != mNextActionType) {
            return null;
        }

        return new SdkData((Map<String, ?>) mNextAction.get(NextActionType.UseStripeSdk.code));
    }

    @Nullable
    public RedirectData getRedirectData() {
        if (NextActionType.RedirectToUrl != mNextActionType) {
            return null;
        }

        final Map<String, Object> nextAction;

        if (Status.RequiresAction == mStatus) {
            nextAction = mNextAction;
        } else {
            nextAction = null;
        }

        if (nextAction == null) {
            return null;
        }

        final NextActionType nextActionType = NextActionType
                .fromCode((String) nextAction.get(FIELD_NEXT_ACTION_TYPE));
        if (NextActionType.RedirectToUrl == nextActionType) {
            final Object redirectToUrl = nextAction.get(nextActionType.code);
            if (redirectToUrl instanceof Map) {
                return RedirectData.create((Map) redirectToUrl);
            }
        }

        return null;
    }

    @Nullable
    public String getReceiptEmail() {
        return mReceiptEmail;
    }

    @Nullable
    public String getSource() {
        return mSource;
    }

    @Nullable
    public Status getStatus() {
        return mStatus;
    }

    private PaymentIntent(
            @Nullable String id,
            @Nullable String objectType,
            @NonNull List<String> paymentMethodTypes,
            @Nullable Long amount,
            @Nullable Long canceledAt,
            @Nullable String captureMethod,
            @Nullable String clientSecret,
            @Nullable String confirmationMethod,
            @Nullable Long created,
            @Nullable String currency,
            @Nullable String description,
            @Nullable Boolean liveMode,
            @Nullable Map<String, Object> nextAction,
            @Nullable String receiptEmail,
            @Nullable String source,
            @Nullable Status status
    ) {
        mId = id;
        mObjectType = objectType;
        mPaymentMethodTypes = paymentMethodTypes;
        mAmount = amount;
        mCanceledAt = canceledAt;
        mCaptureMethod = captureMethod;
        mClientSecret = clientSecret;
        mConfirmationMethod = confirmationMethod;
        mCreated = created;
        mCurrency = currency;
        mDescription = description;
        mLiveMode = liveMode;
        mNextAction = nextAction;
        mReceiptEmail = receiptEmail;
        mSource = source;
        mStatus = status;
        mNextActionType = mNextAction != null ?
                NextActionType.fromCode((String) mNextAction.get(FIELD_NEXT_ACTION_TYPE)) : null;
    }

    @NonNull
    public static String parseIdFromClientSecret(@NonNull String clientSecret) {
        return clientSecret.split("_secret")[0];
    }

    @Nullable
    public static PaymentIntent fromString(@Nullable String jsonString) {
        try {
            return fromJson(new JSONObject(jsonString));
        } catch (JSONException ignored) {
            return null;
        }
    }

    @Nullable
    public static PaymentIntent fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null ||
                !VALUE_PAYMENT_INTENT.equals(jsonObject.optString(FIELD_OBJECT))) {
            return null;
        }

        final String id = optString(jsonObject, FIELD_ID);
        final String objectType = optString(jsonObject, FIELD_OBJECT);
        final List<String> paymentMethodTypes = jsonArrayToList(
                jsonObject.optJSONArray(FIELD_PAYMENT_METHOD_TYPES));
        final Long amount = optLong(jsonObject, FIELD_AMOUNT);
        final Long canceledAt = optLong(jsonObject, FIELD_CANCELED);
        final String captureMethod = optString(jsonObject, FIELD_CAPTURE_METHOD);
        final String clientSecret = optString(jsonObject, FIELD_CLIENT_SECRET);
        final String confirmationMethod = optString(jsonObject, FIELD_CONFIRMATION_METHOD);
        final Long created = optLong(jsonObject, FIELD_CREATED);
        final String currency = optCurrency(jsonObject, FIELD_CURRENCY);
        final String description = optString(jsonObject, FIELD_DESCRIPTION);
        final Boolean livemode = optBoolean(jsonObject, FIELD_LIVEMODE);
        final String receiptEmail = optString(jsonObject, FIELD_RECEIPT_EMAIL);
        final Status status = Status.fromCode(optString(jsonObject, FIELD_STATUS));
        final Map<String, Object> nextAction = optMap(jsonObject, FIELD_NEXT_ACTION);
        final String source = optString(jsonObject, FIELD_SOURCE);

        return new PaymentIntent(
                id,
                objectType,
                paymentMethodTypes,
                amount,
                canceledAt,
                captureMethod,
                clientSecret,
                confirmationMethod,
                created,
                currency,
                description,
                livemode,
                nextAction,
                receiptEmail,
                source,
                status);
    }

    @NonNull
    private static List<String> jsonArrayToList(@Nullable JSONArray jsonArray) {
        final List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.getString(i));
                } catch (JSONException ignored) {
                }
            }
        }

        return list;
    }

    @NonNull
    @Override
    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        putStringIfNotNull(jsonObject, FIELD_ID, mId);
        putStringIfNotNull(jsonObject, FIELD_OBJECT, mObjectType);
        putArrayIfNotNull(jsonObject, FIELD_PAYMENT_METHOD_TYPES,
                listToJsonArray(mPaymentMethodTypes));
        putLongIfNotNull(jsonObject, FIELD_AMOUNT, mAmount);
        putLongIfNotNull(jsonObject, FIELD_CANCELED, mCanceledAt);
        putStringIfNotNull(jsonObject, FIELD_CAPTURE_METHOD, mCaptureMethod);
        putStringIfNotNull(jsonObject, FIELD_CLIENT_SECRET, mClientSecret);
        putStringIfNotNull(jsonObject, FIELD_CONFIRMATION_METHOD, mConfirmationMethod);
        putLongIfNotNull(jsonObject, FIELD_CREATED, mCreated);
        putStringIfNotNull(jsonObject, FIELD_CURRENCY, mCurrency);
        putStringIfNotNull(jsonObject, FIELD_DESCRIPTION, mDescription);
        putBooleanIfNotNull(jsonObject, FIELD_LIVEMODE, mLiveMode);
        putMapIfNotNull(jsonObject, FIELD_NEXT_ACTION, mNextAction);
        putStringIfNotNull(jsonObject, FIELD_RECEIPT_EMAIL, mReceiptEmail);
        putStringIfNotNull(jsonObject, FIELD_SOURCE, mSource);
        putStringIfNotNull(jsonObject, FIELD_STATUS, mStatus != null ? mStatus.code : null);
        return jsonObject;
    }

    @NonNull
    @Override
    public Map<String, Object> toMap() {
        final AbstractMap<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, mId);
        map.put(FIELD_OBJECT, mObjectType);
        map.put(FIELD_PAYMENT_METHOD_TYPES, mPaymentMethodTypes);
        map.put(FIELD_AMOUNT, mAmount);
        map.put(FIELD_CANCELED, mCanceledAt);
        map.put(FIELD_CLIENT_SECRET, mClientSecret);
        map.put(FIELD_CAPTURE_METHOD, mCaptureMethod);
        map.put(FIELD_CONFIRMATION_METHOD, mConfirmationMethod);
        map.put(FIELD_CREATED, mCreated);
        map.put(FIELD_CURRENCY, mCurrency);
        map.put(FIELD_DESCRIPTION, mDescription);
        map.put(FIELD_LIVEMODE, mLiveMode);
        map.put(FIELD_NEXT_ACTION, mNextAction);
        map.put(FIELD_RECEIPT_EMAIL, mReceiptEmail);
        map.put(FIELD_STATUS, mStatus != null ? mStatus.code : null);
        map.put(FIELD_SOURCE, mSource);
        StripeNetworkUtils.removeNullAndEmptyParams(map);
        return map;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || (obj instanceof PaymentIntent && typedEquals((PaymentIntent) obj));
    }

    private boolean typedEquals(@NonNull PaymentIntent paymentIntent) {
        return ObjectUtils.equals(mId, paymentIntent.mId)
                && ObjectUtils.equals(mObjectType, paymentIntent.mObjectType)
                && ObjectUtils.equals(mAmount, paymentIntent.mAmount)
                && ObjectUtils.equals(mCanceledAt, paymentIntent.mCanceledAt)
                && ObjectUtils.equals(mCaptureMethod, paymentIntent.mCaptureMethod)
                && ObjectUtils.equals(mClientSecret, paymentIntent.mClientSecret)
                && ObjectUtils.equals(mConfirmationMethod, paymentIntent.mConfirmationMethod)
                && ObjectUtils.equals(mCreated, paymentIntent.mCreated)
                && ObjectUtils.equals(mCurrency, paymentIntent.mCurrency)
                && ObjectUtils.equals(mDescription, paymentIntent.mDescription)
                && ObjectUtils.equals(mLiveMode, paymentIntent.mLiveMode)
                && ObjectUtils.equals(mReceiptEmail, paymentIntent.mReceiptEmail)
                && ObjectUtils.equals(mSource, paymentIntent.mSource)
                && ObjectUtils.equals(mStatus, paymentIntent.mStatus)
                && ObjectUtils.equals(mPaymentMethodTypes, paymentIntent.mPaymentMethodTypes)
                && ObjectUtils.equals(mNextAction, paymentIntent.mNextAction)
                && ObjectUtils.equals(mNextActionType, paymentIntent.mNextActionType);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hash(mId, mObjectType, mAmount, mCanceledAt, mCaptureMethod,
                mClientSecret, mConfirmationMethod, mCreated, mCurrency, mDescription, mLiveMode,
                mReceiptEmail, mSource, mStatus, mPaymentMethodTypes, mNextAction, mNextActionType);
    }

    /**
     * See https://stripe.com/docs/api/payment_intents/object#payment_intent_object-next_action-type
     */
    public enum NextActionType {
        RedirectToUrl("redirect_to_url"),
        UseStripeSdk("use_stripe_sdk");

        @NonNull public final String code;

        NextActionType(@NonNull String code) {
            this.code = code;
        }

        @Nullable
        public static NextActionType fromCode(@Nullable String code) {
            if (code == null) {
                return null;
            }

            for (NextActionType nextActionType : values()) {
                if (nextActionType.code.equals(code)) {
                    return nextActionType;
                }
            }

            return null;
        }

        @NonNull
        @Override
        public String toString() {
            return code;
        }
    }

    /**
     * See https://stripe.com/docs/api/payment_intents/object#payment_intent_object-status
     */
    public enum Status {
        Canceled("canceled"),
        Processing("processing"),
        RequiresAction("requires_action"),
        RequiresAuthorization("requires_authorization"),
        RequiresCapture("requires_capture"),
        RequiresConfirmation("requires_confirmation"),
        RequiresPaymentMethod("requires_payment_method"),
        Succeeded("succeeded");

        @NonNull
        public final String code;

        Status(@NonNull String code) {
            this.code = code;
        }

        @Nullable
        public static Status fromCode(@Nullable String code) {
            if (code == null) {
                return null;
            }

            for (Status status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }

            return null;
        }

        @NonNull
        @Override
        public String toString() {
            return code;
        }
    }

    public static class RedirectData {
        static final String FIELD_URL = "url";
        static final String FIELD_RETURN_URL = "return_url";

        /**
         * See <a href="https://stripe.com/docs/api
         * /payment_intents/object#payment_intent_object-next_action-redirect_to_url-url">
         * PaymentIntent.next_action.redirect_to_url.url
         * </a>
         */
        @NonNull public final Uri url;

        /**
         * See <a href="https://stripe.com/docs/api
         * /payment_intents/object#payment_intent_object-next_action-redirect_to_url-return_url">
         * PaymentIntent.next_action.redirect_to_url.return_url
         * </a>
         */
        @Nullable public final Uri returnUrl;

        @Nullable
        static RedirectData create(@NonNull Map<?, ?> redirectToUrlHash) {
            final Object urlObj = redirectToUrlHash.get(FIELD_URL);
            final Object returnUrlObj = redirectToUrlHash.get(FIELD_RETURN_URL);
            final String url = (urlObj instanceof String) ? urlObj.toString() : null;
            final String returnUrl = (returnUrlObj instanceof String) ?
                    returnUrlObj.toString() : null;
            if (url == null) {
                return null;
            }

            return new RedirectData(url, returnUrl);
        }

        @VisibleForTesting
        RedirectData(@NonNull String url, @Nullable String returnUrl) {
            this.url = Uri.parse(url);
            this.returnUrl = returnUrl != null ? Uri.parse(returnUrl) : null;
        }
    }

    public static final class SdkData {
        private static final String FIELD_TYPE = "type";

        private static final String TYPE_3DS2 = "stripe_3ds2_fingerprint";
        private static final String TYPE_3DS1 = "three_d_secure_redirect";

        @NonNull final String type;
        @NonNull final Map<String, ?> data;

        SdkData(@NonNull Map<String, ?> data) {
            this.type = Objects.requireNonNull((String) data.get(FIELD_TYPE));
            this.data = data;
        }

        public boolean is3ds2() {
            return TYPE_3DS2.equals(type);
        }

        public boolean is3ds1() {
            return TYPE_3DS1.equals(type);
        }
    }
}
