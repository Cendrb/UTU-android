package cz.cendrb.utu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
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

    public static Events events;
    public static Exams exams;
    public static Tasks tasks;

    private static LoadResult loadResult;

    public static LoadResult getData(final Context context) {

        if (DataLoader.events == null)
            DataLoader.events = new Events();
        if (DataLoader.exams == null)
            DataLoader.exams = new Exams();
        if (DataLoader.tasks == null)
            DataLoader.tasks = new Tasks();

        if(DataLoader.isOnline(context)) {


            HttpLoader loader = new HttpLoader();

            String data = null;
            try {
                OutputStream stream = loader.execute("http://utu.herokuapp.com/details.xml").get();
                if (stream == null) {
                    return LoadResult.Failure;
                }
                else {
                    data = stream.toString();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            XMLParser parser = new XMLParser();
            try {
                setData(parser.execute(data).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return LoadResult.Success;
        }
        else
        {
            loadResult = LoadResult.Nothing;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.unable_to_connect_to_the_internet));
            builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DataLoader.getData(context);
                }
            });
            builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setNeutralButton(context.getResources().getString(R.string.load_latest_downloaded_data), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loadResult = LoadResult.Success;
                    Toast.makeText(context, "Nothing happened", Toast.LENGTH_SHORT).show();
                    // TODO Load from backup
                }
            });
            builder.show();

            return loadResult;
        }
    }

    private static void setData(Document doc) {
        Element utuElement = (Element) doc.getElementsByTagName("utu").item(0);
        Element events = (Element) utuElement.getElementsByTagName("events")
                .item(0);
        Element tasks = (Element) utuElement.getElementsByTagName("tasks")
                .item(0);
        Element exams = (Element) utuElement.getElementsByTagName("exams")
                .item(0);

        DataLoader.events.load(events);
        DataLoader.exams.load(exams);
        DataLoader.tasks.load(tasks);

        Collections.reverse(DataLoader.events.events);
        Collections.reverse(DataLoader.exams.exams);
        Collections.reverse(DataLoader.tasks.tasks);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
class HttpLoader extends AsyncTask<String, Void, OutputStream> {
    @Override
    protected OutputStream doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();
        try {
            //HttpPost httpPost = new HttpPost(params[0]);
            HttpResponse response = client.execute(new HttpGet(params[0]));
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                OutputStream byteStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(byteStream);
                return byteStream;
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

class XMLParser extends AsyncTask<String, Void, Document> {
    @Override
    protected Document doInBackground(String... strings) {
        Document doc = null;
        DocumentBuilderFactory dbf;
        dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(strings[0]));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("XML Parser", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("XML Parser", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("XML Parser", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }
}
