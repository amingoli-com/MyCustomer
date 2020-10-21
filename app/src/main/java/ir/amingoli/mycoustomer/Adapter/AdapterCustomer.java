package ir.amingoli.mycoustomer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterCustomer extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context ctx;
    private List<Customer> items;
    private Listener listener;

    public interface Listener{
        void onClick(Customer customer);
        void edit(Customer customer);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView name;
        public TextView desc;
        public Button edit;
        public ImageView avatar;

        public ViewHolder(View v) {
            super(v);
            view = (View) v;
            name = (TextView) v.findViewById(R.id.name);
            desc = (TextView) v.findViewById(R.id.desc);
            edit = (Button) v.findViewById(R.id.edit);
            avatar = (ImageView) v.findViewById(R.id.image);
        }
    }

    public AdapterCustomer(Context ctx, List<Customer> items, Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_list, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Customer c = items.get(position);
            vItem.name.setText(c.getName());

            String desc = "" ;
            if (!TextUtils.isEmpty(c.getDesc())) desc = desc + c.getDesc() + " - ";
            desc = desc + Tools.formatPhoneNumber(c.getTel());
            vItem.desc.setText(desc);

            vItem.view.setOnClickListener(view -> listener.onClick(c));
            vItem.edit.setOnClickListener(view -> listener.edit(c));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Customer> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}