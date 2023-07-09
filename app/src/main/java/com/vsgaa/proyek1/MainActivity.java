package com.vsgaa.proyek1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE = 100;
    private SimpleAdapter adapter;
    private final ArrayList<Map<String, Object>> itemDataList = new ArrayList<>();
    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/kominfo.proyek1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Aplikasi Catatan Proyek 1");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, this.getTheme()));

        ListView listView = findViewById(R.id.listView);
        adapter = new SimpleAdapter(this, itemDataList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "date"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyResults));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> item = itemDataList.get(i);
                String filename = item.get("name").toString();
                Intent intent = new Intent(MainActivity.this, InsertAndViewActivity.class);
                intent.putExtra("filename", filename);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> item = itemDataList.get(i);
                String filename = item.get("name").toString();
                tampilkanDialogKonfirmasiHapusCatatan(filename,i);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_tambah) {
            Intent intent = new Intent(MainActivity.this, InsertAndViewActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE);
        }
        mengambilListFilePadaFolder();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mengambilListFilePadaFolder() {

        File direktori = new File(path);
        itemDataList.clear();

        if (direktori.exists()) {
            File[] files = direktori.listFiles();
            String[] filesname = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat sdf = new SimpleDateFormat("dd MM YYYY HH:mm:ss");

            for (int i = 0; i < files.length; i++) {
                filesname[i] = files[i].getName();
                Date lastModeDate = new Date(files[i].lastModified());
                dateCreated[i] = sdf.format(lastModeDate);

                Map<String, Object> listItemMap = new HashMap<>();
                listItemMap.put("name", filesname[i]);
                listItemMap.put("date", dateCreated[i]);
                itemDataList.add(listItemMap);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void tampilkanDialogKonfirmasiHapusCatatan(final String filename, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Catatan")
                .setMessage("Apakah anda ingin hapus catatan " + filename)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hapusFile(filename, position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create().show();
    }

    private void hapusFile(final String filename, final int position) {

        File file = new File(path, filename);
        if (file.exists()) {
            file.delete();
            itemDataList.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}