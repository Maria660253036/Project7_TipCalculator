package dougherty.tipcalculator;

/**
 * Created by 660253036 on 12/6/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class TipDatabaseHandler extends SQLiteOpenHelper {

    // Define database constants
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "tipsdatabase.db";

    // Define tips table constants
    public static final String TABLE_TIPS = "tips";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BILL_DATE = "bill_date";
    public static final String COLUMN_BILL_AMOUNT = "bill_amount";
    public static final String COLUMN_TIP_PERCENT = "tip_percent";

    // Define SQLite database variable
    private SQLiteDatabase database;

    public TipDatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create the database table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE_TABLE " + TABLE_TIPS + " (" +
                COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BILL_DATE + "INTEGER NOT NULL" +
                COLUMN_BILL_AMOUNT + "REAL NOT NULL" +
                COLUMN_TIP_PERCENT + "REAL NOT NULL" + ");";

        db.execSQL(query);

    }

    // Make updates to an existing table in the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Delete the entire table if it exists
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_TIPS);

        // Recreate the table with the new properties
        onCreate(db);

    }

    public TipDatabaseHandler open() throws SQLException {

        database = getWritableDatabase();   // Get reference to the database

        return this;
    }

    // Add new row to the table
    public void addTip(Tip tip) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_BILL_DATE, tip.getDateStringFormatted());
        values.put(COLUMN_BILL_AMOUNT, tip.getBillAmountFormatted());
        values.put(COLUMN_TIP_PERCENT, tip.getTipPercentFormatted());

        open();
        database.insert(TABLE_TIPS, null, values);

        close();
    }

    // Delete a record from the database
    public void deleteTip(int billDate, double billAmount, double tipPercent ) {

        open();

        database.execSQL("DELETE FROM " + TABLE_TIPS + " WHERE " + COLUMN_BILL_DATE + "=\"" + billDate
           +  "\"" + " AND " + COLUMN_BILL_AMOUNT + "=\"" + billAmount + "\"" + " AND " + COLUMN_TIP_PERCENT +
            "=\"" + tipPercent + "\";");
    }

    // Delete all records from the database
    public void deleteAllTips() {

        open();

        database.execSQL("DELETE FROM " + TABLE_TIPS + ";");
    }

    public List<Tip> getTips() {

        List<Tip> tips = new ArrayList<Tip>();

        String selectQuery = "SELECT * FROM " + TABLE_TIPS;

        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                Tip tip = new Tip();

                tip.setId(Integer.parseInt(c.getString(0)));
                tip.setDateMillis(Integer.parseInt(c.getString(1)));
                tip.setBillAmount(Float.parseFloat(c.getString(2)));
                tip.setTipPercent(Float.parseFloat(c.getString(3)));

                tips.add(tip);

            }while(c.moveToNext());

        }
        return tips;
    }

    public Cursor readEntry() {

        String[] allColumns = new String[]{
                COLUMN_ID,
                COLUMN_BILL_DATE,
                COLUMN_BILL_AMOUNT,
                COLUMN_TIP_PERCENT
        };

        Cursor c = database.query(TABLE_TIPS, allColumns, null, null, null, null, null);

        if(c != null) {
            c.moveToFirst();
        }

        return c;
    }
}
