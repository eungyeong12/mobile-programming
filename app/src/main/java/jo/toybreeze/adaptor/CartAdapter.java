package jo.toybreeze.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jo.toybreeze.R;
import jo.toybreeze.domain.PaymentToy;
import jo.toybreeze.domain.Toy;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<PaymentToy> toys;
    private List<String> toyIds;
    private FirebaseFirestore db;

    public interface OnItemClickListener {
        void onItemClicked(int position, HashMap<String, Toy> data);
    }

    private CartAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(CartAdapter.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public CartAdapter(List<PaymentToy> toys, List<String> toyIds) {
        this.toys = toys;
        this.toyIds = toyIds;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        CartAdapter.ViewHolder viewHolder = new CartAdapter.ViewHolder(view);
        viewHolder.image.setOnClickListener(view1 -> {
            HashMap<String, Toy> data = new HashMap<>();
            int position = viewHolder.getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String id = toyIds.get(position);
                DocumentReference docRef = db.collection("toys").document(id);
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    Toy toy = documentSnapshot.toObject(Toy.class);
                    data.put(id, toy);
                    itemClickListener.onItemClicked(position, data);
                });
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentToy toy = toys.get(position);
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
        holder.quantity.setText(String.valueOf(toy.getQuantity()));
        holder.price.setText(String.format("%,d", toy.getQuantity() * toy.getPrice()));

        holder.cancel.setOnClickListener(view -> {
            int position1 = holder.getBindingAdapterPosition();
            if (position1 != RecyclerView.NO_POSITION) {
                db.collection("cart").document(id).delete();
                toys.remove(position1);
                toyIds.remove(position1);
                notifyItemRemoved(position1);
                notifyItemRangeChanged(position1, toys.size());
            }
        });

        holder.minus.setOnClickListener(view -> {
            int current = Integer.parseInt(holder.quantity.getText().toString());
            if (current > 1) {
                int newQuantity = current - 1;
                holder.quantity.setText(String.valueOf(newQuantity));
                holder.price.setText(String.format("%,d", newQuantity * toy.getPrice()));
            }
        });

        holder.plus.setOnClickListener(view -> {
            int current = Integer.parseInt(holder.quantity.getText().toString());
            if (current < toy.getMaxQuantity()) {
                int newQuantity = current + 1;
                holder.quantity.setText(String.valueOf(newQuantity));
                holder.price.setText(String.format("%,d", newQuantity * toy.getPrice()));
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private ImageView cancel;
        private ImageView minus;
        private ImageView plus;
        private TextView quantity;
        private TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_cart_image);
            name = itemView.findViewById(R.id.item_cart_name);
            cancel = itemView.findViewById(R.id.item_cart_cancel);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            quantity = itemView.findViewById(R.id.item_cart_quantity);
            price = itemView.findViewById(R.id.item_cart_price);
        }
    }

    @Override
    public int getItemCount() {
        return toys.size();
    }
}
