package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
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
public class FragmentNum extends Fragment {

    public FragmentNum() {
        // Required empty public constructor
    }


    Button btnRetrieveNum;
    EditText etNum;
    TextView tvSmsNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_num, container, false);

        tvSmsNum = view.findViewById(R.id.tvDisplayNum);
        etNum = view.findViewById(R.id.etNum);
        btnRetrieveNum = view.findViewById(R.id.btnGetSms);

        btnRetrieveNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                // Create all messages URI
                Uri uri = Uri.parse("content://sms");

                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                // The filter String
                String filter = "body LIKE ?";
                String[] textInput = etNum.getText().toString().split(" ");
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


                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillies = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillies);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSmsNum.setText(smsBody);
            }
        });
        return view;
    }
}