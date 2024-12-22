package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.adaptor.OrderAdaptor;
import jo.toybreeze.domain.Order;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();
    private ImageView back;
    private RecyclerView recyclerView;
    private OrderAdaptor orderAdaptor;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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

        back.setOnClickListener(view -> finish());

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
