package net.senior.dadapp;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

public class AppDbManager {
    private static AppDbManager ourInstance;
    private static final String database_name = "TODO_DB";
    private ModelDao modelDao;


    public static AppDbManager getInstance(Context c) {
        if (ourInstance == null)
            ourInstance = new AppDbManager(c);

        return ourInstance;
    }

    private AppDbManager(Context c) {
        AppData db = Room.databaseBuilder(c,AppData.class, database_name)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        modelDao = db.getAnswersDao();
    }




    public List<Model> getAll() {
        return modelDao.getAll();
    }





    public long insert(Model model) {

        long id=modelDao.insert(model);
//       todoEntity.id=id;
        return id;

    }

    public void delete(Model model) {
        modelDao.delete(model);
    }




}
