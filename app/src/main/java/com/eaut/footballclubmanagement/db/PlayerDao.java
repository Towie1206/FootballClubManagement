package com.eaut.footballclubmanagement.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.eaut.footballclubmanagement.models.Player;
import java.util.List;

@Dao
public interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY jerseyNumber ASC")
    List<Player> getAllPlayers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Player> players);

    @Query("DELETE FROM players")
    void deleteAll();
}
