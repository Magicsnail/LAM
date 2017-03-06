package snail.demo.moduley;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cheney.lam.annotation.ExportFragment;

import snail.demoframework.BaseFragment;

/**
 * Created by cheney on 17/3/1.
 */
@ExportFragment("YFragment")
public class YFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.y_frag_layout, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.text);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = getArguments().getString("param");
                textView.setText(s);
            }
        });
        return view;
    }
}
