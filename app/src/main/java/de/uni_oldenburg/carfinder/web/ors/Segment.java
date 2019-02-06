
package de.uni_oldenburg.carfinder.web.ors;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Auto-generated
 */
public class Segment {

    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("duration")
    @Expose
    private Double duration;
    @SerializedName("steps")
    @Expose
    private List<Step> steps = null;

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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

}
