package com.mg.hotelmanagementsystem.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by moses on 7/11/18.
 */

public class FirebaseTransaction {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ProgressDialog progressDialog;


    public FirebaseTransaction(Context context) {
        this(context, true);
    }

    public FirebaseTransaction(Context context, boolean withDialog) {
        this(context, "", "Loading...", withDialog);
    }

    public FirebaseTransaction(Context context, String dialogTitle, String dialogMessage, boolean withDialog) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(dialogTitle);
        progressDialog.setMessage(dialogMessage);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if (withDialog)
            progressDialog.show();
    }

    public FirebaseTransaction child(String childName) {
        databaseReference = databaseReference.child(childName);
        return this;
    }

    public void setValue(Object value) {
        setValue(value, null);
    }

    public void setValue(Object value, final DatabaseReference.CompletionListener completionListener) {
        databaseReference.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (completionListener != null)
                    completionListener.onComplete(databaseError, databaseReference);
            }
        });
    }

    public FirebaseTransaction push() {
        databaseReference = databaseReference.push();
        return this;
    }

    public FirebaseTransaction push(String id) {
        databaseReference = databaseReference.child(id);
        return this;
    }

    public void setValue(Object value, DatabaseReference.CompletionListener completionListener, ValueEventListener valueEventListener) {
        read(valueEventListener);
        setValue(value, completionListener);
    }

    public void read(final ValueEventListener valueEventListener, final boolean listen) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (valueEventListener != null)
                    valueEventListener.onDataChange(dataSnapshot);
                if (!listen) {
                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (valueEventListener != null)
                    valueEventListener.onCancelled(databaseError);

                if (!listen) {
                    databaseReference.removeEventListener(this);
                }
            }
        });
    }

    public void read(final ValueEventListener valueEventListener) {
        read(valueEventListener, true);
    }

    public void readChildren(final ChildEventListener childEventListener) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildChanged(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildMoved(dataSnapshot, s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onCancelled(databaseError);
            }
        });
    }
}
