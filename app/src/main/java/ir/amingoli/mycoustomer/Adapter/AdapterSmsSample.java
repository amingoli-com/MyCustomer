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
import ir.amingoli.mycoustomer.model.OrderDetail;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Tools;

public class AdapterSmsSample extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private Boolean populateTextStatus = false;
    private ArrayList<Transaction> items;
    private Listener listener;
    private String customerName;
    private String customerPhone;
    private double totalOrder;
    private double totlaPayed;
    private double totalBedehi;
    private double totalDiscount;
    private double totalAllBedehiCustomer;
    private boolean orderStatus;
    private long orderDateCreated;
    private long todayDate;
    private String orderDetailShort;
    private String orderDetailLong;
    private String orderDetailFull;

    public interface Listener {
        void onClick(Transaction transaction);
        void onClick(String string);
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

    public AdapterSmsSample(Context ctx, ArrayList<Transaction> items, Listener listener,
                            boolean populateTextStatus, String customerName, String customerPhone,
                            double totalOrder, double totlaPayed, double totalBedehi, double totalDiscount,
                            double totalAllBedehiCustomer, boolean orderStatus, long orderDateCreated,
                            long todayDate, String orderDetailShort, String orderDetailLong, String orderDetailFull) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
        this.populateTextStatus = populateTextStatus;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.totalOrder = totalOrder;
        this.totlaPayed = totlaPayed;
        this.totalBedehi = totalBedehi;
        this.totalDiscount = totalDiscount;
        this.totalAllBedehiCustomer = totalAllBedehiCustomer;
        this.orderStatus = orderStatus;
        this.orderDateCreated = orderDateCreated;
        this.todayDate = todayDate;
        this.orderDetailShort = orderDetailShort;
        this.orderDetailLong = orderDetailLong;
        this.orderDetailFull = orderDetailFull;
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
            vItem.view.setOnClickListener(view -> {
                listener.onClick(items.get(position));
                listener.onClick(c);
            });
        }

    }

    private String populateText(String string) {
        if (populateTextStatus){
            String s = "در انتظار";
            if (orderStatus) s = "تکمیل شده";
            String[][] mChars = new String[][]{
                    {"[نام_مشتری]", customerName},
                    {"[تلفن_مشتری]", customerPhone},
                    {"[مبلغ_کل_سفارش]", Tools.getFormattedPrice(totalOrder,ctx)},
                    {"[مبلغ_تخفیف_سفارش]", Tools.getFormattedPrice(totalDiscount,ctx)},
                    {"[مبلغ_پرداخت_شده]", Tools.getFormattedPrice(totlaPayed,ctx)},
                    {"[مبلغ_مانده_سفارش]", Tools.getFormattedPrice(totalBedehi,ctx)},
//                    {"[کل_بدهی_مشتری]", Tools.getFormattedPrice(totalAllBedehiCustomer+totalBedehi,ctx)},
                    {"[مانده_قبل]", Tools.getFormattedPrice(totalAllBedehiCustomer,ctx)},
                    {"[وضعیت_سفارش]", s},
                    {"[لیست_محصولات_کوتاه]", orderDetailShort},
                    {"[لیست_محصولات_جزییات]", orderDetailLong},
                    {"[لیست_محصولات_جزییات_کامل]", orderDetailFull},
                    {"[تاریخ_امروز]", Tools.getFormattedDate(todayDate)},
                    {"[تاریخ_ثبت_سفارش]", Tools.getFormattedDate(orderDateCreated)},
            };
            for (String[] num : mChars) {
                Log.d("amingoli-adapter-sms", "populateText: "+num[0]+"-"+num[1]);
                try {
                    string = string.replace(num[0], num[1]);
                }catch (Exception e){
                    try {
                        string = string.replace(num[0], num[0]);
                    }catch (Exception e2){}
                }

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