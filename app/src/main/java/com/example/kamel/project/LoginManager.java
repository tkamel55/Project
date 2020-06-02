package com.example.kamel.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginManager {
    FirebaseDatabase db;
    DatabaseReference usersTbl;
    ProgressDialog progressDialog;
    Context mContext;
    private boolean visibleProgress = false;
    private LoginFeedbackListener loginFeedbackListener;

    public LoginManager(Context mContext, boolean visibleProgress, LoginFeedbackListener loginFeedbackListener) {
        this.mContext = mContext;
        this.visibleProgress = visibleProgress;
        this.loginFeedbackListener = loginFeedbackListener;
        db = FirebaseDatabase.getInstance();
        usersTbl = db.getReference(StaticAccess.USERS_TABLE);
        progressDialog = new ProgressDialog(mContext);
    }
    ///get logged-in user information by userID
    User detectUser = null;
    public void getUserByID(final long userID) {
        if (visibleProgress) {
            showProgress();
        }
        usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).getUserID() == userID) {
                        detectUser = ds.getValue(User.class);
                        loginFeedbackListener.getLoggedinUser(detectUser);
                        hideProgress();
                        break;
                    }
                }
                if (detectUser == null) {
                    loginFeedbackListener.noUserFound();
                    hideProgress();
                }
                // hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
            }
        });
    }
    /// @check user credential and return the exact login user
    public void loginUser(final String email, final String password) {
        if (emailValidator(email)) {
            if (passwordValidator(password)) {
                detectUser = null;
                if (visibleProgress) {
                    showProgress();
                }
                usersTbl.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            try {
                                if (ds.getValue(User.class).getEmail().equalsIgnoreCase(email) &&
                                        AESCrypt.decrypt(ds.getValue(User.class).getPassword()).equalsIgnoreCase(password)) {
                                    detectUser = ds.getValue(User.class);
                                    loginFeedbackListener.getLoggedinUser(detectUser);
                                    hideProgress();
                                    break;
                                }
                            } catch (Exception e) {
                                loginFeedbackListener.noUserFound();
                                hideProgress();
                            }
                        }
                        if (detectUser == null) {
                            loginFeedbackListener.noUserFound();
                            hideProgress();
                        }
                        // hideProgress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hideProgress();
                    }
                });
            } else {
                loginFeedbackListener.passwordError();
            }
        } else {
            loginFeedbackListener.emailError();
        }

    }

    public void showProgress() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public boolean emailValidator(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            Pattern pattern;
            Matcher matcher;
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }

    public boolean passwordValidator(String password) {
        boolean isEmptyCheckOK = false;
        boolean isLengthOK = false;
        if (!TextUtils.isEmpty(password)) {
            isEmptyCheckOK = true;
            if (password.length() > 5) {
                isLengthOK = true;
            } else {
                isLengthOK = false;
            }
        } else {
            isEmptyCheckOK = false;
        }

        if (isEmptyCheckOK && isLengthOK) {
            return true;
        } else {
            return false;
        }
    }

    public interface LoginFeedbackListener {
        void getLoggedinUser(User user);

        void noUserFound();

        void emailError();

        void passwordError();
    }


}
