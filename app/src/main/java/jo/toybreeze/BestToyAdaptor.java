package jo.toybreeze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import jo.toybreeze.domain.Toy;

public class BestToyAdaptor extends RecyclerView.Adapter<BestToyAdaptor.ViewHolder> {
    private List<Toy> toys;
    private List<String> toyIds;
    private FirebaseFirestore db;

    public BestToyAdaptor(List<Toy> toys, List<String> toyIds) {
        this.toys = toys;
        this.toyIds = toyIds;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BestToyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_best_toy, parent, false);
        BestToyAdaptor.ViewHolder viewHolder = new BestToyAdaptor.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Toy toy = toys.get(position);
        String id = toyIds.get(position);
        DocumentReference docRef = db.collection("images").document(id);
        docRef.get().addOnCompleteListener(imageTask -> {
            if (imageTask.isSuccessful() && imageTask.getResult() != null) {
                String url = imageTask.getResult().getString("url");
                Glide.with(holder.image.getContext())
                        .load(url)
                        .thumbnail()
                        .into(holder.image);
            }
        });

        holder.name.setText(toy.getName());
        holder.price.setText(String.format("%,d원", toy.getMonthPrice()));
        holder.age.setText(toy.getAge() + "세");
        holder.category.setText(toy.getCategory());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView price;
        private TextView age;
        private TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_best_image);
            name = itemView.findViewById(R.id.item_best_name);
            price = itemView.findViewById(R.id.item_best_price);
            age = itemView.findViewById(R.id.item_best_age);
            category = itemView.findViewById(R.id.item_best_category);
        }
    }

    @Override
    public int getItemCount() {
        return toys.size();
    }
}
