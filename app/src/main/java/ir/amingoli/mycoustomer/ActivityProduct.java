package ir.amingoli.mycoustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterProduct;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.util.PriceNumberTextWatcher;
import ir.amingoli.mycoustomer.util.Tools;
import ir.amingoli.mycoustomer.view.DialogAddProduct;

public class ActivityProduct extends AppCompatActivity {

    private View include_load,include_empty,include_search;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterProduct adapter;
    private ArrayList<Product> arrayList;
    private Boolean GET_PRODUCT = false;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        GET_PRODUCT = getIntent().getBooleanExtra("get_product",false);

        populateData();
        initAdapter();
        initSearchView();
        loadProductListByNameOrPrice("",false);
    }

    private void populateData(){
        db = new DatabaseHandler(this);
        include_search = findViewById(R.id.include_search);
        include_load = findViewById(R.id.include_load);
        include_empty = findViewById(R.id.include_empty);
        searchView = include_search.findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
    }

    private void initAdapter(){
        adapter = new AdapterProduct(this, arrayList, this::dialogAddOrEditProduct);
        recyclerView.setAdapter(adapter);
    }

    private void initSearchView(){
        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadProductListByNameOrPrice(query,false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                include_load.setVisibility(View.VISIBLE);
                if (query.matches("[0-9]+") && query.length() > 0) {
                    loadProductListByNameOrPrice(query,true);
                }else {
                    loadProductListByNameOrPrice(query,false);
                }
                return false;
            }
        });
    }

    private void loadProductListByNameOrPrice(String keyword, boolean searchByPrice) {
        arrayList.clear();
        List<Product> items;
        if (searchByPrice){
            items = db.getProductByPrice(keyword);
        }else {
            items = db.getProductByName(keyword);
        }
        for (int i = 0; i < items.size(); i++) {
            arrayList.add(new Product(items.get(i).getId(),items.get(i).getName(),items.get(i).getPrice(),items.get(i).getAmount()));
        }
        if (adapter!=null) adapter.notifyDataSetChanged();
        include_load.setVisibility(View.GONE);
        if (arrayList.isEmpty())
            include_empty.setVisibility(View.VISIBLE); else include_empty.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void dialogAddOrEditProduct(Product value){
        DialogAddProduct dialogAddProduct = new DialogAddProduct(this, value, new DialogAddProduct.listener() {
            @Override
            public void addOrUpdate(Product product) {
                db.saveProduct(product);
                loadProductListByNameOrPrice("",false);
                recyclerView.scrollToPosition(arrayList.size()-1);
            }

            @Override
            public void remove(Product product) {
                dialogDoYouWantToRemoveThisProduct(product);
            }
        });
        dialogAddProduct.show();
    }
    private void dialogDoYouWantToRemoveThisProduct(Product product){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.do_you_want_remove_this_product,product.getName()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    db.deleteProduct(product.getId());
                    loadProductListByNameOrPrice("",false);
                })
                .setNegativeButton(getString(R.string.no),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    public void addCustomer(View view) {
        dialogAddOrEditProduct(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }
}