package com.example.sutot.buddieswithyourtravel.Controllers.Main;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mTopNavBar;
    private ImageButton mBack, mSearchFriends;
    private EditText mInput;
    private RecyclerView mRecyclerView;
    private DatabaseReference allUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        mTopNavBar = (LinearLayout) findViewById(R.id.Search_Friend_Top_NavBar_LLayout);
        mBack = (ImageButton) findViewById(R.id.Search_Friend_Back_Button);
        mInput = (EditText) findViewById(R.id.Search_Friend_Input);
        mSearchFriends = (ImageButton) findViewById(R.id.Search_Friend_Search);

        try {
            allUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.Search_Friend_Result);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mBack.setOnClickListener(this);
        mSearchFriends.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mSearchFriends) {
            String searchInput = mInput.getText().toString();
            SearchForPeople(searchInput);
        }
    }

    private void SearchForPeople(String searchInput) {

        Toast.makeText(this, "Searching", Toast.LENGTH_LONG).show();

        Query searchFriendsQuery = allUserReference.orderByChild("firstName").startAt(searchInput).endAt(searchInput + "\uf8ff");

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.search_result_display,
                UserViewHolder.class,
                searchFriendsQuery

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                String tempFullName = model.getFirstName() + " " + model.getLastName();
                viewHolder.setFullName(tempFullName);
                viewHolder.setShortDescription(model.getBio());
                viewHolder.setProfilePicture(getApplicationContext(), model.getProfilePicture());

            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProfilePicture(Context context, String filepath) {
            CircleImageView mProfilePic = (CircleImageView) mView.findViewById(R.id.Search_Result_Profile_Image);
            StorageReference pictureref;
            if (filepath == null) {
                mProfilePic.setImageResource(R.drawable.no_profile_pic);
            } else {
                pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(filepath);
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(pictureref)
                        .into(mProfilePic);
            }
        }

        public void setFullName(String fullName) {
            TextView postOwner = mView.findViewById(R.id.Search_Result_Full_Name);
            postOwner.setTypeface(null, Typeface.BOLD);
            postOwner.setText(fullName);
        }

        public void setShortDescription(String sDesc) {
            TextView postOwner = mView.findViewById(R.id.Search_Result_ShortDescription);
            if (sDesc != null) {
                if (!sDesc.isEmpty()) {
                    postOwner.setText(sDesc);
                }
                else
                {
                    postOwner.setText(" ");
                }
            } else {
                postOwner.setText(" ");
            }
        }
    }
}
