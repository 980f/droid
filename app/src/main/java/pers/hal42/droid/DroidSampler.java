package pers.hal42.droid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pers.hal42.android.GriddedActivity;
import pers.hal42.android.ViewFormatter;

public class DroidSampler extends GriddedActivity {
    ViewFormatter myView;

    private void myClick(View view) {
        myView.format("\nYou are oppressing me!");
    }

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
        button.setText("Press me");

    }
}
