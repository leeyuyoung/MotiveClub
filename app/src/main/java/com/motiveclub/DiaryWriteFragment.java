package com.motiveclub;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
public class DiaryWriteFragment extends AppCompatActivity {

    private TextView date;
    private ImageButton calendarButton;
    private EditText title;
    private EditText contents;
    private TextView buttonSave;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private String diaryId; // 일기를 식별하는 고유한 ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_write);

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Note"); //데베 노드 이름

        // 이전 화면에서 전달된 수정할 일기 내용과 고유한 ID를 가져옵니다.
        String currentTitle = getIntent().getStringExtra("currentTitle");
        String currentContents = getIntent().getStringExtra("currentContents");
        String currentDate = getIntent().getStringExtra("currentDate");
        if (getIntent().hasExtra("diaryId")) {
            diaryId = getIntent().getStringExtra("diaryId");
        } else {
            diaryId = null; // 새로운 다이어리를 추가하는 경우
        }

        // EditText 초기화 추가
        title = findViewById(R.id.title);
        contents = findViewById(R.id.contents);
        date = findViewById(R.id.date);

        // EditText에 이전 일기 내용을 표시합니다.
        title.setText(currentTitle);
        contents.setText(currentContents);
        date.setText(currentDate);

        //내용입력
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String diaryTitle = title.getText().toString();
                String diaryContents = contents.getText().toString();
                String diaryDate = date.getText().toString();

                if (!diaryTitle.isEmpty() && !diaryContents.isEmpty()) {
                    if (diaryId != null && !diaryId.isEmpty()) {
                        // 기존 다이어리 업데이트
                        DatabaseReference diaryRef = databaseReference.child(diaryId);
                        diaryRef.child("title").setValue(diaryTitle);
                        diaryRef.child("contents").setValue(diaryContents);
                        diaryRef.child("date").setValue(diaryDate);

                        Toast.makeText(DiaryWriteFragment.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 새로운 다이어리 추가
                    String key = databaseReference.push().getKey(); // 고유한 키 생성
                    Note note = new Note(diaryTitle, diaryContents, diaryDate);
                    databaseReference.child(key).setValue(note);
                    Toast.makeText(DiaryWriteFragment.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }

                // 데이터 저장 후 MainActivity로 돌아가는 인텐트 생성
                Intent intent = new Intent(DiaryWriteFragment.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                    // 제목 또는 내용이 비어 있으면 경고 메시지 표시 또는 처리
                    Toast.makeText(DiaryWriteFragment.this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //닫기 버튼
        ImageButton closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MainActivity로 돌아가는 인텐트 생성
                Intent intent = new Intent(DiaryWriteFragment.this, MainActivity.class);
                startActivity(intent);
                // 현재 액티비티 종료
                finish();
            }
        });

        // 캘린더
        calendarButton = findViewById(R.id.calendarButton);


        // 캘린더 이미지를 클릭하면 DatePickerDialog를 표시
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calender = Calendar.getInstance();
                int year = calender.get(Calendar.YEAR);
                int month = calender.get(Calendar.MONTH);
                int day = calender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        int correctedMonth = i2 + 1;
                        String monthStr = (correctedMonth < 10) ? "0" + correctedMonth : String.valueOf(correctedMonth);
                        String dayStr = (i3 < 10) ? "0" + i3 : String.valueOf(i3);

                        date.setText(i + "." + monthStr + "." + dayStr);
                    }
                };
                DatePickerDialog picker = new DatePickerDialog(DiaryWriteFragment.this, R.style.DatePickerStyle, listener, year, month, day);
                picker.show();
            }
        });

    }


}




