package ir.amingoli.mycoustomer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterAddOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private ArrayList<Product> items;
    private Listener listener;

    public interface Listener {
        void onClickPlus(Product product);
        void onClickRemove(Product product);
        void onClickChangeAmount(Product product, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView desc;
        public TextView tv_discount;
        public TextView textBlue;
        public ImageView plus,remove;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            tv_discount = itemView.findViewById(R.id.tv_discount);
            textBlue =  itemView.findViewById(R.id.textBlue);
            plus = itemView.findViewById(R.id.plusAmount);
            remove = itemView.findViewById(R.id.removeAmount);
        }
    }

    public AdapterAddOrder(Context ctx, ArrayList<Product> items, Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Product c = items.get(position);
            vItem.name.setText(c.getName());
            vItem.desc.setText(ctx.getString(R.string.price_of_each_number,
                    Tools.getFormattedPrice(c.getPrice(),ctx)));
            vItem.tv_discount.setText(Tools.getFormattedDiscount(c.getAmount()));
            vItem.textBlue.setText(ctx.getString(R.string.total_price_of_each_number,
                    Tools.getFormattedPrice(c.getPrice_all(),ctx)));
            c.setPosition(position);

            vItem.tv_discount.setOnClickListener(view -> listener.onClickChangeAmount(c,position));
            vItem.plus.setOnClickListener(view -> listener.onClickPlus(c));
            vItem.remove.setOnClickListener(view -> listener.onClickRemove(c));
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

    public void setItems(ArrayList<Product> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}