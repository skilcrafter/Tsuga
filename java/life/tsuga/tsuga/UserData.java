package life.tsuga.tsuga;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserData extends ActionBarActivity {

    protected ImageView mUserImage;
    protected TextView mUsername;
    protected EditText mEmail;
    protected EditText mName;
    protected EditText mLocation;
    protected Button mSaveButton;
    protected ParseUser mCurrentUser;
    protected String mCurrentUsername;
    protected String mCurrentEmail;
    protected String mCurrentName;
    protected String mCurrentLocation;
    protected Button mUserPhotoButton;
    public  Uri mImageUri;
    protected String mFileType = "image";
    protected ParseFile mParseImageFile;
    protected ProgressBar mProgressBar;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        mCurrentUser = ParseUser.getCurrentUser();
        setUserData();
    }

    private void setUserData() {
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mUserImage = (ImageView) findViewById(R.id.userPictureView);
        mUsername = (TextView)findViewById(R.id.usernameField);
        mEmail = (EditText)findViewById(R.id.emailField);
        mSaveButton = (Button)findViewById(R.id.saveDataButton);
        mName = (EditText)findViewById(R.id.personNameField);
        mLocation = (EditText)findViewById(R.id.locationField);

        if (isNetworkAvailable()){
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentUsername = mCurrentUser.getString(ParseConstants.KEY_CURRENT_USERNAME);
        mCurrentEmail = mCurrentUser.getString(ParseConstants.KEY_EMAIL);
        mCurrentName = mCurrentUser.getString(ParseConstants.KEY_PERSON_NAME);
        mCurrentLocation = mCurrentUser.getString(ParseConstants.KEY_PERSON_LOCATION);
        mParseImageFile = mCurrentUser.getParseFile(ParseConstants.KEY_IMAGE);

        if (mParseImageFile !=null){
            mImageUri = Uri.parse(mParseImageFile.getUrl());
            Picasso.with(this).load(mImageUri.toString()).into(mUserImage);
       }
            mProgressBar.setVisibility(View.INVISIBLE);

        mUsername.setText(mCurrentUsername);
        mEmail.setText(mCurrentEmail);
        mName.setText(mCurrentName);
        mLocation.setText(mCurrentLocation);

        mImageUri = null;

        mUserPhotoButton = (Button)findViewById(R.id.userPictureButton);
        mUserPhotoButton.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            photoButton();
          }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveButton();
            }
        });

        }

        else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void photoButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserData.this);
        builder.setItems(R.array.camera_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveButton() {
        String email = mEmail.getText().toString();
        String name = mName.getText().toString();
        String location = mLocation.getText().toString();
        email = email.trim();
        name = name.trim();
        location = location.trim();

        if (mImageUri!=null) {
            byte[] tMFileBytes = TMFileHelper.getByteArrayFromFile(UserData.this, mImageUri);
            tMFileBytes = TMFileHelper.reduceImageForUpload(tMFileBytes);
            String tMFileName = FileHelper.getFileName(UserData.this, mImageUri, mFileType);
            ParseFile tMFile = new ParseFile(tMFileName, tMFileBytes);
           mCurrentUser.put(ParseConstants.KEY_THUMBNAILIMAGE, tMFile);
          }

        if (mImageUri !=null) {
            byte[] fileBytes = FileHelper.getByteArrayFromFile(UserData.this, mImageUri);
            fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            String fileName = FileHelper.getFileName(UserData.this, mImageUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            mCurrentUser.put(ParseConstants.KEY_IMAGE, file);
        }

        mCurrentUser.put(ParseConstants.KEY_EMAIL, email);
        mCurrentUser.put(ParseConstants.KEY_PERSON_NAME, name);
        mCurrentUser.put(ParseConstants.KEY_PERSON_LOCATION, location);
        mCurrentUser.saveInBackground(
                new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(UserData.this, R.string.saved_confirmation, Toast.LENGTH_LONG).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserData.this);
                            builder.setMessage(R.string.posting_error_message)
                                    .setTitle(R.string.attention_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }


    protected DialogInterface.OnClickListener mDialogListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:  //Take Picture
                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                mImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                                if (mImageUri == null){
                                    Toast.makeText(UserData.this, R.string.error_external_storage,
                                            Toast.LENGTH_LONG).show();
                                }
                                else {
                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                                    break;
                                }
                                break;
                            case 1:  //Choose Picture
                                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                choosePhotoIntent.setType("image/*");
                                startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                                break;
                        }
                    }

                    private Uri getOutputMediaFileUri(int mediaType) {
                        if(isExternalStorageAvailable()){
                            String appName = UserData.this.getString(R.string.app_name);
                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES),appName);
                            if (! mediaStorageDir.exists()){
                                if (! mediaStorageDir.mkdirs()){
                                    return null;
                                }
                            }

                            File mediaFile;
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                                    "IMG_"+ timeStamp + ".jpg");

                            return Uri.fromFile(mediaFile);
                        }
                        else {
                            return null;
                        }
                    }

                    private boolean isExternalStorageAvailable(){
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)){
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                };

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK){
                if (requestCode == PICK_PHOTO_REQUEST ){
                    if (data == null){
                        Toast.makeText (this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                    }
                    else {
                        mImageUri = data.getData();
                    }
                }
                ImageView imgView=(ImageView)findViewById(R.id.userPictureView);
                imgView.setImageURI(mImageUri);
            }
            else if (resultCode != RESULT_CANCELED){
                Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
            }
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
