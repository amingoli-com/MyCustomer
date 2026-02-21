package ir.amingoli.mycoustomer.util;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import ir.amingoli.mycoustomer.R;
import ir.amingoli.mycoustomer.data.AppConfig;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class Tools {
    public static long TRANSACTION_TYPE_BEDEHI = 1;
    public static long TRANSACTION_TYPE_PAY_BEDEHI = 2;
    public static long TRANSACTION_TYPE_PAY_DISCOUNT = 3;
    public static long TRANSACTION_TYPE_PAY_BEDEHI_BY_OTHER_METHODE = 4;
    public static long TRANSACTION_TYPE_SMS_SAMPLE = 200;

    public static String getFormattedPrice(Double price, Context ctx) {
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

    public static long dayToMillis(int howBeforeDay) {
        Calendar calendar = Calendar.getInstance();

        // تنظیم به نیمه‌شب امروز
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // عقب‌گرد به روزهای قبل
        if (howBeforeDay >= 1) {
            calendar.add(Calendar.DAY_OF_YEAR, -howBeforeDay);
        }

        return calendar.getTimeInMillis(); // نیمه‌شب روز مورد نظر
    }

    public static String convertNumberToEN(String string) {
        String[][] mChars = new String[][]{
                {"0", "۰"}, {"1", "۱"}, {"2", "۲"}, {"3", "۳"}, {"4", "۴"}, {"5", "۵"}, {"6", "۶"},
                {"7", "۷"}, {"8", "۸"}, {"9", "۹"}, {"4", "٤"}, {"5", "٥"}, {"6", "٦"}
        };
        for (String[] num : mChars) {
            string = string.replace(num[1], num[0])
                    .replace(".","")
                    .replace("/","")
                    .replace(" ","")
                    .replace("٬","")
                    .replace(",","");

        }
        return string;
    }

    public static String formatPhoneNumber(String number){
        /*number  =   number.substring(0, number.length()-4) + " " + number.substring(number.length()-4, number.length());
        number  =   number.substring(0,number.length()-8)+" "+number.substring(number.length()-8,number.length());
        number  =   number.substring(0, number.length()-12)+" "+number.substring(number.length()-12, number.length());*/
        return number;
    }

    public static void copyText(Context context, String textToCopy) {
        // دریافت ClipboardManager
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // ایجاد ClipData
        ClipData clip = ClipData.newPlainText("کپی شده", textToCopy);

        // تنظیم ClipData در کلیپ‌بورد
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            // نمایش پیام موفقیت‌آمیز (اختیاری)
            Toast.makeText(context, "متن کپی شد", Toast.LENGTH_SHORT).show();
        }
    }

    public static String pasteText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String pastedText = item.getText().toString();

            // نمایش پیام موفقیت‌آمیز (اختیاری)
            Toast.makeText(context, "متن پیست شد", Toast.LENGTH_SHORT).show();
            return pastedText;
        } else {
            Toast.makeText(context, "هیچ متنی در کلیپ‌بورد وجود ندارد", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public static void shareText(Context context, String textToShare) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            // متن اصلی برای اشتراک‌گذاری
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

            // موضوع اختیاری (برای ایمیل یا برخی اپلیکیشن‌ها)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "متن اشتراک‌گذاری شده");

            // برای برخی اپلیکیشن‌ها مانند واتس‌اپ می‌توانید شماره یا اطلاعات اضافه کنید
            // shareIntent.putExtra("jid", "98912xxxxxxx@s.whatsapp.net");

            // ایجاد Chooser با عنوان فارسی
            Intent chooser = Intent.createChooser(shareIntent, "اشتراک‌گذاری متن با...");

            // تنظیم flag برای بازگشت به اپلیکیشن پس از اشتراک‌گذاری
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // شروع فعالیت اشتراک‌گذاری
            context.startActivity(chooser);

        } catch (Exception e) {
            Toast.makeText(context, "خطا در اشتراک‌گذاری: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
