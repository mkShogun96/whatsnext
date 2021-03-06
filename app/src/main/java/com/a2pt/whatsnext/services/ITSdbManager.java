package com.a2pt.whatsnext.services;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.a2pt.whatsnext.models.Activity;
import com.a2pt.whatsnext.models.Module;
import com.a2pt.whatsnext.models.Session;
import com.a2pt.whatsnext.models.Teaches;
import com.a2pt.whatsnext.models.User;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2017-09-29.
 */

public class ITSdbManager extends SQLiteOpenHelper{

    // ITS Database Schema
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ITS.db";

    //Creating the User table
    private static final String TABLE_USERS = "users";
    private static final String KEY_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USEREMAIL = "user_email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TYPE_OF_USER = "type_of_user";
    private static final String KEY_COURSEINFO = "course_info";
    private static final String KEY_MODULE_INFO = "module_info";

    //Creating the ACTIVITY Table
    private static final String TABLE_ACTIVITY = "activity";
    private static final String KEY_ACT_ID = "id";
    private static final String KEY_ACT_MOD_ID = "mod_id";
    private static final String KEY_ACT_ACT_TYPE = "act_type";
    private static final String KEY_ACT_TITLE = "title";
    private static final String KEY_ACT_DUE_DATE = "assignment_due_date";
    private static final String KEY_ACT_SUBMISSION_TIME = "assignment_submission_time";
    private static final String KEY_ACT_STATUS = "assignment_status";
    private static final String KEY_ACT_TEST_NAME = "test_name";
    private static final String KEY_ACT_TEST_TIME = "test_time";
    private static final String KEY_ACT_TEST_DATE = "test_date";
    private static final String KEY_ACT_VENUE = "venue";
    private static final String KEY_ACT_LECTURE_START_TIME = "lecture_start_time";
    private static final String KEY_ACT_LECTURE_DAY_OF_WEEK = "lecture_day_of_week";
    private static final String KEY_ACT_LECTURE_DUPLICATE = "duplicate_lecture";
    private static final String KEY_ACT_TYPE_OF_LECTURE = "type_of_lecture";
    private static final String KEY_ACT_START_DATE = "lecture_start_date";
    private static final String KEY_ACT_END_DATE = "lecture_end_date";

    //Creating the Module Table
    private static final String TABLE_MODULE = "modules";
    private static final String KEY_MODULE_MOD_ID = "mod_id";
    private static final String KEY_MODULE_MOD_NAME = "mod_name";

    //Creating the Teaches Table
    private static final String TABLE_TEACHES = "teaches";
    private static final String KEY_TEACHES_SESSION_ID = "session_id";
    private static final String KEY_TEACHES_ID = "user_id"; //This is the same as the userid
    private static final String KEY_TEACHES_MOD_ID = "mod_id";





    public ITSdbManager(Context context) {

        super(context, DATABASE_NAME , null, DATABASE_VERSION); //for default location


    }

    //Creating Tables

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_USEREMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_TYPE_OF_USER + " TEXT,"
                + KEY_COURSEINFO + " TEXT,"
                + KEY_MODULE_INFO + " TEXT"
                + ")";

        String CREATE_MODULE_TABLE = "CREATE TABLE " + TABLE_MODULE + " ("
                + KEY_MODULE_MOD_ID + " TEXT PRIMARY KEY,"
                + KEY_MODULE_MOD_NAME + " TEXT"
                + ")";

        String CREATE_TEACHES_TABLE = "CREATE TABLE " + TABLE_TEACHES + " ("
                + KEY_TEACHES_SESSION_ID + " INTEGER PRIMARY KEY,"
                + KEY_TEACHES_ID + " TEXT,"
                + KEY_TEACHES_MOD_ID + " TEXT, "
                + "FOREIGN KEY(" + KEY_TEACHES_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "), "
                + "FOREIGN KEY (" + KEY_TEACHES_MOD_ID + ") REFERENCES " + TABLE_MODULE + "(" + KEY_MODULE_MOD_ID + "))";


