package com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity.CommentsActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Profile.ProfileActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.SearchActivity;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity.DetailedTripActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private RecyclerView mMainRecyclerView;
    private DatabaseReference refTrips, refLikes, refLikesR;
    private MainActivity mMainActivity;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //a foactivityhez hozzaferunk
        mMainActivity = (MainActivity) getActivity();

        //beallitjuk a recyclerviewt
        mMainRecyclerView = (RecyclerView) view.findViewById(R.id.FavouriteFragment_Main_RecyclerView);
        mMainRecyclerView.setHasFixedSize(true);
        LinearLayoutManager newLinearLayoutManager = new LinearLayoutManager(getContext());
        newLinearLayoutManager.setReverseLayout(true);
        newLinearLayoutManager.setStackFromEnd(true);
        mMainRecyclerView.setLayoutManager(newLinearLayoutManager);

        //a kirandulas tablat syncronizaljuk
        refTrips = FirebaseDatabase.getInstance().getReference();
        refTrips.child("Trips").keepSynced(true);

        refLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
        refLikes.keepSynced(true);

        refLikesR = FirebaseDatabase.getInstance().getReference().child("LikesR");
        refLikesR.keepSynced(true);

        mMainActivity.mTopLeft.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mMainActivity.mTopLeft) {
            Intent searchTrip = new Intent(getContext(), SearchActivity.class);
            searchTrip.putExtra("type", "trip");
            startActivity(searchTrip);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Trips, HomeFragment.TripViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trips, HomeFragment.TripViewHolder>
                (Trips.class, R.layout.card_view_trips, HomeFragment.TripViewHolder.class, refLikesR.child(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            //feltoltjuk a Recycler view-t
            @Override
            protected void populateViewHolder(final HomeFragment.TripViewHolder viewHolder, final Trips model, int position) {

                //megnevezzuk minden kirandulast, azaz megszerezzuk az idjukat
                final String TripKey = getRef(position).getKey();
                final String PersonKey = model.getOwnerID();

                //beallitjuk a cimet es az erdeklodoinek a szamat
                viewHolder.setTitle(model.getTitle());
                viewHolder.setInteresteds();
                //beallitjuk a szerzo nevet
                refTrips.child("Users").child(model.getOwnerID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String username = dataSnapshot.getValue(User.class).getUserName();
                            String filepath = dataSnapshot.getValue(User.class).getProfilePicture();

                            viewHolder.setOwner(username);
                            viewHolder.setOwnerPicture(getContext(), filepath);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                //beallitjuk a tobbi adatot is
                viewHolder.setDescription(model.getShortDescription());
                viewHolder.setStartDate(model.getStartDate());
                viewHolder.setEndDate(model.getEndDate());
                viewHolder.setCreatedTime(model.getmTripCreated());
                viewHolder.setImage(getContext(), model.getFilePath());
                viewHolder.setLikeButtonStatus(TripKey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailedPost = new Intent(getContext(), DetailedTripActivity.class);
                        detailedPost.putExtra("TripKey", TripKey);
                        startActivity(detailedPost);
                    }
                });

                viewHolder.mComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comments = new Intent(getContext(), CommentsActivity.class);
                        comments.putExtra("TripKey", TripKey);
                        startActivity(comments);
                    }
                });

                viewHolder.mLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMainActivity.LikeChecker = true;

                        refLikes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mMainActivity.LikeChecker.equals(true)) {
                                    if (dataSnapshot.child(TripKey).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        refLikes.child(TripKey).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        refLikesR.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(TripKey).removeValue();
                                        mMainActivity.LikeChecker = false;
                                    } else {
                                        refLikes.child(TripKey).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                        refLikesR.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(TripKey).setValue(model);
                                        mMainActivity.LikeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                if (!model.getOwnerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    viewHolder.mOwner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(getContext(), ProfileActivity.class);
                            profile.putExtra("PersonKey", PersonKey);
                            startActivity(profile);
                        }
                    });

                    viewHolder.picView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(getContext(), ProfileActivity.class);
                            profile.putExtra("PersonKey", PersonKey);
                            startActivity(profile);
                        }
                    });
                }

            }
        };
        //racsatoljuk az adaptert a recycler viewre
        mMainRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

}


