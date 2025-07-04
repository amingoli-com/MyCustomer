package ir.amingoli.mycoustomer.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MarketDatabaseManager {

    private static final String TAG = "MarketBackup";
    private static final String DB_NAME = "markeet.db";
    private static final int BUFFER_SIZE = 8192;

    private final Context context;

    public interface BackupListener {
        void onProgress(int progress);
        void onSuccess(File backupFile);
        void onError(String message);
    }

    public MarketDatabaseManager(Context context) {
        this.context = context;
    }

    public void createBackup(final BackupListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 1. ایجاد پوشه بک‌آپ
                    File backupDir = new File(Environment.getExternalStorageDirectory(), "MarketBackups");
                    if (!backupDir.exists() && !backupDir.mkdirs()) {
                        listener.onError("خطا در ایجاد پوشه بک‌آپ");
                        return;
                    }

                    // 2. نام فایل با تاریخ
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                            .format(new Date());
                    String backupName = "market_backup_" + timeStamp + ".zip";
                    File zipFile = new File(backupDir, backupName);

                    // 3. آماده‌سازی دیتابیس
                    flushWAL();

                    // 4. دریافت فایل‌های دیتابیس
                    File dbFile = context.getDatabasePath(DB_NAME);
                    File[] dbFiles = {
                            dbFile,
                            new File(dbFile.getAbsolutePath() + "-wal"),
                            new File(dbFile.getAbsolutePath() + "-shm")
                    };

                    // 5. ایجاد فایل ZIP
                    createZipFile(dbFiles, zipFile, listener);

                    listener.onSuccess(zipFile);

                } catch (Exception e) {
                    Log.e(TAG, "Backup failed", e);
                    listener.onError("خطا در ایجاد پشتیبان: " + e.getMessage());
                }
            }
        }).start();
    }

    private void createZipFile(File[] files, File zipFile, BackupListener listener) throws IOException {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            int validFiles = 0;
            for (File file : files) {
                if (file.exists()) validFiles++;
            }

            int processed = 0;
            for (File file : files) {
                if (!file.exists()) continue;

                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    long totalRead = 0;
                    long fileSize = file.length();

                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                        totalRead += length;

                        int progress = (int) ((processed * 100f / validFiles) +
                                ((totalRead * 100f / fileSize) / validFiles));
                        listener.onProgress(progress);
                    }
                } finally {
                    if (fis != null) fis.close();
                }

                zos.closeEntry();
                processed++;
            }
        } finally {
            if (zos != null) zos.close();
            if (fos != null) fos.close();
        }
    }

    private void flushWAL() {
        SQLiteDatabase db = null;
        try {
            db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            db.execSQL("PRAGMA wal_checkpoint(FULL)");
        } catch (Exception e) {
            Log.e(TAG, "Error flushing WAL", e);
        } finally {
            if (db != null) db.close();
        }
    }

    public File[] getBackupFiles() {
        File backupDir = new File(Environment.getExternalStorageDirectory(), "MarketBackups");
        if (!backupDir.exists()) {
            return new File[0];
        }

        return backupDir.listFiles(new java.io.FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("market_backup_") && name.endsWith(".zip");
            }
        });
    }
}