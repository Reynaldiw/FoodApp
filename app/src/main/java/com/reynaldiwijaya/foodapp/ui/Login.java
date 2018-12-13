package com.reynaldiwijaya.foodapp.ui;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


import com.reynaldiwijaya.foodapp.FoodUtama;
import com.reynaldiwijaya.foodapp.R;
import com.reynaldiwijaya.foodapp.helper.SessionManager;
import com.reynaldiwijaya.foodapp.model.ResponseLogin;
import com.reynaldiwijaya.foodapp.network.ConfigRetrofit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends SessionManager {

    @BindView(R.id.login_username)
    TextInputEditText loginUsername;
    @BindView(R.id.login_password)
    TextInputEditText loginPassword;
    @BindView(R.id.rg_user_admin_sign)
    RadioButton rgUserAdminSign;
    @BindView(R.id.rg_user_biasa_sign)
    RadioButton rgUserBiasaSign;
    @BindView(R.id.sign_in)
    Button signIn;
    @BindView(R.id.register)
    TextView register;

    String levelUser, userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Go Food");

        setUpUser();
    }

    private void setUpUser() {
        if (rgUserAdminSign.isChecked()) levelUser = "Admin";
        else levelUser = "User Biasa";
    }

    @OnClick({R.id.rg_user_admin_sign, R.id.rg_user_biasa_sign, R.id.sign_in, R.id.register})
    public void onViewClicked(View view) {
        userName = loginUsername.getText().toString().trim();
        password = loginPassword.getText().toString().trim();

        switch (view.getId()) {
            case R.id.rg_user_admin_sign:
                levelUser = "Admin";
                break;
            case R.id.rg_user_biasa_sign:
                levelUser = "User Biasa";
                break;
            case R.id.sign_in:
                setLoginUser();
                break;
            case R.id.register:
                intent(Register.class);
                break;
        }
    }

    private void setLoginUser() {
        if (TextUtils.isEmpty(userName)) {
            loginUsername.setError(getString(R.string.error_message));
            loginUsername.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            loginPassword.setError(getString(R.string.error_message));
        } else if (password.length() < 6) {
            loginPassword.setError(getString(R.string.error_message_password));
        }else {
            loginUser();
        }
    }

    private void loginUser() {
        showProgressDialog("Try to Login");
        ConfigRetrofit.getInstance().signIn(userName, password, levelUser)
                .enqueue(new Callback<ResponseLogin>() {
                    @Override
                    public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                        if (response != null && response.isSuccessful()){
                            hideProgressDialog();
                            String result = response.body().getResult();
                            String msg = response.body().getMsg();
                            String userid = response.body().getUser().getIdUser();

                            if (result.equals("1")){
                                intent(FoodUtama.class);
                                shortToast(msg);

                                sessionManager.setIdUser(userid);
                                sessionManager.createSession(userName);

                                finish();
                            } else {
                                shortToast(msg);
                            }
                        }else response.errorBody();
                    }

                    @Override
                    public void onFailure(Call<ResponseLogin> call, Throwable t) {
                        shortToast(t.getMessage());
                    }
                });

    }
}
