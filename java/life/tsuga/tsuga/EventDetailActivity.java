package life.tsuga.tsuga;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;


public class EventDetailActivity extends Activity {

        public static final String TAG = EventDetailActivity.class.getSimpleName();

        String mEventID;
        String mWebsite;
        static double mLatitude;
        static double mLongitude;
        static String mPlace;
        protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    protected void onResume(){
        super.onResume();
        queryData();
    }


    private void queryData() {
        final Intent intent = getIntent();
        mEventID = intent.getExtras().getString("eventId");

        if (isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_EVENTS);
        query.getInBackground(mEventID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {


                    Uri fileUri = Uri.parse((parseObject.getParseFile(ParseConstants.KEY_IMAGE)).getUrl());

                    String event = parseObject.getString(ParseConstants.KEY_EVENT);
                    String comment = parseObject.getString(ParseConstants.KEY_COMMENT);
                    mPlace = parseObject.getString(ParseConstants.KEY_PLACE_SHORT);
                    String address = parseObject.getString(ParseConstants.KEY_ADDRESS);
                    String phoneNumber = parseObject.getString(ParseConstants.KEY_PHONENUMBER);
                    mWebsite = parseObject.getString(ParseConstants.KEY_WEBSITE);
                    String submitter = parseObject.getString(ParseConstants.KEY_USERNAME);
                    mLatitude = parseObject.getDouble((ParseConstants.KEY_LATITUDE));
                    mLongitude = parseObject.getDouble(ParseConstants.KEY_LONGITUDE);

                    ImageView imageView = (ImageView) findViewById(R.id.eventDetailImageView);
                    TextView eventView = (TextView) findViewById(R.id.eventTextView);
                    TextView commentView = (TextView) findViewById(R.id.commentTextView);
                    TextView placeView = (TextView) findViewById(R.id.placeTextView);
                    TextView addressView = (TextView) findViewById(R.id.addressTextView);
                    TextView phoneNumberView = (TextView) findViewById(R.id.phoneNumberTextView);
                    TextView websiteView = (TextView) findViewById(R.id.websiteTextView);
                    TextView submitterView = (TextView) findViewById(R.id.submitterTextView);

                    Picasso.with(EventDetailActivity.this).load(fileUri.toString()).into(imageView);

                    eventView.setText(event);
                    commentView.setText(comment);
                    placeView.setText(mPlace);
                    addressView.setText(address);
                    phoneNumberView.setText(phoneNumber);
                    websiteView.setText(mWebsite);
                    submitterView.setText(submitter);

                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.general_error)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        }

        else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }

        TextView WebTextview = (TextView)findViewById(R.id.websiteTextView);
        WebTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webviewIntent = new Intent(Intent.ACTION_VIEW);
                webviewIntent.setData(Uri.parse(mWebsite));
                startActivity(webviewIntent);
            }
        });

        Button StreetViewButton = (Button)findViewById(R.id.StreetViewButton);
        StreetViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent streetViewIntent = new Intent(EventDetailActivity.this, StreetView.class);
                streetViewIntent.putExtra("LatValue", mLatitude);
                streetViewIntent.putExtra("LongValue", mLongitude);
                startActivity(streetViewIntent);
            }
        });

        TextView addressTextView = (TextView)findViewById(R.id.addressTextView);
        addressTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(EventDetailActivity.this,EventMap.class);
                mapIntent.putExtra("LatValue", mLatitude);
                mapIntent.putExtra("LongValue", mLongitude);
                mapIntent.putExtra("eventTitle", mPlace);
                startActivity(mapIntent);
            }
        });

        Button SaveEventButton = (Button)findViewById(R.id.saveEventButton);
        SaveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseObject message = saveFavorite();

                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                    builder.setMessage(R.string.posting_error_message)
                            .setTitle(R.string.posting_error_message)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                }
            }
        });
    }

    protected ParseObject saveFavorite(){
        ParseObject message = new ParseObject(ParseConstants.CLASS_FAVORITES);
        message.put(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_EVENT, mEventID);

        return message;
    }

    protected void send(ParseObject event){
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(EventDetailActivity.this, R.string.saved_confirmation, Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                    builder.setMessage(R.string.save_error_message)
                            .setTitle(R.string.attention_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }

        return isAvailable;
    }

}
