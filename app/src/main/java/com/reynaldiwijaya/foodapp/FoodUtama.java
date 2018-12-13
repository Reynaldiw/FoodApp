package com.reynaldiwijaya.foodapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.reynaldiwijaya.foodapp.helper.MyFunction;
import com.reynaldiwijaya.foodapp.helper.SessionManager;
import com.reynaldiwijaya.foodapp.model.DataKategoriItem;
import com.reynaldiwijaya.foodapp.model.DataMakananItem;
import com.reynaldiwijaya.foodapp.model.ResponseDataMakanan;
import com.reynaldiwijaya.foodapp.model.ResponseKategori;
import com.reynaldiwijaya.foodapp.network.ConfigRetrofit;
import com.reynaldiwijaya.foodapp.ui.adapter.FoodAdapter;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodUtama extends SessionManager implements SwipeRefreshLayout.OnRefreshListener, FoodAdapter.onItemClick {

    @BindView(R.id.spin_kategori_utama)
    Spinner spinKategoriUtama;
    @BindView(R.id.list_food)
    RecyclerView listFood;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;

    //TODO Deklarasi
    Dialog dialog, dialog2;
    TextInputEditText edtInsertNameFood, edtUpdateId, edtUpdateNameFood;
    Button btnUploadImagesFood, btnUpload, btnCancel, btnUpdate, btnDelete, btnUpdateImages;
    ImageView ivPreviewInsert, ivPreviewUpdate;
    Spinner spinInsert, spinUpdate;
    String idMakanan, userId, kategori, path, time, namaMakanan;
    com.squareup.picasso.Target target;
    Uri filepath;
    Bitmap bitmap;

    List<DataKategoriItem> listKategori;
    List<DataMakananItem> listDataMakanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_utama);
        ButterKnife.bind(this);

        setRequestPermission();
        sendRequestDataKategori(spinKategoriUtama);
        refresh.setOnRefreshListener(this);
        dialogInsertData();
    }

    /**
     * Permission untuk akses data images nya ke device
     */

    private void setRequestPermission() {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_PERMISSION);

    }

    @Override
    public void onRefresh() {
        refresh.setRefreshing(false);
        sendRequestDataMakanan(kategori);    }

    private void sendRequestDataKategori(final Spinner spinnerKategori){
        ConfigRetrofit.getInstance().getDataKategori().enqueue(new Callback<ResponseKategori>() {
            @Override
            public void onResponse(Call<ResponseKategori> call, Response<ResponseKategori> response) {
                if (response != null && response.isSuccessful()){

                    /**
                     * get body json dari list data kategorinya
                     */

                    listKategori = response.body().getDataKategori();

                    //tampung data dalam variable
                    String itemId[] = new String[listKategori.size()];
                    String[] itemName = new String[listKategori.size()];

                    /**
                     * proses looping data
                     */

                    for (int i = 0; i < listKategori.size(); i++){
                        itemId[i] = listKategori.get(i).getIdKategori();
                        itemName[i] = listKategori.get(i).getNamaKategori();
                    }

                    /**
                     * Menampilkan data kategori ke Spinner
                     */

                    ArrayAdapter adapter = new ArrayAdapter(FoodUtama.this, android.R.layout.simple_spinner_item, itemName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerKategori.setAdapter(adapter);
                    spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            kategori = adapterView.getItemAtPosition(i).toString();
                            sendRequestDataMakanan(kategori);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            /**
                             * Nothing to do
                             */
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ResponseKategori> call, Throwable t) {
                Log.d("FoodUtama", t.getMessage());
                shortToast(t.getMessage());
            }
        });
    }

    private void sendRequestDataMakanan(String kategori){
//        showProgressDialog(getString(R.string.loading));

        //Panggil session karena mau get data nya berdasarkan ide usernya
        String idUser = sessionManager.getIdUser();
        ConfigRetrofit.getInstance().getDataMakanan(idUser, kategori).enqueue(new Callback<ResponseDataMakanan>() {

            @Override
            public void onResponse(Call<ResponseDataMakanan> call, Response<ResponseDataMakanan> response) {
//                hideProgressDialog();
                if (response != null && response.isSuccessful()){
                    listDataMakanan = response.body().getDataMakanan();
                    Log.d("FoodUtama", "Data: " + response.message());

                    String itemsIdFood [] = new String[listDataMakanan.size()];

                    for (int i = 0 ; i < listDataMakanan.size(); i++){
                        itemsIdFood[i] = listDataMakanan.get(i).getIdMakanan();
                        userId = itemsIdFood[i];

                    }

                    setUpList(listDataMakanan);
                }
            }

            @Override
            public void onFailure(Call<ResponseDataMakanan> call, Throwable t) {
                Log.d("FoodUtama", t.getMessage());
                longToast(t.getMessage());
            }
        });
    }

    private void setUpList(List<DataMakananItem> listDataMakanan) {
        listFood.setHasFixedSize(true);
        listFood.setLayoutManager(new LinearLayoutManager(this));
        FoodAdapter foodAdapter = new FoodAdapter(this, listDataMakanan);
        listFood.setAdapter(foodAdapter);
    }

    /**
     * Function buat dialog inputan user
     */

    private void dialogInsertData() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(FoodUtama.this);
                dialog.setContentView(R.layout.item_add_food);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);

                edtInsertNameFood = dialog.findViewById(R.id.edt_input_makanan);
                btnUploadImagesFood = dialog.findViewById(R.id.btn_upload_images);
                spinInsert = dialog.findViewById(R.id.spin_kategori);
                ivPreviewInsert = dialog.findViewById(R.id.image_preview_insert);
                btnUpload = dialog.findViewById(R.id.btn_upload);
                btnCancel = dialog.findViewById(R.id.btn_cancel);

                btnUploadImagesFood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser(REQ_CHOOSE_FILE);

                    }
                });

                sendRequestDataKategori(spinInsert);
                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        namaMakanan = edtInsertNameFood.getText().toString().trim();

                        if (TextUtils.isEmpty(namaMakanan)){
                            edtInsertNameFood.setError(getString(R.string.error_message));
                            edtInsertNameFood.requestFocus();

                        }else if (ivPreviewInsert.getDrawable() == null){
                            shortToast(getString(R.string.noimages));

                        }else {
                            insertFoodData(kategori);
                            dialog.dismiss();
                        }

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Menampilkan directory file images
     */

    private void showFileChooser(int requestCode) {

        Intent toGallery = new Intent(Intent.ACTION_PICK);
        toGallery.setType("image/*");
        toGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(toGallery, "Pilih Gambar"),requestCode );

    }

    /**
     * Function / method return data images yang kita request
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyFunction.REQ_CHOOSE_FILE && resultCode == RESULT_OK    && data != null &&
        data.getData() != null) {
            Log.d("FoodUtama", "Photo Was Selected");

            filepath = data.getData();
        }

        try {

            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
            ivPreviewInsert.setImageBitmap(bitmap);

        }catch (IOException e){
            Log.d("FoodUtama", "msg: " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void insertFoodData(String kategori) {
        //mengambil path dari gmbar yang d i upload
        try{
            path = getPath(filepath);
            userId = sessionManager.getIdUser();
            Log.d("FoodUtama", "Data2: " + path);
            shortToast("Success to Add");
        }catch (Exception e){
            Log.d("FoodUtama", "Msg; " + e.getMessage());
            shortToast("Images Terlalu Besar, Silahkan pilih images yang lebih kecil");
            e.printStackTrace();
        }
        /**
         * Sets the maximum time to wait in milliseconds between two upload attempts.
         * This is useful because every time an upload fails, the wait time gets multiplied by
         * {@link UploadService#BACKOFF_MULTIPLIER} and it's not convenient that the value grows
         * indefinitely.
         */

        time = getCurentDate();
        try {
            new MultipartUploadRequest(c, BuildConfig.UPLOAD_URL)
                    .addFileToUpload(path, "image")
                    .addParameter("vsnamamakanan", namaMakanan)
                    .addParameter("vstimeinsert",time )
                    .addParameter("vskategori", kategori)
                    .addParameter("vsiduser", userId)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

            sendRequestDataMakanan(kategori);

        }catch (MalformedURLException e){
            Log.d("Tag","Jat: " + e.getMessage());
            e.printStackTrace();
            shortToast(e.getMessage());
        } catch (FileNotFoundException e){
            Log.d("Tag","Jav: " + e.getMessage());
            shortToast(e.getMessage());
            e.printStackTrace();
        }

    }



    /***
     * Fuction / method untuk mengambil file images nya
     * dan berdasarkan item id yang dipilih menggunakan cursor
     * karena file images mengambil data dari storage database service
     */

    private String getPath(Uri filepath) {

        Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null, MediaStore.Images
                .Media._ID + " = ? ", new String[]{document_id}, null);

        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void onItemClick(int position) {
        dialog2 = new Dialog(FoodUtama.this);
        dialog2.setContentView(R.layout.item_update_food);
        dialog2.setTitle(getString(R.string.data_food));
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.show();

        edtUpdateId = findViewById(R.id.edt_id_makanan);
        edtUpdateNameFood = findViewById(R.id.edt_input_makanan_update);
        btnUpdateImages = findViewById(R.id.btn_update_images);
        spinUpdate = findViewById(R.id.spin_kategori_update);
        ivPreviewUpdate = findViewById(R.id.image_preview_update);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        spinUpdate = findViewById(R.id.spin_kategori_update);

        target = new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //Do something
//            ...

                ivPreviewUpdate.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(c)
                .load(BuildConfig.IMAGE_URL + listDataMakanan.get(position).getFotoMakanan().toString())
                .into(target);

        sendRequestDataKategori(spinUpdate);

        edtUpdateNameFood.setText(listDataMakanan.get(position).getMakanan());
        edtUpdateId.setText(listDataMakanan.get(position).getIdMakanan());
        



        btnUpdateImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_utama, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final AlertDialog.Builder dialog3 = new AlertDialog.Builder(FoodUtama.this);
            dialog3.setTitle("Confirmation");
            dialog3.setMessage("Are you sure ?");
            dialog3.setCancelable(true);
            dialog3.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sessionManager.logout();
                    finish();
                }
            });
            dialog3.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog3.show();
        }

        return super.onOptionsItemSelected(item);
    }
}

