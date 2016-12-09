package pers.hal42.droid;

import pers.hal42.android.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DroidSampler extends GriddedActivity {
    private void myClick(View view) {
        myView.format("You are opressing me!");
    }

    ViewFormatter myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new ViewFormatter(add(TextView.class));
        final Button button = add(Button.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClick(view);
            }
        });

    }
}
