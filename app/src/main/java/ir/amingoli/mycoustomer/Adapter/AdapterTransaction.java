package ir.amingoli.mycoustomer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterTransaction extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private ArrayList<Transaction> items;
    private Listener listener;

    public interface Listener {
        void onClick(Transaction transaction);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView name;
        public TextView desc;
        public TextView tv_id;
        public TextView textBlue;
        public ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            desc = (TextView) itemView.findViewById(R.id.desc);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            textBlue = (TextView) itemView.findViewById(R.id.textBlue);
            avatar = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public AdapterTransaction(Context ctx, ArrayList<Transaction> items, Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_list, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Transaction c = items.get(position);
            vItem.name.setText(c.getDesc());
            if (c.getType() == Tools.TRANSACTION_TYPE_BEDEHI) {
                vItem.desc.setText("بدهی");

            } else if (c.getType() == Tools.TRANSACTION_TYPE_PAY_BEDEHI) {
                vItem.desc.setText("مبلغ پرداخت شده");

            } else if (c.getType() == Tools.TRANSACTION_TYPE_PAY_DISCOUNT) {
                vItem.desc.setText("تخفیف");

            } else if (c.getType() == Tools.TRANSACTION_TYPE_PAY_BEDEHI_BY_OTHER_METHODE) {
                vItem.desc.setText("پرداخت بدهی");
            }
            vItem.tv_id.setText(c.getId()+"");
            vItem.textBlue.setText(Tools.getFormattedPrice(c.getAmount(),ctx));
            vItem.view.setOnClickListener(view -> listener.onClick(c));
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

    public void setItems(ArrayList<Transaction> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}