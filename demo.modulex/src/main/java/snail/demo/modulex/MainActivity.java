package snail.demo.modulex;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheney.lam.sdk.ModuleRequest;
import com.cheney.lam.sdk.ModuleResponse;
import com.cheney.lam.sdk.ModuleRouter;

import snail.demoframework.BaseFragment;

/**
 * Created by cheney on 17/3/1.
 */

public class MainActivity extends Activity {

    TextView textView;
    ViewGroup container;
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        root = (LinearLayout) findViewById(R.id.linearLayout);
        container = (ViewGroup) findViewById(R.id.container);
        textView = (TextView) findViewById(R.id.txt_result);

        findViewById(R.id.btn_test_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                ModuleResponse response = ModuleRequest.acquire()
                        .module("moduley")
                        .api("package")
                        .param("who", 1688)
                        .param("where", "qianniu")
                        .invoke();
                Log.w("Time", "cost : " + (System.currentTimeMillis()-time));
                if (response.isSuccess()) {
                    textView.setText((String) response.getResult());
                }
            }
        });

        findViewById(R.id.btn_test_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("param", "request from xmodule");
                BaseFragment fragment = (BaseFragment) ModuleRouter.instance().getFragment("moduley", "YFragment", bundle);
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.container, fragment).commit();
                }
            }
        });

        findViewById(R.id.btn_test_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = ModuleRouter.instance().getView(MainActivity.this, "moduley", "text", null);
                if (view != null) {
                    root.addView(view);
                }
            }
        });
    }
}
