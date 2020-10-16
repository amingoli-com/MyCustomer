package ir.amingoli.mycoustomer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterAddOrder;
import ir.amingoli.mycoustomer.data.AppConfig;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.OrderDetail;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.util.Tools;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;

public class ActivityAddOrder extends AppCompatActivity {
    String TAG = "amingoli78888";

    public static int STATIC_INTEGER_VALUE = 100;

    private boolean ORDER_STATUS_IS_PIED = false;
    private long ID_CUSTOMER = 0;
    private long ID_ORDER = 0;
    private long ID_ORDER_DETAIL = 0;
    private long CRATED_AT = System.currentTimeMillis();

    private Long ID_THIS_ORDER = System.currentTimeMillis();

    private ArrayList<Product> arrayList;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterAddOrder adapter;
    private TextView totalPrice,customerName,dateToDay,addProduct;
    private View boxCheckboxOrderIsReady,boxCheckBoxOrderIsWaiting,boxCheckBoxSendSmsToCustomer;
    private CheckBox checkboxOrderIsReady,checkBoxOrderIsWaiting,checkBoxSendSmsToCustomer;
    private Button submit;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        ORDER_STATUS_IS_PIED = getIntent().getBooleanExtra("status",false);
        ID_CUSTOMER = getIntent().getLongExtra("id_customer",0);
        ID_ORDER = getIntent().getLongExtra("id_order",0);
        ID_ORDER_DETAIL = getIntent().getLongExtra("id_order_detail",0);
        CRATED_AT = getIntent().getLongExtra("crated_at",System.currentTimeMillis());
        populateData();
        initAdapter();
        if (ID_ORDER_DETAIL != 0 ){
            getListForOrderDetail();
        }
        initCustomerDetail();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STATIC_INTEGER_VALUE && resultCode != 0){
            Product product = new Product();
            product.setId(data != null ? data.getLongExtra("product_id", 0) : 0);
            product.setName(data != null ? data.getStringExtra("product_name") : null);
            product.setPrice(data != null ? data.getDoubleExtra("product_price",0) : null);
            product.setAmount(1.0);
            product.setPrice_all(product.getAmount() * product.getPrice());
            addProductInOrder(product);
            adapter.notifyDataSetChanged();
            setTextTotalPrice();
        }

    }

    private void populateData(){
        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
        totalPrice = findViewById(R.id.all_price);
        customerName = findViewById(R.id.customerName);
        dateToDay = findViewById(R.id.dateToDay);
        addProduct = findViewById(R.id.addProduct);
        checkBoxOrderIsWaiting = findViewById(R.id.checkboxOrderIsWaiting);
        checkBoxSendSmsToCustomer = findViewById(R.id.checkboxSendSmsToCustomer);
        checkboxOrderIsReady = findViewById(R.id.checkboxOrderIsReady);
        boxCheckboxOrderIsReady = findViewById(R.id.boxCheckboxOrderIsReady);
        boxCheckBoxOrderIsWaiting = findViewById(R.id.boxCheckBoxOrderIsWaiting);
        boxCheckBoxSendSmsToCustomer = findViewById(R.id.boxCheckBoxSendSmsToCustomer);
        submit = findViewById(R.id.submit);
    }

    private void initAdapter(){
        adapter = new AdapterAddOrder(this, arrayList, new AdapterAddOrder.Listener() {
            @Override
            public void onClickPlus(Product product) {
                if (!ORDER_STATUS_IS_PIED){
                    product.setAmount(product.getAmount() + 1.0);
                    product.setPrice_all(product.getAmount()*product.getPrice());
                    arrayList.set(product.getPosition(),product);
                    adapter.notifyDataSetChanged();
                    setTextTotalPrice();
                }
            }

            @Override
            public void onClickRemove(Product product) {
                if (!ORDER_STATUS_IS_PIED){
                    if (product.getAmount() == 1) {
                        arrayList.remove(product.getPosition());
                    } else {
                        product.setAmount(product.getAmount() - 1.0);
                        product.setPrice_all(product.getAmount() * product.getPrice());
                        arrayList.set(product.getPosition(),product);
                    }
                    adapter.notifyDataSetChanged();
                    setTextTotalPrice();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initCustomerDetail(){
        List<Customer> customerList = db.getCustomerById(ID_CUSTOMER);
        int size = 0;
        customerName.setText(customerList.get(size).getName());
        dateToDay.setText(Tools.getFormattedDate(CRATED_AT));

    }

//    Set Data From Database in Factor
    private void getListForOrderDetail(){
        if (ORDER_STATUS_IS_PIED){
            addProduct.setVisibility(View.GONE);
            boxCheckboxOrderIsReady.setVisibility(View.GONE);
            boxCheckBoxOrderIsWaiting.setVisibility(View.GONE);
            boxCheckBoxSendSmsToCustomer.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        } else {
            boxCheckBoxOrderIsWaiting.setVisibility(View.GONE);
            boxCheckboxOrderIsReady.setVisibility(View.VISIBLE);
        }

        List<OrderDetail> item = db.getOrderDetailListById(ID_ORDER_DETAIL);
        for (int i = 0; i < item.size(); i++) {
            Product product = new Product();
            product.setId(item.get(i).getId());
            product.setName(item.get(i).getName());
            product.setPrice(item.get(i).getPrice_item());
            product.setAmount(item.get(i).getAmount());
            product.setPrice_all(item.get(i).getPrice_all());
            addProductInOrder(product);
        }
        adapter.notifyDataSetChanged();
        setTextTotalPrice();
    }

//    Save Factor
    private void saveOrder(){
        Order order = new Order();
        if (ID_ORDER != 0) order.setId(ID_ORDER);
        order.setCreated_at(CRATED_AT);
        order.setId_coustomer(ID_CUSTOMER);
        order.setId_order_detail(ID_THIS_ORDER);
        order.setCode(ID_THIS_ORDER+"");
        order.setPrice(getAllPrice());
        if (boxCheckboxOrderIsReady.getVisibility() == View.GONE){
            if (checkBoxOrderIsWaiting.isChecked()){
                order.setStatus(false);
            } else {
                order.setStatus(true);
                if (checkBoxSendSmsToCustomer.isChecked()){
                    String a = AppConfig.SendSmsToCustomer_ORDER_IS_PIAED
                            .replace(AppConfig.CUSTOMER_NAME,customerName.getText().toString())
                            .replace(AppConfig.TOTAL_PRICE,totalPrice.getText().toString());
                    Uri uri = Uri.parse("smsto:09100530593");
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", a);
                    startActivity(it);
                }
            }
        } else {
            if (checkboxOrderIsReady.isChecked()){
                order.setStatus(true);
            }else {
                order.setStatus(false);
            }
        }
        db.saveOrder(order);
    }

    private void saveOrderDetail(){
        for (int i = 0; i < arrayList.size(); i++) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setCreated_at(CRATED_AT);
            orderDetail.setId_product(arrayList.get(i).getId());
            orderDetail.setId_order_detail(ID_THIS_ORDER);
            orderDetail.setName(arrayList.get(i).getName());
            orderDetail.setAmount(arrayList.get(i).getAmount());
            orderDetail.setPrice_item(arrayList.get(i).getPrice());
            orderDetail.setPrice_all(arrayList.get(i).getPrice_all());
            db.saveOrderDetail(orderDetail);
        }

    }

//    Util
    private void addProductInOrder(Product product) {
        if (arrayList.size() == 0){
            arrayList.add(product);
        }else {
            for (int i = 0; i <= arrayList.size()-1; i++) {
                if (product.getId().equals(arrayList.get(i).getId())){
                    Double allAmount = arrayList.get(i).getAmount()+product.getAmount();
                    product.setAmount(allAmount);
                    if (product.getPrice_all() == 0.0 || product.getPrice_all() == null ){
                        product.setPrice_all(allAmount*product.getPrice());
                    }
                    arrayList.set(i,product);
                    return;
                }else if (i == arrayList.size()-1){
                    arrayList.add(product);
                    return;
                }
            }
        }
    }

    private double getAllPrice(){
        double p = 0.0;
        for (int i = 0; i < arrayList.size(); i++) {
            p = p + arrayList.get(i).getPrice_all();
        }
        return p;
    }

    private void setTextTotalPrice(){
        totalPrice.setText(Tools.getFormattedPrice(getAllPrice(),this));
    }

    public void addProduct(View view) {
        if (!ORDER_STATUS_IS_PIED){
            Intent intent = new Intent(this,ActivityProduct.class);
            intent.putExtra("get_product",true);
            startActivityForResult(intent, STATIC_INTEGER_VALUE);
        }
    }

//    onClick
    public void submit(View view) {
        if (arrayList.size() !=0){
            saveOrder();
            saveOrderDetail();
            finish();
        }
    }

    public void setDate(View view) {
        PersianDatePickerDialog picker = new PersianDatePickerDialog(this)
                .setPositiveButtonString(getString(R.string.save))
                .setNegativeButton(getString(R.string.cancel))
                .setTodayButton(getString(R.string.today))
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setInitDate(new PersianCalendar())
                .setActionTextColor(Color.GRAY)
                .setTypeFace(ResourcesCompat.getFont(this, R.font.iranyekan_regular))
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                        CRATED_AT = persianCalendar.getTimeInMillis();
                        dateToDay.setText(Tools.getFormattedDate(CRATED_AT));
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getGregorianChange());//Fri Oct 15 03:25:44 GMT+04:30 1582
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getTimeInMillis());//1583253636577
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getTime());//Tue Mar 03 20:10:36 GMT+03:30 2020
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getDelimiter());//  /
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getPersianLongDate());// سه‌شنبه  13  اسفند  1398
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getPersianLongDateAndTime()); //سه‌شنبه  13  اسفند  1398 ساعت 20:10:36
                        Log.d(TAG, "onDateSelected: "+persianCalendar.getPersianMonthName()); //اسفند
                        Log.d(TAG, "onDateSelected: "+persianCalendar.isPersianLeapYear());//false
                    }
                    @Override
                    public void onDismissed() {

                    }
                });
        picker.show();
    }
}