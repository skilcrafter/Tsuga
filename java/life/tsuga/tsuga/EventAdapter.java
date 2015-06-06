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
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mEvents;
    protected Uri mUri;

    public EventAdapter(Context context, List<ParseObject> events){
        super(context,R.layout.event_list_layout, events);
        mContext = context;
        mEvents = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.event_list_layout, null);

            holder = new ViewHolder();
            holder.eventImageView = (ImageView) convertView.findViewById(R.id.eventImage);
            holder.eventLabel = (TextView) convertView.findViewById(R.id.eventText);
            holder.locationLabel = (TextView) convertView.findViewById(R.id.LocationText);
            holder.submitterLabel = (TextView) convertView.findViewById(R.id.submitterText);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject event = mEvents.get(position);

        holder.eventLabel.setText(event.getString(ParseConstants.KEY_EVENT));
        holder.locationLabel.setText(event.getString(ParseConstants.KEY_PLACE_SHORT));
        holder.submitterLabel.setText(event.getString(ParseConstants.KEY_USERNAME));

        mUri = Uri.parse((event.getParseFile(ParseConstants.KEY_THUMBNAILIMAGE)).getUrl());
        Picasso.with(mContext).load(mUri.toString()).into(holder.eventImageView);

        return convertView;
    }

    private static class ViewHolder {
        ImageView eventImageView;
        TextView eventLabel;
        TextView locationLabel;
        TextView submitterLabel;
    }
}
