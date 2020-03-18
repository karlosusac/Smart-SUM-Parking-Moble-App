package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartsum.smartsumparking.databinding.DialogFragmentLoginBinding;

import java.util.concurrent.Executor;

public class LoginDialogFragment extends DialogFragment {

    //View binding
    private DialogFragmentLoginBinding binding;

    //Firebase
    //Firebase auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;

    //Firebase database
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //TextInputLayot
    private TextInputLayout logInputEmail;
    private TextInputLayout logInputPass;

    //Button
    private Button logBtnConfirm;

    //Dialog listener
    private logDialogFragmentSignInUser listener;

    //Progress bar
    private ProgressBar logProgBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout. dialog_fragment_login, null, false);
        builder.setView(binding.getRoot());

        //Initialize variables
        logInputEmail = binding.logInputEmail;
        logInputPass = binding.logInputPass;
        logBtnConfirm = binding.logBtnConfirm;
        logProgBar = binding.logProgressBar;

        //Hide progess bar
        logProgBar.setVisibility(View.GONE);


        //CODE
        builder.setTitle(R.string.logTitle)
                .setNegativeButton(R.string.logBtnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close the dialog
                    }
                });

        logBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logProgBar.setVisibility(View.VISIBLE);
                enableAndDisableDialogCanceling(false);

                if(!logInputEmail.getEditText().getText().toString().isEmpty() && !logInputPass.getEditText().getText().toString().isEmpty()){

                    mAuth.signInWithEmailAndPassword(logInputEmail.getEditText().getText().toString(), logInputPass.getEditText().getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        listener.signInUser(mAuth.getCurrentUser());
                                        logProgBar.setVisibility(View.GONE);
                                        enableAndDisableDialogCanceling(true);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        logInputEmail.setError(" ");
                                        logInputPass.setError(getString(R.string.logIncorrectCredentials));
                                        logProgBar.setVisibility(View.GONE);
                                        enableAndDisableDialogCanceling(true);
                                    }
                                }
                            });

                } else {
                    logInputEmail.setError(" ");
                    logInputPass.setError(getString(R.string.logEmptyCredientials));
                    logProgBar.setVisibility(View.GONE);
                    enableAndDisableDialogCanceling(true);
                }
            }
        });


        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (logDialogFragmentSignInUser) context;
        } catch (Exception e) {
            Log.d("LogDiagFragError", String.valueOf(e));
        }
    }


    public interface logDialogFragmentSignInUser{
        void signInUser(FirebaseUser user);
    }

    //Disable/Enable dialog closing
    private void enableAndDisableDialogCanceling(boolean trueOrFalse){
        getDialog().setCancelable(trueOrFalse);
        getDialog().setCanceledOnTouchOutside(trueOrFalse);
    }

}
