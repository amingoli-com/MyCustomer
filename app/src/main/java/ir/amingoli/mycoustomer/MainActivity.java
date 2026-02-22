package ir.amingoli.mycoustomer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.List;

import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.GetTotalSales;
import ir.amingoli.mycoustomer.model.Order;
import ir.amingoli.mycoustomer.model.Transaction;
import ir.amingoli.mycoustomer.util.Session;
import ir.amingoli.mycoustomer.util.Tools;
import ir.amingoli.mycoustomer.view.DialogBusinessInfo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "amingoli78888-main";
    DatabaseHandler db;
    TextView salesAll, salesToday, salesThisMonth ,
             totalCustomers, totalProduct, totalOrder, totalDetailOrder,textLogo, totalOrderIsPiedThisMonth,
            totalBedehi,txtVersionApp;
    View live_salesAll,live_salesToday,live_salesThisWeek,live_salesThisMonth;
    
    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this);

        for (int i = 0; i < db.getAllTransaction().size(); i++) {
            Log.d(TAG, "onCreate: "+db.getAllTransaction().get(i).id_order);
        }
    }
    private void initData(){
        initId();
        initValue();
    }

    private void initId(){
//        View include_info_app = findViewById(R.id.include_info_app);
//        View include_order = findViewById(R.id.include_order);
        salesAll = findViewById(R.id.salesAll);
        salesToday = findViewById(R.id.salesToday);
//        salesThisMonth = include_info_app.findViewById(R.id.salesThisMonth);
        salesThisMonth = findViewById(R.id.salesThisMonth);
        txtVersionApp = findViewById(R.id.txtVersionApp);

        live_salesAll = findViewById(R.id.live_salesAll);
        live_salesToday = findViewById(R.id.live_salesToday);
        live_salesThisWeek = findViewById(R.id.live_salesThisWeek);
        live_salesThisMonth = findViewById(R.id.live_salesThisMonth);

//        totalOrderIsPiedThisMonth = include_info_app.findViewById(R.id.totalOrderIsPiedThisMonth);
//        totalCustomers = include_info_app.findViewById(R.id.totalCustomers);
//        totalProduct = include_info_app.findViewById(R.id.totalProducts);
//        totalOrder = include_order.findViewById(R.id.totalOrders);//
        totalCustomers = findViewById(R.id.totalCustomers);
        totalProduct = findViewById(R.id.totalProducts);
        totalOrder = findViewById(R.id.totalOrders);
        totalDetailOrder = findViewById(R.id.totalDetailOrder);
        textLogo = findViewById(R.id.textLogo);
        totalBedehi = findViewById(R.id.totalBedehi);

    }

    @SuppressLint({"StringFormatMatches", "SetTextI18n"})
    private void initValue(){
        GetTotalSales all = getTotalSales(-1);
        GetTotalSales today = getTotalSales(0);
        GetTotalSales thisWeek = getTotalSales(7);
        GetTotalSales thisMonth = getTotalSales(30);
        if (all != null) {
            salesAll.setText(getString(R.string.sales_all,
                    Tools.getFormattedPrice(all.getTotalSales(), this),
                    Tools.getFormattedInteger(all.getSalesCount())));
        } else {
            live_salesAll.setVisibility(View.GONE);
            salesAll.setText(getString(R.string.no_data_sales_all));
        }
        if (today != null) {
            salesToday.setText(getString(R.string.sales_tody,
                    Tools.getFormattedPrice(today.getTotalSales(), this),
                    Tools.getFormattedInteger(today.getSalesCount())));
        } else {
            live_salesToday.setVisibility(View.GONE);
            salesToday.setText(getString(R.string.no_data_sales_today));
        }
        if (thisMonth != null) {
            salesThisMonth.setText("فروش این ماه \n"+Tools.getFormattedPrice(thisMonth.getTotalSales(), this));
//            totalOrderIsPiedThisMonth.setText(Tools.getFormattedInteger(thisMonth.getSalesCount()));
        } else {
            live_salesThisMonth.setVisibility(View.GONE);
            salesThisMonth.setText(getString(R.string.no_data_sales_this_month));
        }

        totalCustomers.setText(getString(R.string.total_customer ,
                Tools.getFormattedInteger(db.getCustomerSize())));
        totalOrder.setText(getString(R.string.total_order_waitting ,
                Tools.getFormattedInteger(db.getOrderIsWaitingSize())));
        totalDetailOrder.setText(getString(R.string.total_detail_order ,
                Tools.getFormattedInteger(db.getOrderIsWaitingSize())));
        totalProduct.setText(getString(R.string.total_product ,
                Tools.getFormattedInteger(db.getProductSize())));

        populateBusinessName();
        textLogo.setOnClickListener(view -> dialogBusinessInfo());

        initTotalBedehi();

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersionApp.setText("نسخه "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initTotalBedehi(){
        double tb = 0.0;
        List<Transaction> bedehi = db.getTransactionBedehiCustomer(Tools.TRANSACTION_TYPE_BEDEHI);
        for (int i = 0; i < bedehi.size(); i++) {
            tb = tb + bedehi.get(i).getAmount();
        }

        double tbpayed = 0.0;
        List<Transaction> bedehiPayed = db.getTransactionBedehiCustomer(
                Tools.TRANSACTION_TYPE_PAY_BEDEHI_BY_OTHER_METHODE);
        for (int i = 0; i < bedehiPayed.size(); i++) {
            tbpayed = tbpayed + bedehiPayed.get(i).getAmount();
        }

        double r = tb-tbpayed;
        if (r > 0){
            totalBedehi.setText(Tools.getFormattedPrice(r,this) +" طلب دارید");
        }else {
            totalBedehi.setText("همه مشتری ها تسویه شده‌اند.");
        }
    }

    private GetTotalSales getTotalSales(int howDays){
        GetTotalSales model = new GetTotalSales();
        double total = 0;
        List<Order> orders;
        if (howDays == -1){
            orders = db.getOrderList(true);
        }else {
            orders = db.getOrderList(true , Tools.dayToMillis(howDays));
        }
        if (!orders.isEmpty()){
            for (int i = 0; i < orders.size(); i++) {
                total = total + orders.get(i).getPrice();
            }
            model.setTotalSales(total);
            model.setSalesCount(orders.size());
            return model;
        }
        return null;
    }

//    onClick
    public void goToActitvityProduct(View view) {
        startActivity(new Intent(this,ActivityProduct.class));
    }

    public void goToActitvityCustomer(View view) {
        startActivity(new Intent(this,ActivityCustomer.class));
    }

    public void goToActivityOrderIsPied(View view) {
        Intent intent = new Intent(this,ActivityReportOrder.class);
        intent.putExtra("order_status",true);
        if (!TextUtils.isEmpty(view.getTag().toString()))
            intent.putExtra("filter",Integer.valueOf(view.getTag().toString()));
        startActivity(intent);
    }

    public void goToActivityOrderIsNotPied(View view) {
        Intent intent = new Intent(this,ActivityReportOrder.class);
        intent.putExtra("order_status",false);
        startActivity(intent);
    }

    public void goToActivityProductsOrdered(View view) {
        Intent intent = new Intent(this,ActivityProductsOrdered.class);
        startActivity(intent);
    }

    public void goToActivityTransaction(View view) {
        Intent intent = new Intent(this,ActivityTransactions.class);
        intent.putExtra("type",Tools.TRANSACTION_TYPE_BEDEHI);
        startActivity(intent);
    }

    public void addOrder(View view) {
        Intent intent = new Intent(this,ActivityCustomer.class);
        intent.putExtra("add_order",true);
        startActivity(intent);
    }

    private void dialogBusinessInfo(){
        DialogBusinessInfo dialogBusinessInfo = new DialogBusinessInfo(this, value -> {
            populateBusinessName();
        });
        dialogBusinessInfo.show();
    }

    private void populateBusinessName(){
        if (Session.getInstance(this).getString("bn") != null
                && !Session.getInstance(this).getString("bn").isEmpty()){
            textLogo.setText(Session.getInstance(this).getString("bn"));
        }
    }

    public void goToActivitySetting(View view) {
        Intent intent = new Intent(this,ActivitySetting.class);
        startActivity(intent);
    }

    public void privacy(View view) {
        showSimpleTextDialog("سیاست حریم خصوصی اپلیکیشن \"مشتریان من\"\n" +
                "این برنامه:\n" +
                "هیچ دسترسی\u200Cای به اینترنت یا داده\u200Cهای شخصی شما ندارد.\n" +
                "تمام اطلاعات فقط روی دستگاه شما ذخیره می\u200Cشود.\n" +
                "برای بکاپ/بازگردانی فقط از فولدر داخلی خود برنامه استفاده می\u200Cکند.\n" +
                "کاربرد اصلی:\n" +
                "ثبت فاکتورها، محاسبه مانده مشتریان و مدیریت تخفیف\u200Cها به صورت کاملاً آفلاین.\n"
        );
    }

    public void about(View view) {
        showSimpleTextDialog("درباره ما - اپلیکیشن \"مشتریان من\"\n" +
                "ویژگی\u200Cهای اپلیکیشن:\n" +
                "طراحی ساده و کاربرپسند\n" +
                "کارایی سریع بدون نیاز به اینترنت\n" +
                "\n" +
                "امنیت کامل داده\u200Cها (ذخیره\u200Cسازی محلی)\n" +
                "هدف از توسعه:\n" +
                "راه\u200Cحلی آسان برای:\n" +
                "✓ ثبت فاکتورها\n" +
                "✓ محاسبه خودکار مانده مشتری\n" +
                "✓ مدیریت تخفیف\u200Cها\n" +
                "✓ پشتیبان\u200Cگیری امن بدون نیاز به دسترسی\u200Cهای سیستمی\n" +
                "تماس با ما:\n" +
                "[09195191378 | selller.ir@gmail.com]\n" +
                "توسعه\u200Cیافته با ❤\uFE0F برای سهولت کار کسب\u200Cوکارهای کوچک\n"
        );
    }

    public void donate(View view) {
        openWebsite("https://selller.ir/mycustomer");
    }

    public void showSimpleTextDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void openWebsite(String url) {
        try {
            // بررسی اینکه آیا URL با http:// یا https:// شروع شده است
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // اگر مرورگری نصب نباشد
            Toast.makeText(this, "هیچ برنامه‌ای برای باز کردن وب‌سایت یافت نشد", Toast.LENGTH_SHORT).show();
        }
    }
}