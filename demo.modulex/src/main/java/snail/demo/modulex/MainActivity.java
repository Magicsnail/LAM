package snail.demo.modulex;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheney.lam.sdk.request.ActionRequest;
import com.cheney.lam.sdk.request.FragmentRequest;
import com.cheney.lam.sdk.request.IActionCallback;
import com.cheney.lam.sdk.request.ActionResponse;
import com.cheney.lam.sdk.ModuleRouter;

import java.util.HashMap;

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

        HashMap<String, Boolean> map = new HashMap<>();
        Boolean b = new Boolean(true);
        map.put("a", b);
        map.put("b", b);
        map.put("c", b);
        root = (LinearLayout) findViewById(R.id.linearLayout);
        container = (ViewGroup) findViewById(R.id.container);
        textView = (TextView) findViewById(R.id.txt_result);

        findViewById(R.id.btn_test_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                ActionResponse response = ActionRequest.acquire()
                        .module("moduley")
                        .action("package")
                        .param("who", 1688)
                        .param("where", "qianniu")
                        .invoke();
                Log.d("Time", "cost : " + (System.currentTimeMillis()-time));
                if (response.isSuccess()) {
                    textView.setText((String) response.getResult());
                }
            }
        });

        findViewById(R.id.btn_test_asyncapi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionRequest.acquire()
                        .module("moduley")
                        .action("asyncapi")
                        .param("good", true)
                        .async(new IActionCallback() {
                            @Override
                            public void actionResponse(ActionResponse response) {
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText("收到异步接口执行结果.");
                                    }
                                });
                                switch (response.getCode()) {
                                    case ActionResponse.ERR_MODULE_NOT_FOUND:
                                        Log.i("test", "module not exit");
                                        break;
                                    case ActionResponse.ERR_API_NOT_SUPPORT:
                                        Log.i("test", "api not support.");
                                        break;
                                    default:
                                        Log.i("test", "get asyncapi responose");
                                        break;
                                }
                            }
                        })
                        .invoke();
            }
        });

        findViewById(R.id.btn_test_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("param", "request from xmodule");
//                BaseFragment fragment = (BaseFragment) ModuleRouter.instance().getFragment("moduley", "YFragment", bundle);
                BaseFragment fragment = (BaseFragment)FragmentRequest.acquire()
                        .module("moduley")
                        .path("YFragment")
                        .param("param", "request from xmodule")
                        .invoke();
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
