package ir.amingoli.mycoustomer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Tools;


public class ActivityCustomerDetail extends AppCompatActivity {
    private Long CUSTOMER_ID = null;
    private DatabaseHandler db;
    private List<Customer> customerList;

    private TextView id,name,tel,desc;
    private TextView waiting_order,last_pied,total_pied,totalBedehi,totalDiscount;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
        initTotalWaitingOrder();
        initLastPied();
        initTotalPricePied();
        initTotalBedehi();
        initTotalDiscount();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail); // set layout
        TRANSPARENTTOOLBAR(); // remove toolbar
        CUSTOMER_ID = getIntent().getLongExtra("id_customer",0);
        db = new DatabaseHandler(this);

        initDataCustomer();
    }

    public void addOrder(View view) {
        Intent intent = new Intent(this,ActivityAddOrder.class);
        intent.putExtra("id_customer",CUSTOMER_ID);
        startActivity(intent);
    }

    private void initTotalWaitingOrder(){
        waiting_order = findViewById(R.id.waiting_order);
        waiting_order.setText(db.getOrderIsWaitingSize(CUSTOMER_ID)+"");
    }

    private void initLastPied(){
        last_pied = findViewById(R.id.last_pied);
        if (db.getOrderLastPiedByIdCustomer(CUSTOMER_ID) == null){
            last_pied.setText(getResources().getString(R.string.no_order_registered));
        }else {
            last_pied.setText(Tools.getFormattedDate(db.getOrderLastPiedByIdCustomer(CUSTOMER_ID)));
        }
    }

    private void initTotalBedehi(){
        totalBedehi = findViewById(R.id.totalBedehi);
        double tb = 0.0;
        List<Transaction> bedehi = db.getTransactionBedehiCustomer(Tools.TRANSACTION_TYPE_BEDEHI,CUSTOMER_ID);
        for (int i = 0; i < bedehi.size(); i++) {
            tb = tb + bedehi.get(i).getAmount();
        }

        double tbpayed = 0.0;
        List<Transaction> bedehiPayed = db.getTransactionBedehiCustomer(
                Tools.TRANSACTION_TYPE_PAY_BEDEHI_BY_OTHER_METHODE,CUSTOMER_ID);
        for (int i = 0; i < bedehiPayed.size(); i++) {
            tbpayed = tbpayed + bedehiPayed.get(i).getAmount();
        }

        double r = tb-tbpayed;
        if (r < 0) r = 0;
        totalBedehi.setText(Tools.getFormattedPrice(r,this));
    }

    private void initTotalDiscount(){
        totalDiscount = findViewById(R.id.totalDiscount);
        double tb = 0.0;
        List<Transaction> discount = db.getTransactionBedehiCustomer(Tools.TRANSACTION_TYPE_PAY_DISCOUNT,CUSTOMER_ID);
        for (int i = 0; i < discount.size(); i++) {
            tb = tb + discount.get(i).getAmount();
        }
        totalDiscount.setText(Tools.getFormattedPrice(tb,this));
    }

    private void initTotalPricePied(){
        double totalPrice = 0.0;
        total_pied = findViewById(R.id.total_pied);
        List<Order> orders = db.getOrderListByCustomerId(CUSTOMER_ID,true);
        for (int i = 0; i < orders.size(); i++) {
            totalPrice = totalPrice + orders.get(i).getPrice();
        }
        total_pied.setText(Tools.getFormattedPrice(totalPrice,this));
    }

    private void initDataCustomer(){
        id = findViewById(R.id.customerId);
        name = findViewById(R.id.customerName);
        tel = findViewById(R.id.customerTel);
        desc = findViewById(R.id.desc);
        customerList = db.getCustomerById(CUSTOMER_ID);
        id.setText(CUSTOMER_ID+"");
        name.setText(customerList.get(0).getName());
        tel.setText(Tools.formatPhoneNumber(customerList.get(0).getTel()));
        desc.setText(customerList.get(0).getDesc());
    }
    /**
     * Tools
     * */
    //  making toolbar transparent
    private void TRANSPARENTTOOLBAR() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            SETWIMDOWFLAG(this, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            SETWIMDOWFLAG(this, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private void SETWIMDOWFLAG(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        } else {
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        win.setAttributes(winParams);
    }

    public void waitingOrder(View view) {
        Intent intent = new Intent(this,ActivityReportOrder.class);
        intent.putExtra("order_status",false);
        intent.putExtra("id_customer",CUSTOMER_ID);
        startActivity(intent);
    }

    public void totalPied(View view) {
        Intent intent = new Intent(this,ActivityReportOrder.class);
        intent.putExtra("order_status",true);
        intent.putExtra("id_customer",CUSTOMER_ID);
        startActivity(intent);
    }

    public void showTransActionList(View view) {
        Intent intent = new Intent(this,ActivityTransactions.class);
        intent.putExtra("id_customer",CUSTOMER_ID);
        intent.putExtra("type",Tools.TRANSACTION_TYPE_BEDEHI);
        startActivity(intent);
    }

    public void lastPied(View view) {
    }
}