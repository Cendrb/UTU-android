package cz.cendrb.utu.utucomponents;

import android.util.Log;

import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Exam {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String SUBJECT = "subject";
    static final String DATE = "date";
    static final String GROUP = "group";

    String title;
    String description;
    Subject subject;
    Date date;
    int group;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

    public Exam(Element data) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        title = data.getAttribute(TITLE);
        description = data.getAttribute(DESCRIPTION);
        subject = Subject.valueOf(data.getAttribute(SUBJECT));
        try {
            date = df.parse(data.getAttribute(DATE));
        } catch (ParseException e) {
            Log.e("Task", "Unknown format date" + e.getMessage());
            date = new Date();
            e.printStackTrace();
        }
        group = Integer.parseInt(data.getAttribute(GROUP));
    }

    public HashMap<String, String> getRecord() {
        HashMap<String, String> record = new HashMap<String, String>();
        record.put(TITLE, title);
        record.put(DESCRIPTION, description);
        record.put(SUBJECT, subject.name());
        record.put(DATE, dateFormat.format(date));
        String stringGroup = "";
        switch (group) {
            case 0:
                stringGroup = "obě skupiny";
                break;
            case 1:
                stringGroup = "první";
                break;
            case 2:
                stringGroup = "druhá";
                break;
        }
        record.put(GROUP, stringGroup);
        return record;
    }

}
