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
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class SearchFriendsActivity extends ListActivity implements SearchView.OnQueryTextListener {

    protected List<ParseUser> mUsers;
    private SearchView mSearchView;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        mSearchView=(SearchView) findViewById(R.id.searchView1);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        setupSearchView();
    }
    protected void onResume() {
       super.onResume();
    }

    private void setupSearchView()
    {
       mSearchView.setIconifiedByDefault(false);
       mSearchView.setOnQueryTextListener(this);
       mSearchView.setSubmitButtonEnabled(true);
       mSearchView.setQueryHint("Search for users");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseObject event = mUsers.get(position);
        String objectId = event.getObjectId();
        Intent intent = new Intent(this, FriendDetailActivity.class);
        intent.putExtra("eventId", objectId);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        if (isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.whereContains(ParseConstants.KEY_PERSON_NAME, s);
        query.setLimit(25);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    FriendAdapter adapter = new FriendAdapter(getListView().getContext(), mUsers);
                    setListAdapter(adapter);
                }
            }
        });

        }
        else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
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