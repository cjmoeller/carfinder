
package de.uni_oldenburg.carfinder.web.ors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Auto-generated
 */
public class Info {

    @SerializedName("attribution")
    @Expose
    private String attribution;
    @SerializedName("engine")
    @Expose
    private Engine engine;
    @SerializedName("service")
    @Expose
    private String service;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("query")
    @Expose
    private Query query;

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

}
