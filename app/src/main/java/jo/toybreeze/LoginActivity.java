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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText edit_email;
    private EditText edit_password;
    private Button btn_sign_in;
    private TextView go_to_signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edit_email = findViewById(R.id.edit_sign_in_email);
        edit_password = findViewById(R.id.edit_sign_in_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        go_to_signup = findViewById(R.id.go_to_signup);
        mAuth = FirebaseAuth.getInstance();

        btn_sign_in.setOnClickListener(view -> {
            String email = edit_email.getText().toString();
            String password = edit_password.getText().toString();
            if (!checkValidation(email, password)) {
                return;
            }
            login(email, password);
        });

        go_to_signup.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    go_to_signup.setTextColor(Color.GRAY);
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    go_to_signup.setTextColor(Color.BLACK);
                    break;
            }
            return true;
        });
    }

    private void login(String email, String password) {
        btn_sign_in.setOnClickListener(view -> mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private boolean checkValidation(String email, String password) {
        if (email.isBlank()) {
            Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isBlank()) {
            Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
