package ir.amingoli.mycoustomer.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterSmsSample extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private Boolean populateTextStatus = false;
    private ArrayList<Transaction> items;
    private Listener listener;
    private DatabaseHandler db;
    private long customerID;
    private long ORDER_CODE;

    public interface Listener {
        void onClick(Transaction transaction);
        void onClickEdit(String string);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView txt;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            txt = (TextView) itemView.findViewById(R.id.txt);
        }
    }

    public AdapterSmsSample(Context ctx, ArrayList<Transaction> items, Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }


    public AdapterSmsSample(Context ctx, ArrayList<Transaction> items, Listener listener,DatabaseHandler db) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
        this.db = db;
    }

    public AdapterSmsSample(Context ctx, ArrayList<Transaction> items, Listener listener,
                            boolean populateTextStatus, DatabaseHandler db, long customerID, long ORDER_CODE) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
        this.populateTextStatus = populateTextStatus;
        this.db = db;
        this.customerID = customerID;
        this.ORDER_CODE = ORDER_CODE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms_sample, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            String c = populateText(items.get(position).getDesc());
            vItem.txt.setText(c);
            vItem.view.setOnClickListener(view -> listener.onClick(items.get(position)));
        }

    }

    private String populateText(String string) {
        if (populateTextStatus){
            Customer customer = db.getCustomer(customerID);
            Order order = db.getOrderByOrderCode(ORDER_CODE);

            String[][] mChars = new String[][]{
                    {"[نام_مشتری]", customer.getName()},
                    {"[تلفن_مشتری]", customer.getTel()},
                    {"[مبلغ_کل_سفارش]", Tools.getFormattedPrice(order.getPrice(),ctx)},
                    {"[مبلغ_تخفیف_سفارش]", Tools.getFormattedPrice(order.getPrice(),ctx)},
                    {"[مبلغ_پرداخت_شده]", Tools.getFormattedPrice(order.getPrice(),ctx)},
                    {"[مبلغ_مانده_سفارش]", Tools.getFormattedPrice(order.getPrice(),ctx)},
//                    {"[کل_بدهی_مشتری]", customer.getName()},
//                    {"[وضعیت_سفارش]", customer.getName()},
//                    {"[لیست_محصولات]", customer.getName()},
//                    {"[تاریخ_امروز]", customer.getName()},
//                    {"[تاریخ_ثبت_سفارش]", customer.getName()},
            };
            for (String[] num : mChars) {
                Log.d("amingoli-adapter-sms", "populateText: "+num[0]+"-"+num[1]);
                string = string.replace(num[0], num[1]);

            }
        }
        return string;
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