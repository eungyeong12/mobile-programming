package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.adaptor.CompanyToyAdaptor;
import jo.toybreeze.domain.Toy;

public class CompanyToyActivity extends AppCompatActivity {
    private static final String TAG = CompanyToyActivity.class.getSimpleName();
    private TextView companyName;
    private RecyclerView recyclerView;
    private CompanyToyAdaptor companyToyAdaptor;
    private ImageView back;
    private Button goToSubscribe;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company_toy);
        companyName = findViewById(R.id.subscribe_company);
        recyclerView = findViewById(R.id.company_toy_view);
        back = findViewById(R.id.back);
        goToSubscribe = findViewById(R.id.go_to_subscribe);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String company = intent.getStringExtra("company");
        companyName.setText(company);

        db.collection("companies")
                .document(company)
                .collection("toys")
                .addSnapshotListener((value, error) -> {
                    List<Toy> toys = new ArrayList<>();
                    List<String> toyIds = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        Toy toy = document.toObject(Toy.class);
                        toys.add(toy);
                        toyIds.add(document.getId());
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    companyToyAdaptor = new CompanyToyAdaptor(toys, toyIds);
                    companyToyAdaptor.setOnItemClickListener((position, data) -> {
                        Intent intent2 = new Intent(getApplicationContext(), ToyDetailActivity.class);
                        intent2.putExtra("data", data);
                        startActivity(intent2);
                    });
                    recyclerView.setAdapter(companyToyAdaptor);
                });

        back.setOnClickListener(view -> finish());
    }
}
