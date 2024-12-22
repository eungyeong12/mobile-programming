package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.adaptor.OrderAdaptor;
import jo.toybreeze.domain.Order;
import jo.toybreeze.domain.Subscribe;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();
    private ImageView back;
    private RecyclerView recyclerView;
    private OrderAdaptor orderAdaptor;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout subscribeRandom;
    private TextView subscribeRandomPurchaseDate;
    private TextView subscribeRandomDate;
    private LinearLayout subscribeSelect;
    private TextView subscribeSelectPurchaseDate;
    private TextView subscribeSelectDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.order_recyclerview);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        subscribeRandom = findViewById(R.id.subscribe_random);
        subscribeRandomPurchaseDate = findViewById(R.id.subscribe_random_purchase_date);
        subscribeRandomDate = findViewById(R.id.subscribe_random_date);
        subscribeSelect = findViewById(R.id.subscribe_select);
        subscribeSelectPurchaseDate = findViewById(R.id.subscribe_select_purchase_date);
        subscribeSelectDate = findViewById(R.id.subscribe_select_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        back.setOnClickListener(view -> finish());
        subscribeRandom.setVisibility(View.GONE);
        subscribeSelect.setVisibility(View.GONE);

        DocumentReference docRef = db.collection("subscribe_random").document(currentUser.getEmail());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Subscribe subscribe = documentSnapshot.toObject(Subscribe.class);
                if (subscribe != null) {
                    subscribeRandomPurchaseDate.setText(dateFormat.format(subscribe.getCreatedAt()));

                    String startDate = dateFormat.format(subscribe.getStartDate());
                    String endDate = dateFormat.format(subscribe.getEndDate());
                    subscribeRandomDate.setText("대여 기간: " + startDate + " ~ " + endDate);

                    subscribeRandom.setVisibility(View.VISIBLE);

                }
            }
        });

        DocumentReference docRef2 = db.collection("subscribe_select").document(currentUser.getEmail());
        docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Subscribe subscribe = documentSnapshot.toObject(Subscribe.class);
                if (subscribe != null) {
                    subscribeSelectPurchaseDate.setText(dateFormat.format(subscribe.getCreatedAt()));

                    String startDate = dateFormat.format(subscribe.getStartDate());
                    String endDate = dateFormat.format(subscribe.getEndDate());
                    subscribeSelectDate.setText("대여 기간: " + startDate + " ~ " + endDate);

                    subscribeSelect.setVisibility(View.VISIBLE);
                }
            }
        });

        db.collection("orders")
                .document(currentUser.getEmail())
                .collection("order")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    List<Order> orders = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        Order order = document.toObject(Order.class);
                        orders.add(order);
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    orderAdaptor = new OrderAdaptor(orders);
                    orderAdaptor.setOnItemClickListener((position, data) -> {
                        Intent intent2 = new Intent(getApplicationContext(), ToyDetailActivity.class);
                        intent2.putExtra("data", data);
                        startActivity(intent2);
                    });
                    recyclerView.setAdapter(orderAdaptor);
                });
    }
}
