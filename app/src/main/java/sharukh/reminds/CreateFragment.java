package sharukh.reminds;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import sharukh.reminds.Constants.Constants;
import sharukh.reminds.Constants.Reminder;
import sharukh.reminds.Constants.TinyDB;

public class CreateFragment extends Fragment implements View.OnClickListener
{
     Context context;
      TextInputLayout hintContainerEditTextTitle, hintContainerEditTextText;
      static TextInputEditText titleEditText, textEditText;
      TinyDB tinyDB;
      ArrayList<Reminder> reminderArrayList = new ArrayList<>();
      int year, month, day, hour, min;
      Calendar calendar;

      Reminder newReminder;

      AppCompatCheckBox timedCheckBox;

      NotificationManager notificationManager;

    public CreateFragment()
    {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = getContext();
        tinyDB = new TinyDB(context);
        reminderArrayList = tinyDB.getListReminder("reminderArrayList", Reminder.class);
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        hintContainerEditTextTitle = (TextInputLayout) view.findViewById(R.id.hintContainerEditTextTitle);
        hintContainerEditTextText = (TextInputLayout) view.findViewById(R.id.hintContainerEditTextText);
        titleEditText = (TextInputEditText) view.findViewById(R.id.titleEditText);
        textEditText = (TextInputEditText) view.findViewById(R.id.textEditText);
        timedCheckBox = (AppCompatCheckBox) view.findViewById(R.id.timedCheckBox);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        timedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    min = calendar.get(Calendar.MINUTE);

                    final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
                    {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute)
                        {
                            hour = hourOfDay;
                            min = minute;
                            calendar.set(year, month, day, hour, min);
                        }
                    }, hour, min, false);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
                    {
                        @Override
                        public void onDateSet(DatePicker view, int yearOfCalendar, int monthOfYear, int dayOfMonth)
                        {
                            year = yearOfCalendar;
                            month = monthOfYear;
                            day = dayOfMonth;
                            timePickerDialog.show();
                        }
                    }, year, month, day);

                    datePickerDialog.show();


                } else
                {
                    calendar = Calendar.getInstance();
                }
            }
        });

        getActivity().findViewById(R.id.floatingActionButton).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.floatingActionButton:
            {
                if (titleEditText.getText().toString().equals("") || textEditText.getText().toString().equals(""))
                {
                    //Either one of them is empty
                    if (titleEditText.getText().toString().equals(""))
                    {
                        hintContainerEditTextTitle.setErrorEnabled(true);
                        hintContainerEditTextTitle.setError("Title cannot be empty");
                    } else
                    {
                        hintContainerEditTextText.setErrorEnabled(true);
                        hintContainerEditTextText.setError("a description will help you remember better");
                    }

                } else
                {
                    String title = titleEditText.getText().toString();
                    String text = textEditText.getText().toString();

                    if (timedCheckBox.isChecked())
                    {
                        newReminder = new Reminder(title, text, calendar);
                        Intent intent = new Intent(getContext(), MyReceiver.class);
                        intent.setAction("CREATE_NOTIFICATION");
                        intent.putExtra("id", newReminder.id);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), newReminder.id, intent, 0);
                        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, newReminder.timeToRemind.getTimeInMillis(), pendingIntent);
                        Snackbar.make(view, "Reminder created. You will be Notified at " + Constants.getTimeAgo(newReminder.timeToRemind.getTimeInMillis(), getContext()), Snackbar.LENGTH_INDEFINITE).show();
                    } else
                    {
                        newReminder = new Reminder(title, text);
                        createNotification(title, text);
                        Snackbar.make(view, "Reminder created. Check the Notification Panel", Snackbar.LENGTH_LONG).show();
                    }

                    if (!reminderArrayList.contains(newReminder))
                        reminderArrayList.add(newReminder);

                    HistoryFragment.listViewAdapter.updateListContent(reminderArrayList);
                    tinyDB.putListReminder("reminderArrayList", reminderArrayList);

                }
                break;
            }
        }
    }

    public void createNotification(String title, String text)
    {
        Intent doneIntent = new Intent("DISMISS_REMINDER").putExtra("id", newReminder.id);
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context, newReminder.id, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder reminderNotification = new NotificationCompat.Builder(context);
        reminderNotification.setSmallIcon(R.drawable.ic_stat_fire);
        reminderNotification.setContentTitle(title);
        reminderNotification.setContentText(text);
        reminderNotification.setPriority(Notification.PRIORITY_HIGH);
        reminderNotification.setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(text));
        reminderNotification.setOngoing(true);
        reminderNotification.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        reminderNotification.addAction(R.drawable.ic_stat_action_done, "Mark as Done", donePendingIntent);
        notificationManager.notify(newReminder.id, reminderNotification.build());
    }


}
