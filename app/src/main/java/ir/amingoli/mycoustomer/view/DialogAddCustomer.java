package ir.amingoli.mycoustomer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.model.Customer;

public class DialogAddCustomer extends AlertDialog implements
        View.OnClickListener {

    private Activity ac;
    private listener listener;
    private EditText name, desc, tel;
    private TextInputLayout name_lyt, desc_lyt, tel_lyt;
    private Button submit;
    private boolean auto_cancelable = true;

    public DialogAddCustomer(Activity a, listener listeners) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ac = a;
        this.listener = listeners;
    }

    public DialogAddCustomer(Activity a, listener listeners , boolean autoCancelable) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ac = a;
        this.listener = listeners;
        this.auto_cancelable = autoCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.item_dialog_add_customer);
        name = findViewById(R.id.customerName);
        desc = findViewById(R.id.customerDesc);
        tel = findViewById(R.id.customerNumber);

        name_lyt = findViewById(R.id.name_lyt);
        desc_lyt = findViewById(R.id.desc_lyt);
        tel_lyt = findViewById(R.id.tel_lyt);


        name.requestFocus();
        if (name.hasFocusable()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        setCancelable(auto_cancelable);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(getText(name))){
            name_lyt.setError(ac.getString(R.string.error_name_customer));
            requestFocus(name);
        } else if (getText(tel).length() < 7){
            name_lyt.setErrorEnabled(false);
            tel_lyt.setError(ac.getString(R.string.error_tel_customer));
            requestFocus(tel);
        } else {
            tel_lyt.setErrorEnabled(false);
            Customer customer = new Customer(getText(name),getText(desc),getText(tel));
            listener.result(customer);
            dismiss();
        }

    }

    public interface listener{
        void result(Customer customer);
    }

    private String getText(EditText editText){
        return editText.getText().toString();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}