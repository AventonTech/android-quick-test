package ni.alvaro.dev.aventontest.async;

import android.os.AsyncTask;

import ni.alvaro.dev.aventontest.database.BuddyDao;
import ni.alvaro.dev.aventontest.models.Buddy;

public class BuddiesInsertionTask extends AsyncTask<Buddy,Void,Void> {
    private final BuddyDao mBuddyDao;

    public BuddiesInsertionTask(BuddyDao buddyDao) {
        this.mBuddyDao = buddyDao;
    }

    @Override
    protected final Void doInBackground(Buddy... buddy) {
        mBuddyDao.insert(buddy);
        return null;
    }
}
