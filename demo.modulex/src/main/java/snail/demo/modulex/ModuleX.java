package snail.demo.modulex;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.cheney.lam.annotation.Module;
import com.cheney.lam.sdk.IModule;
import com.cheney.lam.sdk.request.ActionResponse;
import com.cheney.lam.sdk.finder.ModuleInject;

import java.util.Map;

import snail.demoframework.BaseFragment;

/**
 * Created by cheney on 17/3/1.
 */
@Module("modulex")
public class ModuleX implements IModule<BaseFragment> {
    private static String version = "1.0";
    private static String name = "modulex";

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
    public ActionResponse invokeApi(int reqId, String api, Map<String, Object> param) {
        return ModuleInject.invokeAction(this, reqId, api, param);
    }

    @Override
    public BaseFragment getFragment(String path, Bundle params) {
        return (BaseFragment) ModuleInject.finder(this, path, params);
    }

    @Override
    public View getView(Context context, String path, Bundle params) {
        return null;
    }

}
