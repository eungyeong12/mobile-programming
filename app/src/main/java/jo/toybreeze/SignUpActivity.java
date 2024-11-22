package jo.toybreeze;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import jo.toybreeze.domain.User;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();

    private EditText edit_name;
    private EditText edit_email;
    private EditText edit_password;
    private EditText edit_phone_number;
    private EditText edit_addr;
    private EditText edit_detail_addr;
    private Button btn_signup;
    private TextView go_to_login;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_phone_number = findViewById(R.id.edit_phone_number);
        edit_addr = findViewById(R.id.edit_addr);
        edit_detail_addr = findViewById(R.id.edit_detail_addr);
        btn_signup = findViewById(R.id.btn_signup);
        go_to_login = findViewById(R.id.go_to_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edit_addr.setFocusable(false);
        edit_addr.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SearchAddressActivity.class);
            getSearchResult.launch(intent);
        });

        btn_signup.setOnClickListener(view -> {
            String name = edit_name.getText().toString();
            String email = edit_email.getText().toString();
            String password = edit_password.getText().toString();
            String phoneNumber = edit_phone_number.getText().toString();
            String addr = edit_addr.getText().toString();
            String detailAddr = edit_detail_addr.getText().toString();

            if (!checkValidation(name, email, password, phoneNumber, addr, detailAddr)) {
                return;
            }

            User user = new User(name, email, password, phoneNumber, addr, detailAddr);
            addUser(user);
        });

        go_to_login.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    go_to_login.setTextColor(Color.GRAY);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    go_to_login.setTextColor(Color.BLACK);
                    break;
            }
            return true;
        });
    }

    private void addUser(User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        db.collection("users").add(user);
                        Toast.makeText(this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            displayErrorMessage(exception);
                        }
                    }
                });
    }

    private void displayErrorMessage(Exception exception) {
        if (exception.getMessage().contains("The email address is badly formatted")) {
            Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        if (exception.getMessage().contains("Password should be at least 6 characters")) {
            Toast.makeText(this, "비밀번호는 6자 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
        }
        if (exception.getMessage().contains("The email address is already in use by another account")) {
            Toast.makeText(this, "이미 가입된 계정입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkValidation(String name, String email, String password, String phoneNumber, String addr, String detailAddr) {
        if (name.isBlank()) {
            Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isBlank()) {
            Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isBlank()) {
            Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.isBlank()) {
            Toast.makeText(this, "전화번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (addr.isBlank()) {
            Toast.makeText(this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (detailAddr.isBlank()) {
            Toast.makeText(this, "상세주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Search Address Activitiy로부터의 결과 값이 전달
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        edit_addr.setText(data);
                    }
                }
            }
    );
}
