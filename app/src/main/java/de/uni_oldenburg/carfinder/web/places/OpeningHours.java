
package de.uni_oldenburg.carfinder.web.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Auto-generated
 */
public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

}
