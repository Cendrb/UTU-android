package cz.cendrb.utu.administrationactivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import cz.cendrb.utu.ManualSelect;
import cz.cendrb.utu.R;
import cz.cendrb.utu.TaskWithProgressDialog;
import cz.cendrb.utu.utu;
import cz.cendrb.utu.utucomponents.Exam;
import cz.cendrb.utu.utucomponents.Task;

public class AddEditExam extends Activity {

    SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy");
    Spinner subjectSelect;
    Spinner groupSelect;
    Button saveButton;
    Button dateSelectButton;
    EditText titleText;
    EditText descriptionText;
    EditText additionalInformationText;

    int group;
    String subjectName;
    Date eDate;

    boolean editMode = false;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_exam);

        subjectSelect = (Spinner) findViewById(R.id.addExamSubject);
        ArrayAdapter<CharSequence> subjectAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (Map.Entry<String, Integer> entry : utu.utuClient.subjects.entrySet()) {
            subjectAdapter.add(entry.getKey());
        }
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSelect.setAdapter(subjectAdapter);

        subjectSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subjectName = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        groupSelect = (Spinner) findViewById(R.id.addExamGroup);
        ArrayAdapter<CharSequence> groupAdapter = ArrayAdapter.createFromResource(this, R.array.groups_array, android.R.layout.simple_spinner_item);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSelect.setAdapter(groupAdapter);
        groupSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                group = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveButton = (Button) findViewById(R.id.addExamSaveButton);

        titleText = (EditText) findViewById(R.id.addExamName);

        descriptionText = (EditText) findViewById(R.id.addExamDescription);

        additionalInformationText = (EditText) findViewById(R.id.addExamAdditionalInformation);

        dateSelectButton = (Button) findViewById(R.id.addExamDate);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString(Exam.TITLE);
            if (title != null && !title.equals("")) {
                titleText.setText(title);
                descriptionText.setText(bundle.getString(Exam.DESCRIPTION));
                subjectSelect.setSelection(new ArrayList<Integer>(utu.utuClient.subjects.values()).indexOf(bundle.getInt(Task.SUBJECT)));
                additionalInformationText.setText(bundle.getString(Exam.ADDITIONAL_INFO_URL));
                groupSelect.setSelection(bundle.getInt(Exam.GROUP));
                dateSelectButton.setText(bundle.getString(Exam.DATE));
                id = bundle.getInt(Exam.ID);

                try {
                    eDate = format.parse(bundle.getString(Exam.DATE));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                editMode = true;
            }
        }

        TextView titleText = (TextView) findViewById(R.id.labelTitle);
        if (editMode) {
            titleText.setText(R.string.edit_exam);
            setTitle(R.string.edit_exam);
        } else {
            titleText.setText(R.string.new_exam);
            setTitle(R.string.new_exam);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generic_item, menu);
        if (editMode) {
            menu.add(Menu.NONE, 0, 100, getString(R.string.exterminate));
            menu.getItem(0).setIcon(android.R.drawable.ic_menu_delete);
            menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == 0) {
            // Delete
            new ExamRemover(this).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDateSelectButtonClick(final View view) {
        ManualSelect.DatePickerFragment dialog = new ManualSelect.DatePickerFragment();
        dialog.show(getFragmentManager(), "Choose penis");
        dialog.setOnDateChangedListener(new ManualSelect.DatePickerFragment.OnDateChangedListener() {
            @Override
            public void dateChanged(Date date) {
                ((Button) view).setText(format.format(date));
                eDate = date;
            }
        });
    }

    public void onSaveButtonClick(View view) {
        if (editMode)
            new ExamUpdater(this).execute();
        else
            new ExamAdder(this).execute();
    }

    public class ExamRemover extends TaskWithProgressDialog<Boolean> {

        public ExamRemover(Activity activity) {
            super(activity, getString(R.string.wait), getString(R.string.item_deleting), null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return utu.utuClient.deleteExam(id);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(activity, getString(R.string.item_was_successfully_deleted), Toast.LENGTH_LONG).show();
                finish();
            } else
                Toast.makeText(activity, getString(R.string.failed_to_delete_item), Toast.LENGTH_LONG).show();
        }
    }

    public class ExamAdder extends TaskWithProgressDialog<Boolean> {

        public ExamAdder(Activity activity) {
            super(activity, getString(R.string.wait), getString(R.string.saving_item_to_database), null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Exam exam = new Exam(titleText.getText().toString(), descriptionText.getText().toString(), group, utu.utuClient.subjects.get(subjectName), eDate, additionalInformationText.getText().toString(), id);
            return utu.utuClient.addExam(exam);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(activity, getString(R.string.item_was_succesfully_added_to_database), Toast.LENGTH_LONG).show();
                finish();
            } else
                Toast.makeText(activity, getString(R.string.failed_to_add_item), Toast.LENGTH_LONG).show();
        }
    }

    public class ExamUpdater extends TaskWithProgressDialog<Boolean> {

        public ExamUpdater(Activity activity) {
            super(activity, getString(R.string.wait), getString(R.string.item_updating), null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Exam exam = new Exam(titleText.getText().toString(), descriptionText.getText().toString(), group, utu.utuClient.subjects.get(subjectName), eDate, additionalInformationText.getText().toString(), id);
            return utu.utuClient.updateExam(exam);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(activity, getString(R.string.item_successfully_edited), Toast.LENGTH_LONG).show();
                finish();
            } else
                Toast.makeText(activity, getString(R.string.failed_to_edit_item), Toast.LENGTH_LONG).show();
        }
    }
}
