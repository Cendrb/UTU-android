package cz.cendrb.utu;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import cz.cendrb.utu.utucomponents.Events;
import cz.cendrb.utu.utucomponents.Exams;
import cz.cendrb.utu.utucomponents.Tasks;


public class utu extends Activity implements ActionBar.TabListener {

    public static final String NAME = "UTU";
    public static DataLoader dataLoader;
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

        SimpleDateFormat parser = new SimpleDateFormat("dd. MM. yyyy");

        Bundle bundle = getIntent().getExtras();

        try {
            String email = bundle.getString(DataLoader.EMAIL);
            String password = bundle.getString(DataLoader.PASSWORD);
            dataLoader = new DataLoader(email, password);
        } catch (Exception e) {
            e.printStackTrace();
            dataLoader = new DataLoader();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_utu);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        try {
            new Refresher(this).execute().get();
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
                            .setTabListener(this)
            );
        }
    }

    private void refresh(final int pageIndex) {
        Refresher refresher = new Refresher(this, new Runnable() {
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh) {
            item.setEnabled(false);
            int current = mViewPager.getCurrentItem();
            refresh(current);

            item.setEnabled(true);

            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_utu, container, false);
            ListView list = (ListView) rootView.findViewById(R.id.utuListView);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    list.setAdapter(new SimpleAdapter(container.getContext(), utu.dataLoader.tasks.getListForAdapter(), R.layout.task_item, Tasks.from, Tasks.to));
                    break;
                case 2:
                    list.setAdapter(new SimpleAdapter(container.getContext(), utu.dataLoader.events.getListForAdapter(), R.layout.event_item, Events.from, Events.to));
                    break;
                case 3:
                    list.setAdapter(new SimpleAdapter(container.getContext(), utu.dataLoader.exams.getListForAdapter(), R.layout.exam_item, Exams.from, Exams.to));
                    break;
            }
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
            return PlaceholderFragment.newInstance(position + 1);
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

    public class Refresher extends AsyncTask<Void, Void, LoadResult> {
        Activity context;
        ProgressDialog dialog;
        Runnable postAction;
        File backupFile;

        public Refresher(Activity context) {
            this.context = context;
        }

        public Refresher(Activity context, Runnable postAction) {
            this.context = context;
            this.postAction = postAction;
        }

        @Override
        protected LoadResult doInBackground(Void... voids) {
            backupFile = context.getFileStreamPath(DataLoader.BACKUP_FILE_NAME);
            if (isOnline(context))
                if (utu.dataLoader.loadFromNetAndBackup(backupFile))
                    return LoadResult.WebSuccess;
            if (backupFile.exists() && utu.dataLoader.loadFromBackup(backupFile))
                return LoadResult.BackupSuccess;
            return LoadResult.Failure;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getResources().getString(R.string.loading_data));
            dialog.setTitle(context.getResources().getString(R.string.wait));
            dialog.setIndeterminate(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LoadResult loadResult) {
            dialog.hide();
            if (postAction != null)
                postAction.run();
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy HH:mm");
            switch (loadResult) {
                case WebSuccess:
                    context.setTitle(context.getString(R.string.app_name) + " (" + sdf.format(new Date()) + ")");
                    break;
                case BackupSuccess:
                    context.setTitle(context.getString(R.string.app_name) + " (" + sdf.format(backupFile.lastModified()) + ")");
                    Toast.makeText(context, R.string.successfully_loaded_from_backup, Toast.LENGTH_LONG).show();
                    break;
                case Failure:
                    Toast.makeText(context, R.string.failed_to_load_data, Toast.LENGTH_LONG).show();
                    break;
            }
            super.onPostExecute(loadResult);
        }
    }

}

