package life.tsuga.tsuga;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class PostActivity extends ActionBarActivity {

    protected EditText mEvent;
    protected Uri mImageUri;
    protected String mFileType = "image";
    protected String mPostEvent;
    protected Button mMapButton;
    int PLACE_PICKER_REQUEST = 1;
    protected String mPlaceShort;
    protected EditText mComment;
    protected String mPostComment;
    protected EditText mLocationField;
    protected String mAddress;
    protected String mId;
    protected String mPhoneNumber;
    protected Uri mWebsiteUri;
    protected String mLatLag;
    protected LatLngBounds mViewport;
    protected String mWebsiteUriString;
    protected LatLng mEventLatLng;
    protected double mLatitude;
    protected double mLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mImageUri = getIntent().getData();
        ImageView imgView=(ImageView)findViewById(R.id.postImage);
        imgView.setImageURI(mImageUri);


        mMapButton = (Button)findViewById(R.id.mapbutton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Context context = getApplicationContext();
                try {
                    startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                mPlaceShort = place.getName().toString();
                mAddress = place.getAddress().toString();
                mId = place.getId();
                mPhoneNumber = place.getPhoneNumber().toString();
                mWebsiteUri = place.getWebsiteUri();
                if (mWebsiteUri != null) {
                  mWebsiteUriString = mWebsiteUri.toString();
                }
                mLatLag = place.getLatLng().toString();
                mEventLatLng = place.getLatLng();
                mLatitude= mEventLatLng.latitude;

                mLongitude = mEventLatLng.longitude;

                mViewport = place.getViewport();

                if (mPlaceShort != null) {
                    EditText editText = (EditText) findViewById(R.id.locationField);
                    editText.setText(mPlaceShort);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_post:
                mEvent = (EditText)findViewById(R.id.eventField);
                mPostEvent = mEvent.getText().toString();
                mPostEvent = mPostEvent.trim();

                mComment = (EditText)findViewById(R.id.commentField);
                mPostComment = mComment.getText().toString();
                mPostComment = mPostComment.trim();

                mLocationField = (EditText)findViewById(R.id.locationField);
                mPlaceShort = mLocationField.getText().toString();
                mPlaceShort = mPlaceShort.trim();

                ParseObject message = createMessage();
                if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.posting_error_message)
                            .setTitle(R.string.posting_error_message)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected ParseObject createMessage(){
        ParseObject message = new ParseObject(ParseConstants.CLASS_EVENTS);
        message.put(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_USERNAME, ParseUser.getCurrentUser().getString(ParseConstants.KEY_PERSON_NAME));
        message.put(ParseConstants.KEY_EVENT, mPostEvent);

        if (mPlaceShort !=null){
        message.put(ParseConstants.KEY_PLACE_SHORT,mPlaceShort);}
        message.put(ParseConstants.KEY_COMMENT,mPostComment);
        if (mAddress !=null){
        message.put(ParseConstants.KEY_ADDRESS,mAddress);}
        if (mId !=null){
        message.put(ParseConstants.KEY_PLACESID,mId);}
        if (mPhoneNumber != null){
        message.put(ParseConstants.KEY_PHONENUMBER,mPhoneNumber);}
        message.put(ParseConstants.KEY_LATITUDE,mLatitude);
        message.put(ParseConstants.KEY_LONGITUDE,mLongitude);
        if(mWebsiteUri!=null) {
            message.put(ParseConstants.KEY_WEBSITE, mWebsiteUriString);
        }

        byte[]fileBytes = FileHelper.getByteArrayFromFile(this, mImageUri);

        if (fileBytes == null){
            return null;
        }
        else {
            fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            String fileName = FileHelper.getFileName(this, mImageUri,mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_IMAGE, file);
        }

        byte[]tMFileBytes = TMFileHelper.getByteArrayFromFile(this, mImageUri);
        if (tMFileBytes == null){
            return null;
        }
        else {
            tMFileBytes = TMFileHelper.reduceImageForUpload(tMFileBytes);
            String tMFileName = FileHelper.getFileName(this, mImageUri,mFileType);
            ParseFile tMFile = new ParseFile(tMFileName, tMFileBytes);
            message.put(ParseConstants.KEY_THUMBNAILIMAGE, tMFile);
        }
        return message;
    }

    protected void send(ParseObject event){
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(PostActivity.this,R.string.post_confirmation, Toast.LENGTH_LONG).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setMessage(R.string.posting_error_message)
                            .setTitle(R.string.attention_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
