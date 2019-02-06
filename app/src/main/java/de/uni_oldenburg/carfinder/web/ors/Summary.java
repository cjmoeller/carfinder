
package de.uni_oldenburg.carfinder.web.ors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Auto-generated
 */
public class Summary {

    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("duration")
    @Expose
    private Double duration;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

}
