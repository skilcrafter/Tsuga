package life.tsuga.tsuga;

import android.app.Application;

import com.parse.Parse;

public class TsugaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "GuAdbrNPxqw1Iv4sXC7mVc0wnQt3nADUxPfEWPBC", "pGNOF0vX0RC2aRyZVYA2IwbmC8UnSTZ2JW6ivcIv");
    }
}
