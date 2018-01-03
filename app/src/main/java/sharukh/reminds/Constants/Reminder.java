package sharukh.reminds.Constants;

import java.util.Calendar;
import java.util.Random;

public class Reminder
{
    public int id;
    public String title, text;
    public Calendar timeToRemind;


    public Reminder(String title, String text)
    {
        this.title = title.trim();
        this.text = text.trim();
        this.id = new Random().nextInt();
        this.timeToRemind = Calendar.getInstance();
    }

    public Reminder(String title, String text, Calendar timeToRemind)
    {
        this.title = title.trim();
        this.text = text.trim();
        this.id = new Random().nextInt();
        this.timeToRemind = timeToRemind;
    }

    public boolean hasSameId(Object object)
    {
        return object != null && object instanceof Reminder && this.id == ((Reminder) object).id;
    }

    @Override
    public boolean equals(Object object)
    {
        return object != null && object instanceof Reminder && this.title.equals(((Reminder) object).title);
    }

    @Override
    public int hashCode()
    {
        return 1;
    }


}
