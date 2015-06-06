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
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    public static Uri mImageUri;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }
        else {
        }

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.action_post_event:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.action_favorite_event:
                Intent favoriteIntent = new Intent(this,FavoritesActivity.class);
                startActivity(favoriteIntent);
                return true;

            case R.id.action_search_friends:
                Intent searchFriendsIntent = new Intent(this, SearchFriendsActivity.class);
                startActivity(searchFriendsIntent);
                return true;

            case R.id.action_edit_friends:
                Intent editFriendsIntent = new Intent(this, EditFriendsActivity.class);
                startActivity(editFriendsIntent);
                return true;

            case R.id.action_all_users:
                Intent allUsersIntent = new Intent(this, AllUsersActivity.class);
                startActivity(allUsersIntent);
                return true;

            case R.id.action_user_info:
                Intent userInfoIntent = new Intent(this,UserData.class);
                startActivity(userInfoIntent);
                return true;

            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                                Toast.makeText(MainActivity.this,R.string.error_external_storage,
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

                        String appName = MainActivity.this.getString(R.string.app_name);
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
            Intent postIntent = new Intent(this, PostActivity.class);
            postIntent.setData(mImageUri);

            String fileType = ParseConstants.TYPE_IMAGE;

            postIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(postIntent);
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
