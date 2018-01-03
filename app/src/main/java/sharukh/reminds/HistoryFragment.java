package sharukh.reminds;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import sharukh.reminds.Adapters.ListViewAdapter;
import sharukh.reminds.Constants.Reminder;
import sharukh.reminds.Constants.TinyDB;

public class HistoryFragment extends Fragment implements ListView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    static ListViewAdapter listViewAdapter;
    Context context;
    TinyDB tinyDB;
    ArrayList<Reminder> reminderArrayList = new ArrayList<>();
    ListView historyList;

    public HistoryFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = getActivity().getApplicationContext();
        tinyDB = new TinyDB(context);
        reminderArrayList = tinyDB.getListReminder("reminderArrayList", Reminder.class);
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        listViewAdapter = new ListViewAdapter(getActivity(), reminderArrayList);
        historyList = (ListView) view.findViewById(R.id.list_view);
        historyList.setEmptyView(view.findViewById(R.id.list_view_empty));
        historyList.setOnItemClickListener(this);
        historyList.setOnItemLongClickListener(this);
        historyList.setAdapter(listViewAdapter);
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_clear_history).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id)
    {
        Reminder newReminder = reminderArrayList.get(position);
        Intent doneIntent = new Intent("DISMISS_REMINDER").putExtra("id", newReminder.id);
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context, newReminder.id, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder reminderNotification = new NotificationCompat.Builder(context);
        reminderNotification.setSmallIcon(R.drawable.ic_stat_fire);
        reminderNotification.setContentTitle(newReminder.title);
        reminderNotification.setContentText(newReminder.text);
        reminderNotification.setPriority(Notification.PRIORITY_HIGH);
        reminderNotification.setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(newReminder.title).bigText(newReminder.text));
        reminderNotification.setOngoing(true);
        reminderNotification.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        reminderNotification.addAction(R.drawable.ic_stat_action_done, "Mark as Done", donePendingIntent);
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(newReminder.id, reminderNotification.build());

        Snackbar.make(view, "Reminder created. Check the Notification Panel", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id)
    {
        Snackbar.make(view, "Are you sure you want to delete this Reminder?", Snackbar.LENGTH_LONG).setAction("DELETE", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                reminderArrayList.remove(position);
                listViewAdapter.notifyDataSetChanged();
                tinyDB.putListReminder("reminderArrayList", reminderArrayList);
            }
        }).show();
        return true;
    }
}
