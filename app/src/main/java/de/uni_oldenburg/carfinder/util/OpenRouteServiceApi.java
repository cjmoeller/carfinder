package de.uni_oldenburg.carfinder.util;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;


public class OpenRouteServiceApi {


    //"&coordinates=8.34234,48.23424|7C8.34423,48.26424&profile=foot-walking"
    private String adresse;
    private double start_lat;
    private double start_lon;
    private double dest_lat;
    private double dest_lon;
    private double duration;

    public OpenRouteServiceApi(){

    }

    public OpenRouteServiceApi(double start_lat, double start_lon, double dest_lat, double dest_lon){
        this.start_lat = start_lat;
        this.start_lon = start_lon;
        this.dest_lat = dest_lat;
        this.dest_lon = dest_lon;
    }


    public void executeReqeustCall(){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, adresse, null,
                response -> {
                    try{
                        setDuration(response.getDouble("duration"));
                    }catch(Throwable e){
                        e.printStackTrace();
                    }
                }
                , error -> {
                    //TODO: well.. do nothing?
        });
    }


    public void createNewAdresse(){
        adresse = Constants.OPEN_ROUTE_SERVICE_ADRESSE + "&coordinates=" + Double.toString(start_lon) + "," + Double.toString(start_lat)
                + "|" + Double.toString(dest_lon) + "," + Double.toString(dest_lat);
    }

    public void createNewAdresse(double lon, double lat){
        adresse = Constants.OPEN_ROUTE_SERVICE_ADRESSE + "&coordinates=" + Double.toString(lon) + "," + Double.toString(lat)
                + "|" + Double.toString(dest_lon) + "," + Double.toString(lat);
    }

    public void setStartAdresse(double start_lat, double start_lon){
        this.start_lat = start_lat;
        this.start_lon = start_lon;
    }

    public void setDestAdresse(double dest_lat, double dest_lon) {
        this.dest_lat = dest_lat;
        this.dest_lon = dest_lon;
    }

    public void setDuration(double duration){
        this.duration = duration;
    }


    public double getStart_lat(){return start_lat;}

    public double getStart_lon(){return start_lon;}

    public double getDest_lat(){return dest_lat;}

    public double getDest_lon(){return dest_lon;}

    public double getDuration(){return duration;}


}
