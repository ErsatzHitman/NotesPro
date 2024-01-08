package com.example.notespro1;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsAcitivity extends AppCompatActivity {

    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;
    TextView pagetitleTextView;
    String title,content,docId;
    TextView deleteNoteTextViewbtn;
    boolean isEditMode=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details_acitivity);
        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pagetitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewbtn = findViewById(R.id.delete_note_text_view_btn);

        title =getIntent().getStringExtra("title");
        content =getIntent().getStringExtra("content");
        docId =getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()) isEditMode=true;

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pagetitleTextView.setText("Edit your note");
            deleteNoteTextViewbtn.setVisibility(View.VISIBLE);
        }
        deleteNoteTextViewbtn.setOnClickListener((v)->deleteNotefromFirebase());

        saveNoteBtn.setOnClickListener( (v)-> saveNote());
    }
    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty() ){
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNotetoFirebase(note);
    }
    void saveNotetoFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsAcitivity.this,"Note added Successfully");
                    finish();
                }
                else {
                    Utility.showToast(NoteDetailsAcitivity.this,"Failed while adding notes");
                }
            }
        });


    }
    void deleteNotefromFirebase(){
        DocumentReference documentReference;

            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsAcitivity.this,"Note deleted Successfully");
                    finish();
                }
                else {
                    Utility.showToast(NoteDetailsAcitivity.this,"Failed while deleting the note");
                }
            }
        });
    }
}