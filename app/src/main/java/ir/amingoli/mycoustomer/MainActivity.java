package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    TextView salesAll, salesToday, salesThisWeek, salesThisMonth;
    
    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
        setCount();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this);
        initId();
        initValue();
    }


    private GetTotalSales getTotalSales(int howDays){
        GetTotalSales model = new GetTotalSales();
        double total = 0;
        List<Order> orders;
        if (howDays == 0){
            orders = db.getOrderList(true);
        }else {
            orders = db.getOrderList(true , Tools.convertDayToMillis(howDays));
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

    private void initId(){
        salesAll = findViewById(R.id.salesAll);
        salesToday = findViewById(R.id.salesToday);
        salesThisWeek = findViewById(R.id.salesThisWeek);
        salesThisMonth = findViewById(R.id.salesThisMonth);
    }

    @SuppressLint("StringFormatMatches")
    private void initValue(){
        GetTotalSales all = getTotalSales(0);
        GetTotalSales today = getTotalSales(1);
        GetTotalSales thisWeek = getTotalSales(7);
        GetTotalSales thisMonth = getTotalSales(30);
        if (all != null)
            salesAll.setText(getString(R.string.sales_all,
                    Tools.getFormattedPrice(all.getTotalSales(),this),
                    Tools.getFormattedInteger(all.getSalesCount())));
        if (today != null)
            salesToday.setText(getString(R.string.sales_tody,
                    Tools.getFormattedPrice(today.getTotalSales(),this),
                    Tools.getFormattedInteger(today.getSalesCount())));
        if (thisWeek != null)
            salesThisWeek.setText(getString(R.string.sales_this_week,
                    Tools.getFormattedPrice(thisWeek.getTotalSales(),this),
                    Tools.getFormattedInteger(thisWeek.getSalesCount())));
        if (thisMonth != null)
            salesThisMonth.setText(getString(R.string.sales_this_month,
                    Tools.getFormattedPrice(thisMonth.getTotalSales(),this),
                    Tools.getFormattedInteger(thisMonth.getSalesCount())));
    }


    @SuppressLint("SetTextI18n")
    private void setCount(){
        /*TextView totalProduct = findViewById(R.id.total_product);
        TextView totalCustomer = findViewById(R.id.total_customer);
        TextView totalOrderPied = findViewById(R.id.total_orderPied);
        TextView totalOrderWaiting = findViewById(R.id.total_orderWaiting);
        TextView total_productOrdered = findViewById(R.id.total_productOrdered);

        totalProduct.setText(db.getProductSize() +" "+ getResources().getString(R.string.stock));
        totalCustomer.setText(db.getCustomerSize() +" "+ getResources().getString(R.string.customer));
        totalOrderPied.setText(db.getOrderIsPiedSize() +" ");
        totalOrderWaiting.setText(db.getOrderIsWaitingSize() +" ");
        total_productOrdered.setText(getResources().getString(R.string.from)+" "+ db.getOrderIsWaitingSize() +" "+ getResources().getString(R.string.order));*/
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
}