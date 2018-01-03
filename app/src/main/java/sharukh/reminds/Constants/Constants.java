package sharukh.reminds.Constants;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sharukh.reminds.R;

public class Constants
{

    public static String getTimeAgo(long time, Context context)
    {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;

        if (time < 1000000000000L)
        {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0)
        {
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("h:mm aa dd-MMM-yyyy", Locale.getDefault());
            return formatter.format(date);
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS)
        {
            return context.getResources().getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS)
        {
            return context.getResources().getString(R.string.a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS)
        {
            return diff / MINUTE_MILLIS + context.getResources().getString(R.string.minutes_ago);
        } else if (diff < 90 * MINUTE_MILLIS)
        {
            return context.getResources().getString(R.string.an_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS)
        {
            return diff / HOUR_MILLIS + context.getResources().getString(R.string.hours_ago);
        } else if (diff < 48 * HOUR_MILLIS)
        {
            return context.getResources().getString(R.string.yesterday);
        } else
        {
            return diff / DAY_MILLIS + context.getResources().getString(R.string.days_ago);
        }
    }
}
