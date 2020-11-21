package uk.co.davidkanekanian.fabrik.persistence;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ChainDao {
    // Create

    @Query("INSERT INTO points (x, y) VALUES (:x, :y)")
    long addPoint(float x, float y);

    @Query("INSERT INTO chains (name) VALUES (:name)")
    long addChain(String name);

    @Query("INSERT INTO chain_point (chain_id, point_id) VALUES (:chainId, :pointId)")
    void addPointToChain(int chainId, int pointId);

    // Read

    @Query("SELECT * FROM chains")
    Chain[] getAllChains();

    @Query("SELECT points.* FROM chain_point "
            + "LEFT JOIN points on point_id = points.id "
            + "WHERE chain_id = :chain_id")
    Point[] getPointsInChain(int chain_id);

    // Update

    @Query("UPDATE chains SET name = :name WHERE id = :id")
    void updateChain(int id, String name);

    @Query("UPDATE points SET x = :x, y = :y WHERE id = :id")
    void updatePoint(int id, float x, float y);

    // Delete

    @Query("DELETE FROM chain_point WHERE chain_id = :chainId AND point_id = :pointId")
    void removePointFromChain(int chainId, int pointId);

    @Query("DELETE FROM chain_point WHERE chain_id = :chainID")
    void removeAllPointsFromChain(int chainID);

    @Query("DELETE FROM points WHERE id = :id")
    void deletePoint(int id);

    @Query("DELETE FROM chains WHERE id = :id")
    void deleteChain(int id);
}
