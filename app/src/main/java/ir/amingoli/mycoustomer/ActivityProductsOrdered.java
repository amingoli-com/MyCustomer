package ir.amingoli.mycoustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterProductsOrderd;
import ir.amingoli.mycoustomer.Adapter.AdapterReportOrder;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.OrderDetail;
import ir.amingoli.mycoustomer.model.Product;

public class ActivityProductsOrdered extends AppCompatActivity {

    private boolean ORDER_IS_PIED = false;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private AdapterProductsOrderd adapter;
    private ArrayList<Product> arrayList;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_ordered);
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
        adapter = new AdapterProductsOrderd(this, arrayList, this::showDetailOrder);
        recyclerView.setAdapter(adapter);
    }

    private void showOrderList(){
        arrayList.clear();
        List<Order> orders = db.getOrderList(ORDER_IS_PIED);
        if (!orders.isEmpty()){
            for (int i = 0; i < orders.size(); i++) {
                getProductOrder(orders.get(i).getId_order_detail());
            }
        }else {
            findViewById(R.id.include_empty).setVisibility(View.VISIBLE);
        }
    }

    private void getProductOrder(long ID_ORDER_DETAIL){
        List<OrderDetail> item = db.getOrderDetailListById(ID_ORDER_DETAIL);
        for (int i = 0; i < item.size(); i++) {
            Product product = new Product();
            product.setId(item.get(i).getId_product());
            product.setName(item.get(i).getName());
            product.setPrice(item.get(i).getPrice_item());
            product.setAmount(item.get(i).getAmount());
            product.setPrice_all(item.get(i).getPrice_all());
            addProductInOrder(product);
        }
    }

    private void showDetailOrder(Order order){
    }


    private void addProductInOrder(Product product) {
        if (arrayList.size() == 0){
            arrayList.add(product);
            adapter.notifyDataSetChanged();
        }else {
            for (int i = 0; i <= arrayList.size(); i++) {
                if (product.getId().equals(arrayList.get(i).getId())){
                    Double allAmount = arrayList.get(i).getAmount()+product.getAmount();
                    product.setAmount(allAmount);
                    if (product.getPrice_all() == 0.0 || product.getPrice_all() == null ){
                        product.setPrice_all(allAmount*product.getPrice());
                    }
                    arrayList.set(i,product);
                    adapter.notifyDataSetChanged();
                    return;
                }else if (i == arrayList.size()-1){
                    arrayList.add(product);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }
}