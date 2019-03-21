package ni.alvaro.dev.aventontest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Buddy {
    @SerializedName("lt")
    @Expose
    private String lt;
    @SerializedName("lg")
    @Expose
    private String lg;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
