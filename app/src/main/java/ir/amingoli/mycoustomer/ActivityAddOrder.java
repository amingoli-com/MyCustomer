package ir.amingoli.mycoustomer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ir.amingoli.mycoustomer.Adapter.AdapterAddOrder;
import ir.amingoli.mycoustomer.Adapter.AdapterSmsSample;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.OrderDetail;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.PriceNumberTextWatcher;
import ir.amingoli.mycoustomer.util.Tools;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;

public class ActivityAddOrder extends AppCompatActivity {
    String TAG = "amingoli78888";

    public static int STATIC_INTEGER_VALUE = 100;

    private Order order;
    private boolean ORDER_STATUS_IS_PIED = false;
    private long ID_CUSTOMER = 0;
    private long ID_ORDER = 0; // ID from orders table
    private long ORDER_DETAIL_CODE = 0; // This is the unique code for order detail
    private Double _totalPayed = null;
    private Double _totalDiscount = 0.0;
    private Double _totalBedehi = 0.0;
    private long ORDER_DATE = System.currentTimeMillis(); // The actual order date

    // This is the unique identifier for the order detail
    private Long ORDER_UNIQUE_CODE = System.currentTimeMillis();
    private boolean IS_EDIT_MODE = false;

    ArrayList<Transaction> transactionArrayList;
    private ArrayList<Product> productsList;
    private ArrayList<OrderDetail> orderDetailArrayList;
    private RecyclerView recyclerView, recyclerViewSmsSample;
    private DatabaseHandler db;
    private AdapterAddOrder adapter;
    private TextView totalPrice, totalDiscount, totalPayed, textBedehkaran, customerName, dateToDay,
            addProduct;
    private View boxCheckboxOrderIsReady, boxCheckBoxOrderIsWaiting,
            viewPayed, viewDiscount, viewBedehi;
    private CheckBox checkboxOrderIsReady, checkBoxOrderIsWaiting;
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

        // Get intent data
        ORDER_STATUS_IS_PIED = getIntent().getBooleanExtra("status", false);
        ID_CUSTOMER = getIntent().getLongExtra("id_customer", 0);
        ID_ORDER = getIntent().getLongExtra("id_order", 0);
        ORDER_DETAIL_CODE = getIntent().getLongExtra("id_order_detail", 0);

        long intentDate = getIntent().getLongExtra("created_at", 0);
        if (intentDate != 0) {
            ORDER_DATE = intentDate;
        }

        Log.d(TAG, "onCreate: ORDER_DETAIL_CODE: " + ORDER_DETAIL_CODE + " - ID_ORDER: " + ID_ORDER);
        Log.d(TAG, "onCreate: ORDER_DATE from intent: " + intentDate);

        // Check if we're in edit mode
        if (ORDER_DETAIL_CODE != 0) {
            IS_EDIT_MODE = true;
            ORDER_UNIQUE_CODE = ORDER_DETAIL_CODE;
            findViewById(R.id.remove).setVisibility(View.VISIBLE);
        }

        populateData();
        initAdapter();

        // Load data if in edit mode
        if (IS_EDIT_MODE) {
            loadOrderData();
        }

