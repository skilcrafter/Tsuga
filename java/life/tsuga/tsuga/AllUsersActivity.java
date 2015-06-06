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

import java.util.List;

   public class AllUsersActivity extends ListActivity {

    protected List<ParseUser> mUserList;
    protected ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    protected void onResume(){
        super.onResume();
        if (isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);
            userQuery();
        }
        else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

       private void userQuery() {
           ParseQuery<ParseUser> query = ParseUser.getQuery();
           query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
           query.setLimit(1000);
           query.findInBackground(new FindCallback<ParseUser>() {
               @Override
               public void done(List<ParseUser> parseuserlist, ParseException e) {

                   mProgressBar.setVisibility(View.INVISIBLE);
                   if (e == null) {
                       mUserList = parseuserlist;

                       String[] usernames = new String[mUserList.size()];
                       int i = 0;
                       for (ParseObject event : mUserList) {
                           usernames[i] = event.getObjectId();
                           i++;
                       }

                       FriendAdapter adapter = new FriendAdapter(getListView().getContext(), mUserList);
                       setListAdapter(adapter);
                   }
               }
           });
       }

       @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject event =  mUserList.get(position);
        String objectId = event.getObjectId();
        Intent intent = new Intent(this, FriendDetailActivity.class);
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