        String CREATE_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_ACTIVITY + " ("
                + KEY_ACT_ID + " INTEGER PRIMARY KEY,"
                + KEY_ACT_MOD_ID + " TEXT ,"
                + KEY_ACT_ACT_TYPE + " TEXT,"
                + KEY_ACT_TITLE + " TEXT,"
                + KEY_ACT_DUE_DATE + " DATE,"
                + KEY_ACT_SUBMISSION_TIME + " DATE,"
                + KEY_ACT_STATUS + " INTEGER,"
                + KEY_ACT_TEST_NAME + " TEXT,"
                + KEY_ACT_TEST_TIME + " TIME,"
                + KEY_ACT_TEST_DATE + " DATE,"
                + KEY_ACT_VENUE + " TEXT,"
                + KEY_ACT_LECTURE_START_TIME + " DATE,"
                + KEY_ACT_LECTURE_DAY_OF_WEEK + " TEXT,"
                + KEY_ACT_LECTURE_DUPLICATE + " INTEGER,"
                + KEY_ACT_TYPE_OF_LECTURE + " TEXT, "
                + KEY_ACT_START_DATE + " DATE, "
                + KEY_ACT_END_DATE + " DATE"
                + ")";


        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_MODULE_TABLE);
        db.execSQL(CREATE_TEACHES_TABLE);
        db.execSQL(CREATE_ACTIVITY_TABLE);
        insertDummyData(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHES);
        onCreate(db);
    }

    public void insertData(User user, SQLiteDatabase db){
        //get data repository in write mode


        ContentValues values = new ContentValues();


        values.put(KEY_ID, user.getUserID());
        values.put(KEY_USERNAME, user.getUserName());
        values.put(KEY_USEREMAIL, user.getUserEmail());
        values.put(KEY_PASSWORD, user.getUserPassword());
        values.put(KEY_TYPE_OF_USER, user.getUserType());
        values.put(KEY_COURSEINFO, user.getCourseInfo());
        values.put(KEY_MODULE_INFO, user.getModuleInfo());

        db.insert(TABLE_USERS, null, values);

    }


    /**
     * Instead have separate insert methods for each activity type.
     * Then there wont be null exceptions
     */
    private void insertLecture(Activity activity, SQLiteDatabase db){


        ContentValues values = new ContentValues();

        values.put(KEY_ACT_MOD_ID, activity.getModID());
        values.put(KEY_ACT_ACT_TYPE, activity.getActType());
        values.put(KEY_ACT_VENUE, activity.getLectureVenue());
        values.put(KEY_ACT_LECTURE_START_TIME, activity.getLecStartTime().toString().substring(0,5));
        values.put(KEY_ACT_LECTURE_DAY_OF_WEEK, activity.getDayOfWeek());
        values.put(KEY_ACT_LECTURE_DUPLICATE, activity.getIsDuplicate());
        values.put(KEY_ACT_TYPE_OF_LECTURE, activity.getTypeOfLecture());
        values.put(KEY_ACT_START_DATE, activity.getStartDate().toString());
        values.put(KEY_ACT_END_DATE, activity.getEndDate().toString());

        db.insert(TABLE_ACTIVITY, null, values);



    }

    public void insertAssignment(Activity activity, SQLiteDatabase db){


        ContentValues values = new ContentValues();

        values.put(KEY_ACT_MOD_ID, activity.getModID());
        values.put(KEY_ACT_ACT_TYPE, activity.getActType());
        values.put(KEY_ACT_TITLE, activity.getAssignmentTitle());
        values.put(KEY_ACT_DUE_DATE, activity.getAssignmentDueDate().toString());
        values.put(KEY_ACT_SUBMISSION_TIME, activity.getAssignmentDueTime().toString().substring(0,5));
        values.put(KEY_ACT_STATUS, activity.getAssignmentStatus());

        db.insert(TABLE_ACTIVITY, null, values);


    }

    public void insertAssignment(Activity activity){
        System.out.println("DEBUG ITSdbManager: INSERT ASSIGNMENT");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        System.out.println(activity.getAssignmentDueDate().toString());

        values.put(KEY_ACT_MOD_ID, activity.getModID());
        values.put(KEY_ACT_ACT_TYPE, activity.getActType());
        values.put(KEY_ACT_TITLE, activity.getAssignmentTitle());
        values.put(KEY_ACT_DUE_DATE, activity.getAssignmentDueDate().toString());
        values.put(KEY_ACT_SUBMISSION_TIME, activity.getAssignmentDueTime().toString().substring(0,5));
        values.put(KEY_ACT_STATUS, activity.getAssignmentStatus());

        db.insert(TABLE_ACTIVITY, null, values);
        db.close();

    }

    public void insertTest(Activity activity, SQLiteDatabase db){


        ContentValues values = new ContentValues();

        values.put(KEY_ACT_MOD_ID, activity.getModID());
        values.put(KEY_ACT_ACT_TYPE, activity.getActType());
        values.put(KEY_ACT_TEST_NAME, activity.getTestDescriiption());
        values.put(KEY_ACT_TEST_DATE, activity.getTestDate().toString());
        values.put(KEY_ACT_TEST_TIME, activity.getTestTime().toString().substring(0,5));
        values.put(KEY_ACT_VENUE, activity.getTestVenue());

        db.insert(TABLE_ACTIVITY, null, values);


    }

    public void insertTest(Activity activity){
        System.out.println("DEBUG ITSdbManager: INSERT ASSIGNMENT");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ACT_MOD_ID, activity.getModID());
        values.put(KEY_ACT_ACT_TYPE, activity.getActType());
        values.put(KEY_ACT_TEST_NAME, activity.getTestDescriiption());
        values.put(KEY_ACT_TEST_DATE, activity.getTestDate().toString());
        values.put(KEY_ACT_TEST_TIME, activity.getTestTime().toString().substring(0,5));
        values.put(KEY_ACT_VENUE, activity.getTestVenue());

        db.insert(TABLE_ACTIVITY, null, values);
        db.close();

    }


    public void insertTeaches(Teaches teaches, SQLiteDatabase db)
    {


        ContentValues values = new ContentValues();

        values.put(KEY_TEACHES_ID, teaches.getUserID());
        values.put(KEY_TEACHES_MOD_ID, teaches.getModID());

        db.insert(TABLE_TEACHES, null, values);

    }

    public void insertModule(Module module, SQLiteDatabase db)
    {


        ContentValues values = new ContentValues();

        values.put(KEY_MODULE_MOD_ID, module.getModID());
        values.put(KEY_MODULE_MOD_NAME, module.getModName());

        db.insert(TABLE_MODULE, null, values);

    }

    public void updateAssignment(Activity assignment, int actID){

        SQLiteDatabase db = this.getWritableDatabase();


        String query = "UPDATE " + TABLE_ACTIVITY + " SET " + KEY_ACT_TITLE + " = '" + assignment.getAssignmentTitle()
                + "' , " + KEY_ACT_DUE_DATE + " = '" + assignment.getAssignmentDueDate() + "' , " + KEY_ACT_SUBMISSION_TIME + " = '" + assignment.getAssignmentDueTime()
                + "' WHERE " + KEY_ACT_ID + " = " + actID;


        db.execSQL(query);
    }

    public void updateTest(Activity test, int actID){

        SQLiteDatabase db = this.getWritableDatabase();


        String query = "UPDATE " + TABLE_ACTIVITY + " SET " + KEY_ACT_VENUE + " = '" + test.getTestVenue()
                + "' , " + KEY_ACT_TEST_DATE + " = '" + test.getTestDate() + "' , " + KEY_ACT_TEST_TIME + " = '" + test.getTestTime().toString().substring(0,5)
                + "' WHERE " + KEY_ACT_ID + " = " + actID;


        db.execSQL(query);
    }


    public User getUser(String username){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,                                                                                            //Table Name - SELECT FROM TABLE
                new String[]{KEY_ID, KEY_USERNAME, KEY_USEREMAIL, KEY_PASSWORD, KEY_TYPE_OF_USER, KEY_COURSEINFO, KEY_MODULE_INFO},      //All the Fields that you watn to capture
                KEY_ID + "=?",                                                                                    //Where Username = ?
                new String[]{ String.valueOf(username)},                                                                // Where ? = String.Value(What you are looking for)
                null, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        User user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

        cursor.close();
        return user;
    }

    public boolean getCompareLoginCredentials(String loginUsername, String loginPassword) {

        System.out.println("CHECKING LOGIN DETAILS");
        SQLiteDatabase db = this.getReadableDatabase();
        boolean validLogin = false;

        //Get all the Usernames and Passwords from the Users Table
        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);


        // loop through data to get and compare login details
        if(cursor != null & cursor.moveToFirst()){ //Go to first entry
            do {
                System.out.println("DB DEBUG: getting username and password from Table");
                String dbUsername = cursor.getString(cursor.getColumnIndex(KEY_ID));
                String dbPassword = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
                System.out.println(dbUsername);
                System.out.println(dbPassword);
                System.out.println("DB DEBUG: dbUsername = " + dbUsername);
                System.out.println("DB DEBUG: dbPassword = " + dbPassword);


                if (dbUsername.equals(loginUsername) && dbPassword.equals(loginPassword)) {
                    validLogin = true;
                    break;
                }


            }while(cursor.moveToNext());
        }


        cursor.close();


        return validLogin;
    }

    public void setupLocalDB(User userToAdd, dbManager localDB, ITSdbManager ITSdb) {



        localDB.insertData(userToAdd);

        String[] moduleInfo = userToAdd.getModules();


        for (String moduleDetail: moduleInfo)
        {
            localDB.addLecture(moduleDetail, ITSdb);
            localDB.addAssignment(moduleDetail, ITSdb);
            localDB.addTest(moduleDetail, ITSdb);
        }

    }

    public void refreshLocalDB(User userToAdd, dbManager localDB, ITSdbManager ITSdb){
        localDB.insertData(userToAdd);

        String[] moduleInfo = userToAdd.getModules();


        for (String moduleDetail: moduleInfo)
        {
            localDB.addAssignment(moduleDetail, ITSdb);
            localDB.addTest(moduleDetail, ITSdb);
        }
    }

    public int getAssignmentID(Activity assignment){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_ACT_ID + " FROM " + TABLE_ACTIVITY + " WHERE " + KEY_ACT_ACT_TYPE + " = '" + assignment.getActType()
                + "' AND " + KEY_ACT_TITLE + " = '" + assignment.getAssignmentTitle() + "' AND " + KEY_ACT_MOD_ID + " = '" + assignment.getModID() + "'";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null & cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ACT_ID));
            return id;
        }

        return assignment.getActID();
    }

    public int getTestID(Activity test){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_ACT_ID + " FROM " + TABLE_ACTIVITY + " WHERE " + KEY_ACT_ACT_TYPE + " = 'test' AND "
                + KEY_ACT_VENUE + " = '" + test.getTestVenue() + "' AND " + KEY_ACT_MOD_ID + " = '" + test.getModID()
                + "' AND " + KEY_ACT_TEST_DATE + " = '" + test.getTestDate().toString() + "' AND " + KEY_ACT_TEST_TIME + " = '" + test.getTestTime().toString().substring(0,5) + "'";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null & cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ACT_ID));
            return id;
        }
        System.out.println("!!!!! COULD NOT FIND TEST ID !!!!!!!");
        return 0;
    }

    public void deleteActivity(int actID){

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_ACTIVITY +" WHERE " + KEY_ACT_ID +" = " + actID;
        db.execSQL(query);

    }

    public List<Activity> getDuplicateLectures(String selectedModule) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Activity> lectures = new ArrayList<>();

        Cursor cursor = null;

        String query = "SELECT * FROM " + TABLE_ACTIVITY +
                " WHERE " + KEY_ACT_ACT_TYPE +" = ? AND " + KEY_ACT_LECTURE_DUPLICATE + " = ? AND " + KEY_ACT_MOD_ID + " = ? ORDER BY " + KEY_ACT_LECTURE_START_TIME + " ASC";

        String[] values = { "lecture", "1", selectedModule};
        cursor = db.rawQuery(query, values);

        if(cursor != null && cursor.moveToFirst()){
            System.out.println("DEBUG Duplicate Lectures is:" + cursor.getCount());
            do{
                int ActID = cursor.getInt(cursor.getColumnIndex(KEY_ACT_ID));
                String id = cursor.getString(cursor.getColumnIndex(KEY_ACT_MOD_ID));
                String typeOfActivity = cursor.getString(cursor.getColumnIndex(KEY_ACT_ACT_TYPE));
                String venue = cursor.getString(cursor.getColumnIndex(KEY_ACT_VENUE));
                String lectureTime = cursor.getString(cursor.getColumnIndex(KEY_ACT_LECTURE_START_TIME));
                String lectureDayOfWeek = cursor.getString(cursor.getColumnIndex(KEY_ACT_LECTURE_DAY_OF_WEEK));
                int duplicate = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACT_LECTURE_DUPLICATE)));
                String lectType = cursor.getString(cursor.getColumnIndex(KEY_ACT_TYPE_OF_LECTURE));
                String startDateString = cursor.getString(cursor.getColumnIndex(KEY_ACT_START_DATE));
                String endDateString = cursor.getString(cursor.getColumnIndex(KEY_ACT_END_DATE));


                //Note that in the Database the time is stores like this - 17:45
                //Note that Local Time takes in (int Hours, int Minutes)_ as parameters

                String[] temptime = lectureTime.split(":");  //Split the Time string into 17 abd 45
                int hours = Integer.parseInt(temptime[0]); //set the hours integer
                int minutes = Integer.parseInt(temptime[1]); //set the minutes integer
                LocalTime startTime = new LocalTime(hours, minutes);

                String[] temdateStart = startDateString.split("-"); //split date up into 2017, 10, 23
                int yearStart = Integer.parseInt(temdateStart[0]);  //save year as 2017
                int monthStart = Integer.parseInt(temdateStart[1]);  //save month as 10
                int dayStart = Integer.parseInt(temdateStart[2]); //save day as 23
                LocalDate startDate = new LocalDate(yearStart, monthStart, dayStart);  //create new local date

                String[] temdateEnd = endDateString.split("-"); //split date up into 2017, 10, 23
                int yearEnd = Integer.parseInt(temdateEnd[0]);  //save year as 2017
                int monthEnd = Integer.parseInt(temdateEnd[1]);  //save month as 10
                int dayEnd = Integer.parseInt(temdateEnd[2]); //save day as 23
                LocalDate endDate = new LocalDate(yearEnd, monthEnd, dayEnd);  //create new local date

                Activity activity = new Activity(id, typeOfActivity, venue, startTime, lectureDayOfWeek, duplicate, lectType, startDate, endDate);
                activity.setActID(ActID);
                lectures.add(activity);

            }while (cursor.moveToNext());

        }

        return lectures;
    }

    public void insertDummyData(SQLiteDatabase db){
        //Gonna use Dummy Variables here but we will have to set up a fake acounts from Textfile or List or something
        //Insert Activities



        User user = new User("s215006941", "Carl Meyer", "s215006941@nmmu.ac.za", "abc123", "student", "BSc Computer Science", "WRAP302,WRL301,MATH214,MATH203,STAT203,WRR301");
        insertData(user, db);
        user = new User("s215144988", "Gerrit Naude", "s215144988@nmmu.ac.za", "def456", "student", "BSc Information Systems","WRAP302,EBM202,WRUI301,WRB302,WRR301");
        insertData(user, db);
        user = new User("s215039882", "Jacques De Bruyn", "ss215144988@nmmu.ac.za", "rock", "student", "BSc Geology","BOT140,GEN211,BOT210,GGL201,GGL114,BOT230,GGL303,GEN312,BOT240,GGL304,GIS211");
        insertData(user, db);
        user = new User("Vogts.Dieter", "Dieter Vogts", "vogts.dieter@mandela.ac.za", "wrap2017", "lecturer", "Computer Science", "WRAP301,WRAP302,WRA301");
        insertData(user, db);
        user = new User("Nel.Janine", "Janine Nel", "nel.janine@mandela.ac.za", "wrr301", "lecturer", "Computer Science", "WRR301, WRI201, WRI202");
        insertData(user, db);
        user = new User("Hugo.Johan", "Johan Hugo", "hugo.johan@mandela.ac.za", "stat", "lecturer", "Statistics", "STAT203");
        insertData(user, db);
        user = new User("Parsons.Christopher", "Christopher Parsons", "parsons.christopher@mandela.ac.za", "algebra", "lecturer", "Mathematics", "MATH203");
        insertData(user, db);
        user = new User("Thelejane.Tanki", "Tanki Thelejane", "thelejane.tanki@mandela.ac.za", "alright", "lecturer", "Mathematics", "MATH214");
        insertData(user, db);
        user = new User("Duplessis.MC", "MC Duplessis", "duplessis.mc@mandela.ac.za", "language", "lecturer", "Computer Science", "WRL301");
        insertData(user, db);
        user = new User("Duplessis.MC", "MC Duplessis", "duplessis.mc@mandela.ac.za", "language", "lecturer", "Computer Science", "WRL301");
        insertData(user, db);
        user = new User("Barnard.Lynette", "Lynette Barnard", "barnard.lynette@mandela.ac.za", "dbui", "lecturer", "Computer Science", "WRUI301");
        insertData(user, db);
        user = new User("Vanderpost.Lida", "Lida Van Der Post", "vanderpost.lida@mandela.ac.za", "wrb", "lecturer", "Computer Science", "WRB302");
        insertData(user, db);
        user = new User("Oosthuizen.Nadine", "Nadine Oosthuizen", "oosthuizen.nadine@mandela.ac.za", "ebm", "lecturer", "Business", "EBM202");
        insertData(user, db);
        user = new User("Campbell.E", "E Campbell", "campbell.e@mandela.ac.za", "bot230", "lecturer", "Botany", "BOT230");
        insertData(user, db);
        user = new User("Brunsdon.G", "G Brunsdon", "brunsdon.g@mandela.ac.za", "GGL303", "lecturer", "Geology", "GGL303");
        insertData(user, db);
        user = new User("Williams.L", "L Williams", "williams.l@mandela.ac.za", "gen312", "lecturer", "Botany", "GEN312");
        insertData(user, db);



        LocalDate firstSemesterStart = new LocalDate(2017, 2,6);
        LocalDate firstSemesterEnd = new LocalDate(2017, 5, 26);
        System.out.println(firstSemesterEnd);
        LocalDate secondSemesterStart = new LocalDate(2017, 6, 17);
        LocalDate secondSemesterEnd = new LocalDate(2017, 10, 27);


        //Insert Activities
        //Monday Lectures
        Activity activity = new Activity("MATH203", "lecture", "35 00 17", new LocalTime(7,45),"monday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRAP302", "lecture", "35 01 01", new LocalTime(9,5), "monday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        //Tuesday Lectures
        activity = new Activity("MATH214", "lecture", "04 00 01", new LocalTime(7,45), "tuesday", 1, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("MATH214", "lecture", "04 00 01", new LocalTime(9,5), "tuesday", 1, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        activity = new Activity("STAT203", "lecture", "04 00 3", new LocalTime(14,5), "tuesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("STAT203","lecture", "07 02 48", new LocalTime(15,30), "tuesday", 0, "P", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("STAT203","lecture", "07 02 48", new LocalTime(16,45), "tuesday", 0, "T", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        //Wednesday Lectures
        activity = new Activity("STAT203", "lecture", "35 00 17", new LocalTime(7,45),"wednesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("MATH203", "lecture", "35 00 18", new LocalTime(9,5),"wednesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRR301", "lecture", "09 02 02", new LocalTime(10,25),"wednesday", 0, "L", firstSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("MATH214", "lecture", "07 02 50", new LocalTime(14,5),"wednesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRAP302", "lecture", "09 02 04", new LocalTime(15,35),"wednesday", 0, "P", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        //Thursday Lectures
        activity = new Activity("MATH214", "lecture", "07 02 50", new LocalTime(7,45),"thursday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRL301", "lecture", "35 00 18", new LocalTime(10,25),"thursday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRL301", "lecture", "35 00 16", new LocalTime(14,5),"thursday", 0, "T", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("STAT203", "lecture", "35 00 18", new LocalTime(16,45),"thursday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        //Friday Lectures
        activity = new Activity("WRMS302", "lecture", "09 02 02", new LocalTime(9,5), "friday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        //Assignments
        activity = new Activity("WRAP302", "assignment", "Assignment 5", new LocalDate(2017,9,12), new LocalTime(23,35), 0);
        insertAssignment(activity, db);
        activity = new Activity("STAT203", "assignment", "Prac 2", new LocalDate(2017,9,12), new LocalTime(14,5), 0);
        insertAssignment(activity, db);
        activity = new Activity("WRR301", "assignment", "Final", new LocalDate(2017,10,23), new LocalTime(12,0), 0);
        insertAssignment(activity, db);
        activity = new Activity("WRAP302", "assignment", "Mark Assignment 6", new LocalDate(2017,10,9), new LocalTime(23,0), 0);
        insertAssignment(activity, db);
        activity = new Activity("WRAP301", "assignment", "Assignment 01", new LocalDate(2017,3,16), new LocalTime(23,0), 0);
        insertAssignment(activity, db);
        activity = new Activity("WRA301", "assignment", "Research Paper", new LocalDate(2017,4,23), new LocalTime(23,0), 0);
        insertAssignment(activity, db);
        //Tests
        activity = new Activity("STAT203", "test", "Tut Test 2",new LocalDate(2017,10,24),new LocalTime(18,0), "07 02 48");
        insertTest(activity, db);
        activity = new Activity("MATH214", "test", "Seme Test 2",new LocalDate(2017,9,14),new LocalTime(18,0), "Heinz Benz Hall");
        insertTest(activity, db);
        activity = new Activity("WRL301", "test", "Seme Test 2",new LocalDate(2017,10,4),new LocalTime(18,0), "35 00 17");
        insertTest(activity, db);
        activity = new Activity("WRAP302", "test", "Seme Test 2",new LocalDate(2017,10,6),new LocalTime(14,0), "09 02 04");
        insertTest(activity, db);
        activity = new Activity("STAT203", "test", "Seme Test 2",new LocalDate(2017,10,10),new LocalTime(18,0), "07 02 48");
        insertTest(activity, db);
        activity = new Activity("MATH203", "test", "Seme Test 2",new LocalDate(2017,10,12),new LocalTime(18,0), "Indoor Sport Centre");
        //Carl Exam
        insertTest(activity, db);
        activity = new Activity("STAT203", "test", "EXAM",new LocalDate(2017,11,3),new LocalTime(9,0), "07 02 48");
        insertTest(activity, db);
        activity = new Activity("MATH203", "test", "EXAM",new LocalDate(2017,11,6),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);
        activity = new Activity("WRL301", "test", "EXAM",new LocalDate(2017,11,7),new LocalTime(9,0), "35 00 01");
        insertTest(activity, db);
        activity = new Activity("WRAP302", "test", "EXAM",new LocalDate(2017,11,11),new LocalTime(14,0), "09 02 05");
        insertTest(activity, db);
        activity = new Activity("MATH214", "test", "EXAM",new LocalDate(2017,11,20),new LocalTime(14,0), "261 02 33");
        insertTest(activity, db);
        //jacques Exam
        activity = new Activity("BOT230", "test", "EXAM",new LocalDate(2017,11,2),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);
        activity = new Activity("GGL303", "test", "EXAM",new LocalDate(2017,11,4),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);
        activity = new Activity("GEN312", "test", "EXAM",new LocalDate(2017,11,8),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);
        activity = new Activity("BOT240", "test", "EXAM",new LocalDate(2017,11,10),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);
        activity = new Activity("GGL304", "test", "EXAM",new LocalDate(2017,11,13),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);
        activity = new Activity("GIS211", "test", "EXAM",new LocalDate(2017,11,15),new LocalTime(9,0), "123 00 02");
        insertTest(activity, db);

        //TESTING to see if IT WORKS WITH LECTURES OF FIRST SEMESTER IN ITS
        activity = new Activity("MATH211", "lecture", "35 00 17", new LocalTime(9,5),"monday", 0, "L", firstSemesterStart, firstSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRAP301", "lecture", "35 01 01", new LocalTime(10,25), "monday", 0, "L", firstSemesterStart, firstSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRR301", "lecture", "35 01 01", new LocalTime(9,5), "friday", 0, "L", firstSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Insert Jacques Time Table Data
        //Monday Lectures
        activity = new Activity("BOT140", "lecture", "127 00 02", new LocalTime(7,45),"monday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("GEN211", "lecture", "127 00 02", new LocalTime(14,5), "monday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("BOT210", "lecture", "127 00 17", new LocalTime(16,45), "monday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Tuesday Lectures
        activity = new Activity("GGL201", "lecture", "13 03 61", new LocalTime(7,45), "tuesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("BOT210", "lecture", "127 00 17", new LocalTime(9,5), "tuesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("GGL114", "lecture", "13 03 69", new LocalTime(14,5), "tuesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("BOT210","lecture", "12 01 05", new LocalTime(15,25), "tuesday", 0, "P", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Wednesday Lectures
        activity = new Activity("BOT210", "lecture", "35 00 17", new LocalTime(9,5),"wednesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("GGL201", "lecture", "13 03 61", new LocalTime(14,5),"wednesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Thursday Lectures
        activity = new Activity("GGL201", "lecture", "13 03 61", new LocalTime(7,45),"thursday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("GEN211", "lecture", "127 00 02", new LocalTime(9,5),"thursday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("GGL201", "lecture", "13 03 69", new LocalTime(15,25),"thursday", 0, "P", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Friday Lectures
        activity = new Activity("GGL201", "lecture", "13 03 69", new LocalTime(10,25), "friday", 0, "P", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("GEN211", "lecture", "12 01 05", new LocalTime(15,25), "friday", 0, "P", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Insert Gerrit TIme Table Data

        //Tuesday Lectures
        activity = new Activity("EBM202", "lecture", "123 00 26", new LocalTime(14,5), "tuesday", 1, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);


        //Wednesday Lectures
        activity = new Activity("EBM202", "lecture", "35 00 40", new LocalTime(9,5),"wednesday", 1, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRB302", "lecture", "09 02 40", new LocalTime(9,5),"wednesday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("WRR301", "lecture", "09 01 01", new LocalTime(10,25),"wednesday", 0, "P", firstSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);


        //Thursday Lectures
        activity = new Activity("WRUI301", "lecture", "09 02 06", new LocalTime(7,45),"thursday", 0, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);

        //Friday Lectures
        activity = new Activity("EBM202", "lecture", "35 00 05", new LocalTime(9,5), "friday", 1, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);
        activity = new Activity("EBM202", "lecture", "35 00 40", new LocalTime(14,5), "friday", 1, "L", secondSemesterStart, secondSemesterEnd);
        insertLecture(activity, db);


        Module module = new Module("WRAP302", "Advanced Programming 2");
        insertModule(module, db);
        module = new Module("WRL301", "Language And Automata Theory");
        insertModule(module, db);
        module = new Module("MATH214", "Real Analysis");
        insertModule(module, db);
        module = new Module("MATH203", "Linear Algebra");
        insertModule(module, db);
        module = new Module("STAT203", "Regression Analysis");
        insertModule(module, db);
        module = new Module("WRR301", "Year Project");
        insertModule(module, db);
        module = new Module("EBM202", "Supply Management & Logistics");
        insertModule(module, db);
        module = new Module("WRB302", "Business Management");
        insertModule(module, db);
        module = new Module("BOT140", "Introduction to Botany");
        insertModule(module, db);
        module = new Module("GEN211", "Genetics 2");
        insertModule(module, db);
        module = new Module("BOT210", "Botany 2");
        insertModule(module, db);
        module = new Module("GGL201", "Geography 2");
        insertModule(module, db);
        module = new Module("GGL144", "Industrial Geology");
        insertModule(module, db);
        module = new Module("GGL303", "Advanced Geology 3");
        insertModule(module, db);
        module = new Module("GEN312", "Human Genome Analysis");
        insertModule(module, db);
        module = new Module("BOT240", "Photosynthesis Analysis");
        insertModule(module, db);
        module = new Module("GGL304", "Vulcanic Reactions");
        insertModule(module, db);
        module = new Module("GIS211", "Information Geology");
        insertModule(module, db);

        Teaches teaches = new Teaches("Vogts.Dieter", "WRAP302");
        insertTeaches(teaches, db);
        teaches = new Teaches("Vogts.Dieter", "WRA301");
        insertTeaches(teaches, db);
        teaches = new Teaches("Nel.Janine", "WRI202");
        insertTeaches(teaches, db);
        teaches = new Teaches("Nel.Janine", "WRI201");
        insertTeaches(teaches, db);
        teaches = new Teaches("Nel.Janine", "WRR301");
        insertTeaches(teaches, db);
        teaches = new Teaches("Hugo.Johan", "STAT203");
        insertTeaches(teaches, db);
        teaches = new Teaches("Parsons.Christopher", "MATH203");
        insertTeaches(teaches, db);
        teaches = new Teaches("Thelejane.Tanki", "MATH214");
        insertTeaches(teaches, db);
        teaches = new Teaches("Duplessis.MC", "WRL301");
        insertTeaches(teaches, db);
        teaches = new Teaches("Barnard.Lynette", "WRUI301");
        insertTeaches(teaches, db);
        teaches = new Teaches("Vanderpost.Lida", "WRB302");
        insertTeaches(teaches, db);
        teaches = new Teaches("Oosthuizen.Nadine", "EBM202");
        insertTeaches(teaches, db);
        teaches = new Teaches("E Campbell", "BOT230");
        insertTeaches(teaches, db);
        teaches = new Teaches("Brunsdon.G", "GGL303");
        insertTeaches(teaches, db);
        teaches = new Teaches("Williams.L", "GEN312");
        insertTeaches(teaches, db);




    }


}
