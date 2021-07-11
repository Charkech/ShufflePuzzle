package com.shufflepuzzle;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.shufflepuzzle.Constants.SETTINGS_NAME;

public class PuzzleDatabaseHelper {
    private Context context;
    private SQLiteOpenHelper _openHelper;

    private static final String TABLE_CATEGORY = "puzzle_category";
    private static final String TABLE_LEVELS = "puzzle_game";
    private static final String TABLE_SETTINGS = "puzzle_settings";

    private static final String TABLE_SETTINGS_FILED_ID = "_id";
    private static final String TABLE_SETTINGS_FILED_SETTING_NAME = "setting_name";
    private static final String TABLE_SETTINGS_FILED_SETTING_VALUE = "setting_value";

    private static final String TABLE_CATEGORY_FILED_ID = "_id";
    private static final String TABLE_CATEGORY_FILED_CATEGORY_NAME = "cat_name";
    private static final String TABLE_CATEGORY_FILED_IMAGE = "cat_img";

    private static final String TABLE_LEVELS_FILED_ID = "_id";
    private static final String TABLE_LEVELS_FILED_CAT_ID = "cat_id";
    private static final String TABLE_LEVELS_FILED_LEVEL_NAME = "level";
    private static final String TABLE_LEVELS_FILED_IMAGE = "image";
    private static final String TABLE_LEVELS_FILED_IMAGE_NAME = "image_name";
    private static final String TABLE_LEVELS_FILED_RATING = "rating";
    private static final String TABLE_LEVELS_FILED_LEVEL_STATUS = "status";
    private static final String TABLE_LEVELS_FILED_LEVEL_LEVEL_COIN = "coin";
    private static final String TABLE_LEVELS_FILED_LEVEL_LEVEL_HIGH_COIN = "high_coin";
    private static final String TABLE_LEVELS_FILED_LEVEL_LEVEL_MODE = "mode";  // 0 for easy and 1 for hard
    private static final String TABLE_LEVELS_FILED_UPDATE_AT = "update_at";


    public PuzzleDatabaseHelper(Context context) {
        String databseName = context.getPackageName();
        this.context = context;
        _openHelper = new SimpleSQLiteOpenHelper(context, databseName);
    }

    class SimpleSQLiteOpenHelper extends SQLiteOpenHelper {

        SimpleSQLiteOpenHelper(Context context, String databseName) {

            super(context, databseName, null, 1);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            //Table Puzzle Game Settings
            db.execSQL("create table " + TABLE_SETTINGS + " (" +
                    TABLE_SETTINGS_FILED_ID + " INTEGER," +
                    TABLE_SETTINGS_FILED_SETTING_NAME + " TEXT," +
                    TABLE_SETTINGS_FILED_SETTING_VALUE + " INTEGER)");

            //Table Puzzle Game Categories
            db.execSQL("create table " + TABLE_CATEGORY + " (" +
                    TABLE_CATEGORY_FILED_ID + " INTEGER," +
                    TABLE_CATEGORY_FILED_CATEGORY_NAME + " TEXT," +
                    TABLE_CATEGORY_FILED_IMAGE + " TEXT)");

            //Table Puzzle Game Categories
            db.execSQL("create table " + TABLE_LEVELS + " (" +
                    TABLE_LEVELS_FILED_ID + " INTEGER," +
                    TABLE_LEVELS_FILED_CAT_ID + " INTEGER," +
                    TABLE_LEVELS_FILED_LEVEL_NAME + " INTEGER," +
                    TABLE_LEVELS_FILED_IMAGE + " TEXT," +
                    TABLE_LEVELS_FILED_IMAGE_NAME + " TEXT," +
                    TABLE_LEVELS_FILED_RATING + " INTEGER," +
                    TABLE_LEVELS_FILED_LEVEL_STATUS + " INTEGER," +  // 0 for remaining 1 for complete
                    TABLE_LEVELS_FILED_LEVEL_LEVEL_COIN + " INTEGER," +
                    TABLE_LEVELS_FILED_LEVEL_LEVEL_HIGH_COIN + " INTEGER," +
                    TABLE_LEVELS_FILED_LEVEL_LEVEL_MODE + " INTEGER," +
                    TABLE_LEVELS_FILED_UPDATE_AT + " TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.disableWriteAheadLogging();
        }

    }

    public ArrayList<CategoryModel> getAllCategories() {
        ArrayList<CategoryModel> levelModelArrayList = new ArrayList<>();
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return levelModelArrayList;
        }

