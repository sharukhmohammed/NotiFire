package sharukh.reminds.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import sharukh.reminds.Constants.Constants;
import sharukh.reminds.Constants.Reminder;
import sharukh.reminds.HistoryFragment;
import sharukh.reminds.R;

public class ListViewAdapter extends BaseAdapter
{
    ArrayList<Reminder> reminderArrayList = new ArrayList<Reminder>();
    Activity context;

    public ListViewAdapter(Activity context, ArrayList<Reminder> reminderArrayList)
    {
        super();
        this.context = context;
        this.reminderArrayList = reminderArrayList;
    }

    @Override
    public int getCount()
    {
        return reminderArrayList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }


    private class ViewHolder
    {
        TextView txtViewTitle;
        TextView txtViewDescription;
        TextView txtViewTime;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        Reminder reminder = reminderArrayList.get(position);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.history_row, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.listItem_title);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.listItem_text);
            holder.txtViewTime = (TextView) convertView.findViewById(R.id.listItem_time);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(reminder.title);
        holder.txtViewDescription.setText(reminder.text);
        holder.txtViewTime.setText(Constants.getTimeAgo(reminderArrayList.get(position).timeToRemind.getTimeInMillis(), context));
        return convertView;
    }


    public void updateListContent(ArrayList<Reminder> reminderArrayList)
    {
        this.reminderArrayList.clear();
        this.reminderArrayList.addAll(reminderArrayList);
        this.notifyDataSetChanged();
    }

}
