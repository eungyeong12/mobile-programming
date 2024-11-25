package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import jo.toybreeze.domain.Toy;

public class ToyDetailActivity extends AppCompatActivity {
    private static final String TAG = ToyDetailActivity.class.getSimpleName();
    private ImageView back;
    private ImageView home;
    private ImageView image;
    private de.hdodenhof.circleimageview.CircleImageView logo;
    private TextView company;
    private TextView name;
    private TextView tag;
    private TextView age;
    private TextView category;
    private TextView quantity;
    private TextView month_price;
    private TextView three_month_price;
    private TextView description;
    private TextView policy;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_toy_detail);
        back = findViewById(R.id.back);
        home = findViewById(R.id.home);
        image = findViewById(R.id.detail_image);
        logo = findViewById(R.id.detail_company_logo);
        company = findViewById(R.id.detail_company_name);
        name = findViewById(R.id.detail_name);
        tag = findViewById(R.id.detail_tag);
        age = findViewById(R.id.detail_age);
        category = findViewById(R.id.detail_category);
        quantity = findViewById(R.id.detail_quantity);
        month_price = findViewById(R.id.detail_month_price);
        three_month_price = findViewById(R.id.detail_three_month_detail);
        description = findViewById(R.id.detail_description);
        policy = findViewById(R.id.detail_policy);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        HashMap<String, Toy> data = (HashMap<String, Toy>) intent.getSerializableExtra("data");

        String id = data.keySet().iterator().next();
        Toy toy = data.get(id);

        DocumentReference docRef = db.collection("logo").document(toy.getCompany());
        docRef.get().addOnCompleteListener(imageTask -> {
            if (imageTask.isSuccessful() && imageTask.getResult() != null) {
                String url = imageTask.getResult().getString("url");
                Glide.with(logo.getContext())
                        .load(url)
                        .dontAnimate()
                        .thumbnail()
                        .into(logo);
            }
        });

        DocumentReference docRef2 = db.collection("images").document(id);
        docRef2.get().addOnCompleteListener(imageTask -> {
            if (imageTask.isSuccessful() && imageTask.getResult() != null) {
                String url = imageTask.getResult().getString("url");
                Glide.with(image.getContext())
                        .load(url)
                        .thumbnail()
                        .into(image);
            }
        });

        company.setText(toy.getCompany());
        name.setText(toy.getName());
        List<String> tags = toy.getTags();
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append("#").append(tag).append("   ");
        }
        tag.setText(sb);
        age.setText(toy.getAge() + "세");
        category.setText(toy.getCategory());
        int toyQuantity = toy.getQuantity() - toy.getSellQuantity();
        quantity.setText(toyQuantity + "개");
        month_price.setText(String.format("%,d원", toy.getMonthPrice()));
        three_month_price.setText(String.format("%,d원", toy.getThreeMonthPrice()));
        description.setText(toy.getDescription().toString().replace("\\n", System.lineSeparator()));
        policy.setText(toy.getPolicy().toString().replace("\\n", System.lineSeparator()));

        back.setOnClickListener(view -> finish());

        home.setOnClickListener(view -> startActivity(new Intent(ToyDetailActivity.this, MainActivity.class)));
    }
}
