package cz.cendrb.utu.administrationactivities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cz.cendrb.utu.ManualSelect;
import cz.cendrb.utu.R;
import cz.cendrb.utu.TaskWithProgressDialog;
import cz.cendrb.utu.utu;
import cz.cendrb.utu.utucomponents.Exam;

/**
 * Created by Cendrb on 27. 10. 2014.
 */
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        super.onCreate(savedInstanceState);
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
        new ExamAdder(this, getString(R.string.wait), getString(R.string.saving_item_to_database), null).execute();
    }

    public class ExamAdder extends TaskWithProgressDialog<Boolean> {

        public ExamAdder(Activity activity, String titleMessage, String message, Runnable postAction) {
            super(activity, titleMessage, message, postAction);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Exam exam = new Exam(titleText.getText().toString(), descriptionText.getText().toString(), group, utu.utuClient.subjects.get(subjectName), eDate, additionalInformationText.getText().toString());
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
}
