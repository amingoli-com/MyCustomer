package ir.amingoli.mycoustomer.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
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
import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.model.Customer;

public class DialogAddSmsSample extends AlertDialog implements
        View.OnClickListener {

    private Activity ac;
    private listener listener;
    private RecyclerView recyclerView;
    private AdapterSmsSample adapter;
    private EditText name;
    private TextInputLayout name_lyt;
    private Button submit;

    public DialogAddSmsSample(Activity a, listener listeners) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ac = a;
        this.listener = listeners;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.item_dialog_add_sms_sample);

        name_lyt = findViewById(R.id.name_lyt);
        name = findViewById(R.id.customerName);
        submit = findViewById(R.id.submit);
        recyclerView = findViewById(R.id.recyclerView);


        name.requestFocus();
        if (name.hasFocusable()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }




        ArrayList<String> s = new ArrayList<>();
        s.add("[نام_مشتری]");
        s.add("[تلفن_مشتری]");
        s.add("[مبلغ_کل_سفارش]");
        s.add("[مبلغ_تخفیف_سفارش]");
        s.add("[مبلغ_پرداخت_شده]");
        s.add("[مبلغ_مانده_سفارش]");
        s.add("[کل_بدهی_مشتری]");
        s.add("[وضعیت_سفارش]");
        s.add("[لیست_محصولات]");
        s.add("[تاریخ_امروز]");
        s.add("[تاریخ_ثبت_سفارش]");
        adapter = new AdapterSmsSample(ac, s, new AdapterSmsSample.Listener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(String string) {
                name.setText(name.getText().toString()+string);
            }

            @Override
            public void onClickEdit(String string) {

            }
        });
        recyclerView.setAdapter(adapter);


        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(getText(name))){
            name_lyt.setError(ac.getString(R.string.error_name));
            requestFocus(name);
        }

    }

    public interface listener{
        void result(Customer customer);
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