package cz.cendrb.utu;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.cendrb.utu.administrationactivities.AddEditExam;
import cz.cendrb.utu.administrationactivities.AddEditTask;
import cz.cendrb.utu.utucomponents.Events;
import cz.cendrb.utu.utucomponents.Exam;
import cz.cendrb.utu.utucomponents.Exams;
import cz.cendrb.utu.utucomponents.Task;
import cz.cendrb.utu.utucomponents.Tasks;


public class utu extends Activity implements ActionBar.TabListener {

    static final String NAME = "UTU";
    boolean administrator;

    public static String getPrefix() {
        return NAME;
    }

    public static UtuClient utuClient = new UtuClient();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    //Handler handler = new Handler();
    Menu menu;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_utu);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        final utu utuActivity = this;

        new Refresher(this, getResources().getString(R.string.wait), getResources().getString(R.string.loading_data), new Runnable() {
            @Override
            public void run() {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);

                // Set up the action bar.
                final ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                } else {
                    Log.e(utu.NAME, "Failed to get actionbar");
                }

                // When swiping between different sections, select the corresponding
                // tab. We can also use ActionBar.Tab#select() to do this if we have
                // a reference to the Tab.
                mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

                // For each of the sections in the app, add a tab to the action bar.
                for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                    // Create a tab with text corresponding to the page title defined by
                    // the adapter. Also specify this Activity object, which implements
                    // the TabListener interface, as the callback (listener) for when
                    // this tab is selected.
                    actionBar.addTab(
                            actionBar.newTab()
                                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                                    .setTabListener(utuActivity)
                    );
                }

                new IsAdministrator().execute();
            }
        }).execute();
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void refresh(final int pageIndex) {
        Refresher refresher = new Refresher(this, getResources().getString(R.string.wait), getResources().getString(R.string.loading_data), new Runnable() {
            @Override
            public void run() {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setCurrentItem(pageIndex, false);
            }
        });
        refresher.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.utu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            item.setEnabled(false);
            int current = mViewPager.getCurrentItem();
            refresh(current);

            item.setEnabled(true);

            return true;
        }
        if (id == R.id.action_logout) {
            logout();
            finish();
            return true;
        }
        if (id == 1) {
            // New exam
            Intent intent = new Intent(this, AddEditExam.class);
            startActivity(intent);
        }
        if (id == 2) {
            // New task
            Intent intent = new Intent(this, AddEditTask.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        if (utuClient.isLoggedIn())
            new LogOffWithProgressDialog(this, getResources().getString(R.string.wait), getResources().getString(R.string.logging_off), new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }).execute();
        else
            finish();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int sectionNumber;

        public PlaceholderFragment(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_utu, container, false);
            ListView list = (ListView) rootView.findViewById(R.id.utuListView);

            switch (sectionNumber) {
                case 1:
                    list.setAdapter(new SimpleAdapter(container.getContext(), utu.utuClient.tasks.getListForAdapter(), R.layout.task_item, Tasks.from, Tasks.to));
                    break;
                case 2:
                    list.setAdapter(new SimpleAdapter(container.getContext(), utu.utuClient.events.getListForAdapter(), R.layout.event_item, Events.from, Events.to));
                    break;
                case 3:
                    list.setAdapter(new SimpleAdapter(container.getContext(), utu.utuClient.exams.getListForAdapter(), R.layout.exam_item, Exams.from, Exams.to));
                    break;
            }

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (administrator) {
                        switch (sectionNumber) {
                            case 1:
                                HashMap<String, String> taskData = ((HashMap<String, String>) adapterView.getItemAtPosition(i));
                                int taskId = Integer.parseInt(taskData.get(Task.ID));
                                Task task = utu.utuClient.tasks.findTaskWithId(taskId);
                                if (task != null) {
                                    task.startEditActivity(getActivity());
                                } else {
                                    Log.d(getPrefix(), "Unable to find task with id: " + String.valueOf(taskId));
                                }
                                break;
                            case 2:
                                break;
                            case 3:
                                HashMap<String, String> examData = ((HashMap<String, String>) adapterView.getItemAtPosition(i));
                                int examId = Integer.parseInt(examData.get(Task.ID));
                                Exam exam = utu.utuClient.exams.findExamWithId(examId);
                                if (exam != null) {
                                    exam.startEditActivity(getActivity());
                                } else {
                                    Log.d(getPrefix(), "Unable to find exam with id: " + String.valueOf(examId));
                                }
                                break;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return new PlaceholderFragment(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public class IsAdministrator extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return utuClient.isAdministrator();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                menu.add(Menu.NONE, 1, 100, R.string.new_exam);
                menu.add(Menu.NONE, 2, 101, R.string.new_task);
                administrator = true;
            } else
                administrator = false;

            super.onPostExecute(aBoolean);
        }
    }

    public class LogOffWithProgressDialog extends TaskWithProgressDialog<Void> {
        public LogOffWithProgressDialog(Activity activity, String titleMessage, String message, Runnable postAction) {
            super(activity, titleMessage, message, postAction);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            utu.utuClient.logout();
            return null;
        }
    }

    public class Refresher extends TaskWithProgressDialog<LoadResult> {

        public Refresher(Activity activity, String titleMessage, String message, Runnable postAction) {
            super(activity, titleMessage, message, postAction);
        }

        @Override
        protected LoadResult doInBackground(Void... voids) {
            if (isOnline(activity)) {
                if (utu.utuClient.loadFromNetAndBackup(activity)) {
                    return LoadResult.WebSuccess;
                }
            } else {
                if (utu.utuClient.backupExists(activity)) {
                    if(utu.utuClient.loadFromBackup(activity))
                        return LoadResult.BackupSuccess;
                    else
                        return LoadResult.BackupFailure;
                }
            }
            return LoadResult.Failure;
        }

        @Override
        protected void onPostExecute(LoadResult loadResult) {
            DateFormat format = new SimpleDateFormat(" dd. MM. yyyy (HH:mm)");
            DateFormat labelFormat = new SimpleDateFormat(" - dd. MM. (HH:mm)");
            switch (loadResult) {
                case WebSuccess:
                    activity.setTitle(activity.getString(R.string.app_name) + " (AKTUÁLNÍ)");
                    break;
                case BackupFailure:
                    Toast.makeText(activity, getString(R.string.failed_to_load_data_from_backup), Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case BackupSuccess:
                    Date date = new Date(utuClient.getLastModifiedFromBackupData(activity));
                    Toast.makeText(activity, getString(R.string.successfully_loaded_from_backup) + format.format(date), Toast.LENGTH_LONG).show();
                    activity.setTitle(activity.getString(R.string.app_name) + labelFormat.format(date));
                    break;
                case Failure:
                    Toast.makeText(activity, R.string.failed_to_load_data, Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
            super.onPostExecute(loadResult);
        }
    }

}

