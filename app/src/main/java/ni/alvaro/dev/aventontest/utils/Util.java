package ni.alvaro.dev.aventontest.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import ni.alvaro.dev.aventontest.R;

public class Util {

    private static AlertDialog.Builder createAlertBuilder(Context context, int titleRes, int messageRes){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (titleRes == -1)
            builder.setTitle(R.string.attention);
        else
            builder.setTitle(context.getString(titleRes));

        builder.setMessage(context.getString(messageRes));
        return builder;
    }
    public static AlertDialog.Builder createAlertBuilder(Context context, int messageRes){
        return createAlertBuilder(context, -1, messageRes);
    }
}
