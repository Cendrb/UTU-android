package cz.cendrb.utu;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.cendrb.utu.utucomponents.Events;
import cz.cendrb.utu.utucomponents.Exam;
import cz.cendrb.utu.utucomponents.Exams;
import cz.cendrb.utu.utucomponents.Task;
import cz.cendrb.utu.utucomponents.Tasks;

public class UtuClient {
    static final String BACKUP_FILE_NAME = "utudata";
    static final String BACKUP_SUBJECTS_FILE_NAME = "subjects";

    public Events events;
    public Exams exams;
    public Tasks tasks;

    public HashMap<String, Integer> subjects;

    private boolean loggedIn;

    HttpClient client;



    public UtuClient() {
        events = new Events();
        exams = new Exams();
        tasks = new Tasks();
        client = new DefaultHttpClient();
    }

    public boolean addExam(Exam exam) {
        try {
            List<NameValuePair> examData = new ArrayList<NameValuePair>();
            examData.add(new BasicNameValuePair("exam[title]", exam.getTitle()));
            examData.add(new BasicNameValuePair("exam[description]", exam.getDescription()));
            examData.add(new BasicNameValuePair("exam[subject_id]", String.valueOf(exam.getSubject())));
            examData.add(new BasicNameValuePair("exam[additional_info_url]", exam.getAdditionalInfoUrl()));
            examData.add(new BasicNameValuePair("exam[group]", String.valueOf(exam.getGroup())));

            SimpleDateFormat format = new SimpleDateFormat("MM");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(exam.getDate());
            examData.add(new BasicNameValuePair("exam[date(3i)]", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));
            examData.add(new BasicNameValuePair("exam[date(2i)]", format.format(exam.getDate())));
            examData.add(new BasicNameValuePair("exam[date(1i)]", String.valueOf(calendar.get(Calendar.YEAR))));

            String result = getStringFrom(getPOSTResponseWithParams("http://utu.herokuapp.com/exams.whoa", examData));
            return result.equals("success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addTask(Task task) {
        try {
            List<NameValuePair> taskData = new ArrayList<NameValuePair>();
            taskData.add(new BasicNameValuePair("task[title]", task.getTitle()));
            taskData.add(new BasicNameValuePair("task[description]", task.getDescription()));
            taskData.add(new BasicNameValuePair("task[subject_id]", String.valueOf(task.getSubject())));
            taskData.add(new BasicNameValuePair("task[additional_info_url]", task.getAdditionalInfoUrl()));
            taskData.add(new BasicNameValuePair("task[group]", String.valueOf(task.getGroup())));

            SimpleDateFormat format = new SimpleDateFormat("MM");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(task.getDate());
            taskData.add(new BasicNameValuePair("task[date(3i)]", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));
            taskData.add(new BasicNameValuePair("task[date(2i)]", format.format(task.getDate())));
            taskData.add(new BasicNameValuePair("task[date(1i)]", String.valueOf(calendar.get(Calendar.YEAR))));

            String result = getStringFrom(getPOSTResponseWithParams("http://utu.herokuapp.com/tasks.whoa", taskData));
            return result.equals("success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateTask(Task task) {
        try {
            List<NameValuePair> taskData = new ArrayList<NameValuePair>();
            taskData.add(new BasicNameValuePair("_method", "patch"));
            taskData.add(new BasicNameValuePair("task[title]", task.getTitle()));
            taskData.add(new BasicNameValuePair("task[description]", task.getDescription()));
            taskData.add(new BasicNameValuePair("task[subject_id]", String.valueOf(task.getSubject())));
            taskData.add(new BasicNameValuePair("task[additional_info_url]", task.getAdditionalInfoUrl()));
            taskData.add(new BasicNameValuePair("task[group]", String.valueOf(task.getGroup())));

            SimpleDateFormat format = new SimpleDateFormat("MM");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(task.getDate());
            taskData.add(new BasicNameValuePair("task[date(3i)]", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));
            taskData.add(new BasicNameValuePair("task[date(2i)]", format.format(task.getDate())));
            taskData.add(new BasicNameValuePair("task[date(1i)]", String.valueOf(calendar.get(Calendar.YEAR))));

            String result = getStringFrom(getPOSTResponseWithParams("http://utu.herokuapp.com/tasks/" + task.getId() + ".whoa", taskData));
            return result.equals("success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExam(Exam exam) {
        try {
            List<NameValuePair> examData = new ArrayList<NameValuePair>();
            examData.add(new BasicNameValuePair("_method", "patch"));
            examData.add(new BasicNameValuePair("exam[title]", exam.getTitle()));
            examData.add(new BasicNameValuePair("exam[description]", exam.getDescription()));
            examData.add(new BasicNameValuePair("exam[subject_id]", String.valueOf(exam.getSubject())));
            examData.add(new BasicNameValuePair("exam[additional_info_url]", exam.getAdditionalInfoUrl()));
            examData.add(new BasicNameValuePair("exam[group]", String.valueOf(exam.getGroup())));

            SimpleDateFormat format = new SimpleDateFormat("MM");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(exam.getDate());
            examData.add(new BasicNameValuePair("exam[date(3i)]", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));
            examData.add(new BasicNameValuePair("exam[date(2i)]", format.format(exam.getDate())));
            examData.add(new BasicNameValuePair("exam[date(1i)]", String.valueOf(calendar.get(Calendar.YEAR))));

            String result = getStringFrom(getPOSTResponseWithParams("http://utu.herokuapp.com/exams/" + exam.getId() + ".whoa", examData));
            return result.equals("success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExam(int id) {
        try {
            List<NameValuePair> examData = new ArrayList<NameValuePair>();
            examData.add(new BasicNameValuePair("_method", "delete"));
            String result = getStringFrom(getPOSTResponseWithParams("http://utu.herokuapp.com/exams/" + id + ".whoa", examData));
            return result.equals("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTask(int id) {
        try {
            List<NameValuePair> examData = new ArrayList<NameValuePair>();
            examData.add(new BasicNameValuePair("_method", "delete"));
            String result = getStringFrom(getPOSTResponseWithParams("http://utu.herokuapp.com/tasks/" + id + ".whoa", examData));
            return result.equals("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAdministrator() {
        String result = getStringFrom("http://utu.herokuapp.com/administrator_authenticated");
        if (result != null)
            return result.equals("true");
        else
            return false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean login(String email, String password) {
        try {
            List<NameValuePair> loginData = new ArrayList<NameValuePair>();
            loginData.add(new BasicNameValuePair("email", email));
            loginData.add(new BasicNameValuePair("password", password));

            HttpResponse response = getPOSTResponseWithParams("http://utu.herokuapp.com/login.whoa", loginData);
            Log.d(utu.getPrefix(), response.getStatusLine().toString());
            loggedIn = response.getStatusLine().getStatusCode() == 200;
            return loggedIn;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        try {
            client.execute(new HttpGet("http://utu.herokuapp.com/logout"));
            loggedIn = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadFromNetAndBackup(Activity activity) {
        String result;
        boolean subSuccess = false;
        boolean utuSuccess = false;

        File subjectsFile = activity.getFileStreamPath(BACKUP_SUBJECTS_FILE_NAME);
        result = getStringFrom("http://utu.herokuapp.com/subjects.xml");
        if (result != null) {
            if (!setSubjectsData(parseXML(result)))
                return false;
            backupData(result, subjectsFile);
            subSuccess = true;
        }

        File utuFile = activity.getFileStreamPath(BACKUP_FILE_NAME);
        result = getStringFrom("http://utu.herokuapp.com/details.xml");
        if (result != null) {
            if (!setUtuData(parseXML(result)))
                return false;
            backupData(result, utuFile);
            utuSuccess = true;
        }

        return subSuccess && utuSuccess;
    }

    public boolean loadFromBackup(Activity activity) {
        File utuFile = activity.getFileStreamPath(BACKUP_FILE_NAME);
        File subjectsFile = activity.getFileStreamPath(BACKUP_SUBJECTS_FILE_NAME);

        if (!utuFile.exists() || !subjectsFile.exists())
            return false;

        if (!setSubjectsData(parseXML(loadData(subjectsFile))))
            return false;

        if (!setUtuData(parseXML(loadData(utuFile))))
            return false;

        return true;
    }

    public boolean backupExists(Activity activity)
    {
        File utuFile = activity.getFileStreamPath(BACKUP_FILE_NAME);
        File subjectsFile = activity.getFileStreamPath(BACKUP_SUBJECTS_FILE_NAME);
        return utuFile.exists() && subjectsFile.exists();
    }

    public long getLastModifiedFromBackupData(Activity activity) {
        return activity.getFileStreamPath(BACKUP_FILE_NAME).lastModified();
    }

    private String getStringFrom(String url) {
        HttpResponse response;
        try {
            response = client.execute(new HttpGet(url));
            return getStringFrom(response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getStringFrom(HttpResponse response) throws IOException {
        OutputStream byteStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(byteStream);
        return byteStream.toString();
    }

    private HttpResponse getPOSTResponseWithParams(String url, List<NameValuePair> data) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
        return client.execute(httpPost);
    }

    private String loadData(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void backupData(String rawXml, File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(rawXml.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document parseXML(String rawXml) {
        Document doc;
        DocumentBuilderFactory dbf;
        dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(rawXml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // return DOM
        return doc;
    }

    private boolean setSubjectsData(Document doc) {
        try {
            Element subjectsElement = (Element) doc.getElementsByTagName("subjects").item(0);
            subjects = new HashMap<String, Integer>();

            for (int counter = subjectsElement.getChildNodes().getLength() - 1; counter > 0; counter--) {
                Node node = subjectsElement.getChildNodes().item(counter);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element subject = (Element) node;
                    String name = subject.getAttribute("name");
                    int id = Integer.parseInt(subject.getAttribute("id"));

                    subjects.put(name, id);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean setUtuData(Document doc) {
        try {
            Element utuElement = (Element) doc.getElementsByTagName("utu").item(0);
            Element events = (Element) utuElement.getElementsByTagName("events")
                    .item(0);
            Element tasks = (Element) utuElement.getElementsByTagName("tasks")
                    .item(0);
            Element exams = (Element) utuElement.getElementsByTagName("exams")
                    .item(0);

            this.events.clearAndLoad(events);
            this.exams.clearAndLoad(exams);
            this.tasks.clearAndLoad(tasks);

            Collections.reverse(this.events.events);
            Collections.reverse(this.exams.exams);
            Collections.reverse(this.tasks.tasks);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
