package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterSmsSample;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Tools;
import ir.amingoli.mycoustomer.view.DialogAddSmsSample;

public class ActivitySetting extends AppCompatActivity {


    TextView addSmsSample;
    RecyclerView recyclerView;
    AdapterSmsSample adapter;
    DialogAddSmsSample d;
    private DatabaseHandler db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        db = new DatabaseHandler(this);
        addSmsSample = findViewById(R.id.addSmsSample);
        recyclerView = findViewById(R.id.recyclerView);

        addSmsSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d = new DialogAddSmsSample(ActivitySetting.this, new DialogAddSmsSample.listener() {
                    @Override
                    public void result(String text, Transaction transaction) {
                        Transaction t = new Transaction();
                        if (transaction != null) t.setId(transaction.getId());
                        t.setType(Tools.TRANSACTION_TYPE_SMS_SAMPLE);
                        Log.d("amingoli-a-s", "DialogAddSmsSample result: "+text);
                        t.setDesc(text);
                        db.saveTransaction(t);
                        initAdapter();
                        d.dismiss();
                    }

                    @Override
                    public void remove(Transaction transaction) {

                    }
                }, null);
                d.show();
            }
        });
        initAdapter();
    }

    private void initAdapter(){
        ArrayList<Transaction> transactionsList =
                (ArrayList<Transaction>) db.getAllTransactionByType(Tools.TRANSACTION_TYPE_SMS_SAMPLE);

        adapter = new AdapterSmsSample(this, transactionsList, new AdapterSmsSample.Listener() {
            @Override
            public void onClick(Transaction transaction) {
                d = new DialogAddSmsSample(ActivitySetting.this, new DialogAddSmsSample.listener() {
                    @Override
                    public void result(String text, Transaction transaction) {
                        Transaction t = new Transaction();
                        if (transaction != null) t.setId(transaction.getId());
                        t.setType(Tools.TRANSACTION_TYPE_SMS_SAMPLE);
                        Log.d("amingoli-a-s", "DialogAddSmsSample result: "+text);
                        t.setDesc(text);
                        db.saveTransaction(t);
                        initAdapter();
                        d.dismiss();
                    }

                    @Override
                    public void remove(Transaction transaction) {
                        db.deleteTransactionByID(transaction.getId());
                        initAdapter();
                        d.dismiss();
                    }
                }, transaction);
                d.show();
            }

            @Override
            public void onClickEdit(String string) {

            }
        });
        recyclerView.setAdapter(adapter);
    }
}