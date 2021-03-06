package com.joneikholm.firebasehello;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.joneikholm.firebasehello.adapter.MyRecycleViewAdapter;
import com.joneikholm.firebasehello.model.Note;
import com.joneikholm.firebasehello.storage.MemoryStorage;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String notes = "notes";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyRecycleViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecycleViewAdapter();
        recyclerView.setAdapter(adapter);
        startNoteListener();
    }

    private void editNote() {
        //edit
        DocumentReference docRef = db.collection(notes).document("8K2cP9EnkTpZ5xNTlawm");
        Map<String,String> map = new HashMap<>();
        map.put("head", "changed head");
        map.put("body", "changed body");
        docRef.set(map);
    }

    private void deleteNote() {
        // delete:
        DocumentReference docRef = db.collection(notes).document("MMc3yCJqpl3ebmCELsFs");
        docRef.delete();
    }

    private void startNoteListener() {  // break until 15.20
        db.collection(notes).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot values, @Nullable FirebaseFirestoreException e) {
                MemoryStorage.notes.clear();
                for (DocumentSnapshot snap: values.getDocuments()) {
                   Log.i("all", "read from FB " + snap.getId() + " " + snap.get("body").toString());
                    MemoryStorage.notes.add(new Note(snap.getId(),
                            snap.get("head").toString(), snap.get("body").toString()));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addNewNote() {
        DocumentReference docRef = db.collection(notes).document();
        Map<String,String> map = new HashMap<>();
        map.put("head", "new headline 2");
        map.put("body", "new body 2");
        docRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("all", "added successfully");
            }
        });
    }
}
