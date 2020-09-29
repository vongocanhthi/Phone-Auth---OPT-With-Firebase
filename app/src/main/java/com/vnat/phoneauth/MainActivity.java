package com.vnat.phoneauth;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ccpPhoneNumber)
    CountryCodePicker ccpPhoneNumber;
    @BindView(R.id.edtPhoneNumber)
    EditText edtPhoneNumber;
    @BindView(R.id.pvVerifyOTP)
    EditText pvVerifyOTP;
    @BindView(R.id.btnRequestOTP)
    Button btnRequestOTP;
    @BindView(R.id.btnVeryfyOTP)
    Button btnVeryfyOTP;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.txtTime)
    TextView txtTime;

    FirebaseAuth auth;

    String phoneNumber = "";
    String mVerificationId = "";
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
        event();

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if (code != null){
                pvVerifyOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(MainActivity.this, "Send code success", Toast.LENGTH_SHORT).show();
            mVerificationId = s;
            mResendToken = forceResendingToken;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Verify success", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(MainActivity.this, "Verify not success", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void init() {
        auth = FirebaseAuth.getInstance();

    }

    private void event() {
        btnRequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = "+" + ccpPhoneNumber.getSelectedCountryCode().concat(edtPhoneNumber.getText().toString());

                if (edtPhoneNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Phone number not empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendVerificationCodeToUser(phoneNumber);
                }
            }
        });

        btnVeryfyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pvVerifyOTP.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Veryfy OTP not empty", Toast.LENGTH_SHORT).show();
                } else {
                    String code = pvVerifyOTP.getText().toString();
                    if (!code.isEmpty()){
                        verifyCode(code);
                    }
                }
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callbacks
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        //updateUI(currentUser);
    }

}