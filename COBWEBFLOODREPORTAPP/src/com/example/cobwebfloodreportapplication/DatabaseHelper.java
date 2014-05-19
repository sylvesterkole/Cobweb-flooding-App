package com.example.cobwebfloodreportapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Database and tables
	private static final String DBNAME = "COBWEB_DB";
	private static final int VERSION = 34;

	// Database: Observation Table
	private static final String OBSTABLE = "Observation";
	private static final String OID = "obsID";
	private static final String LAT = "Latitude";
	private static final String LON = "Longitude";
	private static final String DATE = "DateTaken";
	private static final String TIME = "TimeTaken";
	private static final String FORMAT = "Format";
	private static final String FLASH = "Flash";
	private static final String FSTOP = "FStop";
	private static final String ISO = "ISO";
	private static final String TYPE = "ReportType";
	private static final String FLOWV = "FlowVelocity";
	private static final String DEPTH = "DepthEstimate";
	private static final String NOTE = "Note";
	private static final String POLYGON = "Polygon";

	// Database: User Table
	private static final String USERTABLE = "User";
	private static final String USERID = "UserID";
	private static final String USERNAME = "UserName";
	private static final String USERTYPE = "UserType";
	private static final String RANK = "Rank";
	private static final String EMAIL = "Email";
	private static final String PASSWORD = "UserNmae";

	// Database: Image Table
	private static final String IMAGETABLE = "Image";
	private static final String IMAGEID = "ImageID";
	private static final String FILELOC = "FileLoc";
	private static final String POLYLINE = "Polyline";

	// static final String deviceTable = "Device";

	// Device table
	// static final String imageID="ImageID";
	// static final String deviceId = "DeviceId";
	// static final String make = "Make";
	// static final String model = "model";

	public DatabaseHelper(Context context) {

		super(context, DBNAME, null, VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// System.out.println("creating db");
		createTables(db);
		// InsertDepts(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String dife = "DROP TABLE IF EXISTS ";
		db.execSQL(dife + USERTABLE);
		db.execSQL(dife + IMAGETABLE);
		// db.execSQL(dife + deviceTable);
		// db.execSQL(dife + zoneTable);
		db.execSQL(dife + OBSTABLE);

		onCreate(db);

	}

	private void createTables(SQLiteDatabase db) {

		try {
			db.execSQL("CREATE TABLE " + USERTABLE + " (" + USERID
					+ " TEXT PRIMARY KEY, " + USERNAME + " TEXT, " + USERTYPE
					+ " TEXT , " + RANK + " TEXT, " + EMAIL + " TEXT, "
					+ PASSWORD + " TEXT);");

			db.execSQL("CREATE TABLE " + IMAGETABLE + "(" + IMAGEID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + POLYLINE
					+ "  TEXT, " + FILELOC + " TEXT NOT NULL, " + OID
					+ " INTEGER NOT NULL ,FOREIGN KEY (" + OID
					+ ") REFERENCES " + OBSTABLE + " (" + OID + "));");

			db.execSQL("CREATE TABLE " + OBSTABLE + "(" + TYPE + " TEXT, "
					+ FLOWV + " TEXT, " + DEPTH + " TEXT, " + NOTE + " TEXT, "
					+ OID + " INTEGER PRIMARY KEY," + LAT + " REAL, " + LON
					+ " REAL, " + DATE + " TEXT, " + TIME + " TEXT, " + FORMAT
					+ " TEXT, " + FLASH + " TEXT, " + FSTOP + " TEXT, " + ISO
					+ " TEXT, " + POLYGON + " TEXT, " + USERID
					+ " TEXT NOT NULL);");

		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * db.execSQL("CREATE TABLE " + deviceTable + " (" + deviceId +
		 * " TEXT, " + make + " TEXT, " + model + " TEXT)");
		 */

	}

	// forcing referential integrity by enabling foreign keys

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");

		}
	}

	/*
	 * protected void insertIntoUserTable() { SQLiteDatabase db =
	 * getWritableDatabase(); ContentValues cv = new ContentValues();
	 * 
	 * // cv.put(colDeptID, 1); // cv.put(colDeptName, "Sales"); //
	 * db.insert(deptTable, colDeptID, cv);
	 * 
	 * // cv.put(colDeptID, 2); // cv.put(colDeptName, "IT"); //
	 * db.insert(deptTable, colDeptID, cv);
	 * 
	 * db.close();
	 * 
	 * }
	 */

	public long insertImageTable(String loc, int obsID) {
		ContentValues cv = new ContentValues();
		return insertImage(cv, loc, obsID);

	}

	private Cursor obsSel(String nVal, String[] col) {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(OBSTABLE, col, POLYGON + " IS " + nVal
				+ "NULL", null, null, null, null);
		if(!cursor.moveToFirst())return null;
		

		return cursor;
	}

	private Cursor imgSel(String nVal, String[] col) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(IMAGETABLE, col, POLYLINE + " IS " + nVal
				+ "NULL", null, null, null,
				null);
		if(!cursor.moveToFirst())return null;
		return cursor;
	}

	public Cursor noPolyObs() {

		return obsSel("", new String[] { OID, LAT, LON, DATE, TIME, TYPE,
				DEPTH, FLOWV, NOTE });
	}

	public Cursor polyObs() {

		return obsSel("NOT ", new String[] { OID, LAT, LON, DATE, TIME, TYPE,
				DEPTH, FLOWV, NOTE, POLYGON });
	}

	public Cursor noLineImage() {

		return imgSel("", new String[] { OID,FILELOC });
	}

	public Cursor lineImage() {

		return imgSel("NOT ", new String[] {OID, FILELOC, POLYLINE });
	}

	long insertImage(ContentValues cVal, String loc, int obsID) {
		SQLiteDatabase db = this.getWritableDatabase();
		cVal.put(FILELOC, loc);
		cVal.put(OID, obsID);
		long l = db.insert(IMAGETABLE, null, cVal);
		db.close();
		return l;
	}

	public long insertImagePoly(String loc, int obsID, String line) {

		ContentValues cv = new ContentValues();
		cv.put(POLYLINE, line);
		return insertImage(cv, loc, obsID);

	}

	/*
	 * protected void insertImageMetaTable() {
	 * 
	 * }
	 */

	/*
	 * protected void insertDeviceTable() {
	 * 
	 * }
	 */
	public long insertObs(int nObs, String uID) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cVal = new ContentValues();
		cVal.put(OID, nObs);
		cVal.put(USERID, uID);
		long l = db.insert(OBSTABLE, null, cVal);
		db.close();
		return l;

	}

	public int updateMetaObs(int oid, String depth, String note, String type,
			String date, String velocity, double lat, double lon, String polygon) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cVal = new ContentValues();
		cVal.put(DEPTH, depth);
		cVal.put(NOTE, note);
		cVal.put(TYPE, type);
		cVal.put(DATE, date);
		cVal.put(FLOWV, velocity);
		cVal.put(LAT, lat);
		cVal.put(LON, lon);
		// cVal.put(, value);
		int i = db.update(OBSTABLE, cVal, OID + " ='" + oid + '\'', null);
		db.close();
		return i;

	}

	/*
	 * protected void insertZoneTable() {
	 * 
	 * }
	 * 
	 * protected void insertTextDataTable() {
	 * 
	 * }
	 */

	public long insertUser(String uID) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(USERID, uID);

		long l = db.insert(USERTABLE, null, cv);
		db.close();
		return l;

	}

	/*
	 * public long UpdateUserInfo(String user) { SQLiteDatabase db =
	 * this.getWritableDatabase(); ContentValues cv = new ContentValues(); //
	 * cv.put(colName, emp.getName()); // cv.put(colAge, emp.getAge()); //
	 * cv.put(colDept, emp.getDept()); long l= db.update(userTable, cv, userName
	 * + "=?", new String[] { user }); db.close(); return l; }
	 * 
	 * public void deleteUser(String emp) { SQLiteDatabase db =
	 * this.getWritableDatabase(); db.delete(userTable, userName + "=?", new
	 * String[] { String.valueOf("me") }); db.close(); }
	 */

	/*
	 * String query: The select statement String[] selection args: The arguments
	 * if a WHERE clause is included in the select statement
	 * 
	 * The result of a query is returned in Cursor object. In a select statement
	 * if the primary key column (the id column) of the table has a name other
	 * than _id, then you have to use an alias in the form SELECT [Column Name]
	 * as _id cause the Cursor object always expects that the primary key column
	 * has the name _id or it will throw an exception .
	 */

	/*
	 * public Cursor rawQuery() {
	 * 
	 * SQLiteDatabase db = this.getReadableDatabase(); Cursor cur =
	 * db.rawQuery("SELECT " + imageID + "as _id," + fileLoc + " from " +
	 * imageTable, new String[] {});
	 * 
	 * return cur; }
	 */

	public String selectID() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT " + USERID + " FROM " + USERTABLE
				+ ';', null);

		if (!cur.moveToFirst())
			return null;

		
		String id = cur.getString(0);
		db.close();
		return id;

	}

	/*
	 * The db.query has the following parameters:
	 * 
	 * String Table Name: The name of the table to run the query against String
	 * [ ] columns: The projection of the query, i.e., the columns to retrieve
	 * String WHERE clause: where clause, if none pass null String [ ] selection
	 * args: The parameters of the WHERE clause String Group by: A string
	 * specifying group by clause String Having: A string specifying HAVING
	 * clause String Order By by: A string Order By by clause
	 */

	/*
	 * public Cursor dbQuery(String id) {
	 * 
	 * SQLiteDatabase db = this.getReadableDatabase(); String[] columns = new
	 * String[] { "_id", type, flowVelocity, depth }; Cursor c =
	 * db.query(textDataTable, columns, imageID + "=?", new String[] { id },
	 * null, null, null); return c; }
	 */

	/*
	 * 
	 * 
	 * 
	 * Result sets of queries are returned in Cursor objects. There are some
	 * common methods that you will use with cursors:
	 * 
	 * boolean moveToNext(): moves the cursor by one record in the result set,
	 * returns false if moved past the last row in the result set. boolean
	 * moveToFirst(): moves the cursor to the first row in the result set,
	 * returns false if the result set is empty. boolean moveToPosition(int
	 * position): moves the cursor to a certain row index within the boolean
	 * result set, returns false if the position is un-reachable boolean
	 * moveToPrevious(): moves the cursor to the previous row in the result set,
	 * returns false if the cursor is past the first row. boolean moveToLast():
	 * moves the cursor to the lase row in the result set, returns false if the
	 * result set is empty. There are also some useful methods to check the
	 * position of a cursor: boolean isAfterLast(), isBeforeFirst, isFirst,
	 * isLast and isNull(columnIndex). Also if you have a result set of only one
	 * row and you need to retrieve values of certain columns, you can do it
	 * like this:
	 */

}
