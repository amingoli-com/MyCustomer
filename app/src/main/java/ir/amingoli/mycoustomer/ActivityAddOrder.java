package ir.amingoli.mycoustomer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.amingoli.mycoustomer.Adapter.AdapterAddOrder;
import ir.amingoli.mycoustomer.data.AppConfig;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.OrderDetail;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.PriceNumberTextWatcher;
import ir.amingoli.mycoustomer.util.SetTextForSendSms;
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
    private Double _totalPayed = null;
    private Double _totalDiscount = 0.0;
    private Double _totalBedehi = 0.0;
    private long CRATED_AT = System.currentTimeMillis();

    private Long ID_THIS_ORDER = System.currentTimeMillis();

    private ArrayList<Product> productsList;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterAddOrder adapter;
    private TextView totalPrice,totalDiscount,totalPayed,textBedehkaran,customerName,dateToDay,
            addProduct;
    private View boxCheckboxOrderIsReady,boxCheckBoxOrderIsWaiting,boxCheckBoxSendSmsToCustomer,
            viewPayed,viewDiscount,viewBedehi;
    private CheckBox checkboxOrderIsReady,checkBoxOrderIsWaiting,checkBoxSendSmsToCustomer;
    private Button submit;

    private String customer_phone, customer_name;

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

        Log.d(TAG, "onCreate: ID_ORDER_DETAIL: "+ID_ORDER_DETAIL+" - ID_THIS_ORDER: "+ID_THIS_ORDER);
        if (ID_ORDER_DETAIL != 0 ){
            Log.d(TAG, "onCreate: ID_ORDER_DETAIL != 0");
            ID_THIS_ORDER = ID_ORDER_DETAIL;
            findViewById(R.id.remove).setVisibility(View.VISIBLE);
            getListForOrderDetail();
            checkboxOrderIsReady.setChecked(true);
        }
        getListPayed();
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
        productsList = new ArrayList<>();
        totalPrice = findViewById(R.id.all_price);
        totalDiscount = findViewById(R.id.totalDiscount);
        totalPayed = findViewById(R.id.totalPayed);
        viewDiscount = findViewById(R.id.viewDiscount);
        viewPayed = findViewById(R.id.viewPayed);
        viewBedehi = findViewById(R.id.viewBedehi);
        textBedehkaran = findViewById(R.id.textBedehkaran);
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

        viewDiscount.setOnClickListener(view -> changeDiscount());
        viewPayed.setOnClickListener(view -> changePayed());
    }

    private void initAdapter(){
        adapter = new AdapterAddOrder(this, productsList, new AdapterAddOrder.Listener() {
            @Override
            public void onClickPlus(Product product) {
                product.setAmount(product.getAmount() + 1.0);
                product.setPrice_all(product.getAmount()*product.getPrice());
                productsList.set(product.getPosition(),product);
                adapter.notifyDataSetChanged();
                setTextTotalPrice();
            }

            @Override
            public void onClickRemove(Product product) {
                if (product.getAmount() == 1) {
                    productsList.remove(product.getPosition());
                } else {
                    product.setAmount(product.getAmount() - 1.0);
                    product.setPrice_all(product.getAmount() * product.getPrice());
                    productsList.set(product.getPosition(),product);
                }
                adapter.notifyDataSetChanged();
                setTextTotalPrice();
            }

            @Override
            public void onClickChangeAmount(Product product, int position) {
                changeAmount(product,position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initCustomerDetail(){
        List<Customer> customerList = db.getCustomerById(ID_CUSTOMER);
        int size = 0;
        customer_name = customerList.get(size).getName();
        customer_phone = customerList.get(size).getTel();
        customerName.setText(customer_name);
        dateToDay.setText(Tools.getFormattedDate(CRATED_AT));

    }

//    Set Data From Database in Factor
    private void getListForOrderDetail(){
        if (ORDER_STATUS_IS_PIED){
//            addProduct.setVisibility(View.GONE);
            boxCheckboxOrderIsReady.setVisibility(View.GONE);
            boxCheckBoxOrderIsWaiting.setVisibility(View.GONE);
            boxCheckBoxSendSmsToCustomer.setVisibility(View.GONE);
//            submit.setVisibility(View.GONE);
        } else {
            boxCheckBoxOrderIsWaiting.setVisibility(View.GONE);
            boxCheckboxOrderIsReady.setVisibility(View.VISIBLE);
        }

        List<OrderDetail> item = db.getOrderDetailListById(ID_ORDER_DETAIL);
        for (int i = 0; i < item.size(); i++) {
            Product product = new Product();
            product.setId(item.get(i).getId_product());
            product.setName(item.get(i).getName());
            product.setPrice(item.get(i).getPrice_item());
            product.setPrice_all(item.get(i).getPrice_all());
            product.setAmount(item.get(i).getAmount());
            addProductInOrder(product);
        }
        adapter.notifyDataSetChanged();
        setTextTotalPrice();
    }

    private void getListPayed() {
        Transaction tPayed = getTransactionByType(Tools.TRANSACTION_TYPE_PAY_BEDEHI);
        Transaction tBedehi = getTransactionByType(Tools.TRANSACTION_TYPE_BEDEHI);
        Transaction tDiscount = getTransactionByType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT);

        if (tPayed != null) {
            _totalPayed = tPayed.amount;
            Log.d(TAG, "getListPayed: "+tPayed.amount);
        }else {_totalPayed = getPricePayed();}
        if (tBedehi != null) _totalBedehi = tBedehi.amount;
        if (tDiscount != null) _totalDiscount = tDiscount.amount;
        setTextTotalPrice();
    }

//    Save Order
    Order order;
    private void saveOrder(){
        db.deleteOrderByOrderCode(ID_THIS_ORDER);
        order = new Order();
        if (ID_ORDER != 0) order.setId(ID_ORDER);
        order.setCreated_at(CRATED_AT);
        order.setId_coustomer(ID_CUSTOMER);
        order.setId_order_detail(ID_THIS_ORDER);
        order.setCode(ID_THIS_ORDER+"");
        order.setPrice(getAllPrice());
        if (boxCheckboxOrderIsReady.getVisibility() == View.GONE){
            if (checkBoxOrderIsWaiting.isChecked()){
                order.setStatus(false);
                if (checkBoxSendSmsToCustomer.isChecked()){
                    sendSms(SetTextForSendSms.SEND_SMS_ORDER_IS_WAITING(
                            this
                            , customer_name
                            , getAllPrice()
                            , productsList));
                }
            } else {
                order.setStatus(true);
                if (checkBoxSendSmsToCustomer.isChecked()){
                    sendSms(SetTextForSendSms.SEND_SMS_ORDER_IS_PIED(customer_name));
                }
            }
        } else {
            if (checkboxOrderIsReady.isChecked()){
                order.setCreated_at(System.currentTimeMillis());
                order.setStatus(true);
                if (checkBoxSendSmsToCustomer.isChecked()){
                    sendSms(SetTextForSendSms.SEND_SMS_ORDER_IS_WAITING_IS_PIED(customer_name));
                }
            }else {
                order.setStatus(false);
            }
        }
        db.saveOrder(order);
    }

    private void saveOrderDetail(){

        db.deleteOrderDetailByOrderCode(ID_THIS_ORDER);

        OrderDetail orderDetail = new OrderDetail();
        for (int i = 0; i < productsList.size(); i++) {

            orderDetail.setCreated_at(CRATED_AT);
            orderDetail.setId_product(productsList.get(i).getId());
            orderDetail.setId_order_detail(ID_THIS_ORDER);
            orderDetail.setName(productsList.get(i).getName());
            Log.d(TAG, "saveOrderDetail: "+productsList.get(i).getAmount());
            orderDetail.setAmount(productsList.get(i).getAmount());
            orderDetail.setPrice_item(productsList.get(i).getPrice());
            orderDetail.setPrice_all(productsList.get(i).getPrice_all());
            db.saveOrderDetail(orderDetail);

            double amount = 0;
            if (db.getProduct(productsList.get(i).getId()) != null){
                amount = db.getProduct(productsList.get(i).getId()).getAmount() - productsList.get(i).getAmount();
            }
            if (amount < 0) amount = 0;
            productsList.get(i).amount = amount;
            if (order.getStatus()){
                db.saveProduct(productsList.get(i));
            }
        }

    }

    private void saveTransAction(){
        db.deleteTransactionByOrderCode(ID_THIS_ORDER);
//        -------- Payed
        Transaction transactionPayed = new Transaction();
        if (getTransactionByType(Tools.TRANSACTION_TYPE_PAY_BEDEHI) != null) {
            transactionPayed.setId(Objects.requireNonNull(getTransactionByType(Tools.TRANSACTION_TYPE_PAY_BEDEHI)).id);
        }
        transactionPayed.setId_order(ID_THIS_ORDER);
        transactionPayed.setId_customer(ID_CUSTOMER);
        transactionPayed.setType(Tools.TRANSACTION_TYPE_PAY_BEDEHI);
        Log.d(TAG, "_totalPayed: "+_totalPayed);
        transactionPayed.setAmount(_totalPayed);
        db.saveTransaction(transactionPayed);

//        --------- Bedehi
        Transaction transactionBedehi = new Transaction();
        if (getTransactionByType(Tools.TRANSACTION_TYPE_BEDEHI) != null) {
            transactionBedehi.setId(Objects.requireNonNull(getTransactionByType(Tools.TRANSACTION_TYPE_BEDEHI)).id);
        }
        transactionBedehi.setId_order(ID_THIS_ORDER);
        transactionBedehi.setId_customer(ID_CUSTOMER);
        transactionBedehi.setType(Tools.TRANSACTION_TYPE_BEDEHI);
        transactionBedehi.setAmount(_totalBedehi);
        db.saveTransaction(transactionBedehi);


//        --------- Discount
        Transaction transactionDiscount = new Transaction();
        if (getTransactionByType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT) != null) {
            transactionDiscount.setId(Objects.requireNonNull(getTransactionByType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT)).id);
        }
        transactionDiscount.setId_order(ID_THIS_ORDER);
        transactionDiscount.setId_customer(ID_CUSTOMER);
        transactionDiscount.setType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT);
        transactionDiscount.setAmount(_totalDiscount);
        db.saveTransaction(transactionDiscount);


    }

    private Transaction getTransactionByType(Long type){
        try {
            Log.d(TAG, "getTransactionByType: ok");
            return db.getTransaction(type,ID_THIS_ORDER,ID_CUSTOMER).get(0);
        }catch (Exception e){
            Log.e(TAG, "getTransactionByType: ERROR", e);
        }
        Log.d(TAG, "getTransactionByType: null");
        return null;
    }

//    Util
    private void sendSms(String text){
        Uri uri = Uri.parse("smsto:"+customer_phone);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", text);
        startActivity(it);
    }
    private void addProductInOrder(Product product) {
        if (productsList.size() == 0){
            productsList.add(product);
        }else {
            for (int i = 0; i <= productsList.size()-1; i++) {
                if (product.getId().equals(productsList.get(i).getId())){
                    Double allAmount = productsList.get(i).getAmount()+product.getAmount();
                    product.setAmount(allAmount);
                    product.setPrice_all(allAmount*product.getPrice());
                    productsList.set(i,product);
                    setTextTotalPrice();
                    return;
                }else if (i == productsList.size()-1){
                    productsList.add(product);
                    return;
                }
            }
        }
    }

    private double getAllPrice(){
        double p = 0.0;
        for (int i = 0; i < productsList.size(); i++) {
            p = p + productsList.get(i).getPrice_all();
        }
        return p;
    }

    private double getPricePayed(){
        if (_totalPayed != null){
            if (_totalPayed > getAllPrice()) return getAllPrice();
            else return _totalPayed;
        }else return getAllPrice();
    }

    private void setTextTotalPrice(){
        totalPrice.setText(Tools.getFormattedPrice(getAllPrice(),this));
        totalDiscount.setText(Tools.getFormattedPrice(_totalDiscount,this));
        totalPayed.setText(Tools.getFormattedPrice(getPricePayed(),this));

        if ((getPricePayed() + _totalDiscount) < getAllPrice()){
            _totalBedehi = getAllPrice() - (getPricePayed() + _totalDiscount);
            textBedehkaran.setText(Tools.getFormattedPrice(_totalBedehi,this));
            viewBedehi.setVisibility(View.VISIBLE);
        }else {
            _totalBedehi = 0.0;
            textBedehkaran.setText("");
            viewBedehi.setVisibility(View.GONE);
        }
    }

    public void addProduct(View view) {
        Intent intent = new Intent(this,ActivityProduct.class);
        intent.putExtra("get_product",true);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

//    dialog
    @SuppressLint("SetTextI18n")
    private void changeAmount(Product product , int i){
        View view = View.inflate(this, R.layout.item_dialog_change_amount, null);
        EditText amount = view.findViewById(R.id.amount);
        if (product.getAmount() != 0 && product.getAmount() != null){
            amount.setText(product.getAmount()+"");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_discount_product, product.getName()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    if (!TextUtils.isEmpty(amount.getText().toString())
                            && Double.parseDouble(amount.getText().toString()) != 0){
                        product.setAmount(Double.valueOf(amount.getText().toString()));
                        product.setPrice_all(product.getAmount()*product.getPrice());
                        productsList.set(i,product);
                        adapter.notifyDataSetChanged();
                        setTextTotalPrice();
                    }else {
                        Toast.makeText(this, getString(R.string.is_not_decimal), Toast.LENGTH_SHORT).show();
                    }

                })
                .show();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void changePayed(){
        View view = View.inflate(this, R.layout.item_dialog_text_amount, null);
        EditText amount = view.findViewById(R.id.amount);
        amount.addTextChangedListener(new PriceNumberTextWatcher(amount));
        amount.setText(getPricePayed()+"");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_discount_or_payed, "پرداختی"))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    double d = Double.parseDouble(Tools.convertNumberToEN(amount.getText().toString().trim()));
                    if (!TextUtils.isEmpty(amount.getText().toString())){
                        if (d <= getAllPrice()){
                            _totalPayed = d;
                            setTextTotalPrice();
                        }else {
                            Toast.makeText(this, "مبلغ پرداختی نباید از مبلغ سفارش بیشتر باشد", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(this, getString(R.string.is_not_decimal), Toast.LENGTH_SHORT).show();
                    }

                })
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void changeDiscount(){
        View view = View.inflate(this, R.layout.item_dialog_text_amount, null);
        EditText amount = view.findViewById(R.id.amount);
        amount.addTextChangedListener(new PriceNumberTextWatcher(amount));
        amount.setText(_totalDiscount+"");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_discount_or_payed, "تخفیف"))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    double d = Double.parseDouble(Tools.convertNumberToEN(amount.getText().toString().trim()));
                    if (!TextUtils.isEmpty(amount.getText().toString())){
                        if (d <= (getAllPrice() - getPricePayed())){
                            _totalDiscount = d;
                            setTextTotalPrice();
                        }else {
                            Toast.makeText(this, "مبلغ تخفیف نباید از مبلغ سفارش بیشتر باشد", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, getString(R.string.is_not_decimal), Toast.LENGTH_SHORT).show();
                    }

                })
                .show();
    }


    //    onClick
    public void submit(View view) {
        if (productsList.size() !=0){
            saveOrder();
            saveOrderDetail();
            saveTransAction();
            finish();
        }
    }

    public void remove(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.do_you_want_remove_this_order))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    db.deleteOrder(ID_ORDER);
                    db.deleteOrderByOrderCode(ID_ORDER_DETAIL);
                    db.deleteOrderDetail(ID_ORDER_DETAIL);
                    db.deleteTransactionByOrderCode(ID_THIS_ORDER);
                    finish(); })
                .setNegativeButton(getString(R.string.no),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
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
//                .setShowInBottomSheet(true)
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