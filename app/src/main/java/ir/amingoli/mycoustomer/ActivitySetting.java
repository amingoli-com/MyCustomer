package ir.amingoli.mycoustomer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.amingoli.mycoustomer.Adapter.AdapterSmsSample;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.DatabaseBackupRestore;
import ir.amingoli.mycoustomer.util.Tools;
import ir.amingoli.mycoustomer.view.DialogAddSmsSample;

public class ActivitySetting extends AppCompatActivity {

    private DatabaseBackupRestore dbBackupRestore;

    private TextView addSmsSample;
    private RecyclerView recyclerView;
    private DialogAddSmsSample d,d1;
    private DatabaseHandler db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dbBackupRestore = new DatabaseBackupRestore(this, this);

        TextView btnBackup = findViewById(R.id.backup);
        TextView btnRestore = findViewById(R.id.restore);

        btnBackup.setOnClickListener(v -> {
            if (checkPermissions()) dbBackupRestore.backupDatabase();
        });
        btnRestore.setOnClickListener(v -> {
            if(checkPermissions()) dbBackupRestore.restoreDatabase();
        });


        db = new DatabaseHandler(this);
        addSmsSample = findViewById(R.id.addSmsSample);
        recyclerView = findViewById(R.id.recyclerView);

        addSmsSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("amingoli-a-s", "DialogAddSmsSample new DialogAddSmsSample: ");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dbBackupRestore.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
            return false;
        }
        return true;
    }

    private void initAdapter(){
        ArrayList<Transaction> transactionsList =
                (ArrayList<Transaction>) db.getAllTransactionByType(Tools.TRANSACTION_TYPE_SMS_SAMPLE);

        AdapterSmsSample adapter = new AdapterSmsSample(this, transactionsList, (transaction, string) -> {

            Log.d("amingoli-a-s", "new AdapterSmsSample->DialogAddSmsSample : ");
            d1 = new DialogAddSmsSample(ActivitySetting.this, new DialogAddSmsSample.listener() {
                @Override
                public void result(String text, Transaction transaction) {
                    Transaction t = new Transaction();
                    if (transaction != null) t.setId(transaction.getId());
                    t.setType(Tools.TRANSACTION_TYPE_SMS_SAMPLE);
                    Log.d("amingoli-a-s", "DialogAddSmsSample result: "+text);
                    t.setDesc(text);
                    db.saveTransaction(t);
                    initAdapter();
                    d1.dismiss();
                }

                @Override
                public void remove(Transaction transaction) {
                    db.deleteTransactionByID(transaction.getId());
                    initAdapter();
                    d1.dismiss();
                }
            }, transaction);
            d1.show();

            Log.d("amingoli-a-s", "DialogAddSmsSample: ");
        });
        recyclerView.setAdapter(adapter);
    }
}