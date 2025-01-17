package com.stripe.android;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.utils.ObjectUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A class representing a Stripe API or Analytics request.
 */
final class ApiRequest extends StripeRequest {
    static final String MIME_TYPE = "application/x-www-form-urlencoded";
    static final String API_HOST = "https://api.stripe.com";

    private static final String ANALYTICS_HOST = "https://q.stripe.com";

    @NonNull final Options options;

    private ApiRequest(@NonNull Method method,
                       @NonNull String url,
                       @Nullable Map<String, ?> params,
                       @NonNull Options options) {
        super(method, url, params, MIME_TYPE);
        this.options = options;
    }

    @NonNull
    static ApiRequest createGet(@NonNull String url,
                                @NonNull Options options) {
        return new ApiRequest(Method.GET, url, null, options);
    }

    @NonNull
    static ApiRequest createGet(@NonNull String url,
                                @NonNull Map<String, ?> params,
                                @NonNull Options options) {
        return new ApiRequest(Method.GET, url, params, options);
    }

    @NonNull
    static ApiRequest createPost(@NonNull String url,
                                 @NonNull Options options) {
        return new ApiRequest(Method.POST, url, null, options);
    }

    @NonNull
    static ApiRequest createPost(@NonNull String url,
                                 @NonNull Map<String, ?> params,
                                 @NonNull Options options) {
        return new ApiRequest(Method.POST, url, params, options);
    }

    @NonNull
    static ApiRequest createDelete(@NonNull String url,
                                   @NonNull Options options) {
        return new ApiRequest(Method.DELETE, url, null, options);
    }

    @NonNull
    static ApiRequest createAnalyticsRequest(@NonNull Map<String, ?> params,
                                             @NonNull Options options) {
        return new ApiRequest(Method.GET, ANALYTICS_HOST, params, options);
    }

    @NonNull
    @Override
    Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Charset", CHARSET);
        headers.put("Accept", "application/json");
        headers.put("User-Agent",
                String.format(Locale.ROOT, "Stripe/v1 AndroidBindings/%s",
                        BuildConfig.VERSION_NAME));

        // debug headers
        final AbstractMap<String, String> propertyMap = new HashMap<>();
        propertyMap.put("java.version", System.getProperty("java.version"));
        propertyMap.put("os.name", "android");
        propertyMap.put("os.version", String.valueOf(Build.VERSION.SDK_INT));
        propertyMap.put("bindings.version", BuildConfig.VERSION_NAME);
        propertyMap.put("lang", "Java");
        propertyMap.put("publisher", "Stripe");

        headers.put("X-Stripe-Client-User-Agent", new JSONObject(propertyMap).toString());
        headers.put("Stripe-Version", ApiVersion.getDefault().getCode());

        headers.put("Authorization", String.format(Locale.ENGLISH,
                "Bearer %s", options.apiKey));
        if (options.stripeAccount != null) {
            headers.put("Stripe-Account", options.stripeAccount);
        }
        return headers;
    }

    @NonNull
    @Override
    byte[] getOutputBytes() throws UnsupportedEncodingException, InvalidRequestException {
        return createQuery().getBytes(CHARSET);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hash(getBaseHashCode(), options);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj) ||
                (obj instanceof ApiRequest && typedEquals((ApiRequest) obj));
    }

    private boolean typedEquals(@NonNull ApiRequest obj) {
        return super.typedEquals(obj) && ObjectUtils.equals(options, obj.options);
    }

    /**
     * Data class representing options for a Stripe API request.
     */
    static final class Options {
        @NonNull final String apiKey;
        @Nullable final String stripeAccount;

        @NonNull
        static Options create(@NonNull String apiKey) {
            return new Options(apiKey, null);
        }

        @NonNull
        static Options create(@NonNull String apiKey, @Nullable String stripeAccount) {
            return new Options(apiKey, stripeAccount);
        }

        private Options(
                @NonNull String apiKey,
                @Nullable String stripeAccount) {
            this.apiKey = new ApiKeyValidator().requireValid(apiKey);
            this.stripeAccount = stripeAccount;
        }

        @Override
        public int hashCode() {
            return ObjectUtils.hash(apiKey, stripeAccount);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj) || (obj instanceof Options && typedEquals((Options) obj));
        }

        private boolean typedEquals(@NonNull Options obj) {
            return ObjectUtils.equals(apiKey, obj.apiKey) &&
                    ObjectUtils.equals(stripeAccount, obj.stripeAccount);
        }
    }
}
