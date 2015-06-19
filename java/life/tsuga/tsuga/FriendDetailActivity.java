package life.tsuga.tsuga;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class FriendDetailActivity extends Activity {

    String mFriendID;
    ImageView friendImageView;
    TextView friendName;
    TextView friendLocation;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected static ArrayList<String> mRecipientsList;
    protected ParseUser mDetailUser;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    protected void onResume(){
        super.onResume();

        if (isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);
            detail();
        }
        else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }

    }

    private void detail() {
        final Intent intent = getIntent();
        mFriendID = intent.getExtras().getString("eventId");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(mFriendID, new GetCallback<ParseUser>() {


            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {

                    mDetailUser = parseUser;

                    friendImageView = (ImageView) findViewById(R.id.friendImage);
                    friendName = (TextView) findViewById(R.id.friendNameText);
                    friendLocation = (TextView) findViewById(R.id.friendLocationText);

                    friendName.setText(parseUser.getString(ParseConstants.KEY_PERSON_NAME));
                    friendLocation.setText(parseUser.getString(ParseConstants.KEY_PERSON_LOCATION));

                    Uri friendFileUri = Uri.parse((parseUser.getParseFile(ParseConstants.KEY_IMAGE)).getUrl());

                    Picasso.with(FriendDetailActivity.this).load(friendFileUri.toString()).into(friendImageView);

                } else {
                }
            }


        });

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> friendsQuery = mFriendsRelation.getQuery();
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (e == null) {

                    mFriends = friends;
                    ArrayList<String> mRecipientIds = new ArrayList<String>();
                    for (int i = 0; i < mFriends.size(); i++) {
                        mRecipientIds.add(mFriends.get(i).getObjectId());
                    }
                    mRecipientsList = mRecipientIds;
                    if (mRecipientsList.contains(mFriendID)) {
                        Button saveButton = (Button) findViewById(R.id.saveButton);
                        saveButton.setVisibility(View.INVISIBLE);
                        Button deleteButton = (Button) findViewById(R.id.deletButton);
                        deleteButton.setVisibility(View.VISIBLE);

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFriendsRelation.remove(mDetailUser);
                                mCurrentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                        } else {
                                            Button deleteButton = (Button) findViewById(R.id.deletButton);
                                            deleteButton.setVisibility(View.INVISIBLE);
                                            Toast.makeText(FriendDetailActivity.this, R.string.saved_confirmation, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                            }
                        });
                    } else {
                        Button saveButton = (Button) findViewById(R.id.saveButton);
                        saveButton.setVisibility(View.VISIBLE);
                        Button deleteButton = (Button) findViewById(R.id.deletButton);
                        deleteButton.setVisibility(View.INVISIBLE);

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFriendsRelation.add(mDetailUser);
                                mCurrentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                        } else {
                                            Button saveButton = (Button) findViewById(R.id.saveButton);
                                            saveButton.setVisibility(View.INVISIBLE);
                                            Toast.makeText(FriendDetailActivity.this, R.string.saved_confirmation, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    ;
                } else {
                    //error message
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
