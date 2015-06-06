package life.tsuga.tsuga;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class FavoritesActivity extends ListActivity {

    protected List<ParseObject> mFavorites;
    protected String mCurrentUser;
    protected List<ParseObject> mEvents;
    protected static ArrayList<String> mFavoritesList;
    protected ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);
            retrieveEvents();
        }else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void retrieveEvents() {
        mCurrentUser = ParseUser.getCurrentUser().getObjectId();
        ParseQuery query = new ParseQuery<>(ParseConstants.CLASS_FAVORITES);
        query.whereEqualTo(ParseConstants.KEY_USER_ID, mCurrentUser);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {

                    mFavorites = list;
                    ArrayList<String> mFavoritesId = new ArrayList<String>();
                    for (int i = 0; i < mFavorites.size(); i++) {
                        mFavoritesId.add(mFavorites.get(i).getString(ParseConstants.KEY_EVENT));
                    }
                    mFavoritesList = mFavoritesId;
                    viewList();
                }
            }
        });
    }

    private void viewList() {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_EVENTS);
        query.whereContainedIn("objectId", mFavoritesList);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> events, ParseException e) {

                if (e == null) {
                    mEvents = events;

                    String[] usernames = new String[mEvents.size()];
                    int i = 0;
                    for (ParseObject event : mEvents) {
                        usernames[i] = event.getString(ParseConstants.KEY_USERNAME);
                        i++;
                    }
                    EventAdapter adapter = new EventAdapter(getListView().getContext(), mEvents);
                    setListAdapter(adapter);
                }
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseObject event =  mEvents.get(position);
        String objectId = event.getObjectId();
        Intent intent = new Intent(this, FavoriteEventDetail.class);
        intent.putExtra("eventId", objectId);
        startActivity(intent);
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

