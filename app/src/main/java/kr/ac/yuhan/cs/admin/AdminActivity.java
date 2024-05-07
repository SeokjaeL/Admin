package kr.ac.yuhan.cs.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.yuhan.cs.admin.adapter.AdminAdapter;
import kr.ac.yuhan.cs.admin.adapter.MemberAdapter;
import kr.ac.yuhan.cs.admin.data.AdminData;
import kr.ac.yuhan.cs.admin.data.MemberData;
import kr.ac.yuhan.cs.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminActivity extends AppCompatActivity {
    private NeumorphCardView adminListCardView;
    private NeumorphCardView editTextSearchAdminField;
    private NeumorphButton adminSearchBtn;
    private NeumorphImageView backBtn;
    private EditText input_searchId;
    // 파이어스토어 DB
    private FirebaseFirestore db;
    // 세션 객체
    private SharedPreferences sharedPreferences;
    // 어댑터
    private AdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        LinearLayout adminListPage = (LinearLayout) findViewById(R.id.adminListPage);

        ListView listView = findViewById(R.id.listView);

        // 어댑터 설정
        adapter = new AdminAdapter(this, new ArrayList<AdminData>());
        listView.setAdapter(adapter);

        // 관리자 데이터 가져오기
        fetchAdminData();

        // 현재 mode값 받음
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity에서 전달된 배경 색상 값을 받음
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));
        // 배경 색상을 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);
        adminListCardView = (NeumorphCardView) findViewById(R.id.adminListCardView);
        editTextSearchAdminField = (NeumorphCardView) findViewById(R.id.editTextSearchAdminField);
        adminSearchBtn = (NeumorphButton) findViewById(R.id.adminSearchBtn);
        input_searchId = (EditText) findViewById(R.id.input_searchId);

        // 리스트뷰 아이템 클릭 리스너 설정
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템의 정보를 가져옴
                AdminData selectedItem = (AdminData) adapter.getItem(position);
                showAdminInfoDialog(selectedItem);
            }
        });

        if (modeValue == 1) {
            ChangeMode.applySubTheme(adminListPage, modeValue);

            // 새 이미지로 바꿔주세요.
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            ChangeMode.setDarkShadowCardView(adminListCardView);
            ChangeMode.setDarkShadowCardView(editTextSearchAdminField);
            ChangeMode.setDarkShadowCardView(adminSearchBtn);
        } else {
            adminSearchBtn.setBackgroundColor(Color.rgb(0, 174, 142));
            ChangeMode.setLightShadowCardView(adminSearchBtn);
        }

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

        adminSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 로그인한 관리자 ID 가져오기
                SharedPreferences sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String currentAdminId = sharedPreferences.getString("admin_id", null);

                db = FirebaseFirestore.getInstance();
                ArrayList<AdminData> dataList = new ArrayList<>();

                String searchId = input_searchId.getText().toString().trim();
                Query query;

                if (searchId.isEmpty()) {
                    // 검색어가 없을 경우 전체 문서를 조회
                    query = db.collection("Admin");
                } else {
                    // 입력된 검색어로 시작하는 adminId를 가진 문서를 조회
                    query = db.collection("Admin").orderBy("adminId").startAt(searchId).endAt(searchId + '\uf8ff');
                }

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 1; // num을 위한 카운터 시작 값
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals(currentAdminId)) {
                                    String adminId = document.getId();
                                    String password = document.getString("password");
                                    String position = document.getString("position");

                                    dataList.add(new AdminData(i, adminId, password, position));
                                    i++;  // 다음 num 값 증가
                                }
                            }
                            if (dataList.isEmpty()) {
                                adapter.updateData(dataList);
                                Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.updateData(dataList);
                                Toast.makeText(getApplicationContext(), "검색 완료", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("Firestore Search", "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "검색 중 오류 발생", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });






    }

    // 관리자 데이터 가져오기
    private void fetchAdminData () {
        // 현재 로그인한 관리자 ID 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String currentAdminId = sharedPreferences.getString("admin_id", null);

        db = FirebaseFirestore.getInstance();
        ArrayList<AdminData> dataList = new ArrayList<>();
        db.collection("Admin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 1; // num을 위한 카운터 시작 값
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.getId().equals(currentAdminId)) {  // 현재 로그인한 관리자 제외
                            String adminId = document.getString("adminId");
                            String password = document.getString("password");
                            String position = document.getString("position");

                            dataList.add(new AdminData(i, adminId, password, position));
                            i++;  // 다음 num 값 증가
                        }
                    }
                    if (dataList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        adapter.updateData(dataList);
                    }
                } else {
                    Log.w("AdminActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static void showErrorDialog (Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("오류 발생")
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 확인 버튼을 눌렀을 때 처리할 내용
                        dialog.dismiss(); // 다이얼로그를 닫습니다.
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAdminInfoDialog (AdminData selectedItem){
        // 다이얼로그를 생성하고 레이아웃을 설정
        Dialog dialog = new Dialog(AdminActivity.this);
        dialog.setContentView(R.layout.dialog_admin_item_info);

        // 다이얼로그 내의 TextView를 가져와서 멤버 정보로 설정
        TextView textViewAdminNum = dialog.findViewById(R.id.textViewAdminNum);
        textViewAdminNum.setText("Num: " + selectedItem.getAdminNum());

        TextView textViewAdminId = dialog.findViewById(R.id.textViewAdminId);
        textViewAdminId.setText("Id: " + selectedItem.getAdminId());

        TextView textViewAdminPosition = dialog.findViewById(R.id.textViewAdminPosition);
        textViewAdminPosition.setText("Postion: " + selectedItem.getAdminPosition());

        // 다이얼로그를 보여줌
        dialog.show();
    }
}