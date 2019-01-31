package de.uni_oldenburg.carfinder.util;

/**
 * Singleton that helps to determine if the user deleted the current parking spot.
 */
public class DeleteStateHelper {

    public boolean isDeletedCurrentSpot() {
        return deletedCurrentSpot;
    }

    public void setDeletedCurrentSpot(boolean deletedCurrentSpot) {
        this.deletedCurrentSpot = deletedCurrentSpot;
    }

    boolean deletedCurrentSpot;

    private static DeleteStateHelper instance;

    public static DeleteStateHelper getInstance(){
        if(instance == null)
            instance = new DeleteStateHelper();
        return instance;
    }


    private DeleteStateHelper(){
        this.deletedCurrentSpot = false;
    }
}
