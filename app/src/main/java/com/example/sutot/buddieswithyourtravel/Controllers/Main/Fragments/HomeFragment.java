package com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.CommentsActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity.DetailedTripActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    private RecyclerView mMainRecyclerView;
    private DatabaseReference refTrips, refLikes;
    private MainActivity mMainActivity;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //a foactivityhez hozzaferunk
         mMainActivity = (MainActivity) getActivity();

        //beallitjuk a recyclerviewt
        mMainRecyclerView = (RecyclerView) view.findViewById(R.id.HomeFragment_Main_RecyclerView);
        mMainRecyclerView.setHasFixedSize(true);
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //a kirandulas tablat syncronizaljuk
        refTrips = FirebaseDatabase.getInstance().getReference();
        refTrips.child("Trips").keepSynced(true);

        refLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
        refLikes.keepSynced(true);

        mMainActivity.mTopLeft.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Trips, TripViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trips, TripViewHolder>
                (Trips.class, R.layout.card_view_trips, TripViewHolder.class, refTrips.child("Trips")) {
            //feltoltjuk a Recycler view-t
            @Override
            protected void populateViewHolder(final TripViewHolder viewHolder, Trips model, int position) {

                //megnevezzuk minden kirandulast, azaz megszerezzuk az idjukat
                final String TripKey = getRef(position).getKey();

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
                            Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                        mMainActivity.LikeChecker = false;
                                    } else {
                                        refLikes.child(TripKey).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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

            }
        };
        //racsatoljuk az adaptert a recycler viewre
        mMainRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageButton mLike, mComment;
        TextView mLikeNumber;
        int countLikes;
        String currUserID;
        DatabaseReference refLikes;

        public TripViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mLike = (ImageButton) mView.findViewById(R.id.Trip_Interested_Button);
            mLikeNumber = (TextView) mView.findViewById(R.id.Trip_Interested_Text);
            mComment = (ImageButton) mView.findViewById(R.id.Trip_Comments_Button);

            currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            refLikes = FirebaseDatabase.getInstance().getReference().child("Likes");

        }

        public void setTitle(String title) {
            TextView postTitle = mView.findViewById(R.id.Trip_Title_Header);
            postTitle.setText(title);
        }

        public void setInteresteds() {
            TextView postInteresteds = mView.findViewById(R.id.Trip_Interested_Text);
            postInteresteds.setTypeface(null, Typeface.BOLD);
            postInteresteds.setText("0 interesteds");
        }

        public void setOwner(String ownerID) {
            TextView postOwner = mView.findViewById(R.id.Trip_Owner_User);
            postOwner.setTypeface(null, Typeface.BOLD);
            postOwner.setText(ownerID);
        }

        public void setDescription(String shortDesc) {
            TextView postDescription = mView.findViewById(R.id.Trip_Short_Description);
            postDescription.setText(shortDesc);
        }

        public void setStartDate(Date startDate) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = formatter.format(startDate);
            TextView postStartDate = mView.findViewById(R.id.Trip_StartDate_TextView);
            postStartDate.setText(formattedDate);
        }

        public void setEndDate(Date endDate) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = formatter.format(endDate);
            TextView postEndDate = mView.findViewById(R.id.Trip_EndDate_TextView);
            postEndDate.setText(formattedDate);
        }

        public void setCreatedTime(Date createTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = formatter.format(createTime);
            TextView postEndDate = mView.findViewById(R.id.Trip_Uploaded_Time);
            postEndDate.setText(formattedDate);
        }

        public void setImage(Context currentContext, String filepath) {
            ImageView picView = (ImageView) itemView.findViewById(R.id.Trip_Image);
            StorageReference pictureref;
            if (filepath == null) {
                picView.setImageResource(R.drawable.no_profile_pic);
            } else {
                if (!filepath.isEmpty()) {
                    pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(filepath);
                    Glide.with(currentContext)
                            .using(new FirebaseImageLoader())
                            .load(pictureref)
                            .into(picView);
                } else {
                    picView.setImageResource(R.drawable.no_profile_pic);
                }
            }

        }

        public void setLikeButtonStatus(final String tripkey) {
            refLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(tripkey).hasChild(currUserID)) {
                        countLikes = (int) dataSnapshot.child(tripkey).getChildrenCount();
                        mLike.setImageResource(R.drawable.ic_like);
                        mLikeNumber.setText(Integer.toString(countLikes) + " interested peoples");
                    } else {
                        countLikes = (int) dataSnapshot.child(tripkey).getChildrenCount();
                        mLike.setImageResource(R.drawable.ic_dislike);
                        mLikeNumber.setText(Integer.toString(countLikes) + " interested peoples");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setOwnerPicture(Context currentContext, String filepath) {
            ImageView picView = (ImageView) itemView.findViewById(R.id.Trip_Owner_Profile_Pic);
            StorageReference pictureref;
            if (filepath == null) {
                picView.setImageResource(R.drawable.no_profile_pic);
            } else {
                if (!filepath.isEmpty()) {
                    pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(filepath);
                    Glide.with(currentContext)
                            .using(new FirebaseImageLoader())
                            .load(pictureref)
                            .into(picView);
                } else {
                    picView.setImageResource(R.drawable.no_profile_pic);
                }
            }

        }
    }
}


