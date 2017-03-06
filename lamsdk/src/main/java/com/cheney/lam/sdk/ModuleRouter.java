package com.cheney.lam.sdk;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.cheney.lam.sdk.request.AbsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cheney on 17/3/1.
 */
public class ModuleRouter {

    private ModuleConfig config;

    private ConcurrentHashMap<String, IModule> moduleMap;

    private static ModuleRouter router;

    private ModuleRouter() {
        moduleMap = new ConcurrentHashMap<>(10);
    }

    public static ModuleRouter instance() {
        if (router == null) {
            synchronized (ModuleRouter.class) {
                if (router == null) {
                    router = new ModuleRouter();
                }
            }
        }
        return router;
    }

    public void setConfig(ModuleConfig config) {
        this.config = config;
    }

    public ModuleConfig getConfig() {
        if (config == null) {
            config = ModuleConfig.builder().build();
        }
        return config;
    }

    private void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public boolean initialize(Context context, String configFile) {
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader bufReader = null;
        long t = System.nanoTime();
        try {
            in = context.getAssets().open(configFile);
            if (in == null) {
                ModuleLogger.e(configFile + " open failed, it must placed in asserts folder.");
                return false;
            }
            inReader = new InputStreamReader(in, "UTF-8");
            if (inReader == null) {
                ModuleLogger.e(configFile + " open failed.");
                return false;
            }
            bufReader = new BufferedReader(inReader);
            if (bufReader == null) {
                ModuleLogger.e(configFile + " open failed.");
                return false;
            }
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = bufReader.readLine()) != null){
                builder.append(line);
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("modules");
            final String[] names = new String[jsonArray.length()];
            final String[] clazz = new String[jsonArray.length()];
            int size = 0;
            for (int i=0; i<jsonArray.length(); ++i) {
                JSONObject jm = jsonArray.optJSONObject(i);
                if (jm == null) {
                    continue;
                }
                String name = jm.optString("name");
                String pkg = jm.optString("package");
                String cls = jm.optString("class");
                boolean realTime = jm.optBoolean("realtime", false);
                if (realTime) {
                    register(name, pkg + "." + cls);
                } else {
                    names[size] = name;
                    clazz[size] = pkg + "." + cls;
                    ++size;
                }
            }

            if (size > 0) {
                final int msize = size;
                getConfig().getTaskExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0; i<msize; ++i) {
                            register(names[i], clazz[i]);
                        }
                    }
                });
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            safeClose(bufReader);
            safeClose(inReader);
            safeClose(in);
            ModuleLogger.i("initialize cost: " + (System.nanoTime()-t)/1000);
        }
        return false;
    }

    private boolean register(String name, String classPath) {
        long t = System.nanoTime();
        try {
            Class<IModule> xClass = (Class<IModule>) Class.forName(classPath);
            IModule xModule = xClass.newInstance();
            register(xModule);
            ModuleLogger.d("register done: " + name + ", cost: " + (System.nanoTime()-t)/1000);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ModuleLogger.d(name + " register failed: " + classPath);
        return false;
    }

    public void register(IModule module) {
        if (module != null) {
            moduleMap.put(module.getName(), module);
        }
    }

    private boolean isModuleReady(String module) {
        IModule m = moduleMap.get(module);
        return m != null && m.isReady();
    }

    public IModule getReadiedModule(String module) {
        IModule m = moduleMap.get(module);
        if (m != null && m.isReady()) {
            return m;
        }
        ModuleLogger.i(module + " not exit or readied");
        return null;
    }
//
//    private Intent getActivityIntent(Context context, String module, String path, Bundle params) {
//        if (!isModuleReady(module)) {
//            return null;
//        }
//        Uri uri = Uri.parse("router://" + module + path);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        if (params != null && params.size() > 0) {
//            intent.putExtras(params);
//        }
//        if (context instanceof Application) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        return intent;
//    }
//
//    public boolean startActivity(Context context, String module, String path, Bundle params) {
//        Intent intent = getActivityIntent(context, module, path, params);
//        if (intent != null) {
//            try {
//                context.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                Log.e(module, e.getLocalizedMessage());
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public boolean startActivityForResult(Activity context, String module, String path, Bundle params, int requestCode) {
//        Intent intent = getActivityIntent(context, module, path, params);
//        if (intent != null) {
//            try {
//                context.startActivityForResult(intent, requestCode);
//            } catch (ActivityNotFoundException e) {
//                Log.e(module, e.getLocalizedMessage());
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public Fragment getFragment(String module, String path, Bundle params) {
//        IModule iModule = getReadiedModule(module);
//        if (iModule != null) {
//            return iModule.getFragment(path, params);
//        }
//        return null;
//    }

    public View getView(Context context, String module, String path, Bundle params) {
        IModule iModule = getReadiedModule(module);
        if (iModule != null) {
            return iModule.getView(context, path, params);
        }
        return null;
    }

    public IModule router(AbsRequest request) {
        if (request == null) {
            return null;
        }
        IModule module = getReadiedModule(request.getModule());
        return module;
    }

}
