package snail.demo.moduley;

import com.cheney.lam.annotation.ExportAction;
import com.cheney.lam.sdk.IAction;
import com.cheney.lam.sdk.request.ActionRequest;
import com.cheney.lam.sdk.request.ActionResponse;

import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */
@ExportAction("package,add,asyncapi")
public class YAction implements IAction {

    @Override
    public ActionResponse invoke(final int reqId, String api, Map<String, Object> param) {
        ActionResponse response = ActionResponse.acquire();
        if ("package".equals(api)) {
            StringBuilder builder = new StringBuilder("call path package:\n");
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            response.success().putResult(builder.toString());
        } else if ("add".equals(api)) {
            Integer left = (Integer) param.get("left");
            Integer right = (Integer) param.get("right");
            response.success().putResult(new Integer(left + right));
        } else if ("asyncapi".equals(api)) {
            response.responseAsync();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        ActionRequest.response(reqId, ActionResponse.acquire().success());
                    } catch (Exception e) {

                    }
                }
            }).start();
        } else {
            response.errNotSupport();
        }
        return response;
    }
}
