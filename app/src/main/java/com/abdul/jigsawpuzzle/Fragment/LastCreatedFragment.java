package com.abdul.jigsawpuzzle.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.Activity.CreateActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class LastCreatedFragment extends Fragment {

    private static LastCreatedFragment INSTANCE = null;
    RecyclerView recyclerView;

    Query query;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //FireBase UI Adapter
    FirebaseRecyclerOptions<PuzzleItem> options;
    FirebaseRecyclerAdapter<PuzzleItem, ListPuzzleViewHolder> adapter;

    public LastCreatedFragment() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Common.PUZZLES);
        databaseReference.keepSynced(true);

        query = databaseReference.orderByKey().limitToLast(5);

        options = new FirebaseRecyclerOptions.Builder<PuzzleItem>()
                .setQuery(query, PuzzleItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<PuzzleItem, ListPuzzleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListPuzzleViewHolder holder, int position, @NonNull final PuzzleItem model) {
                Picasso.with(getActivity())
                        .load(model.getimageUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.jigsawPuzzle, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getActivity())
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
                        Intent intent = new Intent(getActivity(), CreateActivity.class);
                        Common.puzzleItem = model;
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
    }

    public static LastCreatedFragment getInstance(){
        if (INSTANCE == null)
            INSTANCE = new LastCreatedFragment();
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_last_created, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_favourite);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadLastCreated();

        return view;
    }

    private void loadLastCreated() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}
