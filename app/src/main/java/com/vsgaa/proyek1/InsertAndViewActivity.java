package com.vsgaa.proyek1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class InsertAndViewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/kominfo.proyek1";
    private static final int REQUEST_CODE_STORAGE = 100;
    private EditText editFileName, editContent;
    private String tempCatatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editFileName = findViewById(R.id.editFilename);
        editContent = findViewById(R.id.editContent);
        Button btnSimpan = findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filename = extras.getString("filename");
            editFileName.setText(filename);
            editFileName.setEnabled(false);
            getSupportActionBar().setTitle("Ubah Catatan");
            bacaFile();
        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSimpan) {
            if(editFileName.getText().toString().equals("")||editContent.getText().toString().equals("")){
                Toast.makeText(this,"Nama File & isi catatan kosong", Toast.LENGTH_SHORT).show();
            }else {
                buatDanUbah();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bacaFile() {
        File file = new File(path, editFileName.getText().toString());
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                Log.e(getClass().toString(), "Exception: " + e.getMessage());
            }
            tempCatatan = text.toString();
            editContent.setText(text.toString());
        }
    }

    void buatDanUbah() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        File direktori = new File(path);
        if (!direktori.exists()) {
            direktori.mkdir();
        }

        File file = new File(path, editFileName.getText().toString());
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, false);
            outputStream.write(editContent.getText().toString().getBytes());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Catatan ditambahkan", Toast.LENGTH_SHORT).show();
            tempCatatan = editContent.getText().toString();
        } catch (IOException e) {
            Log.e(getClass().toString(), "Exception: " + e.getMessage());
        }
    }

    private void tampilkanDialogKonfirmasiPenyimpanan() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah anda yakin ingin menyimpan catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buatDanUbah();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create().show();
    }

    @Override
    public void onBackPressed() {
        if (!tempCatatan.equals(editContent.getText().toString())) {
            tampilkanDialogKonfirmasiPenyimpanan();
        } else {
            super.onBackPressed();
        }
    }
}