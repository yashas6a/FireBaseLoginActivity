package com.androidapp.ezTODO;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth fAuth;
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(getContext());

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        onlineUserID = fUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();
        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        task = model.getTask();
                        description = model.getDescription();

                        updateTask();
                    }
                });



            }



            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
    private void updateTask(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView((view));

        final AlertDialog dialog = myDialog.create();

        final EditText mTask = view.findViewById(R.id.edit_task);
        final EditText mDescription = view.findViewById(R.id.edit_description);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button delButton = view.findViewById(R.id.delete_button);
        Button updateButton = view.findViewById(R.id.update_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = mTask.getText().toString().trim();
                description = mDescription.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                Model model = new Model(task, description, key, date);
                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Task updated successfully",Toast.LENGTH_SHORT ).show();
                        } else {
                            String err = task.getException().toString();
                            Toast.makeText(getApplicationContext(),"Failed "+err,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Task Deleted",Toast.LENGTH_SHORT ).show();
                        } else{
                            String err = task.getException().toString();
                            Toast.makeText(getApplicationContext(),"Failed "+err,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();

    }
    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View myView = inflater.inflate(R.layout.input_file, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        dialog.show();

        final EditText task = myView.findViewById(R.id.new_task);
        final EditText description = myView.findViewById(R.id.new_description);
        Button save = myView.findViewById(R.id.save_button);
        Button cancel = myView.findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if(TextUtils.isEmpty(mTask)){
                    task.setError("Task Required");
                    return;
                }
                if(TextUtils.isEmpty(mDescription)){
                    description.setError("Description Required");
                    return;
                } else {
                    loader.setMessage("adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mTask, mDescription, id, date);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Task added Successfully",Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            } else{
                                String error = task.getException().toString();
                                Toast.makeText(getApplicationContext(),"Failed "+error,Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });
                }

                dialog.dismiss();
            }


        });
        dialog.show();
    }
}