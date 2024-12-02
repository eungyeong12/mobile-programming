package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.adaptor.CartAdapter;
import jo.toybreeze.adaptor.CompanyToyAdaptor;
import jo.toybreeze.domain.PaymentToy;
import jo.toybreeze.domain.Toy;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = CartActivity.class.getSimpleName();
    private ImageView back;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.cartView);
        db = FirebaseFirestore.getInstance();

        db.collection("cart")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    List<PaymentToy> toys = new ArrayList<>();
                    List<String> toyIds = new ArrayList<>();

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
    }
}
