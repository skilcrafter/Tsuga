package life.tsuga.tsuga;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class EventMap extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = EventMap.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        Bundle extras = getIntent().getExtras();
        double latValue = extras.getDouble("LatValue");
        double longValue = extras.getDouble("LongValue");
        String eventTitle = extras.getString("eventTitle");

        LatLng mapLocation = new LatLng(latValue, longValue);
        Log.i(TAG, "Location: " + mapLocation);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 13));

        map.addMarker(new MarkerOptions()
                .title(eventTitle)
                .position(mapLocation));
     }
}
