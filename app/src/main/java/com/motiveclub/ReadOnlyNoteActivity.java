package com.motiveclub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ReadOnlyNoteActivity extends AppCompatActivity {

    private TextView title;
    private TextView contents;
    private TextView date;
    private Note selectedNote;
    private DatabaseReference notesRef;

    private BottomSheetDialog bottomSheetDialog;
    private ArrayList<Note> nItems;
    private NoteAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_only);

        // Firebase Database 초기화
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notesRef = database.getReference("notes");

        // Intent에서 노트 데이터를 가져옵니다.
        selectedNote = (Note) getIntent().getSerializableExtra("note");
        // nItems 초기화
        nItems = new ArrayList<>();

        // UI 요소를 찾습니다.
        title = findViewById(R.id.readOnlyTitleTextView);
        contents = findViewById(R.id.readOnlyContentsTextView);
        date = findViewById(R.id.readOnlyDateTextView);

        // 노트 데이터를 UI에 표시합니다.
        if (selectedNote != null) {
            title.setText(selectedNote.getTitle());
            contents.setText(selectedNote.getContents());
            date.setText(selectedNote.getDate());

        }


        //뒤로가기 < 버튼
        ImageButton closeButton = findViewById(R.id.backbtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MainActivity로 돌아가는 인텐트 생성
                Intent intent = new Intent(ReadOnlyNoteActivity.this, MainActivity.class);
                startActivity(intent);
                // 현재 액티비티 종료
                finish();
            }
        });

        //bottom sheet dialog
        final View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // 버튼 클릭 시 하단 시트를 표시
        ImageButton dialogButton = findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 액티비티가 여전히 유효한지 확인합니다 (종료되지 않았거나 파괴되지 않았는지).
                if (!isFinishing()) {
                    bottomSheetDialog.show();
                }
            }
        });

        // changeText 클릭 이벤트 핸들러 (일기 수정하기)
        TextView changeText = bottomSheetDialog.findViewById(R.id.changeText);
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 일기 내용을 가져와서 DiaryWrite 액티비티로 전달
                String currentTitle = title.getText().toString();
                String currentContents = contents.getText().toString();
                String currentDate = date.getText().toString();

                Intent intent = new Intent(ReadOnlyNoteActivity.this, DiaryWriteFragment.class);
                intent.putExtra("currentTitle", currentTitle);
                intent.putExtra("currentContents", currentContents);
                intent.putExtra("currentDate", currentDate);

                // diaryId를 전달하여 수정 모드임을 나타냅니다.
                intent.putExtra("diaryId", selectedNote.getKey());
                startActivityForResult(intent, 1);

            }
        });

        //deleteText 클릭 이벤트 핸들러 (일기 삭제하기)
        TextView deleteText = bottomSheetDialog.findViewById(R.id.deleteText);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedNote != null) {
                    // Firebase 데이터베이스에서 아이템 삭제
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Note");
                    databaseReference.child(selectedNote.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                // 삭제 실패
                                Toast.makeText(getApplicationContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 삭제 성공
                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                // 메인 화면으로 이동
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 대화 상자를 닫습니다.
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

}