package ni.alvaro.dev.aventontest.viewmodels;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import ni.alvaro.dev.aventontest.database.BuddyRepo;
import ni.alvaro.dev.aventontest.models.Buddy;

public class BuddyViewModel extends AndroidViewModel {
    public BuddyRepo getBuddyRepo() {
        return buddyRepo;
    }

    private BuddyRepo buddyRepo;

    public LiveData<List<Buddy>> getBuddies() {
        return buddies;
    }

    private LiveData<List<Buddy>> buddies;
    public BuddyViewModel(@NonNull Application application) {
        super(application);
        this.buddyRepo = new BuddyRepo(application);
        this.buddies = buddyRepo.getAllBuddies();
    }



}
