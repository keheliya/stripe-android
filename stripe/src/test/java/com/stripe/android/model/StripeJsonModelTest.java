package com.stripe.android.model;

import android.support.annotation.NonNull;

import com.stripe.android.testharness.JsonTestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for {@link StripeJsonModel}.
 */
public class StripeJsonModelTest {

    @Test
    public void equals_whenEquals_returnsTrue() {
        assertTrue(StripeJsonModel.class.isAssignableFrom(Card.class));

        Card firstCard = Card.fromString(CardTest.JSON_CARD_USD);
        Card secondCard = Card.fromString(CardTest.JSON_CARD_USD);

        assertEquals(firstCard, secondCard);
        // Just confirming for sanity
        assertNotSame(firstCard, secondCard);
    }

    @Test
    public void equals_whenNotEquals_returnsFalse() {
        assertTrue(StripeJsonModel.class.isAssignableFrom(Card.class));
        final Card firstCard = Card.create("4242", null, null, null);
        final Card secondCard = Card.create("4343", null, null, null);
        assertNotEquals(firstCard, secondCard);
    }

    @Test
    public void hashCode_whenEquals_returnsSameValue() {
        assertTrue(StripeJsonModel.class.isAssignableFrom(Card.class));

        Card firstCard = Card.fromString(CardTest.JSON_CARD_USD);
        Card secondCard = Card.fromString(CardTest.JSON_CARD_USD);
        assertNotNull(firstCard);
        assertNotNull(secondCard);

        assertEquals(firstCard.hashCode(), secondCard.hashCode());
    }

    @Test
    public void hashCode_whenNotEquals_returnsDifferentValues() {
        assertTrue(StripeJsonModel.class.isAssignableFrom(Card.class));

        Card usdCard = Card.fromString(CardTest.JSON_CARD_USD);
        Card eurCard = Card.fromString(CardTest.JSON_CARD_EUR);

        assertNotNull(usdCard);
        assertNotNull(eurCard);

        assertNotEquals(usdCard.hashCode(), eurCard.hashCode());
    }

    @Test
    public void putStripeJsonModelListIfNotNull_forMapsWhenNull_doesNothing() {
        Map<String, Object> sampleMap = new HashMap<>();
        StripeJsonModel.putStripeJsonModelListIfNotNull(sampleMap, "mapkey", null);

        assertTrue(sampleMap.isEmpty());
    }

    @Test
    public void putStripeJsonModelListIfNotNull_forJsonWhenNull_doesNothing() {
        JSONObject jsonObject = new JSONObject();
        StripeJsonModel.putStripeJsonModelListIfNotNull(jsonObject, "jsonkey", null);

        assertFalse(jsonObject.has("jsonkey"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void putStripeJsonModelListIfNotNull_forMapsWhenNotNull_addsExpectedList() {
        List<ExampleJsonModel> exampleJsonModels = new ArrayList<>();
        exampleJsonModels.add(new ExampleJsonModel());
        exampleJsonModels.add(new ExampleJsonModel());

        Map<String, Object> originalMap = new HashMap<>();
        StripeJsonModel.putStripeJsonModelListIfNotNull(originalMap, "mapkey", exampleJsonModels);

        assertFalse(originalMap.isEmpty());
        assertTrue(originalMap.get("mapkey") instanceof List);

        List<Map<String, Object>> modelList = (List) originalMap.get("mapkey");
        List<Map<String, Object>> expectedList = new ArrayList<>();
        expectedList.add(new ExampleJsonModel().toMap());
        expectedList.add(new ExampleJsonModel().toMap());

        JsonTestUtils.assertListEquals(expectedList, modelList);
    }

    @Test
    public void putStripeJsonModelListIfNotNull_forJsonWhenNotNull_addsExpectedList() {
        List<ExampleJsonModel> exampleJsonModels = new ArrayList<>();
        exampleJsonModels.add(new ExampleJsonModel());
        exampleJsonModels.add(new ExampleJsonModel());

        JSONObject jsonObject = new JSONObject();
        StripeJsonModel.putStripeJsonModelListIfNotNull(jsonObject, "listkey", exampleJsonModels);

        assertEquals(1, jsonObject.length());
        JSONArray jsonArray = jsonObject.optJSONArray("listkey");
        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.length());

        JSONArray expectedArray = new JSONArray();
        expectedArray.put(new ExampleJsonModel().toJson());
        expectedArray.put(new ExampleJsonModel().toJson());

        JsonTestUtils.assertJsonArrayEquals(expectedArray, jsonArray);
    }

    private static class ExampleJsonModel extends StripeJsonModel {

        @NonNull
        @Override
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("akey", "avalue");
            map.put("bkey", "bvalue");
            return map;
        }

        @NonNull
        @Override
        public JSONObject toJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("akey", "avalue");
                jsonObject.put("bkey", "bvalue");
            } catch (JSONException unexpected) {
                fail("Test data failure: " + unexpected.getMessage());
            }
            return jsonObject;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ExampleJsonModel)) {
                return false;
            }

            ExampleJsonModel other = (ExampleJsonModel) obj;
            Map<String, Object> myMap = this.toMap();
            Map<String, Object> otherMap = other.toMap();
            for (String key : myMap.keySet()) {
                if (!otherMap.containsKey(key) || myMap.get(key).equals(otherMap.get(key))) {
                    return false;
                }
            }
            return myMap.size() == otherMap.size();
        }
    }
}
