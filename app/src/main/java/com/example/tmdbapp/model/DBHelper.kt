package com.example.tmdbapp.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DBHelper(val context: Context) : SQLiteOpenHelper(context,DBHelper.DATABASE_NAME,null,DBHelper.DATABASE_VERSION) {
    private val TABLE_NAME="Favorites"
    private val COL_ID = "id"
    private val COL_MOVIENAME = "movieName"
    private val COL_MOVIEOVERVIEW = "movieOverView"
    private val COL_RELEASEDATE = "movieReleaseDate"
    private val COL_VOTEAVERAGE = "movieVoteAverage"
    private val COL_MOVIEBACKDROPPATH = "movieBackDropPath"
    companion object {
        private val DATABASE_NAME = "SQLITE_DATABASE"//database adı
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY , $COL_MOVIENAME  VARCHAR(256),$COL_MOVIEOVERVIEW VARCHAR(256)," +
                "$COL_RELEASEDATE VARCHAR(256),$COL_VOTEAVERAGE DOUBLE,$COL_MOVIEBACKDROPPATH  VARCHAR(256) )"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertData(movie:Result){
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID , movie.id)
        contentValues.put(COL_MOVIENAME, movie.original_title)
        contentValues.put(COL_MOVIEOVERVIEW, movie.overview)
        contentValues.put(COL_VOTEAVERAGE, movie.vote_average)
        contentValues.put(COL_RELEASEDATE, movie.release_date)
        contentValues.put(COL_MOVIEBACKDROPPATH, movie.backdrop_path)
        val result = sqliteDB.insert(TABLE_NAME,null,contentValues)

        Toast.makeText(context,if(result != -1L) "Kayıt Başarılı" else "Kayıt yapılamadı.", Toast.LENGTH_SHORT).show()
    }


    fun readData():ArrayList<Result>{
        var movieList = ArrayList<Result>()
        val sqliteDB = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = sqliteDB.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                val movie = Result(null,null,null,null,null,null)
                movie.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                movie.original_title = result.getString(result.getColumnIndex(COL_MOVIENAME))
                movie.overview = result.getString(result.getColumnIndex(COL_MOVIEOVERVIEW))
                movie.release_date = result.getString(result.getColumnIndex(COL_RELEASEDATE))
                movie.backdrop_path = result.getString(result.getColumnIndex(COL_MOVIEBACKDROPPATH))
                movie.vote_average=result.getString(result.getColumnIndex(COL_VOTEAVERAGE)).toDouble()
                movie.isFavorite=true
                movieList.add(movie)
            }while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return movieList
    }

    fun deleteData(id: Int){
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(TABLE_NAME,"id=?", arrayOf(id.toString()));
        Toast.makeText(context,"Favoriden Kaldırıldı", Toast.LENGTH_SHORT).show()

    }

}