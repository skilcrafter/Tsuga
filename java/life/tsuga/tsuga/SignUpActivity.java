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
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SignUpActivity extends ActionBarActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected EditText mName;
    protected EditText mLocation;
    protected Button mSignUpButton;
    protected Button mUserPhotoButton;
    public Uri mImageUri;
    protected String mFileType = "image";
    protected ParseUser mCurrentUser;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (isNetworkAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_connection)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mEmail = (EditText)findViewById(R.id.emailField);
        mName = (EditText)findViewById(R.id.personNameField);
        mLocation = (EditText)findViewById(R.id.locationField);

        mUserPhotoButton = (Button)findViewById(R.id.userPictureButton);
        mUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        mSignUpButton = (Button)findViewById(R.id.signupButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                String name = mName.getText().toString();
                String location = mLocation.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();
                name = name.trim();
                location = location.trim();

                if(mImageUri==null || username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || location.isEmpty()){
                    AlertDialog.Builder builder= new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.sign_up_error_message)
                            .setTitle(R.string.sign_up_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put("name", name);
                    newUser.put("location",location);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                          if (e == null){
                                if (mImageUri!= null) {
                                    byte[]fileBytes = FileHelper.getByteArrayFromFile(SignUpActivity.this, mImageUri);
                                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                                    String fileName = FileHelper.getFileName(SignUpActivity.this, mImageUri,mFileType);
                                    ParseFile file = new ParseFile(fileName, fileBytes);

                                    byte[]tMFileBytes = TMFileHelper.getByteArrayFromFile(SignUpActivity.this, mImageUri);
                                    tMFileBytes = TMFileHelper.reduceImageForUpload(tMFileBytes);
                                    String tMFileName = FileHelper.getFileName(SignUpActivity.this, mImageUri,mFileType);
                                    final ParseFile tMFile = new ParseFile(tMFileName, tMFileBytes);
                                    mCurrentUser = ParseUser.getCurrentUser();
                                    mCurrentUser.put(ParseConstants.KEY_IMAGE,file);
                                    mCurrentUser.put(ParseConstants.KEY_THUMBNAILIMAGE, tMFile);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(SignUpActivity.this, R.string.saved_confirmation, Toast.LENGTH_LONG).show();
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                                builder.setMessage(R.string.posting_error_message)
                                                        .setTitle(R.string.attention_error_title)
                                                        .setPositiveButton(android.R.string.ok, null);
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        }
                                    });
                                }

                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder= new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.sign_up_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
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
                                Toast.makeText(SignUpActivity.this, R.string.error_external_storage,
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

                        String appName = SignUpActivity.this.getString(R.string.app_name);
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

        boolean isAvailable = true;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = false;
        }

        return isAvailable;
    }
}
