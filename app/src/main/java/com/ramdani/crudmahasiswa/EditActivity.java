package com.ramdani.crudmahasiswa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;
public class EditActivity extends AppCompatActivity {
    EditText et_nim,et_nama,et_alamat,et_hobi;
    String nim,nama,alamat,hobi;
    Button btn_submit;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        et_nim = findViewById(R.id.et_nim);
        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_hobi = findViewById(R.id.et_hobi);
        btn_submit = findViewById(R.id.btn_submit);
        progressDialog = new ProgressDialog(this);
        getDataIntent();
        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Menambahkan Data...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                nim = et_nim.getText().toString();
                nama = et_nama.getText().toString();
                alamat = et_alamat.getText().toString();
                hobi = et_hobi.getText().toString();
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        validasiData();
                    }},1000);
            }
        });
    }
    void getDataIntent(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            et_nim.setText(bundle.getString("nim"));
            et_nama.setText(bundle.getString("nama"));
            et_alamat.setText(bundle.getString("alamat"));
            et_hobi.setText(bundle.getString("hobi"));
        }else{
            et_nim.setText("");
            et_nama.setText("");
            et_alamat.setText("");
            et_hobi.setText("");
        }
    }
    void validasiData(){
        if(nim.equals("") || nama.equals("")
                || alamat.equals("") || hobi.equals("")){
            progressDialog.dismiss();
            Toast.makeText(EditActivity.this,
                    "Periksa kembali data yang anda masukkan !",
                    Toast.LENGTH_SHORT).show();
        }else {
            updateData();
        }
    }
    void updateData(){
        AndroidNetworking.post("http://192.168.10.10:9090/semester4/Mobile/api-mhs/updateMhs.php")

                        .addBodyParameter("nim",""+nim)
                        .addBodyParameter("nama",""+nama)
                        .addBodyParameter("alamat",""+alamat)
                        .addBodyParameter("hobi",""+hobi)
                        .setTag("Update Data")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener()
                        {
                            @Override

                            public void onResponse(JSONObject response)

                            {
                                progressDialog.dismiss();
                                Log.d("responEdit",""+response);

                                try{ Boolean status =
                                        response.getBoolean("status");
                                    if(status){ new
                                      AlertDialog.Builder(EditActivity.this)
                                      .setMessage("Berhasil Mengupdate Data")
                                      .setCancelable(false)
                                      .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void
                                                    onClick(DialogInterface dialog,
                                                            int which) {
                                                        Intent i = getIntent();
                                                        setResult(RESULT_OK,i);
                                                        EditActivity.this.finish();
                                                    }
                                                }) .show();
                                    }else{ new
                                            AlertDialog.Builder(EditActivity.this)
                                            .setMessage("Gagal Mengupdate Data")
                                            .setCancelable(false)
                                            .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void
                                                onClick(DialogInterface dialog,
                                                        int which) {
                                                    Intent i = getIntent();
                                                        setResult(RESULT_CANCELED,i);
                                                        EditActivity.this.finish();
                                                    }
                                                }) .show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                } }
                            @Override
                            public void onError(ANError anError) {
                            }
                        });
    }
}
