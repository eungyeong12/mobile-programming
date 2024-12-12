package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.toybreeze.adaptor.CartAdapter;
import jo.toybreeze.adaptor.PaymentAdaptor;
import jo.toybreeze.domain.Payment;
import jo.toybreeze.domain.PaymentToy;
import jo.toybreeze.domain.User;
import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private Button payment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User bootUser;
    private double price;
    private TextView name;
    private TextView phone;
    private TextView addr;
    private TextView detailAddr;
    private RecyclerView recyclerView;
    private PaymentAdaptor paymentAdaptor;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        EdgeToEdge.enable(this);
        payment = findViewById(R.id.btn_payment);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        name = findViewById(R.id.payment_user_name);
        phone = findViewById(R.id.payment_user_phone);
        addr = findViewById(R.id.payment_user_addr);
        detailAddr = findViewById(R.id.payment_user_detail_addr);
        recyclerView = findViewById(R.id.payment_toy_view);
        back = findViewById(R.id.back);

        payment.setClickable(false);
        DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            bootUser = documentSnapshot.toObject(User.class);
            name.setText(bootUser.getName());
            phone.setText(formatPhoneNumber(bootUser.getPhoneNumber()));
            addr.setText(bootUser.getAddress());
            detailAddr.setText(bootUser.getDetailAddress());
        });


        Intent intent = getIntent();
        List<Payment> data = (List<Payment>) intent.getSerializableExtra("data");
        List<BootItem> items = new ArrayList<>();
        price = 0.0;
        int sum = 0;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        paymentAdaptor = new PaymentAdaptor(data);
        paymentAdaptor.setOnItemClickListener((position, d) -> {
            Intent intent2 = new Intent(getApplicationContext(), ToyDetailActivity.class);
            intent2.putExtra("data", d);
            startActivity(intent2);
        });
        recyclerView.setAdapter(paymentAdaptor);

        for (Payment payment : data) {
            PaymentToy paymentToy = payment.getPaymentToy();
            double itemPrice = paymentToy.getQuantity() * (double) paymentToy.getPrice();
            BootItem bootItem = new BootItem()
                    .setName(paymentToy.getName())
                    .setId(payment.getToyId())
                    .setQty(paymentToy.getQuantity())
                    .setPrice((double) paymentToy.getPrice());
            price += itemPrice;
            sum += itemPrice;
            items.add(bootItem);
        }

        payment.setText(String.format("%,d원 결제하기", sum));

        payment.setClickable(true);

        payment.setOnClickListener(view -> {
            BootUser user = new BootUser()
                    .setUsername(bootUser.getName())
                    .setEmail(bootUser.getEmail())
                    .setPhone(bootUser.getPhoneNumber())
                    .setArea(bootUser.getAddress())
                    .setArea(bootUser.getDetailAddress()); // 구매자 정보

            BootExtra extra = new BootExtra()
                    .setCardQuota("0"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

            String orderId = "ORDER-" + System.currentTimeMillis();
            Payload payload = new Payload();
            payload.setApplicationId("6747d74d3aa7c4faf96e5127")
                    .setOrderName("부트페이 결제")
                    .setOrderId(orderId)
                    .setPrice(price)
                    .setUser(user)
                    .setExtra(extra)
                    .setItems(items);


            Bootpay.init(getSupportFragmentManager())
                    .setPayload(payload)
                    .setEventListener(new BootpayEventListener() {
                        @Override
                        public void onCancel(String data) {
                            Log.d("bootpay", "cancel: " + data);
                        }

                        @Override
                        public void onError(String data) {
                            Log.d("bootpay", "error: " + data);
                        }

                        @Override
                        public void onClose() {
                            Bootpay.removePaymentWindow();
                        }

                        @Override
                        public void onIssued(String data) {
                            Log.d("bootpay", "issued: " +data);
                        }

                        @Override
                        public boolean onConfirm(String data) {
                            Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                            return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                        }

                        @Override
                        public void onDone(String data) {
                            Log.d("done", data);
                        }
                    }).requestPayment();
        });

        back.setOnClickListener(view -> finish());
    }

    private String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }
}
