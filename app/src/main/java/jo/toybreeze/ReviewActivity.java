package jo.toybreeze;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jo.toybreeze.adaptor.ReviewImageAdaptor;
import jo.toybreeze.domain.Image;
import jo.toybreeze.domain.Review;
import jo.toybreeze.domain.Toy;

public class ReviewActivity extends AppCompatActivity {
    private static final String TAG = "ReviewActivity";

    private ImageView back, addPicture, toyImage;
    private TextView toyName, toyDescription;
    private RatingBar ratingBar;
    private EditText reviewText;
    private RecyclerView pictureRecyclerView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<String> imageUris;
    private ReviewImageAdaptor imageAdaptor;

    private String toyId;

    // ActivityResultLauncher for selecting an image
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        imageUris.add(selectedImageUri.toString());
                        imageAdaptor.notifyDataSetChanged();
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);

        back = findViewById(R.id.back);
        addPicture = findViewById(R.id.add_picture);
        toyImage = findViewById(R.id.review_toy_image);
        toyName = findViewById(R.id.review_toy_name);
        toyDescription = findViewById(R.id.review_toy_description);
        ratingBar = findViewById(R.id.ratingBar);
        reviewText = findViewById(R.id.review_text);
        pictureRecyclerView = findViewById(R.id.picture_recyclerview);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        imageUris = new ArrayList<>();

        // Get data from Intent
        Intent intent = getIntent();
        toyId = intent.getStringExtra("orderId");

        db.collection("toys")
                .document(toyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toy toy = documentSnapshot.toObject(Toy.class);
                        if (toy != null) {
                            toyName.setText(toy.getName());
                            toyDescription.setText(toy.getDescription());
                        }
                    }
                });

        DocumentReference docRef = db.collection("images").document(toyId);
        docRef.get().addOnSuccessListener(imageTask -> {
            if (imageTask.exists()) {
                String url = imageTask.getString("url");
                Glide.with(this).load(url).into(toyImage);
            }
        });

        // Back button
        back.setOnClickListener(view -> finish());

        // Image RecyclerView setup
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pictureRecyclerView.setLayoutManager(layoutManager);
        imageAdaptor = new ReviewImageAdaptor(imageUris);
        pictureRecyclerView.setAdapter(imageAdaptor);

        // Add picture logic
        addPicture.setOnClickListener(view -> openGallery());

        // Submit review
        findViewById(R.id.btn_submit_review).setOnClickListener(view -> submitReview());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String text = reviewText.getText().toString();

        if (rating == 0) {
            Toast.makeText(this, "별점을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (text.isEmpty()) {
            Toast.makeText(this, "리뷰 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review(String.valueOf(System.currentTimeMillis()), mAuth.getCurrentUser().getEmail(), toyId, rating, text, new Date());

        db.collection("reviews").add(review)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Review added with ID: " + documentReference.getId());
                    for (String url : imageUris) {
                        db.collection("review_images")
                                .document(toyId)
                                .collection(review.getReviewId())
                                .document()
                                .set(new Image(url));
                    }
                    Toast.makeText(this, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding review", e);
                    Toast.makeText(this, "리뷰 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }
}