        String queryStr = "select * from " + TABLE_CATEGORY + " order by " + TABLE_CATEGORY_FILED_CATEGORY_NAME + " ASC";
        Cursor cur = db.rawQuery(queryStr, null);
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                while (!cur.isAfterLast()) {
                    int idStr = cur.getInt(cur.getColumnIndex(TABLE_CATEGORY_FILED_ID));
                    String category_name = cur.getString(cur.getColumnIndex(TABLE_CATEGORY_FILED_CATEGORY_NAME));
                    String image = cur.getString(cur.getColumnIndex(TABLE_CATEGORY_FILED_IMAGE));
                    CategoryModel categoryModel = new CategoryModel();

                    categoryModel.setId(idStr);
                    categoryModel.setCategoryName(category_name);
                    categoryModel.setImage(image);
                    levelModelArrayList.add(categoryModel);
                    cur.moveToNext();

                }
            }
        }
        cur.close();
        db.close();

        return levelModelArrayList;
    }

    public ArrayList<LevelModel> getAllLevels(int cat_id) {
        ArrayList<LevelModel> levelModelArrayList = new ArrayList<>();
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return levelModelArrayList;
        }

        String queryStr = "select * from " + TABLE_LEVELS + " where " + TABLE_LEVELS_FILED_CAT_ID + "=" + cat_id + " order by " + TABLE_LEVELS_FILED_LEVEL_NAME + " asc";
        Cursor cur = db.rawQuery(queryStr, null);
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                while (!cur.isAfterLast()) {
                    int idStr = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_ID));
                    int level_name = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_LEVEL_NAME));
                    String level_img = cur.getString(cur.getColumnIndex(TABLE_LEVELS_FILED_IMAGE));
                    String img_name = cur.getString(cur.getColumnIndex(TABLE_LEVELS_FILED_IMAGE_NAME));
                    int level_rating = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_RATING));
                    int level_status = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_LEVEL_STATUS));
                    int level_coin = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_LEVEL_LEVEL_COIN));
                    int level_high_coin = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_LEVEL_LEVEL_HIGH_COIN));
                    int level_mode = cur.getInt(cur.getColumnIndex(TABLE_LEVELS_FILED_LEVEL_LEVEL_MODE));
                    String updatedData = cur.getString(cur.getColumnIndex(TABLE_LEVELS_FILED_UPDATE_AT));
                    LevelModel model = new LevelModel();

                    model.setId(idStr);
                    model.setLevel(level_name);
                    model.setCat_id(cat_id);
                    model.setImage(level_img);
                    model.setImage_name(img_name);
                    model.setRating(level_rating);
                    model.setStatus(level_status);
                    model.setCoin(level_coin);
                    model.setHigh_coin(level_high_coin);
                    model.setMode(level_mode);
                    model.setUpdate_at(updatedData);
                    levelModelArrayList.add(model);
                    cur.moveToNext();

                }
            }
        }
        cur.close();
        db.close();

        return levelModelArrayList;
    }

    public void addAllCategory(List<String> imagesList) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }


        for (int i = 0; i < imagesList.size(); i++) {
            ContentValues row = new ContentValues();

            row.put(TABLE_CATEGORY_FILED_ID, (i + 1));
            row.put(TABLE_CATEGORY_FILED_CATEGORY_NAME, imagesList.get(i));
            try {
                List<String> allLevelByCategory = getAllImagesByCategory(imagesList.get(i));
                if (allLevelByCategory != null && allLevelByCategory.size() > 0) {
                    String level_img = "file:///android_asset/Categories/" + imagesList.get(i) + "/" + allLevelByCategory.get(0);
                    row.put(TABLE_CATEGORY_FILED_IMAGE, level_img);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.insert(TABLE_CATEGORY, null, row);
            }


        }
        db.close();


    }


    private List<String> getAllImagesByCategory(String category) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] files = assetManager.list("Categories/" + category);
        List<String> it = Arrays.asList(files);
        Collections.sort(it);
        return it;
    }

    public void addAllLevels(List<String> imagesList, int category, String categoryname) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }

        for (int i = 0; i < imagesList.size(); i++) {
            ContentValues row = new ContentValues();

            int level_name = (i + 1);
            String level_img = "file:///android_asset/Categories/" + categoryname + "/" + imagesList.get(i);
            int level_rating = 0;
            int level_status = 0;      //0 for remaining and 1 for complete
            int level_high_coin = 0;
            int level_coin = 0;
            int level_mode = 0;
            String updatedData = Constants.getCurrentDate();


            row.put(TABLE_LEVELS_FILED_ID, (i + 1));
            row.put(TABLE_LEVELS_FILED_CAT_ID, category);
            row.put(TABLE_LEVELS_FILED_LEVEL_NAME, level_name);
            row.put(TABLE_LEVELS_FILED_IMAGE, level_img);
            row.put(TABLE_LEVELS_FILED_IMAGE_NAME, categoryname + "/" + imagesList.get(i));
            row.put(TABLE_LEVELS_FILED_RATING, level_rating);
            row.put(TABLE_LEVELS_FILED_LEVEL_STATUS, level_status);
            row.put(TABLE_LEVELS_FILED_LEVEL_LEVEL_COIN, level_coin);
            row.put(TABLE_LEVELS_FILED_LEVEL_LEVEL_HIGH_COIN, level_high_coin);
            row.put(TABLE_LEVELS_FILED_LEVEL_LEVEL_MODE, level_mode);
            row.put(TABLE_LEVELS_FILED_UPDATE_AT, updatedData);
            db.insert(TABLE_LEVELS, null, row);
        }
        db.close();
    }

    public void updateLevel(LevelModel levelModel) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }


        ContentValues row = new ContentValues();
        int level_rating = levelModel.getRating();
        int level_status = levelModel.getStatus();      //0 for remaining and 1 for complete
        int level_coin = levelModel.getCoin();
        int level_high_coin = levelModel.getHigh_coin();
        int level_mode = levelModel.getMode();
        String updatedData = Constants.getCurrentDate();

        row.put(TABLE_LEVELS_FILED_RATING, level_rating);
        row.put(TABLE_LEVELS_FILED_LEVEL_STATUS, level_status);
        row.put(TABLE_LEVELS_FILED_LEVEL_LEVEL_COIN, level_coin);
        row.put(TABLE_LEVELS_FILED_LEVEL_LEVEL_HIGH_COIN, level_high_coin);
        row.put(TABLE_LEVELS_FILED_LEVEL_LEVEL_MODE, level_mode);
        row.put(TABLE_LEVELS_FILED_UPDATE_AT, updatedData);
        db.update(TABLE_LEVELS, row, TABLE_LEVELS_FILED_ID + "=" + levelModel.getId() + " AND " + TABLE_LEVELS_FILED_CAT_ID + "=" + levelModel.getCat_id(), null);
        db.close();
    }

    public void addDefaultSettings() {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }


        //Settings For Sounds
        ContentValues row1 = new ContentValues();
        row1.put(TABLE_SETTINGS_FILED_ID,1);
        row1.put(TABLE_SETTINGS_FILED_SETTING_NAME,SETTINGS_NAME[0]);
        row1.put(TABLE_SETTINGS_FILED_SETTING_NAME,0);
        db.insert(TABLE_SETTINGS, null, row1);

        //Setting for game mode
        ContentValues row2 = new ContentValues();
        row2.put(TABLE_SETTINGS_FILED_ID,2);
        row2.put(TABLE_SETTINGS_FILED_SETTING_NAME,SETTINGS_NAME[1]);
        row2.put(TABLE_SETTINGS_FILED_SETTING_NAME,0);
        db.insert(TABLE_SETTINGS, null, row2);

        db.close();


    }

    public void updateSettings(int id,int value) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }


        ContentValues row = new ContentValues();
        row.put(TABLE_SETTINGS_FILED_SETTING_VALUE, value);
         db.update(TABLE_SETTINGS, row, TABLE_SETTINGS_FILED_ID + "=" + id, null);
        db.close();
    }

    public boolean isMuteGameSound() {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return false;
        }

        String queryStr = "select * from " + TABLE_SETTINGS + " where " + TABLE_SETTINGS_FILED_ID + "=1";
        Cursor cur = db.rawQuery(queryStr, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int idStr = cur.getInt(cur.getColumnIndex(TABLE_SETTINGS_FILED_SETTING_VALUE));
            return idStr==1?true:false;
        }
        cur.close();
        db.close();

        return false;
    }

    public int getGameMode() {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return 0;
        }

        String queryStr = "select * from " + TABLE_SETTINGS + " where " + TABLE_SETTINGS_FILED_ID + "=2";
        Cursor cur = db.rawQuery(queryStr, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int idStr = cur.getInt(cur.getColumnIndex(TABLE_SETTINGS_FILED_SETTING_VALUE));
            return idStr;
        }
        cur.close();
        db.close();

        return 0;
    }

}
