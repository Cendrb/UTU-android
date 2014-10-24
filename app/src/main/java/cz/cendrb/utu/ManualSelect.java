package cz.cendrb.utu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ManualSelect extends Activity {

    int group;
    SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy");
    Button mManualBeginningButton;
    Button mManualEndButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_select);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mManualBeginningButton = (Button) findViewById(R.id.manualBeginningButton);
        mManualBeginningButton.setText(format.format(new Date()));

        mManualEndButton = (Button) findViewById(R.id.manualEndButton);
        mManualEndButton.setText(format.format(new Date()));

        group = 1;

        Spinner manualGroupSpinner = (Spinner) findViewById(R.id.manualGroupSpinner);
        ArrayAdapter<CharSequence> items = ArrayAdapter.createFromResource(this, R.array.groups_array, android.R.layout.simple_spinner_item);
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        manualGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    group = 1;
                else if (i == 1)
                    group = 2;
                else
                    group = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        manualGroupSpinner.setAdapter(items);
    }

    public void onViewButtonClick(View v) {
        Toast.makeText(this, group + " : " + mManualBeginningButton.getText() + " - " + mManualEndButton.getText(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, utu.class);
        intent.putExtra(DataLoader.FROM, mManualBeginningButton.getText());
        intent.putExtra(DataLoader.TO, mManualEndButton.getText());
        intent.putExtra(DataLoader.GROUP, group);
        startActivity(intent);
    }

    public void onDateButtonClick(final View v) {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getFragmentManager(), "Choose penis");
        dialog.setOnDateChangedListener(new DatePickerFragment.OnDateChangedListener() {
            @Override
            public void dateChanged(Date date) {
                ((Button) v).setText(format.format(date));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manual_select, menu);
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
        return super.onOptionsItemSelected(item);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        OnDateChangedListener dateChanged;

        public interface OnDateChangedListener {
            void dateChanged(Date date);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setOnDateChangedListener(OnDateChangedListener listener) {
            dateChanged = listener;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            if (dateChanged != null)
                dateChanged.dateChanged(calendar.getTime());
        }
    }
}
