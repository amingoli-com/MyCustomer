package ir.amingoli.mycoustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
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
        loadProductListByNameOrTel("",false);
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
                loadProductListByNameOrTel(query,false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                include_load.setVisibility(View.VISIBLE);
                if (query.matches("[0-9]+") && query.length() > 0) {
                    loadProductListByNameOrTel(query,true);
                }else {
                    loadProductListByNameOrTel(query,false);
                }
                return false;
            }
        });
    }

    private void loadProductListByNameOrTel(String keyword, boolean searchByTel) {
        arrayList.clear();
        List<Product> items;
        if (searchByTel){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title_dialog = getResources().getString(R.string.add_product);
        String title_button_add = getResources().getString(R.string.add);
        if (GET_PRODUCT && value != null){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("product_id", value.getId());
            resultIntent.putExtra("product_name", value.getName());
            resultIntent.putExtra("product_price", value.getPrice());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }else {
            View itemView_DialogAddProduct = View.inflate(this, R.layout.item_dialog_add_product, null);
            EditText name = itemView_DialogAddProduct.findViewById(R.id.name);
            EditText price = itemView_DialogAddProduct.findViewById(R.id.price);
            price.addTextChangedListener(new PriceNumberTextWatcher(price));
//            EditText amount = itemView_DialogAddProduct.findViewById(R.id.amount);
            builder.setView(itemView_DialogAddProduct);

            if (value !=null){
                name.setText(value.getName());
                price.setText(Tools.getFormattedPrice(value.getPrice(),this));
//                amount.setText(value.getAmount()+"");
                title_dialog = getResources().getString(R.string.update_product);
                title_button_add = getResources().getString(R.string.update);
                builder.setNegativeButton(getString(R.string.remove), (dialogInterface, i) -> {
                    dialogDoYouWantToRemoveThisProduct(value);
                });
            }
            builder.setTitle(title_dialog)
                    .setCancelable(true)
                    .setPositiveButton(title_button_add, (dialog, id) -> {
                        Product product = new Product();
                        product.setName(name.getText().toString());
                        product.setPrice(
                                Double.valueOf(Tools.convertNumberToEN(price.getText().toString())));
                        product.setAmount(0.0);
//                        product.setAmount(Double.valueOf(amount.getText().toString()));
                        if (value != null) product.setId(value.getId());
                        db.saveProduct(product);
                        loadProductListByNameOrTel("",false);
                        recyclerView.scrollToPosition(arrayList.size()-1);
                        dialog.dismiss();
                    }).show();
        }
    }
    private void dialogAddOrEditProduct(){
        dialogAddOrEditProduct(null);
    }
    private void dialogDoYouWantToRemoveThisProduct(Product product){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.do_you_want_remove_this_product,product.getName()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    db.deleteProduct(product.getId());
                    loadProductListByNameOrTel("",false);
                })
                .setNegativeButton(getString(R.string.no),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    public void addCustomer(View view) {
        dialogAddOrEditProduct();
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