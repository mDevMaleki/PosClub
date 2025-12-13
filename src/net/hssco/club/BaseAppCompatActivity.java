package net.hssco.club;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        NavigationHelper.goToWelcome(this);
    }
}
