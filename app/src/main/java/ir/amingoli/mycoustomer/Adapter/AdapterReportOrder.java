package ir.amingoli.mycoustomer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.Product;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterReportOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private ArrayList<Order> items;
    private Listener listener;

    public interface Listener {
        void onClick(Order product);
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

    public AdapterReportOrder(Context ctx, ArrayList<Order> items, Listener listener) {
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
            final Order c = items.get(position);
            vItem.name.setText(c.getCustomer_name());
            vItem.desc.setText(Tools.getFormattedDate(c.getCreated_at()));
            vItem.tv_id.setText(position+1+"");
            vItem.textBlue.setText(Tools.getFormattedPrice(c.getPrice(),ctx));
//            vItem.textBlue.setText(ctx.getResources().getString(R.string.edit));
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

    public void setItems(ArrayList<Order> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}