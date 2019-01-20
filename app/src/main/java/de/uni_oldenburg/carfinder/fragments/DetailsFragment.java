package de.uni_oldenburg.carfinder.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.PhotoUtils;

public class DetailsFragment extends Fragment {


    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    private TextView addedTime;
    private TextView notes;
    private ImageView picture;
    private View rootView;
    private ParkingSpot data;
    private TextView parkingMeter;
    private CardView imageDetails;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        this.initUI();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Sets the parking spot data to display
     *
     * @param data
     */
    public void setData(ParkingSpot data) {

        this.data = data;

        displayData();
    }

    public void displayData() {

        Date currentDate = new Date(data.getTimestamp());
        String dateString = new SimpleDateFormat("dd.MM.yy, HH:mm").format(currentDate);

        if (this.data.getExpiresAt() != -1) {
            Date parkingMeter = new Date(this.data.getExpiresAt());
            String parkingMeterString = new SimpleDateFormat("dd.MM.yy, HH:mm").format(parkingMeter);

            this.parkingMeter.setText(getString(R.string.parking_meter_expires) + " " + parkingMeterString);
        } else {
            this.parkingMeter.setText(getString(R.string.parking_meter_not_set));
        }

        this.addedTime.setText(getString(R.string.added_on) + " " + dateString);
        if (!data.getDescription().equals(getString(R.string.add_note)))
            this.notes.setText(data.getDescription());
        else
            this.notes.setText("-");

        this.picture.post(() -> {
            if (data.getImageLocation() != null) //TODO: check if image exists
                PhotoUtils.loadFileIntoImageView(DetailsFragment.this.picture, data.getImageLocation());
            else {
                imageDetails.setVisibility(View.GONE);
            }
        });

        this.picture.setOnClickListener(view -> zoomImageFromThumb(this.picture, this.picture.getDrawable()));

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


    }


    private void initUI() {
        this.addedTime = rootView.findViewById(R.id.detailsTimeAdded);
        this.picture = rootView.findViewById(R.id.detailsPicture);
        this.notes = rootView.findViewById(R.id.detailsNote);
        this.parkingMeter = rootView.findViewById(R.id.detailsParkingMeter);
        this.imageDetails = rootView.findViewById(R.id.imageDetails);

    }

    /**
     * Modified from https://developer.android.com/training/animation/zoom
     * @param thumbView
     * @param drawable
     */
    private void zoomImageFromThumb(final View thumbView, Drawable drawable) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = rootView.findViewById(
                R.id.detailsPictureBig);
        expandedImageView.setImageDrawable(drawable);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);

        rootView.findViewById(R.id.containerDetails)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).

        // Extend start bounds horizontally
        float startScale = (float) startBounds.height() / finalBounds.height();
        float startWidth = startScale * finalBounds.width();
        float deltaWidth = (startWidth - startBounds.width()) / 2;
        startBounds.left -= deltaWidth;
        startBounds.right += deltaWidth;


        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(this.mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(view -> {
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(expandedImageView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(mShortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }
            });
            set1.start();
            mCurrentAnimator = set1;
        });
    }

}
