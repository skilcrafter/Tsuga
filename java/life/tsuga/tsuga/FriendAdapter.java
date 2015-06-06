package life.tsuga.tsuga;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mEvents;
    protected Uri mUri;

    public FriendAdapter(Context context, List<ParseUser> events){
        super(context,R.layout.friend_list_layout, events);
        mContext = context;
        mEvents = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_list_layout, null);

            holder = new ViewHolder();
            holder.friendImageView = (ImageView) convertView.findViewById(R.id.friendImage);
            holder.friendName = (TextView) convertView.findViewById(R.id.friendNameText);
            holder.friendLocation = (TextView) convertView.findViewById(R.id.friendLocationText);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseObject event = mEvents.get(position);
        holder.friendName.setText(event.getString(ParseConstants.KEY_PERSON_NAME));
        holder.friendLocation.setText(event.getString(ParseConstants.KEY_PERSON_LOCATION));
        mUri = Uri.parse((event.getParseFile(ParseConstants.KEY_THUMBNAILIMAGE)).getUrl());
        Picasso.with(mContext).load(mUri.toString()).into(holder.friendImageView);
        return convertView;
    }

    private static class ViewHolder {
        ImageView friendImageView;
        TextView friendName;
        TextView friendLocation;
    }
}
