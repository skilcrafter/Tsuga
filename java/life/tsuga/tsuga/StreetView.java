package life.tsuga.tsuga;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;


public class StreetView extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback {

    double mlatValue;
    double mlongValue;
    LatLng mMapLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Bundle extras = getIntent().getExtras();
        mlatValue = extras.getDouble("LatValue");
        mlongValue = extras.getDouble("LongValue");
        mMapLocation = new LatLng(mlatValue, mlongValue);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
         streetViewPanorama.setPosition(mMapLocation);
    }
}
