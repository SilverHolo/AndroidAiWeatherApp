package edu.uiuc.cs427app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

/**
 * Room database for managing the storage and CRUD operations of City entities.
 */
@Dao
public interface CityDao {

    /**
     * Inserts a new City entry into the database.
     * 
     * @param city the City entity to be inserted
     */
    @Insert
    void insertCity(City city);

    /**
     * Retrieves a list of City entries for a specified user from the database.
     * 
     * @param userID the ID of the user whose list of City entries to be retrieved
     * @return a list of City entities associated with the specified user
     */
    @Query("SELECT * FROM City WHERE userID = :userID")
    List<City> getCitiesForUser(String userID);

    /**
     * Deletes a specific City entry from the database for the specified user.
     * 
     * @param userID the ID of the user whose list of City entries to be retrieved
     * @param cityName the name of the city to be deleted
     */
    @Query("DELETE FROM City WHERE cityName = :cityName AND userID = :userID")
    void DeleteCity(String userID, String cityName);

}
