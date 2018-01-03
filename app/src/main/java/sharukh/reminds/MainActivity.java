package sharukh.reminds;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import sharukh.reminds.Adapters.TabbedPageAdapter;
import sharukh.reminds.Constants.Reminder;
import sharukh.reminds.Constants.TinyDB;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener
{
      TinyDB tinyDB;
      TabLayout tabLayout;
      ViewPager viewPager;
      TabbedPageAdapter tabbedPageAdapter;
      Toolbar toolbar;
      FloatingActionButton floatingActionButton;

      ArrayList<Reminder> reminderArrayList = new ArrayList<>();
      int backExit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(" NOTIFIRE");
        getSupportActionBar().setIcon(R.drawable.ic_stat_fire);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("CREATE"));
        tabLayout.addTab(tabLayout.newTab().setText("HISTORY"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabbedPageAdapter = new TabbedPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabbedPageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (backExit == 0)
        {
            Snackbar.make(findViewById(R.id.main_layout), "Press back again to exit", Snackbar.LENGTH_SHORT).show();
            CreateFragment.titleEditText.setText("");
            CreateFragment.textEditText.setText("");
            backExit++;
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
            {
                Snackbar.make(findViewById(R.id.main_layout), "Settings", Snackbar.LENGTH_SHORT).show();
                return true;
            }
            case R.id.action_clear_history:
            {
                tinyDB = new TinyDB(getApplicationContext());
                reminderArrayList.clear();
                tinyDB.putListReminder("reminderArrayList", reminderArrayList);
                HistoryFragment.listViewAdapter.updateListContent(reminderArrayList);
                Snackbar.make(findViewById(R.id.main_layout), "History Cleared", Snackbar.LENGTH_SHORT).show();
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        viewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition())
        {
            case 0:
            {
                floatingActionButton.show();
                break;
            }
            case 1:
            {
                floatingActionButton.hide();
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {
        if (tab.getPosition() == 0)
        {
            View view = this.getCurrentFocus();
            if (view != null)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {

    }

}
