package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.toybreeze.adaptor.CartAdapter;
import jo.toybreeze.domain.Payment;
import jo.toybreeze.domain.PaymentToy;
import jo.toybreeze.domain.PaymentType;
import jo.toybreeze.domain.Toy;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = CartActivity.class.getSimpleName();
    private ImageView back;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private CartAdapter cartAdapter;
    private Button goToPayment;
    private List<PaymentToy> toys;
    private List<String> toyIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.cartView);
        db = FirebaseFirestore.getInstance();
        goToPayment = findViewById(R.id.go_to_payment);

        db.collection("cart")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    toys = new ArrayList<>();
                    toyIds = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        PaymentToy paymentToy = document.toObject(PaymentToy.class);
                        toys.add(paymentToy);
                        toyIds.add(document.getId());
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    cartAdapter = new CartAdapter(toys, toyIds);
                    cartAdapter.setOnItemClickListener((position, data) -> {
                        Intent intent2 = new Intent(getApplicationContext(), ToyDetailActivity.class);
                        intent2.putExtra("data", data);
                        startActivity(intent2);
                    });
                    recyclerView.setAdapter(cartAdapter);
                });

        back.setOnClickListener(view -> finish());

        goToPayment.setOnClickListener(view -> {
            List<Payment> payments = new ArrayList<>();
            boolean isAvailable = false;

            List<Map<String, Integer>> checkedItems = cartAdapter.getCheckedItems(recyclerView);
            for (Map<String, Integer> checkedItem : checkedItems) {
                int position = checkedItem.get("position");
                int quantity = checkedItem.get("quantity");
                PaymentToy toy = cartAdapter.getToy(position);
                isAvailable = true;

                if (quantity == 0) {
                    isAvailable = false;
                    return;
                }

                Date now = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DATE, 3);
                Date startDate = calendar.getTime();

                if (toy.getPaymentType().equals("MONTH")) {
                    calendar.add(Calendar.MONTH, 1);
                } else {
                    calendar.add(Calendar.MONTH, 3);
                }

                Date endDate = calendar.getTime();
                PaymentToy newPaymentToy = new PaymentToy(toy.getImage(), toy.getName(), quantity, toy.getMaxQuantity(), toy.getPrice(), toy.getPaymentType(), new Date());

                Payment payment = new Payment(startDate, newPaymentToy, toyIds.get(position), endDate, now);
                payments.add(payment);
            }

            if (isAvailable) {
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("data", (Serializable) payments);
                startActivity(intent);
            }
        });
    }
}
