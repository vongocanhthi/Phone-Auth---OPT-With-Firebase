package com.vnat.phoneauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ccpPhoneNumber)
    CountryCodePicker ccpPhoneNumber;
    @BindView(R.id.edtPhoneNumber)
    EditText edtPhoneNumber;
    @BindView(R.id.btnRequestOTP)
    Button btnRequestOTP;

    String phoneNumber = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        event();

    }

    private void event() {
        btnRequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = edtPhoneNumber.getText().toString();

                if (phoneNo.isEmpty()){
                    Toast.makeText(MainActivity.this, "Phone number not empty", Toast.LENGTH_SHORT).show();
                }else{
                    phoneNumber = "+" + ccpPhoneNumber.getSelectedCountryCode().concat(phoneNo);

                    Intent intent = new Intent(MainActivity.this, VerifyOtpActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }
            }
        });


    }

}