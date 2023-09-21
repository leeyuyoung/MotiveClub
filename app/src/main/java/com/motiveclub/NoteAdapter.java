package com.motiveclub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<Note> nItems;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    int lastPosition = -1;

    public NoteAdapter(ArrayList<Note> nItems, Context context) {
        this.nItems = nItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        // 리사이클러뷰 아이템 클릭 시 읽기 전용 화면으로 이동
        LinearLayout noteItem = view.findViewById(R.id.note_item);
        noteItem.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Note selectedNote = nItems.get(position);
                // 여기에서 읽기 전용 화면으로 이동하는 Intent를 생성하고 시작합니다.
                Intent intent = new Intent(context, ReadOnlyNoteActivity.class);
                intent.putExtra("note", selectedNote); // 선택한 노트 데이터를 전달
                context.startActivity(intent);
            }
        });

        return viewHolder;
    }

    // 데이터 뷰 홀더에 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (viewHolder.getAdapterPosition() > lastPosition) {
            Note item = nItems.get(position);
            viewHolder.title.setText(item.getTitle());
            viewHolder.contents.setText(item.getContents());
            viewHolder.date.setText(item.getDate());
        }

    }

    @Override
    public int getItemCount() {
        return(nItems != null ? nItems.size() : 0);
    }

    //아이템 삭제
    public void removeItem(int position){
        if (position >= 0 && position < nItems.size() && databaseReference != null) {
            Note deleteItem = nItems.get(position); //잉?
            // Firebase에서도 항목 삭제
            databaseReference.child(deleteItem.getKey()).removeValue();

            nItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, nItems.size()); // 아이템 위치 재조정
        }
    }

    public void removeAllItem(){
        nItems.clear();
        notifyDataSetChanged(); // 모든 아이템 삭제 후 화면 갱신
    }

    //아이템 추가
    public void addItem(int position, Note item) {
        if (position >= 0 && position <= nItems.size()) {
            nItems.add(position, item);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, nItems.size()); // 아이템 위치 재조정

            // Firebase에도 항목 추가
            if (databaseReference != null) {
                DatabaseReference newItemReference = databaseReference.push(); // 새로운 키 생성
                item.setKey(newItemReference.getKey()); // 항목에 새로운 키 설정
                newItemReference.setValue(item);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView contents;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.contents = itemView.findViewById(R.id.contents);
            this.date = itemView.findViewById(R.id.date);
        }

    }


}
