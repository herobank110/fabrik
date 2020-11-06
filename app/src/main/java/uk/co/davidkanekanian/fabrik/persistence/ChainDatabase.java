package uk.co.davidkanekanian.fabrik.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = {Chain.class, Point.class, ChainPoint.class}, exportSchema = false)
public abstract class ChainDatabase extends RoomDatabase {
    private static volatile ChainDatabase INSTANCE;

    public abstract ChainDao chainDao();

    public static ChainDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ChainDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChainDatabase.class, "FabrikChain.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
