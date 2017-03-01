package snail.demo.moduley;

import com.cheney.lam.annotation.ExportAction;
import com.cheney.lam.sdk.IAction;
import com.cheney.lam.sdk.ModuleResponse;

import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */

@ExportAction("package,add")
public class YAction implements IAction {

    @Override
    public ModuleResponse invoke(int reqId, String api, Map<String, Object> param) {
        ModuleResponse response = new ModuleResponse();
        if ("package".equals(api)) {
            StringBuilder builder = new StringBuilder("call action package:\n");
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            response.setCode(ModuleResponse.SUCCESS).setResult(builder.toString());
        } else if ("add".equals(api)) {
            Integer left = (Integer) param.get("left");
            Integer right = (Integer) param.get("right");
            response.setCode(ModuleResponse.SUCCESS).setResult(new Integer(left+right));
        } else {
            response.setCode(ModuleResponse.ERR_API_NOT_SUPPORT);
        }
        return response;
    }
}
