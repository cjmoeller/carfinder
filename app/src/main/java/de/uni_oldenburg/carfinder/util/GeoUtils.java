package de.uni_oldenburg.carfinder.util;

import android.location.Location;

import java.util.List;

public class GeoUtils {
    public final static float TRANSITION_SPEED_CONSTANT = 8f / 3.6f; //8 km/h in m/s

    /**
     * Finds transitions in speed. Location should be measured each ~10 seconds
     *
     * @param locations
     * @return
     */
    public static Location getTransitionLocation(List<Location> locations) {
        if (locations.size() == 0)
            return null;
        if (locations.size() < 2)
            return locations.get(0);
        double last = -1;
        for (int i = locations.size() - 1; i >= 0; i--) { //iterate backwards to find the most recent change in speed
            if (last != -1) {
                double diff = Math.abs(locations.get(i).getSpeed() - last);
                if (diff > TRANSITION_SPEED_CONSTANT)
                    return locations.get(i);
            }
            last = locations.get(i).getSpeed();
        }
        return locations.get(locations.size() - 1);
    }
}
