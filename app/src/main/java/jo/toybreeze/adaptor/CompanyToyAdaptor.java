package jo.toybreeze.adaptor;

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

import java.util.HashMap;
import java.util.List;

import jo.toybreeze.R;
import jo.toybreeze.domain.Toy;

public class CompanyToyAdaptor extends RecyclerView.Adapter<CompanyToyAdaptor.ViewHolder> {
    private List<Toy> toys;
    private List<String> toyIds;
    private FirebaseFirestore db;

    public CompanyToyAdaptor(List<Toy> toys, List<String> toyIds) {
        this.toys = toys;
        this.toyIds = toyIds;
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, HashMap<String, Toy> data);
    }

    private CompanyToyAdaptor.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(CompanyToyAdaptor.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public CompanyToyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company_toy, parent, false);
        CompanyToyAdaptor.ViewHolder viewHolder = new CompanyToyAdaptor.ViewHolder(view);

        view.setOnClickListener(view1 -> {
            HashMap<String, Toy> data = new HashMap<>();
            int position = viewHolder.getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String id = toyIds.get(position);
                Toy toy = toys.get(position);
                data.put(id, toy);
            }
            itemClickListener.onItemClicked(position, data);
        });
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
        int quantity = toy.getQuantity() - toy.getSellQuantity();
        holder.quantity.setText(String.format("%,d", quantity));
        holder.age.setText(toy.getAge() + "세");
        holder.category.setText(toy.getCategory());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView description;
        private TextView quantity;
        private TextView age;
        private TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_company_toy_image);
            name = itemView.findViewById(R.id.item_company_toy_name);
            description = itemView.findViewById(R.id.item_company_toy_description);
            quantity = itemView.findViewById(R.id.item_company_toy_quantity);
            age = itemView.findViewById(R.id.item_company_toy_age);
            category = itemView.findViewById(R.id.item_company_toy_category);
        }
    }

    @Override
    public int getItemCount() {
        return toys.size();
    }
}
