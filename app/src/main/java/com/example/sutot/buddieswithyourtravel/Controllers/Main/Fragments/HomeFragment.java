package com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
public class HomeFragment extends android.support.v4.app.Fragment {

    private RecyclerView mMainRecyclerView;
    private DatabaseReference refTrips;

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

        MainActivity mMainActivity = (MainActivity) getActivity();

        mMainRecyclerView = (RecyclerView) view.findViewById(R.id.HomeFragment_Main_RecyclerView);
        mMainRecyclerView.setHasFixedSize(true);
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        refTrips = FirebaseDatabase.getInstance().getReference();
        refTrips.child("Trips").keepSynced(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Trips, TripViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trips, TripViewHolder>
                (Trips.class, R.layout.card_view_trips, TripViewHolder.class, refTrips.child("Trips")) {
            @Override
            protected void populateViewHolder(final TripViewHolder viewHolder, Trips model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setInteresteds();
                refTrips.child("Users").child(model.getOwnerID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String username = dataSnapshot.getValue(User.class).getUserName();
                            viewHolder.setOwner(username);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.setDescription(model.getShortDescription());
                viewHolder.setStartDate(model.getStartDate());
                viewHolder.setEndDate(model.getEndDate());
                viewHolder.setCreatedTime(model.getmTripCreated());
                viewHolder.setImage(getContext(),model.getFilePath());

            }
        };
        mMainRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public TripViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
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
                pictureref = FirebaseStorage.getInstance().getReference().child("Trips/").child("images/").child("No_Cover_Image.jpg");
            } else {
                pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(filepath);
            }
            Glide.with(currentContext)
                    .using(new FirebaseImageLoader())
                    .load(pictureref)
                    .into(picView);
        }
    }
}/*



    }

    */


