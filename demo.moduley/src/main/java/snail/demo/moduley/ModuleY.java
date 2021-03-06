package snail.demo.moduley;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cheney.lam.annotation.Module;
import com.cheney.lam.sdk.AbsModule;

import snail.demoframework.BaseFragment;

/**
 * Created by cheney on 17/3/1.
 */
@Module("moduley")
public class ModuleY extends AbsModule<BaseFragment> {
    private static String version = "1.0";
    private static String name = "moduley";


    @Override
    public void init() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersionName() {
        return version;
    }

    @Override
    public boolean isReady() {
        return true;
    }


    @Override
    public View getView(Context context, String path, Bundle params) {
        if ("text".equals(path)) {
            TextView view = new TextView(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            view.setText("我是来自Y模块的View");
            view.setPadding(20,20,20,20);
            view.setGravity(Gravity.CENTER);
            return view;
        }
        return null;
    }

}
