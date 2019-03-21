package ni.alvaro.dev.aventontest.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import ni.alvaro.dev.aventontest.models.Buddy;

@Dao
public interface BuddyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Buddy buddy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Buddy... buddy);

    @Update
    void update(Buddy buddy);


    @Query("SELECT * from tb_buddy ORDER BY id ASC")
    LiveData<List<Buddy>> getAllBuddies();

}
