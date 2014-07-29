package cz.cendrb.utu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
import java.util.concurrent.ExecutionException;

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

    public boolean loadFromBackup(File file)
    {
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
    /*
            Resources resources = context.getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(resources.getString(R.string.unable_to_connect_to_the_internet));
            builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DataLoader.getData(context);
                }
            });
            builder.setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            final File file = context.getFileStreamPath(DataLoader.BACKUP_FILE_NAME);
            if (file.exists())
                builder.setNeutralButton(resources.getString(R.string.load_latest_downloaded_data), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadResult = LoadResult.Success;
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
                            XMLParser parser = new XMLParser();
                            setData(parser.execute(content).get());
                            loadResult = LoadResult.Success;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            builder.show();

            return loadResult;
        }
    }*/
}
