package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payment = findViewById(R.id.btn_payment);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        payment.setClickable(false);
        DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.get().addOnSuccessListener(documentSnapshot -> bootUser = documentSnapshot.toObject(User.class));
        Intent intent = getIntent();
        List<Payment> data = (List<Payment>) intent.getSerializableExtra("data");
        List<BootItem> items = new ArrayList<>();
        price = 0.0;
        for (Payment payment : data) {
            PaymentToy paymentToy = payment.getPaymentToy();
            double itemPrice = paymentToy.getQuantity() * (double) paymentToy.getPrice();
            BootItem bootItem = new BootItem()
                    .setName(paymentToy.getName())
                    .setId(payment.getToyId())
                    .setQty(paymentToy.getQuantity())
                    .setPrice((double) paymentToy.getPrice());
            price += itemPrice;
            items.add(bootItem);
        }

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

            Map<String, Object> map = new HashMap<>();
            map.put("1", "abcdef");
            map.put("2", "abcdef55");
            map.put("3", 1234);
            payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));

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
    }
}
