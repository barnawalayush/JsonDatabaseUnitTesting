package ayush.example.server;

import com.google.gson.JsonElement;

public class Response {

    String response;
    String reason;
    JsonElement value;

    public Response() {
    }

    public void setResponse(String response) {
        this.response = response;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }
}
