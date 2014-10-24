package cz.cendrb.utu;

import android.os.Debug;
import android.util.Log;

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
import cz.cendrb.utu.utucomponents.Exams;
import cz.cendrb.utu.utucomponents.Tasks;

public class DataLoader {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public static final String BACKUP_FILE_NAME = "utudata";
    public Events events;
    public Exams exams;
    public Tasks tasks;

    HttpClient client;
/*
    Date from;
    Date to;
    int group;

        public static final String FROM = "from";
    public static final String TO = "to";
    public static final String GROUP = "group";

    HttpPost httpPost = new HttpPost("http://utu.herokuapp.com/details.xml");
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair(FROM, from.toString()));
                urlParameters.add(new BasicNameValuePair(TO, to.toString()));
                urlParameters.add(new BasicNameValuePair(GROUP, String.valueOf(group)));

                httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

                response = client.execute(httpPost);

    */

    public DataLoader() {
        events = new Events();
        exams = new Exams();
        tasks = new Tasks();
        client = new DefaultHttpClient();
    }

    public DataLoader(String email, String password) {
        this();
        try {
            HttpPost loginPost = new HttpPost();

            List<NameValuePair> loginData = new ArrayList<NameValuePair>();
            loginData.add(new BasicNameValuePair("email", email));
            loginData.add(new BasicNameValuePair("password", password));

            loginPost.setEntity(new UrlEncodedFormEntity(loginData));

            HttpResponse response = client.execute(loginPost);
            Log.d(utu.NAME, response.getStatusLine().toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadFromNetAndBackup(File backupFile) {
        try {
            HttpResponse response;
            response = client.execute(new HttpGet("http://utu.herokuapp.com/details.xml"));
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                OutputStream byteStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(byteStream);
                String rawXml = byteStream.toString();
                Document doc = parseXML(rawXml);
                if (doc == null)
                    return false;
                setData(doc);
                backupData(rawXml, backupFile);
                return true;
            } else
                return false;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean loadFromBackup(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            String content = sb.toString();
            br.close();
            setData(parseXML(content));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    private void setData(Document doc) {
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
    }
}
