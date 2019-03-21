package ni.alvaro.dev.aventontest.database;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;
import ni.alvaro.dev.aventontest.async.BuddiesInsertionTask;
import ni.alvaro.dev.aventontest.models.Buddy;

public class BuddyRepo {
    private BuddyDao buddyDao;

    public BuddyDao getBuddyDao() {
        return buddyDao;
    }


    public LiveData<List<Buddy>> getAllBuddies() {
        return mAllBuddies;
    }

    public void insertAll(Buddy... buddies){
        new BuddiesInsertionTask(buddyDao).execute(buddies);
    }

    private LiveData<List<Buddy>> mAllBuddies;

    public BuddyRepo(Application application) {
        this.buddyDao = AventonDatabase.getDatabase(application).buddyDao();
        this.mAllBuddies = buddyDao.getAllBuddies();
    }


}
