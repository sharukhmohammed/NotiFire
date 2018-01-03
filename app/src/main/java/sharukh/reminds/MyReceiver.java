package sharukh.reminds;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import sharukh.reminds.Constants.Reminder;
import sharukh.reminds.Constants.TinyDB;

public class MyReceiver extends BroadcastReceiver
{
    public MyReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction())
        {
            case "DISMISS_REMINDER":
            {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(intent.getIntExtra("id", 777));
                break;
            }

            case "CREATE_NOTIFICATION":
            {
                int toRemindId = intent.getIntExtra("id", 777);

                ArrayList<Reminder> reminderArrayList = new TinyDB(context).getListReminder("reminderArrayList", Reminder.class);

                for (int i = 0; i < reminderArrayList.size(); i++)
                {
                    if (reminderArrayList.get(i).id == toRemindId)
                    {
                        Reminder newReminder = reminderArrayList.get(i);
                        Intent doneIntent = new Intent("DISMISS_REMINDER").putExtra("id", newReminder.id);
                        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context, newReminder.id, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder reminderNotification = new NotificationCompat.Builder(context);
                        reminderNotification.setSmallIcon(R.drawable.ic_stat_fire);
                        reminderNotification.setContentTitle(newReminder.title);
                        reminderNotification.setContentText(newReminder.text);
                        reminderNotification.setPriority(Notification.PRIORITY_HIGH);
                        reminderNotification.setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(newReminder.title).bigText(newReminder.text));
                        reminderNotification.setOngoing(true);
                        reminderNotification.setDefaults(Notification.DEFAULT_ALL);
                        reminderNotification.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        reminderNotification.addAction(R.drawable.ic_stat_action_done, "Mark as Done", donePendingIntent);
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(newReminder.id, reminderNotification.build());
                    }
                }
            }
            default:
                Log.d("BroadCastReceiver", "onReceive: " + intent.getAction());
                break;
        }
    }
}
