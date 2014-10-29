package cz.cendrb.utu.administrationactivities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.cendrb.utu.ManualSelect;
import cz.cendrb.utu.R;
import cz.cendrb.utu.utu;
import cz.cendrb.utu.utucomponents.Exam;

/**
 * Created by Cendrb on 27. 10. 2014.
 */
public class AddExam extends Activity {

    SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy");
    Spinner subjectSelect;
    Spinner groupSelect;
    Button saveButton;
    Button dateSelectButton;
    EditText titleText;
    EditText descriptionText;
    EditText additionalInformationText;

    String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_exam);

        subjectSelect = (Spinner) findViewById(R.id.addExamSubject);
        ArrayAdapter<CharSequence> subjectAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for(int i = 0; i < utu.utuClient.subjects.size(); i++) {
            subjectAdapter.add(utu.utuClient.subjects.get(i));
        }
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSelect.setAdapter(subjectAdapter);
        subjectSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectName = (String) adapterView.getItemAtPosition(i);
            }
        });

        groupSelect = (Spinner) findViewById(R.id.addExamGroup);
        ArrayAdapter<CharSequence> groupAdapter = ArrayAdapter.createFromResource(this, R.array.groups_array, android.R.layout.simple_spinner_item);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSelect.setAdapter(groupAdapter);

        saveButton = (Button) findViewById(R.id.addExamSaveButton);

        titleText = (EditText) findViewById(R.id.addExamName);

        descriptionText = (EditText) findViewById(R.id.addExamDescription);

        additionalInformationText = (EditText) findViewById(R.id.addExamAdditionalInformation);

        dateSelectButton = (Button)
    }

    public void onDateSelectButtonClick(final View view)
    {
        ManualSelect.DatePickerFragment dialog = new ManualSelect.DatePickerFragment();
        dialog.show(getFragmentManager(), "Choose penis");
        dialog.setOnDateChangedListener(new ManualSelect.DatePickerFragment.OnDateChangedListener() {
            @Override
            public void dateChanged(Date date) {
                ((Button) view).setText(format.format(date));
            }
        });
    }

    public void onSaveButtonClick(View view)
    {
        Exam exam = new Exam(titleText.getText().toString(), descriptionText.getText().toString(), groupSelect.indexOfChild(groupSelect.getFocusedChild()), utu.utuClient.subjects.indexOfValue(subjectName), format.parse());
        exam
    }
}
