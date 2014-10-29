package cz.cendrb.utu.administrationactivities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.cendrb.utu.ManualSelect;
import cz.cendrb.utu.R;

/**
 * Created by Cendrb on 27. 10. 2014.
 */
public class AddExam extends Activity {

    SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy");
    Spinner subjectSelect;
    Spinner groupSelect;
    Button saveButton;
    EditText titleText;
    EditText descriptionText;
    EditText additionalInformationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_exam);

        subjectSelect = (Spinner) findViewById(R.id.addExamSubject);
        groupSelect = (Spinner) findViewById(R.id.addExamGroup);
        saveButton = (Button) findViewById(R.id.addExamSaveButton);
        titleText = (EditText) findViewById(R.id.addExamName);
        descriptionText = (EditText) findViewById(R.id.addExamDescription);
        additionalInformationText = (EditText) findViewById(R.id.addExamAdditionalInformation);
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

    }
}
