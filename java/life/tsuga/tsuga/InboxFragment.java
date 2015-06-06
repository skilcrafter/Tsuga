package life.tsuga.tsuga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 3/2/2015.
 */
public class InboxFragment extends ListFragment {

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseObject> mEvents;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected List<ParseUser> mFriends;
    protected static ArrayList<String> mRecipientsList;
    protected ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorScheme(R.color.tsuga_orange);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveEvents();
    }

    private void retrieveEvents() {
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> friendsQuery = mFriendsRelation.getQuery();
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mFriends = friends;
                    ArrayList<String> mRecipientIds = new ArrayList<String>();
                    for (int i = 0; i < mFriends.size(); i++) {
                        mRecipientIds.add(mFriends.get(i).getObjectId());
                    }
                    mRecipientsList = mRecipientIds;
                    viewList();
                } else {
                    //error message
                }
            }
        });
    }

    private void viewList() {

        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_EVENTS);

        query.whereContainedIn(ParseConstants.KEY_USER_ID, mRecipientsList);
        query.setLimit(50);
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

        Intent intent = new Intent(getActivity(), EventDetailActivity.class);

        intent.putExtra("eventId",objectId);
        startActivity(intent);
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            retrieveEvents();
        }
    };
}

