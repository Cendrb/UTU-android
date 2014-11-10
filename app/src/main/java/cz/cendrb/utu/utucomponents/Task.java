package cz.cendrb.utu.utucomponents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.cendrb.utu.administrationactivities.AddEditTask;
import cz.cendrb.utu.utu;

public class Task {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String SUBJECT = "subject";
    public static final String DATE = "date";
    public static final String GROUP = "group";
    public static final String ADDITIONAL_INFO_URL = "additional_info_url";
    public static final String ID = "id";

    int id;
    String title;
    String description;
    String additionalInfoUrl;
    int subject;
    String subjectString;
    Date date;
    int group;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

    public Task(String title, String description, int group, int subject, Date date, String additionalInfoUrl, int id) {
        this.title = title;
        this.description = description;
        this.additionalInfoUrl = additionalInfoUrl;
        this.subject = subject;
        this.date = date;
        this.group = group;
        this.id = id;
    }

    public Task(Element data) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        title = data.getAttribute(TITLE);
        description = data.getAttribute(DESCRIPTION);
        additionalInfoUrl = data.getAttribute(ADDITIONAL_INFO_URL);
        subjectString = data.getAttribute(SUBJECT);
        subject = utu.utuClient.subjects.get(subjectString);
        try {
            date = df.parse(data.getAttribute(DATE));
        } catch (ParseException e) {
            Log.e("Task", "Unknown format date" + e.getMessage());
            date = new Date();
            e.printStackTrace();
        }
        group = Integer.parseInt(data.getAttribute(GROUP));
        id = Integer.parseInt(data.getAttribute(ID));
    }

    public HashMap<String, String> getRecord() {
        HashMap<String, String> record = new HashMap<String, String>();
        record.put(TITLE, title);
        record.put(DESCRIPTION, description);
        record.put(SUBJECT, subjectString);
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
        record.put(ID, String.valueOf(id));
        return record;
    }

    public void startEditActivity(Context context)
    {
        Intent intent = new Intent(context, AddEditTask.class);
        intent.putExtra(Task.TITLE, title);
        intent.putExtra(Task.DESCRIPTION, description);
        intent.putExtra(Task.DATE, dateFormat.format(date));
        intent.putExtra(Task.SUBJECT, subject);
        intent.putExtra(Task.GROUP, group);
        intent.putExtra(Task.ADDITIONAL_INFO_URL, additionalInfoUrl);
        intent.putExtra(Task.ID, id);
        context.startActivity(intent);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAdditionalInfoUrl() {
        return additionalInfoUrl;
    }

    public int getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

    public int getGroup() {
        return group;
    }

    public String getSubjectString() {
        return subjectString;
    }

    public int getId() {
        return id;
    }
}
