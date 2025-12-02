package net.hssco.club;

import android.app.Activity;
import android.content.Intent;

public final class NavigationHelper {

    private NavigationHelper() {
        // Utility class
    }

    public static void goToWelcome(Activity activity) {
        Intent intent = new Intent(activity, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
