package jo.toybreeze.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.R;
import jo.toybreeze.domain.Image;
import jo.toybreeze.domain.Review;

public class ReviewAdaptor extends RecyclerView.Adapter<ReviewAdaptor.ViewHolder> {
    private List<Review> reviews;
    private FirebaseFirestore db;
    private ImageAdaptor imageAdaptor;

    public ReviewAdaptor(List<Review> reviews) {
        this.reviews = reviews;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReviewAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        ReviewAdaptor.ViewHolder viewHolder = new ReviewAdaptor.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.ratingBar.setRating(review.getRating());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        holder.date.setText(dateFormat.format(review.getCreatedAt()));
        holder.content.setText(review.getContent());

        db.collection("review_images")
                .document(review.getToy())
                .collection(review.getReviewId())
                .addSnapshotListener((value, error) -> {
                    List<Image> images = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        Image image = new Image(document.getData().get("url").toString());
                        images.add(image);
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    holder.recyclerView.setLayoutManager(linearLayoutManager);
                    imageAdaptor = new ImageAdaptor(images);
                    holder.recyclerView.setAdapter(imageAdaptor);
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RatingBar ratingBar;
        private TextView date;
        private RecyclerView recyclerView;
        private kr.co.prnd.readmore.ReadMoreTextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.item_review_rating_bar);
            date = itemView.findViewById(R.id.item_review_date);
            recyclerView = itemView.findViewById(R.id.item_review_image_view);
            content = itemView.findViewById(R.id.item_review_content);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
