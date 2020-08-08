package net.senior.dadapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface ModelDao {
    @Query("SELECT * FROM Model")
    List<Model> getAll();


    @Query("SELECT * FROM Model WHERE id = :Id")
    Model loadById(long Id);

    @Insert
    long insert(Model model);


    @Update
    void update( Model model);


    @Delete
    void delete(Model model);

}
