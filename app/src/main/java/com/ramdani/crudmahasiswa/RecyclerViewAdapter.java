package com.ramdani.crudmahasiswa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;
import java.util.ArrayList;
public class RecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private final Context mContext;
    private final ArrayList<String> array_nim;
    private final ArrayList<String> array_nama;
    private final ArrayList<String> array_alamat;
    private final ArrayList<String> array_hobi;
    ProgressDialog progressDialog;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nim,tv_nama,tv_alamat,tv_hobi;
        public CardView cv_main;
        public MyViewHolder(View view) {
            super(view);
            cv_main = itemView.findViewById(R.id.cv_main);
            tv_nim = itemView.findViewById(R.id.tv_nim);
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tv_alamat = itemView.findViewById(R.id.tv_alamat);
            tv_hobi = itemView.findViewById(R.id.tv_hobi);
            progressDialog = new ProgressDialog(mContext);
        }
    }
    public RecyclerViewAdapter(Context mContext,
      ArrayList<String> array_nim,
      ArrayList<String> array_nama,
      ArrayList<String> array_alamat,
      ArrayList<String> array_hobi) {
        super();
        this.mContext = mContext;
        this.array_nim = array_nim;
        this.array_nama = array_nama;
        this.array_alamat = array_alamat;
        this.array_hobi = array_hobi;
    }
    @NonNull
    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.template_rv,parent,false);
        return new RecyclerViewAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tv_nim.setText(array_nim.get(position));
        holder.tv_nama.setText(array_nama.get(position));
        holder.tv_alamat.setText(array_alamat.get(position));
        holder.tv_hobi.setText(array_hobi.get(position));
        holder.cv_main.setOnClickListener((View v) -> {
            Intent i = new Intent(mContext, EditActivity.class);
            i.putExtra("nim",array_nim.get(position));
            i.putExtra("nama",array_nama.get(position));
            i.putExtra("alamat",array_alamat.get(position));
            i.putExtra("hobi",array_hobi.get(position));

            ((MainActivity)mContext).startActivityForResult(i,2);
        });
        holder.cv_main.setOnLongClickListener((View v) -> {
            new
                    AlertDialog.Builder((MainActivity)mContext)
                    .setMessage("Ingin menghapus nomor induk" + " "+array_nim.get(position)+" ?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, which) -> {

                     progressDialog.setMessage("Menghapus...");

                     progressDialog.setCancelable(false);
                     progressDialog.show();
                     AndroidNetworking.post("http://192.168.10.10:9090/semester4/Mobile/api-mhs/deleteMhs.php")
                             .addBodyParameter("nim"," "+array_nim.get(position))
                             .setPriority(Priority.MEDIUM)
                             .build()
                             .getAsJSONObject(new JSONObjectRequestListener() {
                                 @Override
                                 public void onResponse(JSONObject response) {
                                     progressDialog.dismiss();
                                     try { boolean status = response.getBoolean("status");
                                         Log.d("status"," "+status);
                                         String result = response.getString("result");
                                         if(status){
                                             if(mContext != null){
                                                 ((MainActivity)mContext)
                                                         .scrollRefresh();
                                             }
                                         }else{
                                             Toast.makeText(
                                                     mContext, " "+result, Toast.LENGTH_SHORT).show();
                                         }
                                     }catch (Exception e){
                                         e.printStackTrace();
                                     }
                                 }
                                 @Override
                                 public void onError(ANError anError) { anError.printStackTrace();
                                 }
                             });
                    })
                    .setNegativeButton("Tidak", (dialog, which) ->
                            dialog.cancel())
                    .show();
            return false;
        });
    }
    @Override
    public int getItemCount() {
        return array_nim.size();
    }
}

