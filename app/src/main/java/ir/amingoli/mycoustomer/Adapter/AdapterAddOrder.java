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
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterAddOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private ArrayList<Product> items;
    private Listener listener;

    public interface Listener {
        void onClickPlus(Product product);
        void onClickRemove(Product product);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView desc;
        public TextView tv_id;
        public TextView textBlue;
        public ImageView avatar;
        public ImageView plus,remove;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            desc = (TextView) itemView.findViewById(R.id.desc);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            textBlue = (TextView) itemView.findViewById(R.id.textBlue);
            avatar = (ImageView) itemView.findViewById(R.id.image);
            plus = (ImageView) itemView.findViewById(R.id.plusAmount);
            remove = (ImageView) itemView.findViewById(R.id.removeAmount);
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
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Product c = items.get(position);
            vItem.name.setText(c.getName());
            vItem.desc.setText(" فی " + Tools.getFormattedPrice(c.getPrice(),ctx));
            vItem.tv_id.setText(c.getAmount()+"");
            vItem.textBlue.setText(" جمع " + Tools.getFormattedPrice(c.getPrice_all(),ctx));
//            vItem.textBlue.setText(ctx.getResources().getString(R.string.edit));
            c.setPosition(position);
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