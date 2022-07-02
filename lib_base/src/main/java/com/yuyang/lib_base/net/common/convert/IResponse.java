package com.yuyang.lib_base.net.common.convert;

/**
 * http响应模板
 *
 * @author 17112281
 * @since 2018/11/7
 */
public class IResponse<T> {

    private int status = -100;
    private String message;
    private JsonDataBean jsonData;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonDataBean getJsonData() {
        return jsonData;
    }

    public void setJsonData(JsonDataBean jsonData) {
        this.jsonData = jsonData;
    }

    public class JsonDataBean {
        private String returnCode;
        private String returnMsg;
        private T data;
        private T USERINFO;

        public String getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(String returnCode) {
            this.returnCode = returnCode;
        }

        public String getReturnMsg() {
            return returnMsg;
        }

        public void setReturnMsg(String returnMsg) {
            this.returnMsg = returnMsg;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public T getUSERINFO() {
            return USERINFO;
        }

        public void setUSERINFO(T USERINFO) {
            this.USERINFO = USERINFO;
        }
    }
}
