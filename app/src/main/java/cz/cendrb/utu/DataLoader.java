package cz.cendrb.utu;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.cendrb.utu.utucomponents.Events;
import cz.cendrb.utu.utucomponents.Exams;
import cz.cendrb.utu.utucomponents.Tasks;

public class DataLoader {

    public static final String BACKUP_FILE_NAME = "utudata";
    public Events events;
    public Exams exams;
    public Tasks tasks;

    public DataLoader() {
        events = new Events();
        exams = new Exams();
        tasks = new Tasks();
    }

    public boolean loadFromNetAndBackup(File backupFile) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(new HttpGet("http://utu.herokuapp.com/details.xml"));
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
