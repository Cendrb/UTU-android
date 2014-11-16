package cz.cendrb.utu.administrationactivities;

import android.app.Activity;
import android.widget.Toast;

import cz.cendrb.utu.R;
import cz.cendrb.utu.TaskWithProgressDialog;
import cz.cendrb.utu.utu;

/**
 * Created by Cendrb on 16. 11. 2014.
 */
public class AddEditEvent {

    public static class EventRemover extends TaskWithProgressDialog<Boolean> {

        int id;
        boolean finish;

        public EventRemover(Activity activity, int id, Runnable postAction) {
            super(activity, activity.getString(R.string.wait), activity.getString(R.string.item_deleting), postAction);
            this.id = id;
            this.activity = activity;
        }

        public EventRemover(Activity activity, int id, boolean finishAfterSuccess) {
            super(activity, activity.getString(R.string.wait), activity.getString(R.string.item_deleting));
            this.id = id;
            this.activity = activity;
            finish = finishAfterSuccess;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return utu.utuClient.deleteEvent(id);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(activity, activity.getString(R.string.item_was_successfully_deleted), Toast.LENGTH_LONG).show();
                if (finish)
                    activity.finish();
            } else
                Toast.makeText(activity, activity.getString(R.string.failed_to_delete_item), Toast.LENGTH_LONG).show();
        }
    }
}