        getListPayed();
        initCustomerDetail();
        initSmsSample();
    }

    private void loadOrderData() {
        // First, get the order to retrieve the correct creation date
        Order existingOrder = db.getOrderByOrderCode(ORDER_UNIQUE_CODE);
        if (existingOrder != null) {
            ORDER_DATE = existingOrder.getCreated_at();
            ID_ORDER = existingOrder.getId();
            Log.d(TAG, "loadOrderData: Retrieved order date: " + ORDER_DATE);
            Log.d(TAG, "loadOrderData: Formatted date: " + Tools.getFormattedDate(ORDER_DATE));
        }

        // Load order details
        List<OrderDetail> item = db.getOrderDetailListById(ORDER_UNIQUE_CODE);
        for (int i = 0; i < item.size(); i++) {
            Product product = new Product();
            product.setId(item.get(i).getId_product());
            product.setName(item.get(i).getName());
            product.setPrice(item.get(i).getPrice_item());
            product.setPrice_all(item.get(i).getPrice_all());
            product.setAmount(item.get(i).getAmount());
            addProductInOrder(product);
        }
        orderDetailArrayList = (ArrayList<OrderDetail>) item;
        adapter.notifyDataSetChanged();
        setTextTotalPrice();

        // Set checkbox states based on order status
        if (existingOrder != null) {
            checkboxOrderIsReady.setChecked(existingOrder.getStatus());
            checkBoxOrderIsWaiting.setChecked(!existingOrder.getStatus());
        }
    }

    private void initSmsSample() {
        try {
            AdapterSmsSample adapterSmsSample = new AdapterSmsSample(this,
                    transactionArrayList,
                    (transaction, string) -> showShareCopyDialog(ActivityAddOrder.this, string), true,
                    customer_name, customer_phone,
                    getAllPrice(), _totalPayed, _totalBedehi, _totalDiscount,
                    getAllTotalBedehiCustomer(),
                    !checkBoxOrderIsWaiting.isChecked() == checkboxOrderIsReady.isChecked(),
                    ORDER_DATE, // Use ORDER_DATE instead of CRATED_AT
                    new Date().getTime(),
                    orderDetailShort(), orderDetailLong(), orderDetailFull());
            recyclerViewSmsSample.setAdapter(adapterSmsSample);
        } catch (Exception e) {
            Log.e(TAG, "initSmsSample error", e);
        }
    }

    private String orderDetailShort() {
        StringBuilder s = new StringBuilder();
        try {
            if (productsList != null) {
                for (int i = 0; i < productsList.size(); i++) {
                    Product o = productsList.get(i);
                    s.append(o.name).append(" ");
                    s.append("(").append(Tools.getFormattedPrice(o.price_all, this)).append(")");
                    s.append("\n");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "orderDetailShort error", e);
        }
        return s.toString();
    }

    private String orderDetailLong() {
        StringBuilder s = new StringBuilder();
        try {
            if (productsList != null) {
                for (int i = 0; i < productsList.size(); i++) {
                    Product o = productsList.get(i);
                    s.append(o.name).append(" ");
                    s.append("× ");
                    s.append(Tools.getFormattedDiscount(o.amount)).append(" عدد ");
                    s.append("(").append(Tools.getFormattedPrice(o.price_all, this)).append(")");
                    s.append("\n");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "orderDetailLong error", e);
        }
        return s.toString();
    }

    private String orderDetailFull() {
        StringBuilder s = new StringBuilder();
        try {
            if (productsList != null) {
                for (int i = 0; i < productsList.size(); i++) {
                    Product o = productsList.get(i);
                    s.append(o.name).append(" ");
                    s.append("(");
                    s.append(Tools.getFormattedDiscount(o.amount)).append(" عدد ");
                    s.append("× ");
                    s.append(Tools.getFormattedPrice(o.price, this));
                    s.append(" = ");
                    s.append(Tools.getFormattedPrice(o.price_all, this));
                    s.append(")");
                    s.append("\n");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "orderDetailFull error", e);
        }
        return s.toString();
    }

    private double getAllTotalBedehiCustomer() {
        double d = 0.0;
        try {
            Transaction thisTransaction = getTransactionByType(Tools.TRANSACTION_TYPE_BEDEHI);
            ArrayList<Transaction> t = (ArrayList<Transaction>) db.getAllTransactionByCustomer(Tools.TRANSACTION_TYPE_BEDEHI, ID_CUSTOMER);
            for (int i = 0; i < t.size(); i++) {
                d = d + t.get(i).getAmount();
            }
            if (thisTransaction != null) d = d - thisTransaction.getAmount();
            return d;
        } catch (Exception e) {
            Log.e(TAG, "getAllTotalBedehiCustomer error", e);
            return d;
        }
    }

    public void showShareCopyDialog(Context context, String textToShare) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_share_copy, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button btnShare = dialogView.findViewById(R.id.btn_share);
        Button btnSendSms = dialogView.findViewById(R.id.btn_send_sms);

        btnShare.setOnClickListener(v -> {
            Tools.shareText(this, textToShare);
            dialog.dismiss();
        });

        btnSendSms.setOnClickListener(v -> {
            sendSms(textToShare);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STATIC_INTEGER_VALUE && resultCode != 0) {
            Product product = new Product();
            product.setId(data != null ? data.getLongExtra("product_id", 0) : 0);
            product.setName(data != null ? data.getStringExtra("product_name") : null);
            product.setPrice(data != null ? data.getDoubleExtra("product_price", 0) : null);
            product.setAmount(1.0);
            product.setPrice_all(product.getAmount() * product.getPrice());
            addProductInOrder(product);
            adapter.notifyDataSetChanged();
            setTextTotalPrice();
        }
    }

    private void populateData() {
        db = new DatabaseHandler(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewSmsSample = findViewById(R.id.recyclerViewSms);
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
        checkboxOrderIsReady = findViewById(R.id.checkboxOrderIsReady);
        boxCheckboxOrderIsReady = findViewById(R.id.boxCheckboxOrderIsReady);
        boxCheckBoxOrderIsWaiting = findViewById(R.id.boxCheckBoxOrderIsWaiting);
        submit = findViewById(R.id.submit);

        viewDiscount.setOnClickListener(view -> changeDiscount());
        viewPayed.setOnClickListener(view -> changePayed());

        transactionArrayList = (ArrayList<Transaction>) db.getAllTransactionByType(Tools.TRANSACTION_TYPE_SMS_SAMPLE);

        if (transactionArrayList.isEmpty()) {
            Transaction t1 = new Transaction();
            t1.setDesc("سلام [نام_مشتری] عزیز" +
                    "\n" +
                    "وضعیت سفارش شما [وضعیت_سفارش] میباشد." +
                    "\n" +
                    "جمع سفارش: [مبلغ_کل_سفارش]");

            Transaction t2 = new Transaction();
            t2.setDesc("سلام امین عزیز" +
                    "\n" +
                    "وضعیت سفارش شما [وضعیت_سفارش] میباشد." +
                    "\n" +
                    "جمع سفارش: [مبلغ_کل_سفارش]" +
                    "\n" +
                    "مانده: [کل_بدهی_مشتری]");
            transactionArrayList.add(t1);
            transactionArrayList.add(t2);
        }

        checkBoxOrderIsWaiting.setOnCheckedChangeListener((compoundButton, b) -> setTextTotalPrice());
        checkboxOrderIsReady.setOnCheckedChangeListener((compoundButton, b) -> setTextTotalPrice());
    }

    private void initAdapter() {
        adapter = new AdapterAddOrder(this, productsList, new AdapterAddOrder.Listener() {
            @Override
            public void onClickPlus(Product product) {
                product.setAmount(product.getAmount() + 1.0);
                product.setPrice_all(product.getAmount() * product.getPrice());
                productsList.set(product.getPosition(), product);
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
                    productsList.set(product.getPosition(), product);
                }
                adapter.notifyDataSetChanged();
                setTextTotalPrice();
            }

            @Override
            public void onClickChangeAmount(Product product, int position) {
                changeAmount(product, position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initCustomerDetail() {
        List<Customer> customerList = db.getCustomerById(ID_CUSTOMER);
        if (customerList != null && !customerList.isEmpty()) {
            customer_name = customerList.get(0).getName();
            customer_phone = customerList.get(0).getTel();
            customerName.setText(customer_name);
        }

        // Always use ORDER_DATE for display
        dateToDay.setText(Tools.getFormattedDate(ORDER_DATE));
        Log.d(TAG, "initCustomerDetail: Setting date to: " + Tools.getFormattedDate(ORDER_DATE));
    }

    private void getListPayed() {
        Transaction tPayed = getTransactionByType(Tools.TRANSACTION_TYPE_PAY_BEDEHI);
        Transaction tBedehi = getTransactionByType(Tools.TRANSACTION_TYPE_BEDEHI);
        Transaction tDiscount = getTransactionByType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT);

        if (tPayed != null) {
            _totalPayed = tPayed.amount;
            Log.d(TAG, "getListPayed: " + tPayed.amount);
        } else {
            _totalPayed = getPricePayed();
        }
        if (tBedehi != null) _totalBedehi = tBedehi.amount;
        if (tDiscount != null) _totalDiscount = tDiscount.amount;
        setTextTotalPrice();
    }

    private void saveOrder() {
        // Delete existing order with this unique code
        db.deleteOrderByOrderCode(ORDER_UNIQUE_CODE);

        order = new Order();
        if (ID_ORDER != 0) {
            order.setId(ID_ORDER);
        }

        // Always use ORDER_DATE which is properly maintained
        order.setCreated_at(ORDER_DATE);
        Log.d(TAG, "saveOrder: Saving order with date: " + ORDER_DATE);

        order.setId_coustomer(ID_CUSTOMER);
        order.setId_order_detail(ORDER_UNIQUE_CODE);
        order.setCode(ORDER_UNIQUE_CODE + "");
        order.setPrice(getAllPrice());

        // Set order status
        if (boxCheckboxOrderIsReady.getVisibility() == View.GONE) {
            order.setStatus(!checkBoxOrderIsWaiting.isChecked());
        } else {
            order.setStatus(checkboxOrderIsReady.isChecked());
        }

        db.saveOrder(order);
    }

    private void saveOrderDetail() {
        db.deleteOrderDetailByOrderCode(ORDER_UNIQUE_CODE);

        for (int i = 0; i < productsList.size(); i++) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setCreated_at(ORDER_DATE); // Always use ORDER_DATE
            orderDetail.setId_product(productsList.get(i).getId());
            orderDetail.setId_order_detail(ORDER_UNIQUE_CODE);
            orderDetail.setName(productsList.get(i).getName());
            Log.d(TAG, "saveOrderDetail: Amount: " + productsList.get(i).getAmount());
            orderDetail.setAmount(productsList.get(i).getAmount());
            orderDetail.setPrice_item(productsList.get(i).getPrice());
            orderDetail.setPrice_all(productsList.get(i).getPrice_all());
            db.saveOrderDetail(orderDetail);

            // Update product inventory if order is completed
            if (order != null && order.getStatus()) {
                double amount = 0;
                Product existingProduct = db.getProduct(productsList.get(i).getId());
                if (existingProduct != null) {
                    amount = existingProduct.getAmount() - productsList.get(i).getAmount();
                }
                if (amount < 0) amount = 0;
                productsList.get(i).amount = amount;
                db.saveProduct(productsList.get(i));
            }
        }
    }

    private void saveTransAction() {
        db.deleteTransactionByOrderCode(ORDER_UNIQUE_CODE);

        // Payed transaction
        Transaction transactionPayed = new Transaction();
        Transaction existingPayed = getTransactionByType(Tools.TRANSACTION_TYPE_PAY_BEDEHI);
        if (existingPayed != null) {
            transactionPayed.setId(existingPayed.id);
        }
        transactionPayed.setId_order(ORDER_UNIQUE_CODE);
        transactionPayed.setId_customer(ID_CUSTOMER);
        transactionPayed.setType(Tools.TRANSACTION_TYPE_PAY_BEDEHI);
        transactionPayed.setAmount(_totalPayed);
        db.saveTransaction(transactionPayed);

        // Bedehi transaction
        Transaction transactionBedehi = new Transaction();
        Transaction existingBedehi = getTransactionByType(Tools.TRANSACTION_TYPE_BEDEHI);
        if (existingBedehi != null) {
            transactionBedehi.setId(existingBedehi.id);
        }
        transactionBedehi.setId_order(ORDER_UNIQUE_CODE);
        transactionBedehi.setId_customer(ID_CUSTOMER);
        transactionBedehi.setType(Tools.TRANSACTION_TYPE_BEDEHI);
        transactionBedehi.setAmount(_totalBedehi);
        db.saveTransaction(transactionBedehi);

        // Discount transaction
        Transaction transactionDiscount = new Transaction();
        Transaction existingDiscount = getTransactionByType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT);
        if (existingDiscount != null) {
            transactionDiscount.setId(existingDiscount.id);
        }
        transactionDiscount.setId_order(ORDER_UNIQUE_CODE);
        transactionDiscount.setId_customer(ID_CUSTOMER);
        transactionDiscount.setType(Tools.TRANSACTION_TYPE_PAY_DISCOUNT);
        transactionDiscount.setAmount(_totalDiscount);
        db.saveTransaction(transactionDiscount);
    }

    private Transaction getTransactionByType(Long type) {
        try {
            List<Transaction> transactions = db.getTransaction(type, ORDER_UNIQUE_CODE, ID_CUSTOMER);
            if (transactions != null && !transactions.isEmpty()) {
                return transactions.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getTransactionByType error", e);
        }
        return null;
    }

    private void sendSms(String text) {
        Uri uri = Uri.parse("smsto:" + customer_phone);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", text);
        startActivity(it);
    }

    private void addProductInOrder(Product product) {
        if (productsList.isEmpty()) {
            productsList.add(product);
        } else {
            for (int i = 0; i < productsList.size(); i++) {
                if (product.getId().equals(productsList.get(i).getId())) {
                    Double allAmount = productsList.get(i).getAmount() + product.getAmount();
                    product.setAmount(allAmount);
                    product.setPrice_all(allAmount * product.getPrice());
                    productsList.set(i, product);
                    setTextTotalPrice();
                    return;
                } else if (i == productsList.size() - 1) {
                    productsList.add(product);
                    return;
                }
            }
        }
    }

    private double getAllPrice() {
        double p = 0.0;
        for (int i = 0; i < productsList.size(); i++) {
            p = p + productsList.get(i).getPrice_all();
        }
        return p;
    }

    private double getPricePayed() {
        if (_totalPayed != null) {
            return Math.min(_totalPayed, getAllPrice());
        } else {
            return getAllPrice();
        }
    }

    private void setTextTotalPrice() {
        totalPrice.setText(Tools.getFormattedPrice(getAllPrice(), this));
        totalDiscount.setText(Tools.getFormattedPrice(_totalDiscount, this));
        totalPayed.setText(Tools.getFormattedPrice(getPricePayed(), this));

        if ((getPricePayed() + _totalDiscount) < getAllPrice()) {
            _totalBedehi = getAllPrice() - (getPricePayed() + _totalDiscount);
            textBedehkaran.setText(Tools.getFormattedPrice(_totalBedehi, this));
            viewBedehi.setVisibility(View.VISIBLE);
        } else {
            _totalBedehi = 0.0;
            textBedehkaran.setText("");
            viewBedehi.setVisibility(View.GONE);
        }
        initSmsSample();
    }

    public void addProduct(View view) {
        Intent intent = new Intent(this, ActivityProduct.class);
        intent.putExtra("get_product", true);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

    @SuppressLint("SetTextI18n")
    private void changeAmount(Product product, int i) {
        View view = View.inflate(this, R.layout.item_dialog_change_amount, null);
        EditText amount = view.findViewById(R.id.amount);
        if (product.getAmount() != 0 && product.getAmount() != null) {
            amount.setText(product.getAmount() + "");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_discount_product, product.getName()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    if (!TextUtils.isEmpty(amount.getText().toString())
                            && Double.parseDouble(amount.getText().toString()) != 0) {
                        product.setAmount(Double.valueOf(amount.getText().toString()));
                        product.setPrice_all(product.getAmount() * product.getPrice());
                        productsList.set(i, product);
                        adapter.notifyDataSetChanged();
                        setTextTotalPrice();
                    } else {
                        Toast.makeText(this, getString(R.string.is_not_decimal), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void changePayed() {
        View view = View.inflate(this, R.layout.item_dialog_text_amount, null);
        EditText amount = view.findViewById(R.id.amount);
        amount.addTextChangedListener(new PriceNumberTextWatcher(amount));
        amount.setText(bigDecimal(getPricePayed()+""));
        Log.d(TAG, "changePayed: "+bigDecimal(getPricePayed()+""));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_discount_or_payed, "پرداختی"))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    double d = Double.parseDouble(Tools.convertNumberToEN(amount.getText().toString().trim()));
                    if (!TextUtils.isEmpty(amount.getText().toString())) {
                        if (d <= getAllPrice()) {
                            _totalPayed = d;
                            setTextTotalPrice();
                        } else {
                            Toast.makeText(this, "مبلغ پرداختی نباید از مبلغ سفارش بیشتر باشد", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.is_not_decimal), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private String bigDecimal(String string){
        String scientificNumber = string;
        BigDecimal bd = new BigDecimal(scientificNumber);
        return bd.toPlainString();
    }

    @SuppressLint("SetTextI18n")
    private void changeDiscount() {
        View view = View.inflate(this, R.layout.item_dialog_text_amount, null);
        EditText amount = view.findViewById(R.id.amount);
        amount.addTextChangedListener(new PriceNumberTextWatcher(amount));
        amount.setText(_totalDiscount + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_discount_or_payed, "تخفیف"))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    double d = Double.parseDouble(Tools.convertNumberToEN(amount.getText().toString().trim()));
                    if (!TextUtils.isEmpty(amount.getText().toString())) {
                        if (d <= (getAllPrice() - getPricePayed())) {
                            _totalDiscount = d;
                            setTextTotalPrice();
                        } else {
                            Toast.makeText(this, "مبلغ تخفیف نباید از مبلغ سفارش بیشتر باشد", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.is_not_decimal), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void submit(View view) {
        if (!productsList.isEmpty()) {
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
                    db.deleteOrderByOrderCode(ORDER_DETAIL_CODE);
                    db.deleteOrderDetail(ORDER_DETAIL_CODE);
                    db.deleteTransactionByOrderCode(ORDER_UNIQUE_CODE);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss())
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
                .setInitDate(new PersianCalendar(ORDER_DATE)) // Initialize with current order date
                .setActionTextColor(Color.GRAY)
                .setTypeFace(ResourcesCompat.getFont(this, R.font.iranyekan_regular))
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                        ORDER_DATE = persianCalendar.getTimeInMillis();
                        dateToDay.setText(Tools.getFormattedDate(ORDER_DATE));
                        Log.d(TAG, "onDateSelected: New order date set to: " + ORDER_DATE);
                    }

                    @Override
                    public void onDismissed() {
                    }
                });
        picker.show();
    }
}