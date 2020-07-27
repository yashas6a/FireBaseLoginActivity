package com.androidapp.ezTODO;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment{
    public static String url;

    public static FirebaseAuth mAuth;
    TextView dp_name, dp_emailid;
    ImageView dp_photo;



    public ProfileFragment() {
        // Required empty public constructor

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dp_name = view.findViewById(R.id.tv_prof_name);
        dp_emailid = view.findViewById(R.id.tv_emailid);
        dp_photo = view.findViewById(R.id.profilepic);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if(signInAccount!=null){
            dp_name.setText(signInAccount.getDisplayName());
            dp_emailid.setText(signInAccount.getEmail());
            String img_url = signInAccount.getPhotoUrl().toString();
            Glide.with(this).load(img_url).into(dp_photo);
        }


        return view;

    }
}