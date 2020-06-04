package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentText extends Fragment {

    public FragmentText() {
        // Required empty public constructor

    }

    Button btnRetrieveText, btnEmail;
    EditText etText;
    TextView tvSmsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        tvSmsText = view.findViewById(R.id.tvDisplayFragText);
        etText = view.findViewById(R.id.etSmsText);
        btnRetrieveText = view.findViewById(R.id.btnGetSms);

        btnEmail = view.findViewById(R.id.btnSendEmail);

        btnRetrieveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }


                // Create all messages URI
                Uri uri = Uri.parse("content://sms");

                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent

                // other options {"date_sent","error_code","locked","person","protocol","read","service_center","status","subject","thread_id"}
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                // The filter String
                String filter = "body LIKE ?";
                String[] textInput = etText.getText().toString().split(" ");
                // The matches for the ?
                String[] filterArgs = new String[textInput.length];

                // Inserting the user input into the filterArgs to filter out
                for (int i = 0; i < textInput.length; i++) {
                    if (textInput.length == 1) {
                        filterArgs[i] = "%" + textInput[i] + "%";
                    } else {
                        filter += "OR BODY LIKE ?";
                        filterArgs[i] = "%" + textInput[i] + "%";
                    }
                }

                // Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSmsText.setText(smsBody);


            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                // Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"18050674@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                String statement = tvSmsText.getText().toString();
                email.putExtra(Intent.EXTRA_TEXT, statement);
                // This MIME type indicates email
                email.setType("message/rfc822");
                // createChooser shows user a list of app that can handle
                // this MIME type, which is, email
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        return view;
    }
}