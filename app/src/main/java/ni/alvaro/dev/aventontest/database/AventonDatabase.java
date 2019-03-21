package ni.alvaro.dev.aventontest.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import ni.alvaro.dev.aventontest.models.Buddy;
import ni.alvaro.dev.aventontest.utils.PropertyManager;

@Database(entities = {Buddy.class}, version = 1, exportSchema = false)
public abstract class AventonDatabase extends RoomDatabase {
    public abstract BuddyDao buddyDao();

    private static volatile AventonDatabase INSTANCE;

    static AventonDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AventonDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AventonDatabase.class, PropertyManager.getInstance().getDbName())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
