package sharukh.reminds.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sharukh.reminds.CreateFragment;
import sharukh.reminds.HistoryFragment;

public class TabbedPageAdapter extends FragmentStatePagerAdapter
{
    int numberOfTabs;

    public TabbedPageAdapter(FragmentManager fragmentManager, int numberOfTabs)
    {
        super(fragmentManager);
        this.numberOfTabs = numberOfTabs;

    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new CreateFragment();
            case 1:
                return new HistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return numberOfTabs;
    }
}
