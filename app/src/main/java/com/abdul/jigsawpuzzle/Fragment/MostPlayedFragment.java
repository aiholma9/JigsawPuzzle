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
public class MostPlayedFragment extends Fragment {

    RecyclerView recyclerView;

    Query query;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseRecyclerOptions<PuzzleItem> options;
    FirebaseRecyclerAdapter<PuzzleItem, ListPuzzleViewHolder> adapter;

    private static MostPlayedFragment INSTANCE = null;


    public MostPlayedFragment() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Common.PUZZLES);

        query = databaseReference.orderByChild("played").limitToLast(10);

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

    public static MostPlayedFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MostPlayedFragment();
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_played, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_mostPlayed);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadMostPlayed();

        return view;
    }

    private void loadMostPlayed() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}
