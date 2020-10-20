package ir.amingoli.mycoustomer.util;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ir.amingoli.mycoustomer.data.AppConfig;
import ir.amingoli.mycoustomer.data.SharedPref;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class Tools {
    public static String getFormattedPrice(Double price, Context ctx) {
        SharedPref sharedPref = new SharedPref(ctx);
        NumberFormat format = NumberFormat.getInstance(AppConfig.PRICE_LOCAL_FORMAT);
        String result = format.format(price);

        if (!AppConfig.PRICE_WITH_DECIMAL) {
//            result = format.format(price.longValue());
            DecimalFormat df = new DecimalFormat("###,###,###");
            result = df.format(price.longValue());
        }

        if (AppConfig.PRICE_CURRENCY_IN_END) {
//            result = result + " " + sharedPref.getInfoData().currency;
            result = result + " تومان";
        } else {
//            result = sharedPref.getInfoData().currency + " " + result;
            result =  " تومان" + result;
        }
        return result;
    }

    public static String getFormattedInteger(int price) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(price);
    }

    public static String getFormattedString(String price) {
        int i = Integer.parseInt(price);
        DecimalFormat df = new DecimalFormat("### ### ## ##");
        return df.format(i);
    }

    public static String getFormattedDiscount(Double count) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(count);
    }

    public static String getFormattedDate(Long dateTime) {
        PersianDateFormat pdformater;
        pdformater = new PersianDateFormat("l j F Y");
//        pdformater = new PersianDateFormat("l j F Y ساعت H:i");
        return pdformater.format(new PersianDate(dateTime));
    }

    public static long dayToMillis(int howBeforeDay){
        long toDay = System.currentTimeMillis();
        long oneDay = 86400000;
        if (howBeforeDay >= 1 ) return toDay -oneDay * howBeforeDay;
        return toDay ;
    }

    public static String convertNumberToEN(String string) {
        String[][] mChars = new String[][]{
                {"0", "۰"}, {"1", "۱"}, {"2", "۲"}, {"3", "۳"}, {"4", "۴"}, {"5", "۵"}, {"6", "۶"},
                {"7", "۷"}, {"8", "۸"}, {"9", "۹"}, {"4", "٤"}, {"5", "٥"}, {"6", "٦"}
        };
        for (String[] num : mChars) {
            string = string.replace(num[1], num[0])
                    .replace(" ","")
                    .replace("٬","")
                    .replace(",","");

        }
        return string;
    }

    public static String formatPhoneNumber(String number){
        number  =   number.substring(0, number.length()-4) + " " + number.substring(number.length()-4, number.length());
        number  =   number.substring(0,number.length()-8)+" "+number.substring(number.length()-8,number.length());
        number  =   number.substring(0, number.length()-12)+" "+number.substring(number.length()-12, number.length());
        return number;
    }

}
