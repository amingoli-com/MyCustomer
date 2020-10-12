package ir.amingoli.mycoustomer.data;

import java.util.Locale;

public class AppConfig {

    /* Locale.US        -> 2,365.12
     * Locale.GERMANY   -> 2.365,12
     */
    public static final Locale PRICE_LOCAL_FORMAT = Locale.GERMANY;

    /* true     -> 2.365,12
     * false    -> 2.365
     */
    public static final boolean PRICE_WITH_DECIMAL = false;

    /* true     -> 2.365,12 USD
     * false    -> USD 2.365
     */
    public static final boolean PRICE_CURRENCY_IN_END = true;


    public static final String CUSTOMER_NAME = " نام مشتری " ;
    public static final String TOTAL_PRODUCT = " تعداد محصولات " ;
    public static final String TOTAL_PRICE = " جمع کل " ;
    public static final String SendSmsToCustomer_ORDER_IS_PIAED = CUSTOMER_NAME + " عزیز سفارش شما به مبلغ " + TOTAL_PRICE + " پرداخت گردید \n با احترام; گالری مشکات";
}
