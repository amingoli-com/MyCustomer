package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.util.Tools;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler db;
    
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
        a();
    }

    private void a(){
        TextView unkow = findViewById(R.id.unkow);
        double total = 0;
        List<Order> orders;
        orders = db.getOrderList(true , Tools.convertDayToMillis(30));

        if (!orders.isEmpty()){
            for (int i = 0; i < orders.size(); i++) {
                total = total + orders.get(i).getPrice();
            }
        }else findViewById(R.id.include_empty).setVisibility(View.VISIBLE);

        unkow.setText(getString(R.string.uknow ,
                Tools.getFormattedPrice(total, this),
                Tools.getFormattedInteger(orders.size())
                ));
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