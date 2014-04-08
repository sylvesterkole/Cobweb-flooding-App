package com.example.cobwebfloodreportapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper  extends SQLiteOpenHelper {

	//Database and tables
	static final String dbName="COBWEB_DB";
	static final String userTable="User";
	static final String imageTable="Image";
	static final String imageMetaTable="Image_Meta";
	static final String deviceTable="Device";
	static final String zoneTable="Zone";
	static final String textDataTable="Text Data";




	//users table
	static final String userID="UserID";
	static final String userName="UserName";
	static final String userType="UserType";
	static final String rank="Rank";
	static final String email="Email";
	static final String password="UserNmae";


	//Image table
	static final String imageID="ImageID";
	//static final String userID="UserID";
	static final String fileLoc="File Loc";


	//Text Data table
	//static final String imageID="ImageID";
	static final String type="ReportType";
	static final String flowVelocity="Flow Velocity";
	static final String depth="Depth Estimate";
	static final String note="Note";




	//Image Meta table
	//static final String imageID="ImageID";
	static final String lat="Latitude";
	static final String lon="Longitude";
	//static final String deviceId="DeviceId";
	static final String date="Date Taken";
	static final String time="Time Taken";
	static final String format="Format";
	static final String flash="Flash";
	static final String f_stop="F-Stop";
	static final String iso="ISO";



	//Device table
	//static final String imageID="ImageID";
	static final String deviceId="DeviceId";
	static final String make="Make";
	static final String model="model";



	//Zone table

	static final String zoneId="Zone Id";
	static final String zoneName="Zone Name";
	static final String polygon="Polygone";




	public DatabaseHelper(Context context) {

		super(context, dbName, null,33); 

	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db); 
		//InsertDepts(db);  
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+userTable);
		db.execSQL("DROP TABLE IF EXISTS "+imageMetaTable);
		db.execSQL("DROP TABLE IF EXISTS "+imageTable);
		db.execSQL("DROP TABLE IF EXISTS "+deviceTable);
		db.execSQL("DROP TABLE IF EXISTS "+zoneTable);
		db.execSQL("DROP TABLE IF EXISTS "+textDataTable);

		onCreate(db);

	}


	private void createTables(SQLiteDatabase db){

		db.execSQL("CREATE TABLE "+userTable+" ("+userID+ " INTEGER PRIMARY KEY, "+
				userName+ " TEXT, "+
				userType+ " TEXT , "+
				rank+ " TEXT, "+
				email+ " TEXT, "+
				password+ " TEXT)");

		db.execSQL("CREATE TABLE "+imageTable+"("+imageID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				fileLoc+ " TEXT NOT NULL, "+
				userID+" INTEGER NOT NULL ,FOREIGN KEY ("+userID+") REFERENCES "+userTable+" ("+userID+"));");	


		db.execSQL("CREATE TABLE "+imageMetaTable+"("+lat+" TEXT, "+
				lon+ " TEXT, "+
				date+ " TEXT, "+
				time+ " TEXT, "+
				format+ " TEXT, "+
				flash+ " TEXT, "+
				f_stop+ " TEXT, "+
				iso+ " TEXT, "+ 
				imageID+" INTEGER NOT NULL ,FOREIGN KEY ("+imageID+") REFERENCES "+imageTable+" ("+imageID+"));");	



		db.execSQL("CREATE TABLE "+deviceTable+" ("+ deviceId+ " TEXT, "+
				make+ " TEXT, "+
				model+  " TEXT)");


		db.execSQL("CREATE TABLE "+textDataTable+"("+type+" TEXT, "+
				flowVelocity+ " TEXT, "+
				depth+ " TEXT, "+
				note+ " TEXT, "+
				imageID+" INTEGER NOT NULL ,FOREIGN KEY ("+imageID+") REFERENCES "+imageTable+" ("+imageID+")," +
				deviceId+" INTEGER NOT NULL ,FOREIGN KEY ("+deviceId+") REFERENCES "+imageTable+" ("+imageID+"));");	




		db.execSQL("CREATE TABLE "+zoneTable+" ("+ zoneId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
				zoneName+ " TEXT, "+
				polygon+  " TEXT)");



	}

	//forcing referential integrity by enabling foreign keys	


	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	} 


	protected  void insertIntoUserTable(){
		SQLiteDatabase db= getWritableDatabase();
		ContentValues cv=new ContentValues();

		//cv.put(colDeptID, 1);
		//  cv.put(colDeptName, "Sales");
		// db.insert(deptTable, colDeptID, cv);

		//  cv.put(colDeptID, 2);
		//   cv.put(colDeptName, "IT");
		//   db.insert(deptTable, colDeptID, cv);

		db.close();

	}


	protected  void insertImageTable(){


	}

	protected  void insertImageMetaTable(){


	}

	protected  void insertDeviceTable(){


	}

	protected  void insertZoneTable(){


	}

	protected  void insertTextDataTable(){


	}




	public int UpdateUserInfo(String user)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		//cv.put(colName, emp.getName());
		//cv.put(colAge, emp.getAge());
		//cv.put(colDept, emp.getDept());
		return db.update(userTable, cv, userName+"=?", 
				new String []{String.valueOf("me")});   
	}




	public void deleteUser(String emp)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(userTable,userName+"=?", new String [] {String.valueOf("me")});
		db.close();
	}

	/*
 String query: The select statement
 String[] selection args: The arguments if a WHERE clause is included in the select statement

 The result of a query is returned in Cursor object.
In a select statement if the primary key column (the id column) of the table has a name other than _id, 
then you have to use an alias in the form SELECT [Column Name] as _id 
cause the Cursor object always expects that the primary key column 
has the name _id or it will throw an exception .
	 */

	public Cursor rawQuery(){

		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cur=db.rawQuery("SELECT "+imageID+"as _id,"+fileLoc+" from "+imageTable,new String [] {});

		return cur;
	}






	/*
  The db.query has the following parameters:
		
		String Table Name: The name of the table to run the query against
		String [ ] columns: The projection of the query, i.e., the columns to retrieve
		String WHERE clause: where clause, if none pass null
		String [ ] selection args: The parameters of the WHERE clause
		String Group by: A string specifying group by clause
		String Having: A string specifying HAVING clause
		String Order By by: A string Order By by clause
	 */

	public Cursor  dbQuery(String id){

		SQLiteDatabase db=this.getReadableDatabase();
		String [] columns=new String[]{"_id",type,flowVelocity,depth};
		Cursor c=db.query(textDataTable, columns, imageID+"=?", 
				new String[]{id}, null, null, null);
		return c;
	}



	/*
	 * 
	 * 
	 * 
	 * Result sets of queries are returned in Cursor objects. There are some common methods that you will use with cursors:

		boolean moveToNext(): moves the cursor by one record in the result set, 
		returns false if moved past the last row in the result set.
		boolean moveToFirst(): moves the cursor to the first row in the result set, 
		returns false if the result set is empty.
		boolean moveToPosition(int position): moves the cursor to a certain row index within the boolean result set, 
		returns false if the position is un-reachable
		boolean moveToPrevious(): moves the cursor to the previous row in the result set, 
		returns false if the cursor is past the first row.
		boolean moveToLast(): moves the cursor to the lase row in the result set, 
		returns false if the result set is empty.
		There are also some useful methods to check the position of a cursor: 
		boolean isAfterLast(), isBeforeFirst, isFirst, isLast and isNull(columnIndex). 
		Also if you have a result set of only one row and you need to retrieve values of certain columns, you can do it like this:
	 */




}
