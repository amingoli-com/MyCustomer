package ir.amingoli.mycoustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterCustomer;
import ir.amingoli.mycoustomer.data.Constant;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;

public class ActivityCustomer extends AppCompatActivity {

    private View include_search,include_empty,include_load;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterCustomer adapter;
    private List<Customer> arrayList;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        populateData();
        initAdapter();
        initSearchView();
        loadCustomerListByNameOrTel("",false);
    }


    private void populateData(){
        db = new DatabaseHandler(this);
        include_search = findViewById(R.id.include_search);
        include_load = findViewById(R.id.include_load);
        include_empty = findViewById(R.id.include_empty);
        searchView = include_search.findViewById(R.id.searchView);
        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initAdapter(){
        adapter = new AdapterCustomer(this, arrayList, this::goToActivityCustomerDetail);
        recyclerView.setAdapter(adapter);
    }

    private void initSearchView(){
        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadCustomerListByNameOrTel(query,false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                include_load.setVisibility(View.VISIBLE);
                if (query.matches("[0-9]+") && query.length() > 0) {
                    loadCustomerListByNameOrTel(query,true);
                }else {
                    loadCustomerListByNameOrTel(query,false);
                }
                return false;
            }
        });
    }

    private void loadCustomerListByNameOrTel(String keyword,boolean searchByTel) {
        arrayList.clear();
        List<Customer> items;
        if (searchByTel){
            items = db.getCustomerByTel(keyword);
        }else {
            items = db.getCustomerByName(keyword);
        }
        for (int i = 0; i < items.size(); i++) {
            arrayList.add(new Customer(items.get(i).getId(),items.get(i).getName(),items.get(i).getTel(),items.get(i).getDesc()));
        }
        if (adapter!=null) adapter.notifyDataSetChanged();
        include_load.setVisibility(View.GONE);
        if (arrayList.isEmpty())
            include_empty.setVisibility(View.VISIBLE); else include_empty.setVisibility(View.GONE);
    }

    private void dialogAddCustomer(){
        View itemView_DialogAddCustomer = View.inflate(this, R.layout.item_dialog_add_customer, null);
        EditText customerName = itemView_DialogAddCustomer.findViewById(R.id.customerName);
        EditText customerNumber = itemView_DialogAddCustomer.findViewById(R.id.customerNumber);
        EditText customerDesc = itemView_DialogAddCustomer.findViewById(R.id.customerDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.add_customer));
        builder.setView(itemView_DialogAddCustomer)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.add), (dialog, id) -> {
                    Customer customer = new Customer();
                    customer.setName(customerName.getText().toString());
                    customer.setTel(customerNumber.getText().toString());
                    customer.setDesc(customerDesc.getText().toString());
                    db.saveCustomer(customer);
                    loadCustomerListByNameOrTel("",false);
                    recyclerView.scrollToPosition(arrayList.size()-1);
                    dialog.dismiss();
                });
        builder.show();
    }

    private void goToActivityCustomerDetail(Customer item){
        Intent intent = new Intent(this,ActivityCustomerDetail.class);
        intent.putExtra("id_customer",item.getId());
        startActivity(intent);
    }

    public void addCustomer(View view) {
        dialogAddCustomer();
    }
}