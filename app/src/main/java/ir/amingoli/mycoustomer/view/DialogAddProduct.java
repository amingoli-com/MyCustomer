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
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.util.PriceNumberTextWatcher;
import ir.amingoli.mycoustomer.util.Tools;

public class DialogAddProduct extends AlertDialog {

    private Activity ac;
    private listener listener;
    private EditText name, price, amount;
    private TextInputLayout name_lyt, price_lyt, amount_lyt;
    private Button submit,remove;
    private Product product = null;

    public DialogAddProduct(Activity a, listener listeners) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ac = a;
        this.listener = listeners;
    }

    public DialogAddProduct(Activity a, Product customer, listener listeners) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ac = a;
        this.listener = listeners;
        this.product = customer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.item_dialog_add_product);

        name_lyt = findViewById(R.id.name_lyt);
        price_lyt = findViewById(R.id.price_lyt);
        amount_lyt = findViewById(R.id.amount_lyt);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        amount = findViewById(R.id.amount);
        remove = findViewById(R.id.remove);
        submit = findViewById(R.id.submit);

        price.addTextChangedListener(new PriceNumberTextWatcher(price));

        if (product != null) setValue(product);

        name.requestFocus();
        if (name.hasFocusable()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        submit.setOnClickListener(view -> onClickSubmit());
        remove.setOnClickListener(view -> onClickRemove());
    }

    private void onClickSubmit(){
        if (TextUtils.isEmpty(getText(name))){
            name_lyt.setError(ac.getString(R.string.error_name));
            requestFocus(name);
        } else if (getText(price).length() <= 1){
            name_lyt.setErrorEnabled(false);
            price_lyt.setError(ac.getString(R.string.error_price));
            requestFocus(price);
        } else {
            price_lyt.setErrorEnabled(false);
            if (product == null){
                product = new Product();
                product.setName(getText(name));
                product.setPrice(Double.parseDouble(Tools.convertNumberToEN(getText(price))));
                product.setAmount(0.0);
            }else {
                product.setName(getText(name));
                product.setPrice(Double.parseDouble(Tools.convertNumberToEN(getText(price))));
            }
            listener.addOrUpdate(product);
            dismiss();
        }
    }

    private void onClickRemove(){
        listener.remove(product);
        dismiss();
    }

    public interface listener{
        void addOrUpdate(Product product);
        void remove(Product product);
    }

    private String getText(EditText editText){
        return editText.getText().toString().trim();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setValue(Product v){
        name.setText(v.getName());
        price.setText(Tools.getFormattedPrice(v.getPrice(),ac));
        amount.setText(Tools.getFormattedDiscount(v.getAmount()));
        submit.setText(ac.getResources().getString(R.string.update));
        if (v.getId() != null) remove.setVisibility(View.VISIBLE);
    }
}