package de.uni_oldenburg.carfinder.persistence;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a Parking Spot.
 */
@Entity(tableName = "parking_spots")
public class ParkingSpot implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * Time when the parking spot entry was created.
     */
    private long timestamp;
    /**
     * User Description.
     */
    private String description;
    /**
     * Location of a picture if present, null otherwise.
     */
    private String imageLocation;
    /**
     * If this parking spot is currently used by the user.
     */
    private boolean isCurrentlyUsed;
    /**
     * The time when the parking clock expires if present. -1 otherwise.
     */
    private long expiresAt;
    /**
     * Latitude of the parking spot.
     */
    private double latitude;
    /**
     * Longitude of the parking spot.
     */
    private double longitude;
    /**
     * Address of the parking spot.
     */
    private String address;


    /**
     * The user defined name of the parking spot.
     */
    private String name;

    public ParkingSpot(long timestamp, String name, String description, String imageLocation, boolean isCurrentlyUsed, long expiresAt, double latitude, double longitude, String address) {
        this.timestamp = timestamp;
        this.description = description;
        this.imageLocation = imageLocation;
        this.isCurrentlyUsed = isCurrentlyUsed;
        this.expiresAt = expiresAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public boolean isCurrentlyUsed() {
        return isCurrentlyUsed;
    }

    public void setCurrentlyUsed(boolean currentlyUsed) {
        isCurrentlyUsed = currentlyUsed;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public ParkingSpot clone() {
        ParkingSpot result = new ParkingSpot(this.timestamp, this.name, this.description,
                this.imageLocation, this.isCurrentlyUsed, this.expiresAt, this.latitude,
                this.longitude, this.address);
        result.setId(this.getId());
        return result;
    }

}
