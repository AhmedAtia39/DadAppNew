package net.senior.dadapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;




@Database(entities = {Model.class}, exportSchema = false, version = 1)
public abstract class AppData extends RoomDatabase {
    public abstract ModelDao getAnswersDao();



}