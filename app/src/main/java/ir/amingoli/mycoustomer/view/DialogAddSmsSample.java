package ir.amingoli.mycoustomer.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import ir.amingoli.mycoustomer.Adapter.AdapterSmsSample;
import ir.amingoli.mycoustomer.Adapter.AdapterSmsSampleData;
import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Tools;

public class DialogAddSmsSample extends AlertDialog{

    private Activity ac;
    private listener listener;
    private Transaction transaction;
    private RecyclerView recyclerView;
    private AdapterSmsSampleData adapter;
    private EditText name;
    private TextInputLayout name_lyt;
    private Button submit,remove;

    public DialogAddSmsSample(Activity a, listener listeners, Transaction transaction) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ac = a;
        this.listener = listeners;
        this.transaction = transaction;
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.item_dialog_add_sms_sample);

        name_lyt = findViewById(R.id.name_lyt);
        name = findViewById(R.id.customerName);
        submit = findViewById(R.id.submit);
        remove = findViewById(R.id.remove);
        recyclerView = findViewById(R.id.recyclerView);


        name.requestFocus();
        if (name.hasFocusable()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }



        ArrayList<Transaction> s = new ArrayList<>();
        s.add(new Transaction("[نام_مشتری]"));
        s.add(new Transaction("[نام_کسب_و_کار_شما]"));
        s.add(new Transaction("[تلفن_مشتری]"));
        s.add(new Transaction("[مبلغ_کل_سفارش]"));
        s.add(new Transaction("[مبلغ_تخفیف_سفارش]"));
        s.add(new Transaction("[مبلغ_پرداخت_شده]"));
        s.add(new Transaction("[مبلغ_مانده_سفارش]"));
        s.add(new Transaction("[مانده_قبل]"));
        s.add(new Transaction("[کل_بدهی_مشتری]"));
        s.add(new Transaction("[وضعیت_سفارش]"));
        s.add(new Transaction("[لیست_محصولات_کوتاه]"));
        s.add(new Transaction("[لیست_محصولات_جزییات]"));
        s.add(new Transaction("[لیست_محصولات_جزییات_کامل]"));
        s.add(new Transaction("[تاریخ_امروز]"));
        s.add(new Transaction("[تاریخ_ثبت_سفارش]"));
        adapter = new AdapterSmsSampleData(ac, s, new AdapterSmsSampleData.Listener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(Transaction transaction, String string) {
                Editable editableText = name.getText();
                int cursorPosition = name.getSelectionStart();
                editableText.insert(cursorPosition, transaction.getDesc());
            }
        });
        recyclerView.setAdapter(adapter);

        if (transaction != null){
            submit.setText("بروزرسانی");
            name.setText(transaction.getDesc());
            remove.setVisibility(View.VISIBLE);
        }else remove.setVisibility(View.GONE);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(getText(name))){
                    name_lyt.setError(ac.getString(R.string.error_name));
                    requestFocus(name);
                }else {
                    listener.result(name.getText().toString(), transaction);
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transaction != null) listener.remove(transaction);
            }
        });
    }

    public interface listener{
        void result(String text, Transaction transaction);
        void remove(Transaction transaction);
    }

    private String getText(EditText editText){
        return editText.getText().toString().trim();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setValue(Customer customer){
        name.setText(customer.getName());
        submit.setText(ac.getResources().getString(R.string.update));
    }
}