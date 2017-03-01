package snail.demo.modulex;

import com.cheney.lam.annotation.ExportAction;
import com.cheney.lam.sdk.IAction;
import com.cheney.lam.sdk.ModuleResponse;

import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */
@ExportAction("xx,xx2")
public class XAction implements IAction {
    @Override
    public ModuleResponse invoke(int reqId, String api, Map<String, Object> param) {
        return null;
    }
}
