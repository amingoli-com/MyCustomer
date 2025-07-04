package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.amingoli.mycoustomer.Adapter.AdapterSmsSample;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.view.DialogAddSmsSample;

public class ActivitySetting extends AppCompatActivity {


    TextView addSmsSample;
    RecyclerView recyclerView;
    AdapterSmsSample adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addSmsSample = findViewById(R.id.addSmsSample);
        recyclerView = findViewById(R.id.recyclerView);

        addSmsSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddSmsSample d = new DialogAddSmsSample(ActivitySetting.this, new DialogAddSmsSample.listener() {
                    @Override
                    public void result(Customer customer) {

                    }
                });
                d.show();
            }
        });


        ArrayList<String> s = new ArrayList<>();
        s.add("سلام");
        s.add("سلام متن تست");
        adapter = new AdapterSmsSample(this, s, new AdapterSmsSample.Listener() {
            @Override
            public void onClick(String string) {

            }

            @Override
            public void onClickEdit(String string) {

            }
        });
        recyclerView.setAdapter(adapter);


    }
}