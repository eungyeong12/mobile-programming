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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jo.toybreeze.R;
import jo.toybreeze.domain.Payment;
import jo.toybreeze.domain.PaymentToy;
import jo.toybreeze.domain.Toy;

public class PaymentAdaptor extends RecyclerView.Adapter<PaymentAdaptor.ViewHolder> {
    private List<Payment> payments;
    private FirebaseFirestore db;

    public interface OnItemClickListener {
        void onItemClicked(int position, HashMap<String, Toy> data);
    }

    private PaymentAdaptor.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(PaymentAdaptor.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public PaymentAdaptor(List<Payment> payments) {
        this.payments = payments;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PaymentAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_toy, parent, false);
        PaymentAdaptor.ViewHolder viewHolder = new PaymentAdaptor.ViewHolder(view);
        viewHolder.image.setOnClickListener(view1 -> {
            HashMap<String, Toy> data = new HashMap<>();
            int position = viewHolder.getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Payment payment = payments.get(position);
                String id = payment.getToyId();
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
        Payment payment = payments.get(position);
        PaymentToy toy = payment.getPaymentToy();

        Glide.with(holder.image.getContext())
                .load(toy.getImage())
                .thumbnail()
                .into(holder.image);

        holder.name.setText(toy.getName());
        holder.quantity.setText(String.format("%,d개", toy.getQuantity()));
        holder.price.setText(String.format("%,d원", toy.getQuantity() * toy.getPrice()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = dateFormat.format(payment.getStartDate());
        String endDate = dateFormat.format(payment.getEndDate());
        holder.date.setText("대여기간: " + startDate + " ~ " + endDate);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView quantity;
        private TextView price;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_payment_toy_image);
            name = itemView.findViewById(R.id.item_payment_toy_name);
            quantity = itemView.findViewById(R.id.item_payment_toy_quantity);
            price = itemView.findViewById(R.id.item_payment_toy_price);
            date = itemView.findViewById(R.id.item_payment_date);
        }
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }
}
