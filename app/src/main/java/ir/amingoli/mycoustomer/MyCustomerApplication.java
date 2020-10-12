package ir.amingoli.mycoustomer;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Locale;

public class MyCustomerApplication extends Application {

    //  salamquran
    public static final String TAG = MyCustomerApplication.class.getSimpleName();
//------------------


    @Override
    public void onCreate() {
        super.onCreate();
    }


    public void refreshLocale(@NonNull Context context) {
        final String language = "fa";

        final Locale locale;
        if (language != null) {
            locale = new Locale(language);
//      locale = new Locale("ar");
        } else {
            // nothing to do...
            return;
        }

        updateLocale(context, locale);
        final Context appContext = context.getApplicationContext();
        if (context != appContext) {
            updateLocale(appContext, locale);
        }
    }

    private void updateLocale(@NonNull Context context, @NonNull Locale locale) {
        final Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
