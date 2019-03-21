package ni.alvaro.dev.aventontest.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ni.alvaro.dev.aventontest.models.Buddy;

public class ServerResponse {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("result")
    @Expose
    private List<Buddy> result = null;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Buddy> getResult() {
        return result;
    }

    public void setResult(List<Buddy> result) {
        this.result = result;
    }
}
