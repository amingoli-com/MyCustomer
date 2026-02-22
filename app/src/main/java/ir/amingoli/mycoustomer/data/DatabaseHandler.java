package ir.amingoli.mycoustomer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.OrderDetail;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.model.Transaction;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "amingoli78888-db";
    private SQLiteDatabase db;
    private Context context;

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    public static final String DATABASE_NAME = "markeet.db";

    // Table Name
    private static final String TABLE_CUSTOMER = "customer";
    private static final String TABLE_ORDER_DETAIL = "order_detail";
    private static final String TABLE_ORDER = "table_order";
    private static final String TABLE_PRODUCT = "product";
    private static final String TABLE_TRANSACTION = "table_transaction";

    // Table Columns names TABLE_CUSTOMER
    private static final String COL_CUSTOMER_ID = "COL_CUSTOMER_ID";
    private static final String COL_CUSTOMER_NAME = "COL_CUSTOMER_NAME";
    private static final String COL_CUSTOMER_TEL = "COL_CUSTOMER_TEL";
    private static final String COL_CUSTOMER_DESC = "COL_CUSTOMER_DESC";
    private static final String COL_CUSTOMER_CREATED_AT = "COL_CUSTOMER_CREATED_AT";

    // Table Columns names TABLE_ORDER_DETAIL
    private static final String COL_ORDER_DETAIL_ID = "COL_ORDER_DETAIL_ID";
    private static final String COL_ORDER_DETAIL_ID_PRODUCT = "COL_ORDER_DETAIL_ID_PRODUCT";
    private static final String COL_ORDER_DETAIL_ID_ORDER_DETAIL = "COL_ORDER_DETAIL_ID_ORDER_DETAIL";
    private static final String COL_ORDER_DETAIL_NAME = "COL_ORDER_DETAIL_NAME";
    private static final String COL_ORDER_DETAIL_AMOUNT = "COL_ORDER_DETAIL_AMOUNT";
    private static final String COL_ORDER_DETAIL_PRICE_ITEM = "COL_ORDER_DETAIL_PRICE_ITEM";
    private static final String COL_ORDER_DETAIL_PRICE_ALL = "COL_ORDER_DETAIL_PRICE_ALL";
    private static final String COL_ORDER_DETAIL_CREATED_AT = "COL_WISH_CREATED_AT";

    // Table Columns names TABLE_ORDER
    private static final String COL_ORDER_ID = "COL_ORDER_ID";
    private static final String COL_ORDER_CODE = "COL_ORDER_CODE";
    private static final String COL_ORDER_ID_CUSTOMER = "COL_ORDER_ID_CUSTOMER";
    private static final String COL_ORDER_ID_ORDER_DETAIL = "COL_ORDER_ID_ORDER_DETAIL";
    private static final String COL_ORDER_PRICE = "COL_ORDER_PRICE";
    private static final String COL_ORDER_PRICE_DISCOUNT = "COL_ORDER_PRICE_DISCOUNT";
    private static final String COL_ORDER_PRICE_DEBIT = "COL_ORDER_PRICE_DEBIT";
    private static final String COL_ORDER_DESC = "COL_ORDER_DESC";
    private static final String COL_ORDER_CREATED_AT = "COL_ORDER_CREATED_AT";
    private static final String COL_ORDER_STATUS = "COL_ORDER_STATUS";

    // Table Columns names TABLE_PRODUCT
    private static final String COL_PRODUCT_ID = "COL_PRODUCT_ID";
    private static final String COL_PRODUCT_NAME = "COL_PRODUCT_NAME";
    private static final String COL_PRODUCT_PRICE = "COL_PRODUCT_PRICE";
    private static final String COL_PRODUCT_AMOUNT = "COL_PRODUCT_AMOUNT";
    private static final String COL_PRODUCT_CREATED_AT = "COL_PRODUCT_CREATED_AT";

    // Table Columns names table_transaction
    private static final String COL_TRANSACTION_ID = "COL_TRANSACTION_ID";
    private static final String COL_TRANSACTION_ID_CUSTOMER = "COL_TRANSACTION_ID_CUSTOMER";
    private static final String COL_TRANSACTION_ID_ORDER = "COL_TRANSACTION_ID_ORDER";
    private static final String COL_TRANSACTION_TYPE = "COL_TRANSACTION_TYPE";
    private static final String COL_TRANSACTION_AMOUNT = "COL_TRANSACTION_AMOUNT";
    private static final String COL_TRANSACTION_DESC = "COL_TRANSACTION_DESC";
    private static final String COL_TRANSACTION_CREATED_AT = "COL_TRANSACTION_CREATED_AT";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.db = getWritableDatabase();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase d) {
        createTableCustomer(d);
        createTableOrderDetail(d);
        createTableOrder(d);
        createTableCart(d);
        createTableTransaction(d);
    }

    private void createTableCustomer(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + " ("
                + COL_CUSTOMER_ID + " INTEGER PRIMARY KEY, "
                + COL_CUSTOMER_NAME + " TEXT, "
                + COL_CUSTOMER_TEL + " TEXT, "
                + COL_CUSTOMER_DESC + " TEXT, "
                + COL_CUSTOMER_CREATED_AT + " NUMERIC "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableOrderDetail(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ORDER_DETAIL + " ("
                + COL_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY, "
                + COL_ORDER_DETAIL_ID_PRODUCT + " INTEGER, "
                + COL_ORDER_DETAIL_ID_ORDER_DETAIL + " INTEGER, "
                + COL_ORDER_DETAIL_NAME + " TEXT, "
                + COL_ORDER_DETAIL_AMOUNT + " NUMERIC, "
                + COL_ORDER_DETAIL_PRICE_ITEM + " NUMERIC, "
                + COL_ORDER_DETAIL_PRICE_ALL + " NUMERIC, "
                + COL_ORDER_DETAIL_CREATED_AT + " NUMERIC "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableOrder(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ORDER + " ("
                + COL_ORDER_ID + " INTEGER PRIMARY KEY, "
                + COL_ORDER_CODE + " TEXT, "
                + COL_ORDER_ID_CUSTOMER + " INTEGER, "
                + COL_ORDER_ID_ORDER_DETAIL + " INTEGER, "
                + COL_ORDER_PRICE + " NUMERIC, "
                + COL_ORDER_DESC + " TEXT, "
                + COL_ORDER_CREATED_AT + " NUMERIC, "
                + COL_ORDER_STATUS + " INTEGER DEFAULT 0 "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void updateTableOrder(SQLiteDatabase db){
        String UPDATE_TABLE = "";
    }

    private void createTableCart(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUCT + " ("
                + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRODUCT_NAME + " TEXT, "
                + COL_PRODUCT_PRICE + " NUMERIC, "
                + COL_PRODUCT_AMOUNT + " INTEGER, "
                + COL_PRODUCT_CREATED_AT  + " NUMERIC "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableTransaction(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + " ("
                + COL_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TRANSACTION_ID_CUSTOMER + " INTEGER, "
                + COL_TRANSACTION_ID_ORDER + " INTEGER, "
                + COL_TRANSACTION_TYPE + " INTEGER, "
                + COL_TRANSACTION_AMOUNT + " NUMERIC, "
                + COL_TRANSACTION_DESC + " TEXT, "
                + COL_TRANSACTION_CREATED_AT  + " NUMERIC "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB ", "onUpgrade " + oldVersion + " to " + newVersion);
        if (oldVersion == 1 && newVersion == 2){
            db.execSQL("ALTER TABLE "+TABLE_ORDER+" ADD COLUMN "+COL_ORDER_STATUS+" TEXT DEFAULT ''");
        }
        if (oldVersion == 2 && newVersion == 3){
            createTableTransaction(db);
        }
    }

    public void truncateDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void saveCustomer(Customer customer) {
        ContentValues values = getCustomerValue(customer);
        // Inserting or Update Row
        db.insertWithOnConflict(TABLE_CUSTOMER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveOrderDetail(OrderDetail orderDetail) {
        ContentValues values = getOrderDetailValue(orderDetail);
        // Inserting or Update Row
        db.insertWithOnConflict(TABLE_ORDER_DETAIL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveOrder(Order order) {
        ContentValues values = getOrderValue(order);
        // Inserting or Update Row
        db.insertWithOnConflict(TABLE_ORDER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveProduct(Product product) {
        ContentValues values = getProductValue(product);
        // Inserting or Update Row
        db.insertWithOnConflict(TABLE_PRODUCT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveTransaction(Transaction transaction) {
        ContentValues values = getTransactionValue(transaction);
        // Inserting or Update Row
        db.insertWithOnConflict(TABLE_TRANSACTION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void updateStatusOrder(String code, String status) {
        ContentValues newValues = new ContentValues();
        newValues.put(COL_ORDER_STATUS, status);
        String[] args = new String[]{code};
        db.update(TABLE_ORDER, newValues, COL_ORDER_CODE + "=?", args);
    }

    private ContentValues getCustomerValue(Customer model) {
        ContentValues values = new ContentValues();
        values.put(COL_CUSTOMER_ID, model.id);
        values.put(COL_CUSTOMER_NAME, model.name);
        values.put(COL_CUSTOMER_TEL, model.tel);
        values.put(COL_CUSTOMER_DESC, model.desc);
        values.put(COL_CUSTOMER_CREATED_AT, model.created_at);
        return values;
    }

    private ContentValues getOrderDetailValue(OrderDetail model) {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_DETAIL_ID, model.id);
        values.put(COL_ORDER_DETAIL_ID_PRODUCT, model.id_product);
        values.put(COL_ORDER_DETAIL_ID_ORDER_DETAIL, model.id_order_detail);
        values.put(COL_ORDER_DETAIL_NAME, model.name);
        values.put(COL_ORDER_DETAIL_AMOUNT, model.amount);
        values.put(COL_ORDER_DETAIL_PRICE_ITEM, model.price_item);
        values.put(COL_ORDER_DETAIL_PRICE_ALL, model.price_all);
        values.put(COL_ORDER_DETAIL_CREATED_AT, model.created_at);
        return values;
    }

    private ContentValues getOrderValue(Order model) {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_ID, model.id);
        values.put(COL_ORDER_CODE, model.code);
        values.put(COL_ORDER_ID_CUSTOMER, model.id_coustomer);
        values.put(COL_ORDER_ID_ORDER_DETAIL, model.id_order_detail);
        values.put(COL_ORDER_PRICE, model.price);
        values.put(COL_ORDER_DESC, model.desc);
        values.put(COL_ORDER_CREATED_AT, model.created_at);
        values.put(COL_ORDER_STATUS, model.status ? 1 : 0 );
        return values;
    }

    private ContentValues getProductValue(Product model) {
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_ID, model.id);
        values.put(COL_PRODUCT_NAME, model.name);
        values.put(COL_PRODUCT_PRICE, model.price);
        values.put(COL_PRODUCT_AMOUNT, model.amount);
        values.put(COL_PRODUCT_CREATED_AT, model.created_at);
        return values;
    }

    private ContentValues getTransactionValue(Transaction model) {
        ContentValues values = new ContentValues();
        values.put(COL_TRANSACTION_ID, model.id);
        values.put(COL_TRANSACTION_ID_CUSTOMER, model.id_customer);
        values.put(COL_TRANSACTION_ID_ORDER, model.id_order);
        values.put(COL_TRANSACTION_TYPE, model.type);
        values.put(COL_TRANSACTION_AMOUNT, model.amount);
        values.put(COL_TRANSACTION_DESC, model.desc);
        values.put(COL_TRANSACTION_CREATED_AT, model.created_at);
        return values;
    }

    public Customer getCustomer(long id) {
        Customer obj = new Customer();
        String query = "SELECT * FROM " + TABLE_CUSTOMER + " n WHERE n." + COL_CUSTOMER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getCustomerByCursor(cursor);
        }
        return obj;
    }

    public OrderDetail getOrderDetail(long id) {
        OrderDetail obj = null;
        String query = "SELECT * FROM " + TABLE_ORDER_DETAIL + " w WHERE w." + COL_ORDER_DETAIL_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getOrderDetailByCursor(cursor);
        }
        return obj;
    }

    public Order getOrder(long id) {
        Order obj = null;
        String query = "SELECT * FROM " + TABLE_ORDER + " o WHERE o." + COL_ORDER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getOrderByCursor(cursor);
        }
        return obj;
    }

    public Order getOrderByOrderCode(long orderCode) {
        Order obj = null;
        String query = "SELECT * FROM " + TABLE_ORDER + " o WHERE o." + COL_ORDER_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{orderCode + ""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getOrderByCursor(cursor);
        }
        return obj;
    }

    public Product getProduct(long id) {
        Product obj = null;
        String query = "SELECT * FROM " + TABLE_PRODUCT + " o WHERE o." + COL_PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getProductByCursor(cursor);
        }
        return obj;
    }

    private Customer getCustomerByCursor(Cursor cur) {
        Customer obj = new Customer();
        obj.id = cur.getLong(cur.getColumnIndex(COL_CUSTOMER_ID));
        obj.name = cur.getString(cur.getColumnIndex(COL_CUSTOMER_NAME));
        obj.tel = cur.getString(cur.getColumnIndex(COL_CUSTOMER_TEL));
        obj.desc = cur.getString(cur.getColumnIndex(COL_CUSTOMER_DESC));
        obj.created_at = cur.getLong(cur.getColumnIndex(COL_CUSTOMER_CREATED_AT));
        return obj;
    }

    private OrderDetail getOrderDetailByCursor(Cursor cur) {
        OrderDetail obj = new OrderDetail();
        obj.id = cur.getLong(cur.getColumnIndex(COL_ORDER_DETAIL_ID));
        obj.id_product = cur.getLong(cur.getColumnIndex(COL_ORDER_DETAIL_ID_PRODUCT));
        obj.id_order_detail = cur.getLong(cur.getColumnIndex(COL_ORDER_DETAIL_ID_ORDER_DETAIL));
        obj.name = cur.getString(cur.getColumnIndex(COL_ORDER_DETAIL_NAME));
        obj.amount = cur.getDouble(cur.getColumnIndex(COL_ORDER_DETAIL_AMOUNT));
        obj.price_item = cur.getDouble(cur.getColumnIndex(COL_ORDER_DETAIL_PRICE_ITEM));
        obj.price_all = cur.getDouble(cur.getColumnIndex(COL_ORDER_DETAIL_PRICE_ALL));
        obj.created_at = cur.getLong(cur.getColumnIndex(COL_ORDER_DETAIL_CREATED_AT));
        return obj;
    }

    private Order getOrderByCursor(Cursor cur) {
        Order obj = new Order();
        obj.id = cur.getLong(cur.getColumnIndex(COL_ORDER_ID));
        obj.code = cur.getString(cur.getColumnIndex(COL_ORDER_CODE));
        obj.id_coustomer = cur.getLong(cur.getColumnIndex(COL_ORDER_ID_CUSTOMER));
        obj.id_order_detail = cur.getLong(cur.getColumnIndex(COL_ORDER_ID_ORDER_DETAIL));
        obj.price = cur.getDouble(cur.getColumnIndex(COL_ORDER_PRICE));
        obj.desc = cur.getString(cur.getColumnIndex(COL_ORDER_DESC));
        obj.created_at = cur.getLong(cur.getColumnIndex(COL_ORDER_CREATED_AT));
        obj.status = cur.getInt(cur.getColumnIndex(COL_ORDER_STATUS)) == 1;
        return obj;
    }

    private Product getProductByCursor(Cursor cur) {
        Product obj = new Product();
        obj.id = cur.getLong(cur.getColumnIndex(COL_PRODUCT_ID));
        obj.name = cur.getString(cur.getColumnIndex(COL_PRODUCT_NAME));
        obj.price = cur.getDouble(cur.getColumnIndex(COL_PRODUCT_PRICE));
        obj.amount = cur.getDouble(cur.getColumnIndex(COL_PRODUCT_AMOUNT));
        obj.created_at = cur.getLong(cur.getColumnIndex(COL_PRODUCT_CREATED_AT));
        return obj;
    }

    private Transaction getTransactionByCursor(Cursor cur) {
        Transaction obj = new Transaction();
        obj.id = cur.getLong(cur.getColumnIndex(COL_TRANSACTION_ID));
        obj.id_customer = cur.getLong(cur.getColumnIndex(COL_TRANSACTION_ID_CUSTOMER));
        obj.id_order = cur.getLong(cur.getColumnIndex(COL_TRANSACTION_ID_ORDER));
        obj.type = cur.getLong(cur.getColumnIndex(COL_TRANSACTION_TYPE));
        obj.amount = cur.getDouble(cur.getColumnIndex(COL_TRANSACTION_AMOUNT));
        obj.desc = cur.getString(cur.getColumnIndex(COL_TRANSACTION_DESC));
        obj.created_at = cur.getLong(cur.getColumnIndex(COL_TRANSACTION_CREATED_AT));
        return obj;
    }

    //    GET CUSTOMER
    public List<Customer> getCustomerByPage(int limit, int offset) {
        List<Customer> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM " + TABLE_CUSTOMER + " n ORDER BY n." + COL_CUSTOMER_ID + " DESC LIMIT " + limit + " OFFSET " + offset + " ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListCustomerByCursor(cursor);
        }
        return items;
    }

    public List<Customer> getCustomerById(Long id) {
        List<Customer> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_CUSTOMER + " DESC where " + COL_CUSTOMER_ID + " = '"+id+"'");
        Cursor cursor = db.rawQuery(sb.toString(),null);
        if (cursor.moveToFirst()) {
            items = getListCustomerByCursor(cursor);
        }
        return items;
    }

    public List<Customer> getCustomerByName(String keyword) {
        List<Customer> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_CUSTOMER + " DESC where " + COL_CUSTOMER_NAME + " like ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[] { "%" + keyword + "%" });
        if (cursor.moveToFirst()) {
            items = getListCustomerByCursor(cursor);
        }
        return items;
    }

    public List<Customer> getCustomerByTel(String keyword) {
        List<Customer> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_CUSTOMER + " DESC where " + COL_CUSTOMER_TEL + " like ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[] { "%" + keyword + "%" });
        if (cursor.moveToFirst()) {
            items = getListCustomerByCursor(cursor);
        }
        return items;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CUSTOMER + " ORDER BY " + COL_CUSTOMER_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            items = getListCustomerByCursor(cursor);
        }
        return items;
    }

    //    GET ORDER DETAIL
    public List<OrderDetail> getOrderDetailListById(Long id) {
        List<OrderDetail> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_ORDER_DETAIL + " DESC where " + COL_ORDER_DETAIL_ID_ORDER_DETAIL + " = ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{id + ""});
        if (cursor.moveToFirst()) {
            items = getListOrderDetailByCursor(cursor);
        }
        return items;
    }

    public List<OrderDetail> getOrderDetailListByOrderCode(Long orderCode) {
        List<OrderDetail> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_ORDER_DETAIL + " DESC where " + COL_ORDER_DETAIL_ID_ORDER_DETAIL + " = ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{orderCode + ""});
        if (cursor.moveToFirst()) {
            items = getListOrderDetailByCursor(cursor);
        }
        return items;
    }

    public List<OrderDetail> getOrderDetailByPage(int limit, int offset) {
        List<OrderDetail> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM " + TABLE_ORDER_DETAIL + " w ORDER BY w." + COL_ORDER_DETAIL_ID + " DESC LIMIT " + limit + " OFFSET " + offset + " ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListOrderDetailByCursor(cursor);
        }
        return items;
    }

    //    GET ORDER
    public List<Order> getOrderList(boolean isPied, long mils) {
        int status = isPied ? 1 : 0;
        List<Order> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM " + TABLE_ORDER
                + " WHERE " + COL_ORDER_CREATED_AT + " >= "+ mils
                +" AND " + COL_ORDER_STATUS + " IN ("+status+") "
                +" ORDER BY " + COL_ORDER_CREATED_AT + " DESC ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListOrderByCursor(cursor);
        }
        return items;
    }

    public List<Order> getOrderList(boolean isPied) {
        int status = isPied ? 1 : 0;
        List<Order> items = new ArrayList<>();
        Cursor cursor = db.query(TABLE_ORDER,
                new String[] {},
                COL_ORDER_STATUS + " IN ("+status+")",
                null, null, null, COL_ORDER_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            items = getListOrderByCursor(cursor);
        }
        return items;
    }

    public List<Order> getOrderList(long idCustomer, boolean isPied, long mils) {
        int status = isPied ? 1 : 0;
        List<Order> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM " + TABLE_ORDER
                + " WHERE " + COL_ORDER_CREATED_AT + " >= "+ mils
                +" AND " + COL_ORDER_STATUS + " IN ("+status+") "
                +" AND " + COL_CUSTOMER_ID + " IN (" +idCustomer+") "
                +" ORDER BY " + COL_ORDER_CREATED_AT + " DESC ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListOrderByCursor(cursor);
        }
        return items;
    }

    public List<Order> getOrderListByCustomerId(long idCustomer, boolean isPied) {
        int status = isPied ? 1 : 0;
        List<Order> items = new ArrayList<>();
        Cursor cursor = db.query(TABLE_ORDER,
                new String[] {},
                COL_ORDER_STATUS + " IN ("+status+")"+ " AND " +COL_ORDER_ID_CUSTOMER + " IN ("+idCustomer+")",
                null, null, null, COL_ORDER_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            items = getListOrderByCursor(cursor);
        }
        return items;
    }

    public Long getOrderLastPiedByIdCustomer(long idCustomer) {
        Cursor cursor = db.query(TABLE_ORDER,
                new String[] {},
                COL_ORDER_STATUS + " IN (1)"+ " AND " +COL_ORDER_ID_CUSTOMER + " IN ("+idCustomer+")",
                null, null, null, COL_ORDER_CREATED_AT + " DESC");
        if (cursor.getCount() == 0) return null;
        cursor.moveToFirst();
        return cursor.getLong(cursor.getColumnIndex(COL_ORDER_CREATED_AT));
    }

    //    GET PRODUCT
    public List<Product> getProductList() {
        List<Product> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM " + TABLE_PRODUCT + " o ORDER BY o." + COL_PRODUCT_ID + " DESC ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListProductByCursor(cursor);
        }
        return items;
    }

    public List<Product> getProductByName(String keyword) {
        List<Product> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_PRODUCT + " DESC where " + COL_PRODUCT_NAME + " like ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[] { "%" + keyword + "%" });
        if (cursor.moveToFirst()) {
            items = getListProductByCursor(cursor);
        }
        return items;
    }

    public List<Product> getProductByPrice(String keyword) {
        List<Product> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_PRODUCT + " DESC where " + COL_PRODUCT_PRICE + " like ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[] { "%" + keyword + "%" });
        if (cursor.moveToFirst()) {
            items = getListProductByCursor(cursor);
        }
        return items;
    }

    private List<Customer> getListCustomerByCursor(Cursor cur) {
        List<Customer> items = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                items.add(getCustomerByCursor(cur));
            } while (cur.moveToNext());
        }
        return items;
    }

    private List<OrderDetail> getListOrderDetailByCursor(Cursor cur) {
        List<OrderDetail> items = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                items.add(getOrderDetailByCursor(cur));
            } while (cur.moveToNext());
        }
        return items;
    }

    private List<Order> getListOrderByCursor(Cursor cur) {
        List<Order> items = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                items.add(getOrderByCursor(cur));
            } while (cur.moveToNext());
        }
        return items;
    }

    private List<Product> getListProductByCursor(Cursor cur) {
        List<Product> items = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                items.add(getProductByCursor(cur));
            } while (cur.moveToNext());
        }
        return items;
    }

    private List<Transaction> getListTransactionByCursor(Cursor cur) {
        List<Transaction> items = new ArrayList<Transaction>();
        if (cur.moveToFirst()) {
            do {
                items.add(getTransactionByCursor(cur));
            } while (cur.moveToNext());
        }
        return items;
    }

    //    DELETE CUSTOMER
    public void deleteCustomer(Long id) {
        db.delete(TABLE_CUSTOMER, COL_CUSTOMER_ID + " = ?", new String[]{id.toString()});
    }
    public void deleteCustomer() {
        db.execSQL("DELETE FROM " + TABLE_CUSTOMER);
    }

    //    DELETE ORDER DETAIL
    public void deleteOrderDetail(Long id) {
        db.delete(TABLE_ORDER_DETAIL, COL_ORDER_DETAIL_ID_ORDER_DETAIL + " = ?", new String[]{id.toString()});
    }

    public void deleteOrderDetailByOrderCode(Long orderCode) {
        db.delete(TABLE_ORDER_DETAIL, COL_ORDER_DETAIL_ID_ORDER_DETAIL + " = ?", new String[]{orderCode.toString()});
    }
    public void deleteTransactionByOrderCode(Long orderCode) {
        db.delete(TABLE_TRANSACTION, COL_TRANSACTION_ID_ORDER + " = ?", new String[]{orderCode.toString()});
    }
    public void deleteTransactionByID(Long id) {
        db.delete(TABLE_TRANSACTION, COL_TRANSACTION_ID + " = ?", new String[]{id.toString()});
    }
    public void deleteOrderByOrderCode(Long orderCode) {
        db.delete(TABLE_ORDER, COL_ORDER_CODE + " = ?", new String[]{orderCode.toString()});
    }
    public void deleteOrderDetail() {
        db.execSQL("DELETE FROM " + TABLE_ORDER_DETAIL);
    }

    //    DELETE ORDER
    public void deleteOrder(Long order_id) {
        db.delete(TABLE_ORDER, COL_ORDER_ID + " = ?", new String[]{order_id.toString()});
    }
    public void deleteOrder() {
        db.execSQL("DELETE FROM " + TABLE_ORDER);
    }

    //    DELETE PRODUCT
    public void deleteProduct(Long order_id) {
        db.delete(TABLE_PRODUCT, COL_PRODUCT_ID + " = ?", new String[]{order_id.toString()});
    }
    public void deleteProduct() {
        db.execSQL("DELETE FROM " + TABLE_PRODUCT);
    }

    //    GET SIZE
    public int getCustomerSize() {
        int count = (int) DatabaseUtils.queryNumEntries(db, TABLE_CUSTOMER);
        return count;
    }

    public int getUnreadOrderDetailSize() {
        String countQuery = "SELECT n." + COL_ORDER_DETAIL_ID + " FROM " + TABLE_ORDER_DETAIL + " n WHERE n." + COL_ORDER_STATUS + "=0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getReadOrderDetailSize() {
        String countQuery = "SELECT n." + COL_ORDER_DETAIL_ID + " FROM " + TABLE_ORDER_DETAIL + " n WHERE n." + COL_ORDER_STATUS + "=1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getOrderDetaiSize() {
        int count = (int) DatabaseUtils.queryNumEntries(db, TABLE_ORDER_DETAIL);
        return count;
    }

    public int getOrderSize() {
        int count = (int) DatabaseUtils.queryNumEntries(db, TABLE_ORDER);
        return count;
    }

    public int getOrderIsWaitingSize() {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_ORDER + " DESC where " + COL_ORDER_STATUS + " = ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{"0"});
        return cursor.getCount();
    }

    public int getOrderIsWaitingSize(long idCustomer) {
        Cursor cursor =db.query(TABLE_ORDER,
                new String[] {},
                COL_ORDER_STATUS + " IN (0)"+ " AND " +COL_ORDER_ID_CUSTOMER + " IN ("+idCustomer+")",
                null, null, null, null);
        return cursor.getCount();
    }

    public int getOrderIsPiedSize() {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_ORDER + " DESC where " + COL_ORDER_STATUS + " = ?");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{"1"});
        return cursor.getCount();
    }

    public int getProductSize() {
        int count = (int) DatabaseUtils.queryNumEntries(db, TABLE_PRODUCT);
        return count;
    }

    //    Transaction
    public List<Transaction> getAllTransaction() {
        List<Transaction> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_TRANSACTION + " DESC ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListTransactionByCursor(cursor);
        }
        return items;
    }

    public List<Transaction> getAllTransactionByCustomer(long type, long id_customer) {
        List<Transaction> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (id_customer == 0 && type == -1){
            sb.append("select * from " + TABLE_TRANSACTION);
        }else if (id_customer == 0 && type > -1){
            sb.append("select * from " + TABLE_TRANSACTION +
                    " w where  w." + COL_TRANSACTION_TYPE + " ='"+type+"'"
            );
        }else {
            sb.append("select * from " + TABLE_TRANSACTION +
                    " w where  w." + COL_TRANSACTION_ID_CUSTOMER + " ='"+id_customer+"'"+
                    "AND w."+ COL_TRANSACTION_TYPE + " ='"+type+"'"
            );
        }
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListTransactionByCursor(cursor);
        }
        return items;
    }

    public List<Transaction> getAllTransactionByType(long type) {
        List<Transaction> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_TRANSACTION +
                " w where  w." + COL_TRANSACTION_TYPE + " ='"+type+"'"
        );
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListTransactionByCursor(cursor);
        }
        return items;
    }

    public List<Transaction> getTransaction(long type, long id_order, long id_customer) {
        List<Transaction> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_TRANSACTION +
                " w where  w." + COL_TRANSACTION_TYPE + " ='"+type+"'"+
                " AND w."+ COL_TRANSACTION_ID_CUSTOMER + " ='"+id_customer+"'"+
                " AND w."+ COL_TRANSACTION_ID_ORDER + " ='"+id_order+"'");
        Log.d(TAG, "getTransaction: "+sb);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListTransactionByCursor(cursor);
        }
        return items;
    }

    public List<Transaction> getTransactionBedehiCustomer(long type, long id_customer) {
        List<Transaction> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_TRANSACTION +
                " w where  w." + COL_TRANSACTION_TYPE + " ='"+type+"'"+
                " AND w."+ COL_TRANSACTION_ID_CUSTOMER + " ='"+id_customer+"'");
        Log.d(TAG, "getTransaction: "+sb);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListTransactionByCursor(cursor);
        }
        return items;
    }

    public List<Transaction> getTransactionBedehiCustomer(long type) {
        List<Transaction> items = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + TABLE_TRANSACTION +
                " w where  w." + COL_TRANSACTION_TYPE + " ='"+type+"'");
        Log.d(TAG, "getTransaction: "+sb);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor.moveToFirst()) {
            items = getListTransactionByCursor(cursor);
        }
        return items;
    }

    public Transaction getTransaction2(long type, long id_order, long id_customer) {
        Transaction obj = null;
        String query = "SELECT * FROM " + TABLE_TRANSACTION +
                " w WHERE w." + COL_TRANSACTION_TYPE + " = ? "+
                "  AND w." + COL_TRANSACTION_ID_CUSTOMER + " = ?"+
                "  AND w." + COL_TRANSACTION_ID_ORDER + " = ?"
                ;
        Cursor cursor = db.rawQuery(query, new String[]{type+"" ,id_order+"", id_customer+""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getTransactionByCursor(cursor);
        }
        return obj;
    }

}
