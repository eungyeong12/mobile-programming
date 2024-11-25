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

public class MainToyAdaptor extends RecyclerView.Adapter<MainToyAdaptor.ViewHolder> {
    private List<Toy> toys;
    private List<String> toyIds;
    private FirebaseFirestore db;

    public MainToyAdaptor(List<Toy> toys, List<String> toyIds) {
        this.toys = toys;
        this.toyIds = toyIds;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MainToyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_toy, parent, false);
        MainToyAdaptor.ViewHolder viewHolder = new MainToyAdaptor.ViewHolder(view);
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
        holder.description.setText(toy.getDescription());
        holder.price.setText(String.format("%,d원", toy.getMonthPrice()));
        holder.age.setText(toy.getAge() + "세");
        holder.category.setText(toy.getCategory());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView description;
        private TextView price;
        private TextView age;
        private TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_main_toy_image);
            name = itemView.findViewById(R.id.item_main_toy_name);
            description = itemView.findViewById(R.id.item_main_toy_description);
            price = itemView.findViewById(R.id.item_main_toy_price);
            age = itemView.findViewById(R.id.item_main_toy_age);
            category = itemView.findViewById(R.id.item_main_toy_category);
        }
    }

    @Override
    public int getItemCount() {
        return toys.size();
    }
}