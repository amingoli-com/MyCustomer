package ir.amingoli.mycoustomer;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.amingoli.mycoustomer.Adapter.AdapterCustomer;
import ir.amingoli.mycoustomer.data.DatabaseHandler;
import ir.amingoli.mycoustomer.model.Customer;
import ir.amingoli.mycoustomer.view.DialogAddCustomer;

public class ActivityCustomer extends AppCompatActivity {

    private View include_search,include_empty,include_load;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterCustomer adapter;
    private List<Customer> arrayList;
    private boolean ADD_ORDER = false;

    @Override
    protected void onResume() {
        super.onResume();
        ((MyCustomerApplication) getApplication()).refreshLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        ADD_ORDER = getIntent().getBooleanExtra("add_order",false);
        populateData();
        initAdapter();
        initSearchView();
        loadCustomerListByNameOrTel("",false);
    }


    private void populateData(){
        db = new DatabaseHandler(this);
        include_search = findViewById(R.id.include_search);
        include_load = findViewById(R.id.include_load);
        include_empty = findViewById(R.id.include_empty);
        searchView = include_search.findViewById(R.id.searchView);
        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        View btnAddFromContact = include_search.findViewById(R.id.btnImportContacts);
        btnAddFromContact.setVisibility(View.VISIBLE);
        btnAddFromContact.setOnClickListener(view -> checkPermissionAndImport());
    }

    private void initAdapter(){
        adapter = new AdapterCustomer(this, arrayList, new AdapterCustomer.Listener() {
            @Override
            public void onClick(Customer customer) {
                goToActivityCustomerDetail(customer);
            }

            @Override
            public void edit(Customer customer) {
                dialogAddCustomer(customer);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initSearchView(){
        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadCustomerListByNameOrTel(query,false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                include_load.setVisibility(View.VISIBLE);
                if (query.matches("[0-9]+") && query.length() > 0) {
                    loadCustomerListByNameOrTel(query,true);
                }else {
                    loadCustomerListByNameOrTel(query,false);
                }
                return false;
            }
        });
    }

    private void loadCustomerListByNameOrTel(String keyword,boolean searchByTel) {
        arrayList.clear();
        List<Customer> items;
        if (searchByTel){
            items = db.getCustomerByTel(keyword);
        }else {
            items = db.getCustomerByName(keyword);
        }
        for (int i = 0; i < items.size(); i++) {
            arrayList.add(new Customer(items.get(i).getId(),items.get(i).getName(),items.get(i).getTel(),items.get(i).getDesc()));
        }
        if (adapter!=null) adapter.notifyDataSetChanged();
        include_load.setVisibility(View.GONE);
        if (arrayList.isEmpty())
            include_empty.setVisibility(View.VISIBLE); else include_empty.setVisibility(View.GONE);
    }

    private void dialogAddCustomer(Customer c){
        DialogAddCustomer dialogAddCustomer = new DialogAddCustomer(this,c, value -> {
            db.saveCustomer(value);
            loadCustomerListByNameOrTel("",false);
        });
        dialogAddCustomer.show();
    }

    private void goToActivityCustomerDetail(Customer item){
        Intent intent = new Intent(this,ActivityCustomerDetail.class);
        intent.putExtra("id_customer",item.getId());
        if (ADD_ORDER) {
            intent = new Intent(this,ActivityAddOrder.class);
            intent.putExtra("id_customer",item.getId());
            startActivity(intent);
            finish();
        } else startActivity(intent);
    }

    public void addCustomer(View view) {
        dialogAddCustomer(null);
    }

    private void checkPermissionAndImport() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            importAllContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            importAllContacts();
        } else {
            Toast.makeText(this, "دسترسی به مخاطبین داده نشد", Toast.LENGTH_SHORT).show();
        }
    }

    private void importAllContacts() {
        // ابتدا چک کردن مجوز
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    100);
            return;
        }

        // اینتنت برای انتخاب مخاطب
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 200);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            include_load.setVisibility(View.VISIBLE);

            Uri contactUri = data.getData();

            new Thread(() -> {
                try {
                    // گرفتن اسم مخاطب
                    String name;
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        cursor.close();

                        // گرفتن شماره تلفن
                        Cursor phoneCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null);

                        if (phoneCursor != null && phoneCursor.moveToFirst()) {
                            String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = phone.replaceAll("[^0-9+]", "");
                            phoneCursor.close();

                            // چک کن قبلا ثبت نشده باشه
                            List<Customer> existingList = db.getCustomerByTel(phone);
                            boolean exists = existingList != null && !existingList.isEmpty();

                            String finalPhone = phone;
                            runOnUiThread(() -> {
                                if (!exists) {
                                    Customer customer = new Customer();
                                    customer.setName(name);
                                    customer.setTel(finalPhone);
                                    db.saveCustomer(customer);
                                    loadCustomerListByNameOrTel("", false);
                                    Toast.makeText(ActivityCustomer.this, "مخاطب با موفقیت اضافه شد", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ActivityCustomer.this, "این شماره قبلا ثبت شده است", Toast.LENGTH_SHORT).show();
                                }
                                include_load.setVisibility(View.GONE);
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(ActivityCustomer.this, "این مخاطب شماره تلفن ندارد", Toast.LENGTH_SHORT).show();
                                include_load.setVisibility(View.GONE);
                            });
                        }
                    } else {
                        name = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("amingoli", "onActivityResult: ",e );
                    runOnUiThread(() -> {
                        Toast.makeText(ActivityCustomer.this, "خطا در خواندن مخاطب", Toast.LENGTH_SHORT).show();
                        include_load.setVisibility(View.GONE);
                    });
                }
            }).start();
        }
    }
}