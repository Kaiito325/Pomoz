package com.example.pomoz.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pomoz.R;
import com.example.pomoz.activities.LoginActivity;
import com.example.pomoz.activities.MainActivity;
import com.example.pomoz.data.db_config.ApiClient;

public class AccountFragment extends Fragment {

    public AccountFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.account_fragment, container, false);
        Button logout = view.findViewById(R.id.logoutBtn);

        logout.setOnClickListener( v ->{
            ApiClient.getInstance(getContext()).logout();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return view;
    }
}
