package ir.amingoli.mycoustomer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DatabaseBackupRestore {
    private static final String DB_NAME = "markeet.db"; // نام دیتابیس
    private static final String BACKUP_DIR = "DatabaseBackups"; // پوشه بکاپ
    private static final String FILE_PROVIDER_AUTHORITY = "ir.amingoli.mycoustomer.fileprovider"; // باید با package name شما تطابق داشته باشد

    private final Context context;
    private final Activity activity;

    public DatabaseBackupRestore(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    // متد برای گرفتن بکاپ از دیتابیس
    public void backupDatabase() {
        try {
            File dbFile = context.getDatabasePath(DB_NAME);

            // بررسی وجود فایل دیتابیس
            if (!dbFile.exists()) {
                Toast.makeText(context, "فایل دیتابیس وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }

            // ایجاد پوشه بکاپ (با بررسی دسترسی)
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), BACKUP_DIR);

            if (!backupDir.exists()) {
                if (!backupDir.mkdirs()) {
                    Toast.makeText(context, "خطا در ایجاد پوشه بکاپ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String backupFileName = DB_NAME + "_backup_" + System.currentTimeMillis() + ".db";
            File backupFile = new File(backupDir, backupFileName);

            // کپی فایل با بررسی خطاها
            try {
                copyFile(dbFile, backupFile);
                Toast.makeText(context, "بکاپ با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
                shareBackupFile(backupFile);
            } catch (IOException e) {
                Toast.makeText(context, "خطا در کپی فایل: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } catch (Exception e) {
            Toast.makeText(context, "خطای عمومی: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // متد برای ریستور دیتابیس
    public void restoreDatabase() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        activity.startActivityForResult(
                Intent.createChooser(intent, "انتخاب فایل بکاپ دیتابیس"),
                123 // کد درخواست
        );
    }

    // پردازش نتیجه انتخاب فایل برای ریستور
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    File dbFile = context.getDatabasePath(DB_NAME);

                    // بستن اتصال به دیتابیس قبل از ریستور
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(
                            dbFile.getPath(),
                            null,
                            SQLiteDatabase.OPEN_READWRITE
                    );
                    db.close();

                    // کپی فایل بکاپ به محل دیتابیس
                    try (FileInputStream in = (FileInputStream) context.getContentResolver().openInputStream(uri);
                         FileOutputStream out = new FileOutputStream(dbFile)) {

                        FileChannel inChannel = in.getChannel();
                        FileChannel outChannel = out.getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                    }

                    Toast.makeText(context, "دیتابیس با موفقیت ریستور شد", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(context, "خطا در ریستور دیتابیس: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    // متد کمکی برای کپی فایل
    private void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {

            FileChannel inChannel = in.getChannel();
            FileChannel outChannel = out.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    // متد برای اشتراک‌گذاری فایل بکاپ
    private void shareBackupFile(File backupFile) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    FILE_PROVIDER_AUTHORITY,
                    backupFile
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/octet-stream");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivity(Intent.createChooser(
                    shareIntent,
                    "اشتراک‌گذاری فایل بکاپ دیتابیس"
            ));
        } catch (Exception e) {
            Toast.makeText(context, "خطا در اشتراک‌گذاری فایل: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}