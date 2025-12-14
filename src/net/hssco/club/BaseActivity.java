package net.hssco.club;

import android.app.Activity;

public class BaseActivity extends Activity {

    @Override
    public void onBackPressed() {
        NavigationHelper.goToWelcome(this);
    }
}
