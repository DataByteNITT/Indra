package com.databyte.indra;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by satra_000 on 01-03-2018.
 */

public class DFragment extends DialogFragment{

    EditText t;
    Button b;
    URLDialogInterface itterface;


    public void Init(URLDialogInterface itterface) {
        this.itterface = itterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.d_fragment,container,false);
        getDialog().setTitle("Enter Url");
        t = v.findViewById(R.id.d_frag_edit);
        b = v.findViewById(R.id.d_frag_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(t.getText().toString())){
                    itterface.onClickedButton(t.getText().toString());
                }
                getDialog().dismiss();
            }
        });
        return v;
    }
}
