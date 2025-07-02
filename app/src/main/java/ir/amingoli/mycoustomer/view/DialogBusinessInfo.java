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
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.util.Session;

public class DialogBusinessInfo extends AlertDialog implements
        View.OnClickListener {

    private Activity ac;
    private listener listener;
    private EditText name;
    private TextInputLayout name_lyt;
    private Button submit;

    public DialogBusinessInfo(Activity a, listener listeners) {
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
        setContentView(R.layout.item_dialog_business_info);

        name_lyt = findViewById(R.id.name_lyt);
        name = findViewById(R.id.businessName);
        submit = findViewById(R.id.submit);

        setValue();

        name.requestFocus();
        if (name.hasFocusable()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(getText(name))){
            name_lyt.setError(ac.getString(R.string.error_name));
            requestFocus(name);
        } else {
            Session.getInstance(this.getContext()).putExtra("bn",getText(name));
            listener.result(getText(name));
            dismiss();
        }

    }

    public interface listener{
        void result(String BusinessName);
    }

    private String getText(EditText editText){
        return editText.getText().toString().trim();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setValue(){
        name.setText(Session.getInstance(this.getContext()).getString("bn"));
        submit.setText(ac.getResources().getString(R.string.update));
    }
}