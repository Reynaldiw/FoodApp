package com.reynaldiwijaya.foodapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.reynaldiwijaya.foodapp.R;
import com.reynaldiwijaya.foodapp.helper.MyFunction;
import com.reynaldiwijaya.foodapp.model.ResponseRegister;
import com.reynaldiwijaya.foodapp.network.ConfigRetrofit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends MyFunction {

    @BindView(R.id.regis_name)
    TextInputEditText regisName;
    @BindView(R.id.regis_alamat)
    TextInputEditText regisAlamat;
    @BindView(R.id.regis_no_tlp)
    TextInputEditText regisNoTlp;
    @BindView(R.id.spin_kelamin)
    Spinner spinKelamin;
    @BindView(R.id.regis_username)
    TextInputEditText regisUsername;
    @BindView(R.id.regis_pass)
    TextInputEditText regisPass;
    @BindView(R.id.regis_confir_pass)
    TextInputEditText regisConfirPass;
    @BindView(R.id.rg_user_admin)
    RadioButton rgUserAdmin;
    @BindView(R.id.rg_user_biasa)
    RadioButton rgUserBiasa;
    @BindView(R.id.sign_up)
    Button signUp;
    @BindView(R.id.login)
    TextView login;

    String jenisKelamin[] = {"Laki - Laki", "Perempuan"};
    String name, alamat, noHp, username, password, conPassword, jenKel, levelUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Go Food");

        setUpUser();
        setUpJenKel();
    }

    /**
     * Cek level user
     */
    private void setUpUser() {
        if (rgUserAdmin.isChecked()) levelUser = "Admin";
        else levelUser = "User Biasa";
    }

    /**
     * Nampilin jenis kelamin dengan spinner
     */

    private void setUpJenKel() {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisKelamin);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKelamin.setAdapter(adapter);
        spinKelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jenKel = jenisKelamin[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /** nothing to do */
            }
        });
    }

    @OnClick({R.id.rg_user_admin, R.id.rg_user_biasa, R.id.sign_up, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rg_user_admin:
                levelUser = "Admin";
                break;
            case R.id.rg_user_biasa:
                levelUser = "User Biasa";
                break;
            case R.id.sign_up:
                setSelectInputan();
                break;
            case R.id.login:
                break;
        }
    }

    private void setSelectInputan() {

        /**
         * cara ambil inputan di edit text / text box
         */

        name = regisName.getText().toString().trim();
        alamat = regisAlamat.getText().toString().trim();
        noHp = regisNoTlp.getText().toString().trim();
        username = regisUsername.getText().toString().trim();
        password = regisPass.getText().toString().trim();
        conPassword = regisConfirPass.getText().toString().trim();

        /**
         * seleksi inputan nya kosong / tidak
         */

        if (TextUtils.isEmpty(name)) {
            regisName.setError(getString(R.string.error_message));
            regisName.requestFocus();
        } else if (TextUtils.isEmpty(alamat)) {
            regisAlamat.setError(getString(R.string.error_message));
            regisAlamat.requestFocus();
        } else if (TextUtils.isEmpty(noHp)) {
            regisNoTlp.setError(getString(R.string.error_message));
            regisNoTlp.requestFocus();
        } else if (noHp.length() < 10) {
            regisNoTlp.setError(getString(R.string.error_message_nohp));
        } else if (TextUtils.isEmpty(username)) {
            regisUsername.setError(getString(R.string.error_message));
            regisUsername.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            regisPass.setError(getString(R.string.error_message));
            regisPass.requestFocus();
        } else if (password.length() < 6) {
            regisPass.setError(getString(R.string.error_message_password));
            regisPass.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            regisConfirPass.setError(getString(R.string.error_message));
            regisConfirPass.requestFocus();
        } else if (!conPassword.equals(password)) {
            regisConfirPass.setError(getString(R.string.error_message_conpassword));
            regisConfirPass.requestFocus();
        } else {
            sendRequestRegister();
        }
    }

    private void sendRequestRegister() {
        showProgressDialog("Loading...");
        ConfigRetrofit.getInstance().registerUser(name, alamat, noHp, jenKel, username, password, levelUser)
                .enqueue(new Callback<ResponseRegister>() {
                    @Override
                    public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                        if (response != null && response.isSuccessful()){
                            hideProgressDialog();

                            String result = response.body().getResult();
                            String msg = response.body().getMsg();

                            if (result.equals("1")){
                                shortToast(msg);
                                intent(Login.class);
                                finish();
                            }else shortToast(msg);
                        }else response.errorBody();
                    }

                    @Override
                    public void onFailure(Call<ResponseRegister> call, Throwable t) {
                        shortToast(t.getMessage());
                    }
                });

    }
}
