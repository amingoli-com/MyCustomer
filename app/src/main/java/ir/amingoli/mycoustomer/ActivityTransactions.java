package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterTransaction;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Transaction;

public class ActivityTransactions extends AppCompatActivity {

    private View include_load,include_empty,include_search;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterTransaction adapter;
    private ArrayList<Transaction> arrayList;
    private long CUSTOMER_ID = 0;
    private long TYPE = -1;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        CUSTOMER_ID = getIntent().getLongExtra("id_customer",0);
        TYPE = getIntent().getLongExtra("type",-1);

        populateData();
        initAdapter();
        loadTransaction();

        TextView t = include_empty.findViewById(R.id.text);
        if (CUSTOMER_ID != 0){
            t.setText("هوراا‌\n"+db.getCustomer(CUSTOMER_ID).getName()+" هیچ بدهی ندارد‌ :)");
        }else {
            t.setText("هورااا\nهیچ بدهی وجود نداره!");
        }
    }

    private void populateData(){
        db = new DatabaseHandler(this);
        include_search = findViewById(R.id.include_search);
        include_load = findViewById(R.id.include_load);
        include_empty = findViewById(R.id.include_empty);
        recyclerView = findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
    }

    private void initAdapter(){
        adapter = new AdapterTransaction(this, arrayList, new AdapterTransaction.Listener() {
            @Override
            public void onClick(Transaction transaction) {
                if (transaction != null){
                    Intent intent = new Intent(ActivityTransactions.this,ActivityAddOrder.class);
                    intent.putExtra("id_customer",transaction.getId_customer());
                    intent.putExtra("id_order_detail",transaction.getId_order());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void loadTransaction() {
        arrayList.clear();
        List<Transaction> items;
        items = db.getAllTransactionByCustomer(TYPE,CUSTOMER_ID);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getAmount() > 0){
                arrayList.add(new Transaction(items.get(i).getId(),items.get(i).getId_customer(),
                        items.get(i).getId_order(),items.get(i).getType(),items.get(i).getAmount(),
                        ""+db.getCustomer(items.get(i).getId_customer()).name
                        ,items.get(i).getCreated_at()));
            }
        }
        if (adapter!=null) adapter.notifyDataSetChanged();
        include_load.setVisibility(View.GONE);
        if (arrayList.isEmpty())
            include_empty.setVisibility(View.VISIBLE); else include_empty.setVisibility(View.GONE);
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