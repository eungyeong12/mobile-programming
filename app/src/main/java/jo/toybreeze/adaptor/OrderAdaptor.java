package jo.toybreeze.adaptor;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import jo.toybreeze.R;
import jo.toybreeze.ReviewActivity;
import jo.toybreeze.domain.Order;
import jo.toybreeze.domain.Toy;

public class OrderAdaptor extends RecyclerView.Adapter<OrderAdaptor.ViewHolder> {
    private List<Order> orders;
    private FirebaseFirestore db;

    public interface OnItemClickListener {
        void onItemClicked(int position, HashMap<String, Toy> data);
    }

    private OrderAdaptor.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OrderAdaptor.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public OrderAdaptor(List<Order> orders) {
        this.orders = orders;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        OrderAdaptor.ViewHolder viewHolder = new OrderAdaptor.ViewHolder(view);
        viewHolder.image.setOnClickListener(view1 -> {
            HashMap<String, Toy> data = new HashMap<>();
            int position = viewHolder.getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Order order = orders.get(position);
                String id = order.getToyId();
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
        Order order = orders.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

        holder.purchaseDate.setText(dateFormat.format(order.getCreatedAt()));

        Glide.with(holder.image.getContext())
                .load(order.getImage())
                .thumbnail()
                .into(holder.image);

        holder.name.setText(order.getName());
        holder.price.setText(String.format("%,d원", order.getPrice()));
        holder.date.setText("대여 기간: " + dateFormat.format(order.getStartDate()) + " ~ " + dateFormat.format(order.getEndDate()));

        holder.layout.setOnClickListener(view -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
            builder.setMessage("리뷰를 작성하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        // 리뷰 작성 액티비티 또는 프래그먼트로 이동
                        Intent intent = new Intent(view.getContext(), ReviewActivity.class);
                        intent.putExtra("orderId", order.getToyId());
                        view.getContext().startActivity(intent);
                    })
                    .setNegativeButton("아니오", (dialog, which) -> {
                        // 아무 작업도 하지 않음
                        dialog.dismiss();
                    });

            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView purchaseDate;
        private TextView name;
        private TextView price;
        private TextView date;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_order_image);
            name = itemView.findViewById(R.id.item_order_name);
            price = itemView.findViewById(R.id.item_order_price);
            purchaseDate = itemView.findViewById(R.id.item_order_purchase_date);
            date = itemView.findViewById(R.id.item_order_date);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
