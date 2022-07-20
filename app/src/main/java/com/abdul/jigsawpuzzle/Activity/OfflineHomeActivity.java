package com.abdul.jigsawpuzzle.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.Interface.ItemClickListener;
import com.abdul.jigsawpuzzle.Model.PuzzleItem;
import com.abdul.jigsawpuzzle.R;
import com.abdul.jigsawpuzzle.ViewHolder.ListPuzzleViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class OfflineHomeActivity extends AppCompatActivity {

    Query query;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseRecyclerOptions<PuzzleItem> options;
    FirebaseRecyclerAdapter<PuzzleItem, ListPuzzleViewHolder> adapter;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Holma Puzzle Game");
        toolbar.setLogo(R.drawable.ic_computer_black_24dp);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_list_wallpaper);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        offline();

    }

    private void offline() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Common.PUZZLES);
        databaseReference.keepSynced(true);

        query = databaseReference.orderByKey().limitToFirst(5);

        options = new FirebaseRecyclerOptions.Builder<PuzzleItem>()
                .setQuery(query, PuzzleItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<PuzzleItem, ListPuzzleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListPuzzleViewHolder holder, int position, @NonNull final PuzzleItem model) {
                Picasso.with(getBaseContext())
                        .load(model.getimageUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.jigsawPuzzle, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getBaseContext())
                                        .load(model.getimageUrl())
                                        .error(R.drawable.ic_terrain_black_24dp)
                                        .into(holder.jigsawPuzzle, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("ERROR_HOLMA", "Could not fetch image");
                                            }
                                        });

                            }
                        });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(OfflineHomeActivity.this, CreateActivity.class);
                        Common.puzzleItem = model;
                        Common.SELECT_KEY = adapter.getRef(position).getKey();
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ListPuzzleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_puzzle_item, parent, false);
                int height = parent.getMeasuredHeight()/2;
                itemView.setMinimumHeight(height);
                return new ListPuzzleViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

}
