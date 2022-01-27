package com.me.cusviewgroup;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class FunctionViewBuilder {

    public static View getFunctionView(Activity context, String name, String description, View.OnClickListener listener) {
        View functionView = context.getLayoutInflater().inflate(R.layout.layout_function_item, null);
        ((TextView) functionView.findViewById(R.id.tv_name)).setText(name);
        ((TextView) functionView.findViewById(R.id.tv_description)).setText(description);
        functionView.setOnClickListener(listener);
        return functionView;
    }
}