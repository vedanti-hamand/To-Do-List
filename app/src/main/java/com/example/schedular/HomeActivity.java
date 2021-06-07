package com.example.schedular;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;


    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;



    private String key = "";
    private String task;
    private String description;

    private ProgressDialog loader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.homeToolbar);//initialize
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Schedular App");
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTask();
            }
        });

    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myview = inflater.inflate(R.layout.input_file , null);
        myDialog.setView(myview);
        
        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText task = myview.findViewById(R.id.Task);
        final EditText Description = myview.findViewById(R.id.description);
        Button save = myview.findViewById(R.id.saveButton);
        Button cancel = myview.findViewById(R.id.cancel_button);

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
                String mDescription = Description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());


                if (TextUtils.isEmpty(mTask)) {
                    task.setError("Task required");
                    return;

                }
                if (TextUtils.isEmpty(mDescription)) {
                    Description.setError("Description Required");
                    return;
                } else {
                    loader.setMessage("Adding your Data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mTask, mDescription, id, date);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(HomeActivity.this, "Task has been inserted Successfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(HomeActivity.this, "Failed:" + error, Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onStart() {
        super.onStart();

      FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyviewHolder> adapter = new FirebaseRecyclerAdapter<Model , MyviewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull MyviewHolder holder, final int position, @NonNull final Model model){
                holder.setData(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      key = getRef(position).getkey();
                      task= model.getTask();
                      description = model.getDescription();

                    updateTask();
                    }
                });
            }
            @NonNull
            @Override
            public MyviewHolder onCreateviewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrived_layout, parent , false);
                return new MyviewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyviewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyviewHolder(@NonNull  View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setTask(String task){
            TextView taskTextview = mView.findViewById(R.id.taskTv);
            taskTextview.setText(task);
        }
        public void setDesc(String desc){
            TextView deskTextview = mView.findViewById(R.id.DescriptionTV);
            deskTextview.setText(desc);

        }
        public void setData(String date){
            TextView dateTextview = mView.findViewById(R.id.dateTv);
        }
    }
    private void updateTask(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data, null);
                myDialog.setView(view);

        AlertDialog dialog = myDialog.create();

                EditText mTask = view.findViewById(R.id.mEdittextTask);
                EditText mDescription = view.findViewById(R.id.Description);

                mTask.setText(task);
                mTask.setSelection(task.length());

                mDescription.setText(description);
                mDescription.setSelection(description.length());

                Button deletebutton = view.findViewById(R.id.Buttondelete);
                Button Updatebutton = view.findViewById(R.id.ButtonUpdate);


                Updatebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        task = mTask.getText().toString().trim();
                        description = mDescription.getText().toString().trim();

                        String data = DateFormat.getDateInstance().format(new Date());

                        Model model = new Model(task, description , key , Date);

                        reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
                                }else {
                                    String error = task.getException().toString();
                                    Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.dismiss();

                        reference.child(key);
                    }
                });
            deletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull  Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        }else{
                                String error = task.getException().toString();
                                Toast.makeText(HomeActivity.this, "failed to delete task", Toast.LENGTH_SHORT).show();
                            }
                    });

                    });

                }
            });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
    return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Logout:
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}