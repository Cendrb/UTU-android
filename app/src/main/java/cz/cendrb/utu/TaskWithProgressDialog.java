package cz.cendrb.utu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by Cendrb on 26. 10. 2014.
 */
public abstract class TaskWithProgressDialog<T> extends AsyncTask<Void, Void, T> {
    protected Activity activity;

    ProgressDialog dialog;
    Runnable postAction;

    String titleMessage;
    String message;

    public TaskWithProgressDialog(Activity activity, String titleMessage, String message, Runnable postAction) {
        this.activity = activity;
        this.titleMessage = titleMessage;
        this.message = message;
        this.postAction = postAction;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(message);
        dialog.setTitle(titleMessage);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(T result) {
        dialog.hide();
        if (postAction != null)
            postAction.run();
        super.onPostExecute(result);
    }
}
