package com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Models.Comment;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailedTripActivity extends AppCompatActivity implements View.OnClickListener {

    //visszalepes es modositas gombok
    private ImageButton mBackToMain, mEditTrip;
    //cim, ido intervallum es a hosszu leiras nezeteinek deklaralasa
    private TextView mTitle, mDateInterval, mLongDescription;
    //a kirandulas hatterkepenek a nezetenek a deklaralasa
    private ImageView mCoverPhoto;
    //a kirandulas kulcsa, amelyet szeretnenk modositani, itt fogjuk tarolni
    private String tripKey;

    //adatbazis referenciak
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<String> listwithphotos;

    //jelenlegi felhasznalot taroljuk majd ebben
    private FirebaseUser mCurrUser;
    private GridView gridView;

    boolean isImageFitToScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_trip);
        //kiolvassuk az aktualis kirandulas kulcsat
        tripKey = getIntent().getExtras().get("TripKey").toString();
        //kiolvassuk a jelenlegi felhasznalot
        mCurrUser = FirebaseAuth.getInstance().getCurrentUser();

        //layout itemek csatolasok
        mBackToMain = findViewById(R.id.DetailedTrip_Back_Button);
        mEditTrip = findViewById(R.id.DetailedTrip_Edit);
        mTitle = findViewById(R.id.DetailedTrip_Title_TextView);
        mDateInterval = findViewById(R.id.DetailedTrip_Date);
        mLongDescription = findViewById(R.id.DetailedTrip_LongDescription);
        mCoverPhoto = findViewById(R.id.DetailedTrip_Photo);
        gridView = findViewById(R.id.DetailedTrip_GridView);


        //referenciak letrehozasa
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips").child(tripKey);

        //beallitasok, a jelenlegi kirandulassal
        listwithphotos = new ArrayList<String>();
        getCommentPhotos();


        //listenerek csatolasa
        mBackToMain.setOnClickListener(this);
        mEditTrip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBackToMain) {
            //visszalepunk a mainbe
            finish();
        }
        if (view == mEditTrip) {
            //atlepunk az editbe, modositasba es atadjuk az aktualis kirandulas kulcsat
            Intent editTrip = new Intent(this, EditTripsActivity.class);
            editTrip.putExtra("TripKey", tripKey);
            startActivity(editTrip);
        }
    }

    private void getCommentPhotos() {
        if (!databaseReference.child("Comments").equals(null)) {
            databaseReference.child("Comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Comment temp = new Comment(child.getValue(Comment.class));
                        if (temp.getFilePath() != null) {
                            if (!temp.getFilePath().isEmpty()) {
                                listwithphotos.add(temp.getFilePath());
                            }
                        }
                    }
                    setLook();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            setLook();
        }
    }

    private void setLook() {
        //lekerjuk a referenciat a felhasznalo tablahoz
        try {
            /*megkeressuk a mi felhasznalonkat, ha megkapjuk atalittjuk az adatait, ha pedig nem akkor valami hiba tortent es biztonsagi
            okokbol visszaugrunk a belepesi fazisba*/
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //lekerjuk az aktualis kirandulast
                    Trips temp = new Trips(dataSnapshot.getValue(Trips.class));
                    /*megnezzuk ha a kurrens user, keszitette a kirandulast, ha igen akkor lehetove tesszuk neki, hogy tudja modositani
                    vagy akar kitorolni*/
                    if (temp.getOwnerID().toString().equals(mCurrUser.getUid().toString())) {
                        //lathatova tesszuk a gombot ha a megfelelo felhasznalo van bejelentkezve
                        mEditTrip.setVisibility(View.VISIBLE);
                    }
                    //kitoltjuk a mezoket az aktualis kirandulas adataival
                    mTitle.setText(temp.getTitle());
                    mLongDescription.setText(temp.getDetailedDescription());
                    //idointervallum meghatarozasa
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String temp1 = formatter.format(temp.getStartDate());
                    String temp2 = formatter.format(temp.getEndDate());
                    String finaltemp = temp1 + " - " + temp2;
                    mDateInterval.setText(finaltemp);
                    //kep betoltese
                    if (temp.getFilePath() == null) {
                        mCoverPhoto.setImageResource(R.drawable.no_profile_pic);
                    } else {
                        if (!temp.getFilePath().isEmpty()) {
                            StorageReference pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(temp.getFilePath().toString());
                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(pictureref)
                                    .into(mCoverPhoto);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

                }
            });
            if (!databaseReference.child("Comments").equals(null)) {
                gridView.setAdapter(new GridAdapter());
            }
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(getApplicationContext(),ViewImage.class).putExtra("img",listwithphotos.get(position)));
                }
            });
        }//ha valami hiba tortent akkor kilepunk az applikaciobol
        catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listwithphotos.size();
        }

        @Override
        public Object getItem(int position) {
            return listwithphotos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.gridview_photos, parent, false);
            ImageView photo = convertView.findViewById(R.id.GridView_Photos_Pic);
            String filepath = getItem(position).toString();
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(FirebaseStorage.getInstance().getReferenceFromUrl(filepath))
                    .into(photo);
            return convertView;
        }
    }

}
