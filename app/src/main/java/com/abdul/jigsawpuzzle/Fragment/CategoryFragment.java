package com.abdul.jigsawpuzzle.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.Interface.ItemClickListener;
import com.abdul.jigsawpuzzle.Activity.ListPuzzleActivity;
import com.abdul.jigsawpuzzle.Model.CategoryItem;
import com.abdul.jigsawpuzzle.R;
import com.abdul.jigsawpuzzle.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    //FireBase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //FireBase UI Adapter
    FirebaseRecyclerOptions<CategoryItem> options;
    FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> adapter;

    //View
    RecyclerView recyclerView;

        private static CategoryFragment INSTANCE = null;

        public CategoryFragment() {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference(Common.CATEGORY);


            options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                    .setQuery(databaseReference, CategoryItem.class) //Select all
                    .build();

            //load imag
            adapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position, @NonNull final CategoryItem model) {
                    Picasso.with(getActivity())
                            .load(model.getImageLink())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(holder.puzzle, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getActivity())
                                            .load(model.getImageLink())
                                            .error(R.drawable.ic_terrain_black_24dp)
                                            .into(holder.puzzle, new Callback() {
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

                    holder.category_name.setText(model.getName());

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Common.CATEGORY_ID_SELECTED = adapter.getRef(position).getKey(); //get Key of item
                            Common.CATEGORY_SELECTED = model.getName();
                            Intent intent = new Intent(getActivity(), ListPuzzleActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_category_item, parent, false);
                    return new CategoryViewHolder(itemView);
                }
            };
        }

        public static  CategoryFragment getInstance(){
            if (INSTANCE == null)
                INSTANCE = new CategoryFragment();
            return INSTANCE;
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_category, container, false);
            recyclerView = (RecyclerView)view.findViewById(R.id.recycler_category);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            setCategory();

            return view;
        }

    private void setCategory() {
            adapter.startListening();
            recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}

