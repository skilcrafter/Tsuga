package life.tsuga.tsuga;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPassword extends ActionBarActivity {

    protected EditText mEmail;
    protected Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mSubmitButton = (Button)findViewById(R.id.resetsubmitbutton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mEmail = (EditText)findViewById(R.id.submitEMail);
                String email = mEmail.getText().toString();
                email = email.trim();

                if(email.isEmpty()){
                    AlertDialog.Builder builder= new AlertDialog.Builder(ForgotPassword.this);
                    builder.setMessage(R.string.reset_error_message)
                            .setTitle(R.string.reset_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    ParseUser.requestPasswordResetInBackground(email,
                            new RequestPasswordResetCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                       finish();
                                    } else {
                                        AlertDialog.Builder builder= new AlertDialog.Builder(ForgotPassword.this);
                                        builder.setMessage(R.string.reset_error_sending_message)
                                                .setTitle(R.string.reset_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }
                                }
                            });
                }
            }
        });
    }
}
