package cz.cendrb.utu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public abstract class TaskWithProgressDialog<T> extends AsyncTask<Void, Void, T> {
    protected Activity activity;

    ProgressDialog dialog;
    Runnable postAction;
    boolean indeterminate = true;

    String titleMessage;
    String message;

    public TaskWithProgressDialog(Activity activity, String titleMessage, String message) {
        this.activity = activity;
        this.titleMessage = titleMessage;
        this.message = message;
    }

    public TaskWithProgressDialog(Activity activity, String titleMessage, String message, boolean indeterminate) {
        this.activity = activity;
        this.titleMessage = titleMessage;
        this.message = message;
        this.indeterminate = indeterminate;
    }

    public TaskWithProgressDialog(Activity activity, String titleMessage, String message, Runnable postAction, boolean indeterminate) {
        this.activity = activity;
        this.titleMessage = titleMessage;
        this.message = message;
        this.postAction = postAction;
        this.indeterminate = indeterminate;
    }

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
        dialog.setIndeterminate(indeterminate);
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
