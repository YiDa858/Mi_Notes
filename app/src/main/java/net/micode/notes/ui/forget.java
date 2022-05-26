package net.micode.notes.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.model.WorkingNote;

public class forget extends Activity {
    public String note_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);

        Intent intent = getIntent();
        note_id = intent.getStringExtra("note_id");

        String selection = "note_id=?";
        String[] args = new String[]{String.valueOf(note_id)};
        Cursor cursor = this.getContentResolver().query(Notes.CONTENT_PASSWORD_URI, WorkingNote.PASSWORD_PROJECTION,
                selection, args, null);
        String question = cursor.getString(2);
        String answer = cursor.getString(3);
        cursor.close();

        TextView qu = findViewById(R.id.question);
        TextView an = findViewById(R.id.answer);
        Button sub = findViewById(R.id.submit);
        qu.setText(question);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (an.getText().equals(answer)) {
                    right();
                } else {
                    wrong();
                }
            }
        });
    }

    public void right() {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.update_password, null);
        TextView password = dialogView.findViewById(R.id.password);
        alBuilder.setView(dialogView);
        String[] ps = new String[0];
        alBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ps[0] = password.getText().toString();
                update_password(ps[0]);
                dialogInterface.dismiss();
            }
        });
        alBuilder.show();
    }

    public void wrong() {
        Toast.makeText(this, "答案错误", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void update_password(String password) {
        int hash = (note_id + password).hashCode();
        String selection = "note_id=?";
        String[] args = new String[]{String.valueOf(note_id)};
        ContentValues mNoteDiffValues = new ContentValues();
        mNoteDiffValues.put(Notes.PasswordColumns.PASSWORD, String.valueOf(hash));
        this.getContentResolver().update(Notes.CONTENT_PASSWORD_URI, mNoteDiffValues, selection, args);
    }
}
