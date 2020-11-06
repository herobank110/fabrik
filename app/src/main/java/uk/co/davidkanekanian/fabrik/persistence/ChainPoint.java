package uk.co.davidkanekanian.fabrik.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chain_point", primaryKeys = {"chain_id", "point_id"})
public class ChainPoint {
    @ColumnInfo(name = "chain_id")
    public int chainId;

    @ColumnInfo(name = "point_id")
    public int pointId;
}
