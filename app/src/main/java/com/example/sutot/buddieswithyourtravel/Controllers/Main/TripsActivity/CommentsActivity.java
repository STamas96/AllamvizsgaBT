package com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Models.Comment;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mComments;
    private ImageButton mSendComment, mUploadPic;
    private EditText mNewComment;
    private String Tripkey;
    private DatabaseReference mUserRef, mCommentsRef;
    private Uri filep;
    private final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Tripkey = getIntent().getExtras().get("TripKey").toString();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentsRef = FirebaseDatabase.getInstance().getReference().child("Trips").child(Tripkey).child("Comments");

        mComments = (RecyclerView) findViewById(R.id.Comments_RecyclerView);
        mComments.setHasFixedSize(true);
        LinearLayoutManager newLinearLayoutManager = new LinearLayoutManager(this);
        newLinearLayoutManager.setReverseLayout(true);
        newLinearLayoutManager.setStackFromEnd(true);
        mComments.setLayoutManager(newLinearLayoutManager);

        mSendComment = findViewById(R.id.Comments_Send_Comment);
        mNewComment = findViewById(R.id.Comments_NewComment_Input);
        mUploadPic = findViewById(R.id.Comments_Upload_Pic);

        mSendComment.setOnClickListener(this);
        mUploadPic.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mSendComment) {
            mUserRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ControlComment(dataSnapshot.child("userName").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if (view == mUploadPic) {
            addnewpostimage();
        }
    }

    //uj kep feltoltese
    private void addnewpostimage() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, GALLERY_REQUEST);
    }

    //a visszakapott adatot lementjuk es beallitjuk hogy jelenjen is meg a UI-n
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                filep = data.getData();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Comment, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(
                Comment.class,
                R.layout.comments_layout,
                CommentsViewHolder.class,
                mCommentsRef
        ) {
            @Override
            protected void populateViewHolder(final CommentsViewHolder viewHolder, Comment model, int position) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("profilePicture")) {
                            viewHolder.setImage(getApplicationContext(), dataSnapshot.child("profilePicture").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.setUserName(model.getUserName());
                viewHolder.setComment(model.getFilePath(), model.getComment());
                viewHolder.setPostedDate(model.getPostedDate());
                viewHolder.setPostedImage(getApplicationContext(), model.getFilePath());
            }
        };
        mComments.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPostedDate(Date post) {
            TextView mCommm = (TextView) mView.findViewById(R.id.CommentLayout_Date);
            SimpleDateFormat date = new SimpleDateFormat("dd-MMMM-yyyy");
            String stime = date.format(post);
            mCommm.setText(stime);
            TextView mCommm2 = (TextView) mView.findViewById(R.id.CommentLayout_Time);
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            String stime2 = time.format(post);
            mCommm2.setText(stime2);
        }


        public void setComment(String path, String comm) {
            TextView mCommm = (TextView) mView.findViewById(R.id.CommentLayout_Comment);
            if (path != null && comm.isEmpty()) {
                mCommm.setTypeface(null, Typeface.ITALIC);
                mCommm.setText("has posted a picture");
                mCommm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                mCommm.setText(comm);
            }
        }

        public void setPostedImage(Context currentContext, String path) {
            if (path != null) {
                ImageView mpic = (ImageView) mView.findViewById(R.id.CommentLayout_ComPic);
                StorageReference pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(path);
                mpic.setVisibility(View.VISIBLE);
                Glide.with(currentContext)
                        .using(new FirebaseImageLoader())
                        .load(pictureref)
                        .into(mpic);
            }
        }

        public void setUserName(String usern) {
            TextView mUserName = (TextView) mView.findViewById(R.id.CommentLayout_UserName);
            mUserName.setText(usern);
        }

        public void setImage(Context currentContext, String filepath) {
            ImageView picView = (ImageView) mView.findViewById(R.id.CommentLayout_ProfPic);
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

    public void ControlComment(final String user) {
        String comment = mNewComment.getText().toString();
        if (comment.isEmpty() && filep == null) {
            Toast.makeText(CommentsActivity.this, "Can't post empty message", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calendar = Calendar.getInstance();
            final String signatureTemp = Objects.toString(System.currentTimeMillis(), null) + FirebaseAuth.getInstance().getUid();
            if (filep != null) {
                FirebaseStorage.getInstance().getReference().child("Trips").child(Tripkey).child("images/").child(signatureTemp)
                        .putFile(filep).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Comment temp = new Comment(FirebaseAuth.getInstance().getUid(),
                                    user, mNewComment.getText().toString(), Calendar.getInstance().getTime(),
                                    task.getResult().getDownloadUrl().toString());
                            mCommentsRef.child(signatureTemp).setValue(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CommentsActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                                        filep = null;
                                        mNewComment.setText("");
                                    } else {
                                        Toast.makeText(CommentsActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        filep = null;
                                        mNewComment.setText("");
                                    }
                                }
                            });
                        } else {
                            Comment temp = new Comment(FirebaseAuth.getInstance().getUid(),
                                    user, mNewComment.getText().toString(), Calendar.getInstance().getTime(), null);
                            mCommentsRef.child(signatureTemp).setValue(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CommentsActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                                        filep = null;
                                        mNewComment.setText("");
                                    } else {
                                        Toast.makeText(CommentsActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        filep = null;
                                        mNewComment.setText("");
                                    }
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                Comment temp = new Comment(FirebaseAuth.getInstance().getUid(),
                        user, mNewComment.getText().toString(), Calendar.getInstance().getTime(), null);
                mCommentsRef.child(signatureTemp).setValue(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CommentsActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                            filep = null;
                            mNewComment.setText("");
                        } else {
                            Toast.makeText(CommentsActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            filep = null;
                            mNewComment.setText("");
                        }
                    }
                });
            }
        }
    }
}
