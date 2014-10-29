package cz.cendrb.utu;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.cendrb.utu.utucomponents.Events;
import cz.cendrb.utu.utucomponents.Exam;
import cz.cendrb.utu.utucomponents.Exams;
import cz.cendrb.utu.utucomponents.Tasks;

public class UtuClient {
    static final String BACKUP_FILE_NAME = "utudata";
    static final String BACKUP_SUBJECTS_FILE_NAME = "subjects";

    public Events events;
    public Exams exams;
    public Tasks tasks;

    public SparseArray<String> subjects;

    private boolean loggedIn;

    HttpClient client;

    public UtuClient() {
        events = new Events();
        exams = new Exams();
        tasks = new Tasks();
        client = new DefaultHttpClient();
    }

    public boolean addExam(Exam exam)
    {

    }

    public boolean isAdministrator()
    {
        if(getStringFrom("http://utu.herokuapp.com/administrator_authenticated").equals("true"))
            return true;
        else
            return false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean login(String email, String password) {
        try {
            HttpPost loginPost = new HttpPost("http://utu.herokuapp.com/login.whoa");

            List<NameValuePair> loginData = new ArrayList<NameValuePair>();
            loginData.add(new BasicNameValuePair("email", email));
            loginData.add(new BasicNameValuePair("password", password));

            loginPost.setEntity(new UrlEncodedFormEntity(loginData));

            HttpResponse response = client.execute(loginPost);
            Log.d(utu.getPrefix(), response.getStatusLine().toString());
            if (response.getStatusLine().getStatusCode() == 200)
                loggedIn = true;
            else
                loggedIn = false;
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

    public long getLastModifiedFromBackupData(Activity activity) {
        return activity.getFileStreamPath(BACKUP_FILE_NAME).lastModified();
    }

    private String getStringFrom(String url) {
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(url));
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                OutputStream byteStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(byteStream);
                return byteStream.toString();
            } else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            subjects = new SparseArray<String>();

            for (int counter = subjectsElement.getChildNodes().getLength() - 1; counter > 0; counter--) {
                Node node = subjectsElement.getChildNodes().item(counter);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element subject = (Element) node;
                    String name = subject.getAttribute("name");
                    int id = Integer.parseInt(subject.getAttribute("id"));

                    subjects.put(id, name);
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
