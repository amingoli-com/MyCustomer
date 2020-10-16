package ir.amingoli.mycoustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterProduct;
import ir.amingoli.mycoustomer.Adapter.AdapterReportOrder;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.util.Tools;

public class ActivityReportOrder extends AppCompatActivity {

    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private AdapterReportOrder adapter;
    private ArrayList<Order> arrayList;
    private boolean ORDER_IS_PIED = true;
    private long CUSTOMER_ID = 0;
    private boolean activityIsCrated = false;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
        showOrderList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_order);
        ORDER_IS_PIED = getIntent().getBooleanExtra("order_status",true);
        CUSTOMER_ID = getIntent().getLongExtra("id_customer",0);
        populateData();
        initAdapter();
        showOrderList();
    }

    private void populateData(){
        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
    }

    private void initAdapter(){
        adapter = new AdapterReportOrder(this, arrayList, this::showDetailOrder);
        recyclerView.setAdapter(adapter);
    }

    private void showOrderList(){
        arrayList.clear();
        List<Order> orders;
        if (CUSTOMER_ID != 0){
            orders = db.getOrderListByCustomerId(CUSTOMER_ID,ORDER_IS_PIED);
        } else orders = db.getOrderList(true , Tools.convertDayToMillis(3));

        if (!orders.isEmpty()){
            for (int i = 0; i < orders.size(); i++) {
                Order item = new Order();
                item.setId(orders.get(i).getId());
                item.setId_coustomer(orders.get(i).getId_coustomer());
                item.setId_order_detail(orders.get(i).getId_order_detail());
                item.setCustomer_name(getCustomerNameById(orders.get(i).getId_coustomer()));
                item.setPrice(orders.get(i).getPrice());
                item.setCreated_at(orders.get(i).getCreated_at());
                arrayList.add(item);
            }
        }else findViewById(R.id.include_empty).setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private String getCustomerNameById(long idCustomer){
        List<Customer> customers = db.getCustomerById(idCustomer);
        return customers.get(0).getName();
    }

    private void showDetailOrder(Order order){
        Intent intent = new Intent(this,ActivityAddOrder.class);
        intent.putExtra("status",ORDER_IS_PIED);
        intent.putExtra("id_customer",order.getId_coustomer());
        intent.putExtra("id_order",order.getId());
        intent.putExtra("id_order_detail",order.getId_order_detail());
        intent.putExtra("crated_at",order.getCreated_at());
        startActivity(intent);
    }
}