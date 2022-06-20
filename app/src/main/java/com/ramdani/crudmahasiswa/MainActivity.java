package com.ramdani.crudmahasiswa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout srl_main;
    RecyclerView rv_main;
    ArrayList<String>
            array_nama,array_nim,array_alamat,array_hobi;
    ProgressDialog progressDialog;
    RecyclerViewAdapter recycleViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srl_main = findViewById(R.id.srl_main);
        rv_main = findViewById(R.id.rv_main);
        progressDialog = new ProgressDialog(this);
        rv_main.hasFixedSize();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv_main.setLayoutManager(layoutManager);
        srl_main.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        scrollRefresh();
                        srl_main.setRefreshing(false);
                    }
                });
        scrollRefresh();
    }
    public void scrollRefresh(){
        progressDialog.setMessage("Mengambil Data.....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }},1200);
    }
    void initializeArray(){
        array_nim = new ArrayList<String>();
        array_nama = new ArrayList<String>();
        array_alamat = new ArrayList<String>();
        array_hobi = new ArrayList<String>();
        array_nim.clear();
        array_nama.clear();
        array_alamat.clear();
        array_hobi.clear();
    }
    public void getData(){
        initializeArray();

        AndroidNetworking.get("http://192.168.10.10:9090/semester4/Mobile/api-mhs/getData.php")
                        .setTag("Get Data")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override

                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try{ Boolean status = response.getBoolean("status");
                                    if(status){
                                        JSONArray ja = response.getJSONArray("result");
                                        Log.d("respon",""+ja);
                                        for(int i = 0 ; i <
                                                ja.length() ; i++){

                                            JSONObject jo =

                                                    ja.getJSONObject(i);

                                            array_nim

                                                    .add(jo.getString("nim"));
                                            array_nama
                                                    .add(jo.getString("nama"));
                                            array_alamat
                                                    .add(jo.getString("alamat"));
                                            array_hobi
                                                    .add(jo.getString("hobi"));
                                        }
                                        recycleViewAdapter = new
                                                RecyclerViewAdapter(
                                                MainActivity.this,
                                                array_nim,array_nama,array_alamat,array_hobi);
                                        rv_main.setAdapter(recycleViewAdapter);
                                    }else{
                                        Toast.makeText(MainActivity.this,
                                                "Gagal Mengambil Data", Toast.LENGTH_SHORT)
                                                        .show();
                                        recycleViewAdapter = new
                                                RecyclerViewAdapter(MainActivity
                                                .this,array_nim,array_nama,array_alamat,array_hobi);
                                        rv_main.setAdapter(recycleViewAdapter);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(ANError anError)

                            {
                        }
});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.menu_add){
            Intent i = new
                    Intent(MainActivity.this,AddActivity.class);
            startActivityForResult(i,1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){ scrollRefresh();
            }else
            if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Canceled",
                        Toast.LENGTH_SHORT).show();
            }
        } if(requestCode==2){
            if(resultCode==RESULT_OK){ scrollRefresh();
            }else
            if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Canceled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}