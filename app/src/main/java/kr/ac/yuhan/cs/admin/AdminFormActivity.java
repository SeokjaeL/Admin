package kr.ac.yuhan.cs.admin;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.admin.func.ChangeTextColor;
import kr.ac.yuhan.cs.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

import kr.ac.yuhan.cs.admin.func.PasswordEncrypion;

public class AdminFormActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;
    // 파이어스토어 DB
    private FirebaseFirestore db;
    // 비밀번호 암호화
    private PasswordEncrypion pe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_form);
        LinearLayout adminFormPage = (LinearLayout) findViewById(R.id.adminFormPage);

        // 현재 mode값 받음
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity에서 전달된 배경 색상 값을 받음
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));
        // 배경 색상을 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        NeumorphCardView adminAddCardView = (NeumorphCardView) findViewById(R.id.adminAddCardView);
        NeumorphCardView editTextIdField = (NeumorphCardView) findViewById(R.id.editTextIdField);
        NeumorphCardView editTextPwField = (NeumorphCardView) findViewById(R.id.editTextPwField);
        NeumorphCardView editTextPositionField = (NeumorphCardView) findViewById(R.id.editTextPositionField);
        NeumorphButton adminAddBtn = (NeumorphButton) findViewById(R.id.adminAddBtn);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);

        // 관리자 아이디, 비밀번호, 직책 에디트 텍스트
        EditText input_adminId = (EditText) findViewById(R.id.input_adminId);
        EditText input_adminPw = (EditText) findViewById(R.id.input_adminPW);
        EditText input_adminPosition = (EditText) findViewById(R.id.input_adminPosition);

        // 파이어스토어 DB
        db = FirebaseFirestore.getInstance();

        if(modeValue == 1) {
            ChangeMode.applySubTheme(adminFormPage, modeValue);

            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);
            ChangeMode.setDarkShadowCardView(adminAddCardView);
            ChangeMode.setDarkShadowCardView(editTextIdField);
            ChangeMode.setDarkShadowCardView(editTextPwField);
            ChangeMode.setDarkShadowCardView(editTextPositionField);
            ChangeMode.setDarkShadowCardView(adminAddBtn);
        }
        else {
            adminAddBtn.setBackgroundColor(Color.rgb(0, 174, 142));
            ChangeMode.setLightShadowCardView(adminAddBtn);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭될 때 ShapeType을 'pressed'로 변경
                backBtn.setShapeType(1);
                // 클릭된 후에는 다시 FLAT으로 변경
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backBtn.setShapeType(0);
                    }
                }, 200);
                finish();
            }
        });

        adminAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminId = input_adminId.getText().toString();
                String adminPw = input_adminPw.getText().toString();
                String adminPosition = input_adminPosition.getText().toString();

                // 필드값 유효성 검사
                if (adminId.isEmpty()) {
                    Toast.makeText(AdminFormActivity.this, "관리자 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (adminPw.isEmpty()) {
                    Toast.makeText(AdminFormActivity.this, "관리자 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (adminPosition.isEmpty()) {
                    Toast.makeText(AdminFormActivity.this, "관리자 직책를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 관리자 중복여부 확인
                db.collection("Admin").document(adminId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(AdminFormActivity.this, "이미 존재하는 관리자 ID입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // 비밀번호 암호화
                            pe = new PasswordEncrypion();
                            String hashedPassword = pe.hashPassword(adminPw);

                            // DB에 저장할 데이터 맵 생성
                            Map<String, Object> admin = new HashMap<>();
                            admin.put("adminId", adminId);
                            admin.put("password", hashedPassword);
                            admin.put("position", adminPosition);

                            // 관리자 등록 처리
                            db.collection("Admin").document(adminId).set(admin)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdminFormActivity.this, "관리자 추가에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                        // Navigate to AdminMenuActivity
                                        finish(); // Optionally finish this activity
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdminFormActivity.this, "관리자 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(AdminFormActivity.this, "데이터베이스 접근 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }



}
