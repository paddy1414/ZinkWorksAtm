package org.patricknorton.zinkworks.ZinkWorksAtm.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

public class Response {
    private final String result;
    private final int responseCode;

    public Response(String result, int responseCode) {
        this.result = result;
        this. responseCode = responseCode;
    }

    public String getResult() {
        return result;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public JSONObject getJsonObject() {
        return new JSONObject(result);
    }
    public JsonArray getJsonArray() {
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(result).getAsJsonArray();
        return jsonArray;
    }
}
