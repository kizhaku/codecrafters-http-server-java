package dto;

import enums.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final String httpVersion;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(Builder builder) {
        this.httpVersion = builder.httpVersion;
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //Append response line
        builder.append(this.httpVersion).append(" ")
                .append(this.statusCode).append(" ")
                .append(this.statusMessage).append("\r\n");

        //Append headers
        headers.forEach((key, value) -> builder.append(key)
                .append(": ")
                .append(value)
                .append("\r\n"));

        //End of header
        builder.append("\r\n");

        //Append body
        builder.append(this.body);

        return builder.toString();
    }

    public static class Builder {
        private final String httpVersion = "HTTP/1.1";
        private final int statusCode;
        private final String statusMessage;
        private Map<String, String> headers;
        private String body = ""; //Text as of now

        public Builder(HttpStatus statusCode) {
            this.statusCode = statusCode.getStatusCode();
            this.statusMessage = statusCode.getStatusMessage();
            this.initHeaderDefaults();
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

        private void initHeaderDefaults() {
            this.headers = new HashMap<>();
            this.headers.put("Content-Type", "text/plain");
        }

    }
}
