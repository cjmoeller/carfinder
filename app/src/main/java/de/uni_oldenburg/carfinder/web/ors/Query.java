
package de.uni_oldenburg.carfinder.web.ors;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Auto-generated
 */
public class Query {

    @SerializedName("profile")
    @Expose
    private String profile;
    @SerializedName("preference")
    @Expose
    private String preference;
    @SerializedName("coordinates")
    @Expose
    private List<List<Double>> coordinates = null;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("units")
    @Expose
    private String units;
    @SerializedName("geometry")
    @Expose
    private Boolean geometry;
    @SerializedName("geometry_format")
    @Expose
    private String geometryFormat;
    @SerializedName("instructions_format")
    @Expose
    private String instructionsFormat;
    @SerializedName("instructions")
    @Expose
    private Boolean instructions;
    @SerializedName("elevation")
    @Expose
    private Boolean elevation;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Boolean getGeometry() {
        return geometry;
    }

    public void setGeometry(Boolean geometry) {
        this.geometry = geometry;
    }

    public String getGeometryFormat() {
        return geometryFormat;
    }

    public void setGeometryFormat(String geometryFormat) {
        this.geometryFormat = geometryFormat;
    }

    public String getInstructionsFormat() {
        return instructionsFormat;
    }

    public void setInstructionsFormat(String instructionsFormat) {
        this.instructionsFormat = instructionsFormat;
    }

    public Boolean getInstructions() {
        return instructions;
    }

    public void setInstructions(Boolean instructions) {
        this.instructions = instructions;
    }

    public Boolean getElevation() {
        return elevation;
    }

    public void setElevation(Boolean elevation) {
        this.elevation = elevation;
    }

}
