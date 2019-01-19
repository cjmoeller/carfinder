
package de.uni_oldenburg.carfinder.web.ors;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("summary")
    @Expose
    private Summary summary;
    @SerializedName("geometry_format")
    @Expose
    private String geometryFormat;
    @SerializedName("geometry")
    @Expose
    private String geometry;
    @SerializedName("segments")
    @Expose
    private List<Segment> segments = null;
    @SerializedName("way_points")
    @Expose
    private List<Integer> wayPoints = null;
    @SerializedName("bbox")
    @Expose
    private List<Double> bbox = null;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public String getGeometryFormat() {
        return geometryFormat;
    }

    public void setGeometryFormat(String geometryFormat) {
        this.geometryFormat = geometryFormat;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public List<Integer> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<Integer> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

}
