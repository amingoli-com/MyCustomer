package ir.amingoli.mycoustomer;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import ir.amingoli.mycoustomer.util.MarketDatabaseManager;

public class ActivitySetting extends AppCompatActivity {


    private MarketDatabaseManager dbManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dbManager = new MarketDatabaseManager(this);

        Button btnBackup = (Button) findViewById(R.id.btn_backup);
        Button btnRestore = (Button) findViewById(R.id.btn_restore);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackup();
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRestoreDialog();
            }
        });
    }

    private void startBackup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("در حال ایجاد پشتیبان");
        progressDialog.setMessage("لطفاً صبر کنید...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbManager.createBackup(new MarketDatabaseManager.BackupListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
                progressDialog.setMessage("پیشرفت: " + progress + "%");
            }

            @Override
            public void onSuccess(final File backupFile) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        showBackupSuccessDialog(backupFile);
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(ActivitySetting.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void showBackupSuccessDialog(File backupFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("پشتیبان موفق");
        builder.setMessage("پشتیبان با موفقیت ایجاد شد:\n" + backupFile.getName());

        builder.setPositiveButton("اشتراک گذاری", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareBackup(backupFile);
            }
        });

        builder.setNegativeButton("باشه", null);
        builder.show();
    }

    private void shareBackup(File backupFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/zip");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backupFile));
        startActivity(Intent.createChooser(shareIntent, "اشتراک گذاری پشتیبان"));
    }

    private void showRestoreDialog() {
        File[] backups = dbManager.getBackupFiles();

        if (backups.length == 0) {
            Toast.makeText(this, "فایل پشتیبان یافت نشد", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] backupNames = new String[backups.length];
        for (int i = 0; i < backups.length; i++) {
            backupNames[i] = backups[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("انتخاب پشتیبان");
        builder.setItems(backupNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmRestore(backups[which]);
            }
        });
        builder.show();
    }

    private void confirmRestore(final File backupFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تایید بازیابی");
        builder.setMessage("آیا از بازیابی این پشتیبان اطمینان دارید؟\n" +
                backupFile.getName());

        builder.setPositiveButton("بازیابی", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startRestore(backupFile);
            }
        });

        builder.setNegativeButton("لغو", null);
        builder.show();
    }

    private void startRestore(File backupFile) {
        // پیاده‌سازی عملیات بازیابی
        // ...
    }
}