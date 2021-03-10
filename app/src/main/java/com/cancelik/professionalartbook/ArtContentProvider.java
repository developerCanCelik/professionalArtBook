package com.cancelik.professionalartbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {
    /************************Kaydedilecek Dosya PATH**************************/
    //"content://com.cancelik.professionalartbook.ArtContentProvider/arts"
    static final  String PROVIDER_NAME = "com.cancelik.professionalartbook.ArtContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);
    /************************Kaydedilecek Dosya PATH**************************/


    static final String NAME = "name";
    static final String IMAGE = "image";
    private static HashMap<String ,String> ART_PROJECTION_MAP;


    /****************************URI MATCHER ?*****************************/
    //oluşturduğumuz uri leri kontrol etmemize yarar.
    //Genellikle birden fazla tablolar varsa onu kullanıyoruz. Asıl amacı gelen
            //bilgiler ile tabloları eşleştirmek
    static final int ARTS = 1;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }
    /****************************URI MATCHER ?*****************************/


    /****************************DATABASE**********************************/
    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "Arts";
    static final String ARTS_TABLE_NAME = "arts";
    //Database de versiyonlar ne işe yarıyor. Veri tabanı güncellerken kullanıyoruz
    static final int DATABASE_VERSION = 1;
    //NOT NULL -> tablonun boş bıraklmasına izin verilmiyor
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE " + ARTS_TABLE_NAME
            + "(name TEXT NOT NULL," + "image BLOB NOT NULL);";

    //SQLite verileri alabilmek için bir verilere ait bir uri oluşturacağız
    private static  class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //EĞER BÖYLE BİR TABLO VARSA YOK ET
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ARTS_TABLE_NAME);
            //Ve onCreate çağırıp tekrardan oluşturduk
            onCreate(sqLiteDatabase);
        }
    }
    /****************************DATABASE**********************************/

    @Override
    public boolean onCreate() {
        Context context = getContext();
        //helper ile bağlama işlemini gerçekleştirdik
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        //eğer sqliteDAtabase boş değil ise metghod öyle çalıştır.
        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        //query yapmak için hazırlanmış bir sınıf
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);
        //path ler ile eşleştirme yapıyoruz
        switch (uriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;
            default:

        }
        //name göre sıralama yapıyor
        if (s1 == null || s1.matches("")){
            s1 = NAME;
        }
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,strings,s,strings1,null,null,s1);
        //uri de bir değişiklik olursa, izleyebileceğiz
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowID =sqLiteDatabase.insert(ARTS_TABLE_NAME,"",contentValues);
        if (rowID >0){
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }
        throw new SQLException("Eroor");

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int rowCount = 0;
        switch (uriMatcher.match(uri)){
            case ARTS:
                rowCount = sqLiteDatabase.delete(ARTS_TABLE_NAME,s,strings);
                break;
            default:
                throw  new IllegalArgumentException("Failed Uri");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int rowCount = 0;
        switch (uriMatcher.match(uri)){
            case ARTS:
                rowCount = sqLiteDatabase.update(ARTS_TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw  new IllegalArgumentException("Failed Uri");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowCount;
    }
}
