package com.example.evacuateme.Activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.evacuateme.Fragment.RegistrationFragment;
import com.example.evacuateme.Fragment.SignInFragment;
import com.example.evacuateme.R;

public class LogRegActivity extends AppCompatActivity {
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_reg);
        Intent intent = getIntent();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(intent.getBooleanExtra("isExist", false)){
            SignInFragment signInFragment = new SignInFragment();
            fragmentTransaction.replace(R.id.log_reg_container_fragment, signInFragment);
        }
        else {
            RegistrationFragment registrationFragment = new RegistrationFragment();
            Bundle bundle = new Bundle();
            bundle.putString("phoneNumber", intent.getStringExtra("phoneNumber"));
            registrationFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.log_reg_container_fragment, registrationFragment);
        }
        fragmentTransaction.commit();
    }
}
