package kr.ac.yuhan.cs.admin.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.ac.yuhan.cs.admin.R;
import kr.ac.yuhan.cs.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

import kr.ac.yuhan.cs.admin.func.PasswordEncrypion;

public class LoginActivity extends AppCompatActivity {
    private NeumorphButton loginBtn;
    private NeumorphImageView backBtn;
    // 파이어스토어 DB
    private FirebaseFirestore db;
    // 세션 객체
    private SharedPreferences sharedPreferences;
    // 비밀번호 암호화
    private PasswordEncrypion pe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        LinearLayout loginPage = (LinearLayout) findViewById(R.id.loginPage);

        // 현재 mode값 받음
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity에서 전달된 배경 색상 값을 받음
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));
        // 배경 색상을 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        Drawable darkIdImage = getResources().getDrawable(R.drawable.user);
        Drawable darkPwImage = getResources().getDrawable(R.drawable.lock);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);
        loginBtn = (NeumorphButton) findViewById(R.id.loginBtn);

        NeumorphCardView loginCardView = (NeumorphCardView) findViewById(R.id.loginCardView);
        NeumorphCardView editTextIdField = (NeumorphCardView) findViewById(R.id.editTextIdField);
        NeumorphCardView editTextPwField = (NeumorphCardView) findViewById(R.id.editTextPwField);
        EditText input_id = (EditText) findViewById(R.id.input_id);
        EditText input_pw = (EditText) findViewById(R.id.input_pw);

        // 파이어스토어 DB
        db = FirebaseFirestore.getInstance();

        if(modeValue == 1) {
            ChangeMode.applySubTheme(loginPage, modeValue);

            // 새 이미지로 바꿔주세요.
            darkIdImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            darkPwImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            input_id.setCompoundDrawablesWithIntrinsicBounds(darkIdImage, null, null, null);
            input_pw.setCompoundDrawablesWithIntrinsicBounds(darkPwImage, null, null, null);

            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            ChangeMode.setDarkShadowCardView(loginCardView);
            ChangeMode.setDarkShadowCardView(editTextIdField);
            ChangeMode.setDarkShadowCardView(editTextPwField);
            ChangeMode.setDarkShadowCardView(loginBtn);
        }
        else {
            loginBtn.setBackgroundColor(Color.rgb(0, 174, 142));
            ChangeMode.setLightShadowCardView(loginBtn);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setShapeType(1);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginBtn.setShapeType(0);
                    }
                }, 200);

                // 에디트 텍스트에서 입력한 아이디, 비밀번호 값 받아오기
                String adminId = input_id.getText().toString().trim();
                String adminPwd = input_pw.getText().toString().trim();

                // 관리자 아이디나 비밀번호를 입력하지 않은 경우 토스트 메시지 출력
                if(adminId.isEmpty()){
                    Toast.makeText(LoginActivity.this, "관리자 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(adminPwd.isEmpty()){
                    Toast.makeText(LoginActivity.this, "관리자 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 암호화
                pe = new PasswordEncrypion();
                String hashedPassword = pe.hashPassword(adminPwd);

                // 로그인 처리
                db.collection("Admin").document(adminId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists() && task.getResult().getString("password").equals(hashedPassword)) {
                            // 로그인 성공 후 토스트 메시지 출력
                            Toast tMsg = Toast.makeText(LoginActivity.this, "관리자 로그인에 성공했습니다.", Toast.LENGTH_SHORT);
                            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                                    .getDefaultDisplay();
                            int xOffset = (int) (Math.random() * display.getWidth()); // x좌표
                            int yOffset = (int) (Math.random() * display.getHeight()); // y좌표
                            tMsg.setGravity(Gravity.TOP | Gravity.LEFT, xOffset, yOffset);
                            tMsg.show();

                            // 로그인 세션 생성
                            sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("admin_id", adminId);
                            editor.apply();

                            finish();
                            // 관리자 아이디나 비밀번호가 일치하지 않는 경우
                        } else {
                            Toast.makeText(LoginActivity.this, "관리자 아이디나 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 로그인 중 오류가 발생한 경우
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn.setShapeType(1);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backBtn.setShapeType(0);
                    }
                }, 200);
                finish();
            }
        });
    }

}