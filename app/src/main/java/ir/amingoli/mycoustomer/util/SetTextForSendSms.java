package ir.amingoli.mycoustomer.util;

import android.content.Context;

import java.util.List;

import ir.amingoli.mycoustomer.model.Product;


public class SetTextForSendSms {
    String customer_name;
    double total_price;
    Product order_info;
    long order_date;
    String business_name;

    public static String SEND_SMS_ORDER_IS_PIED (String customer_name){
        return customer_name + " از خرید شما متشکریم.";
    }
    public static String SEND_SMS_ORDER_IS_WAITING (String customer_name, String total_price){
        return customer_name + " سفارش شما به مبلغ " + total_price + " ثبت گردید.";
    }
    public static String SEND_SMS_ORDER_IS_WAITING (Context context, String customer_name, double total_price, List<Product> order_info){
        StringBuilder sb = new StringBuilder("جزئیات سفارش شما:\n");
        for (int i = 0; i <order_info.size(); i++) {
            sb.append(order_info.get(i).getName());
            sb.append(" (").append(Tools.getFormattedDiscount(order_info.get(i).getAmount())).append(") ");
            sb.append(" = ").append(Tools.getFormattedPrice(order_info.get(i).getPrice_all(), context));
            sb.append("\n");
        }
        return customer_name + " سفارش شما به مبلغ " + Tools.getFormattedPrice(total_price,context) + " ثبت گردید." + "\n"+ sb ;
    }
    public static String SEND_SMS_ORDER_IS_WAITING_IS_PIED (String customer_name){
        return customer_name + " سفارش شما آماده گردید." ;
    }
}
