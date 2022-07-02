package com.yuyang.lib_base.net.unify;

public class HttpRequest {


    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD"),
        MOVE("MOVE"),
        COPY("COPY"),
        DELETE("DELETE"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE"),
        CONNECT("CONNECT");

        private final String value;

        private HttpMethod(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}
