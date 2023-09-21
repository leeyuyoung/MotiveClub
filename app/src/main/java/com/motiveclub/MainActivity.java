package com.motiveclub;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> nItems;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main);

        ImageButton buttonWrite = findViewById(R.id.buttonWrite);
        // 일기 작성 버튼 클릭 시 이벤트 처리
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryWriteFragment.class);
                startActivity(intent);
            }
        });

        // Firebase 초기화
        FirebaseApp.initializeApp(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // 리사이클러뷰 역순
        layoutManager.setStackFromEnd(true); // 리사이클러뷰 역순
        recyclerView.setLayoutManager(layoutManager);

        nItems = new ArrayList<>();

        // 어댑터 초기화 및 RecyclerView에 연결
        adapter = new NoteAdapter(nItems, this);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Note"); //DB 연결
        databaseReference.addValueEventListener(new ValueEventListener() { //addListenerForSingleValueEvent
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터 받아오는 곳
                Log.d("FirebaseData", "DataSnapshot: " + dataSnapshot.toString());
                nItems.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ //반복문으로 데이터 추출
                    Note note = snapshot.getValue(Note.class);
                    note.setKey(snapshot.getKey()); // Firebase 키를 설정합니다.
                    nItems.add(note); //담은 데이터 배열리스트에 넣고 뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침 , adapter.notifyItemRangeInserted(startPosition, (int) dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //에러 발생 시
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new NoteAdapter(nItems, this);
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getBindingAdapterPosition(); // 또는 getAbsoluteAdapterPosition()

                if (direction == ItemTouchHelper.LEFT) {

                    Log.d("SwipeTest", "Swiped at position: " + position);
                    //삭제할 아이템 담아두기
                    Note deleteItem = nItems.get(position);
                    databaseReference.child(deleteItem.getKey()).removeValue(); // Firebase

                    //삭제 기능
                    nItems.remove(position);
                    adapter.removeItem(position);
                    adapter.notifyItemRemoved(position);

                    //복구기능
                    Snackbar.make(recyclerView, deleteItem.getTitle(), Snackbar.LENGTH_LONG)
                            .setAction("복구", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    nItems.add(position, deleteItem);
                                    adapter.addItem(position, deleteItem);
                                    adapter.notifyItemInserted(position);

                                    databaseReference.child(deleteItem.getKey()).setValue(deleteItem); // Firebase에 다시 추가
                                }
                            }).show();

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.WHITE)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(Color.RED)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);


    }
}

