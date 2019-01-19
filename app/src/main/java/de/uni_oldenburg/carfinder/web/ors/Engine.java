
package de.uni_oldenburg.carfinder.web.ors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Engine {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("build_date")
    @Expose
    private String buildDate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

}
