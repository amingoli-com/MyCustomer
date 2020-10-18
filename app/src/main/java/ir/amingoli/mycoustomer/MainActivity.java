package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.List;

import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.GetTotalSales;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.util.Tools;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler db;
    TextView salesAll, salesToday, salesThisWeek, salesThisMonth ,
             totalCustomers, totalProduct, totalOrder, totalDetailOrder;
    View live_salesAll,live_salesToday,live_salesThisWeek,live_salesThisMonth;
    
    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this);
    }
    private void initData(){
        initId();
        initValue();
    }

    private void initId(){
        salesAll = findViewById(R.id.salesAll);
        salesToday = findViewById(R.id.salesToday);
        salesThisWeek = findViewById(R.id.salesThisWeek);
        salesThisMonth = findViewById(R.id.salesThisMonth);

        live_salesAll = findViewById(R.id.live_salesAll);
        live_salesToday = findViewById(R.id.live_salesToday);
        live_salesThisWeek = findViewById(R.id.live_salesThisWeek);
        live_salesThisMonth = findViewById(R.id.live_salesThisMonth);

        totalCustomers = findViewById(R.id.totalCustomers);
        totalProduct = findViewById(R.id.totalProducts);
        totalOrder = findViewById(R.id.totalOrders);
        totalDetailOrder = findViewById(R.id.totalDetailOrder);

    }

    @SuppressLint("StringFormatMatches")
    private void initValue(){
        GetTotalSales all = getTotalSales(0);
        GetTotalSales today = getTotalSales(1);
        GetTotalSales thisWeek = getTotalSales(7);
        GetTotalSales thisMonth = getTotalSales(30);
        if (all != null) {
            salesAll.setText(getString(R.string.sales_all,
                    Tools.getFormattedPrice(all.getTotalSales(), this),
                    Tools.getFormattedInteger(all.getSalesCount())));
        } else live_salesAll.setVisibility(View.GONE);
        if (today != null) {
            salesToday.setText(getString(R.string.sales_tody,
                    Tools.getFormattedPrice(today.getTotalSales(), this),
                    Tools.getFormattedInteger(today.getSalesCount())));
        } else live_salesToday.setVisibility(View.GONE);
        if (thisWeek != null) {
            salesThisWeek.setText(getString(R.string.sales_this_week,
                    Tools.getFormattedPrice(thisWeek.getTotalSales(), this),
                    Tools.getFormattedInteger(thisWeek.getSalesCount())));
        } else live_salesThisWeek.setVisibility(View.GONE);
        if (thisMonth != null) {
            salesThisMonth.setText(getString(R.string.sales_this_month,
                    Tools.getFormattedPrice(thisMonth.getTotalSales(), this),
                    Tools.getFormattedInteger(thisMonth.getSalesCount())));
        } else live_salesThisMonth.setVisibility(View.GONE);

        totalCustomers.setText(getString(R.string.total_customer ,
                Tools.getFormattedInteger(db.getCustomerSize())));
        totalOrder.setText(getString(R.string.total_order ,
                Tools.getFormattedInteger(db.getOrderIsWaitingSize())));
        totalDetailOrder.setText(getString(R.string.total_detail_order ,
                Tools.getFormattedInteger(db.getOrderIsWaitingSize())));
        totalProduct.setText(getString(R.string.total_product ,
                Tools.getFormattedInteger(db.getProductSize())));
    }

    private GetTotalSales getTotalSales(int howDays){
        GetTotalSales model = new GetTotalSales();
        double total = 0;
        List<Order> orders;
        if (howDays == 0){
            orders = db.getOrderList(true);
        }else {
            orders = db.getOrderList(true , Tools.dayToMillis(howDays));
        }
        if (!orders.isEmpty()){
            for (int i = 0; i < orders.size(); i++) {
                total = total + orders.get(i).getPrice();
            }
            model.setTotalSales(total);
            model.setSalesCount(orders.size());
            return model;
        }
        return null;
    }

//    onClick
    public void goToActitvityProduct(View view) {
        startActivity(new Intent(this,ActivityProduct.class));
    }

    public void goToActitvityCustomer(View view) {
        startActivity(new Intent(this,ActivityCustomer.class));
    }

    public void goToActivityOrderIsPied(View view) {
        Intent intent = new Intent(this,ActivityReportOrder.class);
        intent.putExtra("order_status",true);
        if (!TextUtils.isEmpty(view.getTag().toString()))
            intent.putExtra("filter",Integer.valueOf(view.getTag().toString()));
        startActivity(intent);
    }

    public void goToActivityOrderIsNotPied(View view) {
        Intent intent = new Intent(this,ActivityReportOrder.class);
        intent.putExtra("order_status",false);
        startActivity(intent);
    }

    public void goToActivityProductsOrdered(View view) {
        Intent intent = new Intent(this,ActivityProductsOrdered.class);
        startActivity(intent);
    }

    public void addOrder(View view) {
        Intent intent = new Intent(this,ActivityCustomer.class);
        intent.putExtra("add_order",true);
        startActivity(intent);
    }
}