package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.domain.User;
import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class SubscribeActivity extends AppCompatActivity {
    private static final String TAG = SubscribeActivity.class.getSimpleName();
    private LinearLayout randomSubscribe;
    private LinearLayout selectSubscribe;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User bootUser;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscribe);
        randomSubscribe = findViewById(R.id.subscribe_random);
        selectSubscribe = findViewById(R.id.subscribe_select);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        back = findViewById(R.id.back);

        DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            bootUser = documentSnapshot.toObject(User.class);
        });

        List<BootItem> bootItems = new ArrayList<>();
        bootItems.add(new BootItem()
                .setId("subscribe_random_" + System.currentTimeMillis())
                .setName("Random Subscription")
                .setPrice(30000.0)
                .setQty(1));

        randomSubscribe.setOnClickListener(view -> {
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
                    .setPrice(30000.0)
                    .setUser(user)
                    .setExtra(extra)
                    .setItems(bootItems);


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
                        public void onDone(String d) {
                            Log.d("done", d);
                        }
                    }).requestPayment();
        });

        back.setOnClickListener(view -> finish());
    }
}
