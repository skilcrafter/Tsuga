package life.tsuga.tsuga;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;


public class PostHelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_help);

        ImageView postimageview = (ImageView)findViewById(R.id.postImage);
        Drawable postRes = getResources().getDrawable(R.drawable.instruction_posting_image);
        postimageview.setImageDrawable(postRes);

        ImageView mapimageview = (ImageView)findViewById(R.id.mapImage);
        Drawable mapRes = getResources().getDrawable(R.drawable.instruction_map);
        mapimageview.setImageDrawable(mapRes);

   }
}
