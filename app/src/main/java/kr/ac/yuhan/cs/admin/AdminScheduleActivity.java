package kr.ac.yuhan.cs.admin;

import static kr.ac.yuhan.cs.admin.db.DatabaseHelper.COLUMN_CONTENT;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.admin.db.DatabaseHelper;
import kr.ac.yuhan.cs.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminScheduleActivity extends AppCompatActivity {

    private LinearLayout schedulePage;
    private NeumorphCardView scheduleCardView;
    private NeumorphCardView scheduleRegisterCardView;
    private NeumorphCardView input_todo;
    private TextView selectedDateTextView;
    private EditText todoEditText;
    private TextView todoTextView;
    private CalendarView calendarView;
    private NeumorphButton addTodoButton;
    private NeumorphButton handle;
    private NeumorphImageView backBtn;

    private DatabaseHelper dbHelper;

    // Map to store tasks for each date
    private Map<String, String> tasks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule);

        dbHelper = new DatabaseHelper(this);

        schedulePage = (LinearLayout) findViewById(R.id.schedulePage);

        // 현재 mode값 받음
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity에서 전달된 배경 색상 값을 받음
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));
        // 배경 색상을 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        todoEditText = findViewById(R.id.todoEditText);
        todoTextView = findViewById(R.id.todoTextView);
        calendarView = findViewById(R.id.calendarView);
        addTodoButton = findViewById(R.id.addTodoButton);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);
        handle = (NeumorphButton) findViewById(R.id.handle);
        scheduleCardView = (NeumorphCardView) findViewById(R.id.scheduleCardView);
        scheduleRegisterCardView = (NeumorphCardView) findViewById(R.id.scheduleRegisterCardView);
        input_todo = (NeumorphCardView) findViewById(R.id.input_todo);
        addTodoButton = (NeumorphButton) findViewById(R.id.addTodoButton);

        if(modeValue == 1) {
            // 폰트 색상 변경
            ChangeMode.applySubTheme(schedulePage, modeValue);

            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);
            ChangeMode.setDarkShadowCardView(handle);
            ChangeMode.setDarkShadowCardView(scheduleCardView);
            ChangeMode.setDarkShadowCardView(scheduleRegisterCardView);
            ChangeMode.setDarkShadowCardView(input_todo);
            ChangeMode.setDarkShadowCardView(addTodoButton);
        }
        else {
            handle.setBackgroundColor(Color.rgb(0, 174, 142));
            addTodoButton.setBackgroundColor(Color.rgb(0, 174, 142));

            ChangeMode.setLightShadowCardView(handle);
            ChangeMode.setLightShadowCardView(addTodoButton);
        }

        // CalendarView에서 날짜가 선택되었을 때 이벤트 처리
        // CalendarView에서 날짜가 선택되었을 때 이벤트 처리
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜를 TextView에 표시
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                selectedDateTextView.setText("선택된 날짜 : " + selectedDate);

                // 선택된 날짜에 해당하는 할일을 표시
                Cursor cursor = dbHelper.getTodosByDate(selectedDate);
                if (cursor != null && cursor.moveToFirst()) {
                    String task = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                    todoTextView.setText("선택된 날짜의 할일 목록\n" + task);
                } else {
                    todoTextView.setText("선택된 날짜의 할일이 없습니다.");
                }

                // Cursor를 닫음
                if (cursor != null) {
                    cursor.close();
                }
            }
        });


        // 할일 추가 버튼 클릭 이벤트 처리
        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = selectedDateTextView.getText().toString().replace("선택된 날짜 : ", "");
                String task = todoEditText.getText().toString();

                // 선택된 날짜에 해당하는 할일을 저장
                boolean isInserted = dbHelper.addTodo(selectedDate, task);
                if (isInserted) {
                    todoTextView.setText("할일이 추가되었습니다.");
                } else {
                    todoTextView.setText("할일 추가에 실패하였습니다.");
                }
                todoEditText.getText().clear();
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
