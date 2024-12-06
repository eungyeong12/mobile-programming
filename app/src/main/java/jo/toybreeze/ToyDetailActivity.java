package jo.toybreeze;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.toybreeze.adaptor.ReviewAdaptor;
import jo.toybreeze.domain.Payment;
import jo.toybreeze.domain.PaymentToy;
import jo.toybreeze.domain.PaymentType;
import jo.toybreeze.domain.Review;
import jo.toybreeze.domain.Toy;
import jo.toybreeze.domain.User;

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
    private Button payment1;
    private Button payment2;
    private LinearLayout paymentLayout;
    private LinearLayout btnLayout;
    private LinearLayout detailView;
    private TextView paymentName;
    private ImageView minus;
    private TextView paymentQuantity;
    private TextView paymentPrice;
    private ImageView plus;
    private boolean isAMonthPayment = true;
    private Button payment;
    private String imageUrl = "";
    private LinearLayout companyLayout;
    private Button addCart;
    private FirebaseAuth mAuth;
    private ImageView cart;
    private RecyclerView review;
    private TextView reviewCount;
    private ReviewAdaptor reviewAdaptor;
    private int cartQuantity;

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
        payment1 = findViewById(R.id.btn_payment1);
        payment2 = findViewById(R.id.btn_payment2);
        paymentLayout = findViewById(R.id.payment_layout);
        btnLayout = findViewById(R.id.btn_layout);
        detailView = findViewById(R.id.detail_view);
        paymentName = findViewById(R.id.detail_payment_name);
        minus = findViewById(R.id.minus);
        paymentQuantity = findViewById(R.id.detail_payment_quantity);
        paymentPrice = findViewById(R.id.detail_payment_price);
        plus = findViewById(R.id.plus);
        payment = findViewById(R.id.btn_payment);
        companyLayout = findViewById(R.id.company_layout);
        addCart = findViewById(R.id.btn_cart);
        mAuth = FirebaseAuth.getInstance();
        cart = findViewById(R.id.cart);
        review = findViewById(R.id.toy_detail_review);
        reviewCount = findViewById(R.id.toy_detail_review_count);
        paymentLayout.setVisibility(View.GONE);

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
                imageUrl = imageTask.getResult().getString("url");
                Glide.with(image.getContext())
                        .load(imageUrl)
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

        minus.setOnClickListener(view -> {
            int current = Integer.parseInt(paymentQuantity.getText().toString());
            if (current > 0) {
                int newQuantity = current - 1;
                paymentQuantity.setText(String.valueOf(newQuantity));
                int price = 0;
                if (isAMonthPayment) {
                    price = toy.getMonthPrice();
                } else {
                    price = toy.getThreeMonthPrice();
                }
                paymentPrice.setText(String.format("%,d", newQuantity * price));
            }
        });

        plus.setOnClickListener(view -> {
            int current = Integer.parseInt(paymentQuantity.getText().toString());
            if (current < toyQuantity) {
                int newQuantity = current + 1;
                paymentQuantity.setText(String.valueOf(newQuantity));
                int price = 0;
                if (isAMonthPayment) {
                    price = toy.getMonthPrice();
                } else {
                    price = toy.getThreeMonthPrice();
                }
                paymentPrice.setText(String.format("%,d", newQuantity * price));
            }
        });

        payment1.setOnClickListener(view -> {
            isAMonthPayment = true;
            paymentLayout.setVisibility(View.VISIBLE);
            btnLayout.setVisibility(View.GONE);
            paymentName.setText(name.getText().toString());
            paymentQuantity.setText("0");
            paymentPrice.setText("0");
        });

        payment2.setOnClickListener(view -> {
            isAMonthPayment = false;
            paymentLayout.setVisibility(View.VISIBLE);
            btnLayout.setVisibility(View.GONE);
            paymentQuantity.setText("0");
            paymentPrice.setText("0");
        });

        detailView.setOnTouchListener((view, motionEvent) -> {
            if (!isTouchInsideView(paymentLayout, motionEvent)) {
                btnLayout.setVisibility(View.VISIBLE);
                paymentLayout.setVisibility(View.GONE);
                return true; // 이벤트 소비
            }
            return false;
        });

        this.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (paymentLayout.getVisibility() == View.VISIBLE) {
                    btnLayout.setVisibility(View.VISIBLE);
                    paymentLayout.setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        });

        companyLayout.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), CompanyToyActivity.class);
            intent1.putExtra("company", company.getText().toString());
            startActivity(intent1);
        });

        db.collection("reviews")
                .whereEqualTo("toy", id)
                .addSnapshotListener((value, error) -> {
                    List<Review> reviews = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        Review r = document.toObject(Review.class);
                        reviews.add(r);
                    }

                    reviewCount.setText(String.format("%,d", reviews.size()));
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    review.setLayoutManager(linearLayoutManager);
                    reviewAdaptor = new ReviewAdaptor(reviews);
                    review.setAdapter(reviewAdaptor);
                });

        addCart.setOnClickListener(view -> {
            if (paymentQuantity.getText().toString().equals("0")) {
                return;
            }
            int price = 0;
            String type = "";
            if (isAMonthPayment) {
                price = toy.getMonthPrice();
                type = String.valueOf(PaymentType.MONTH);
            } else {
                price = toy.getThreeMonthPrice();
                type = String.valueOf(PaymentType.THREE_MONTH);
            }

            cartQuantity = Integer.parseInt(paymentQuantity.getText().toString().replace("개", ""));
            DocumentReference docRef3 = db.collection("cart").document();
            docRef3.get().addOnSuccessListener(documentSnapshot -> {
                PaymentToy existedToy = documentSnapshot.toObject(PaymentToy.class);
                if (existedToy != null) {
                    cartQuantity += existedToy.getQuantity();
                }
            });

            PaymentToy paymentToy = new PaymentToy(
                    imageUrl,
                    name.getText().toString(),
                    cartQuantity,
                    toyQuantity,
                    price,
                    type,
                    new Date()
            );

            db.collection("cart").document(id + "_" + type).set(paymentToy);

            Snackbar snackbar = Snackbar.make(view, "장바구니에 상품을 담았습니다.", Snackbar.LENGTH_LONG)
                    .setAction("바로가기", v -> {
                        startActivity(new Intent(ToyDetailActivity.this, CartActivity.class));
                    })
                    .setBackgroundTint(Color.parseColor("#A1D6B2"))
                    .setTextColor(Color.WHITE)
                    .setActionTextColor(Color.WHITE);

            TextView actionTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
            if (actionTextView != null) {
                SpannableString content = new SpannableString("바로가기");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                actionTextView.setText(content);
            }

            snackbar.show();
        });

        cart.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CartActivity.class)));

        payment.setOnClickListener(view -> {
            if (paymentQuantity.getText().toString().equals("0")) {
                return;
            }
            int price = 0;
            String type = "";
            if (isAMonthPayment) {
                price = toy.getMonthPrice();
                type = String.valueOf(PaymentType.MONTH);
            } else {
                price = toy.getThreeMonthPrice();
                type = String.valueOf(PaymentType.THREE_MONTH);
            }
            PaymentToy paymentToy = new PaymentToy(
                    imageUrl,
                    name.getText().toString(),
                    Integer.parseInt(paymentQuantity.getText().toString().replace("개", "")),
                    toyQuantity,
                    price,
                    type,
                    new Date()
            );

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DATE, 3);
            Date startDate = calendar.getTime();

            if (isAMonthPayment) {
                calendar.add(Calendar.MONTH, 1);
            } else {
                calendar.add(Calendar.MONTH, 3);
            }

            Date endDate = calendar.getTime();

            Payment payment = new Payment(startDate, paymentToy, id, endDate, now);
            List<Payment> payments = new ArrayList<>();
            payments.add(payment);

            Intent intent2 = new Intent(getApplicationContext(), PaymentActivity.class);
            intent2.putExtra("data", (Serializable) payments);
            startActivity(intent2);
        });
    }

    private boolean isTouchInsideView(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float x = event.getRawX();
        float y = event.getRawY();

        return x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight());
    }
}
