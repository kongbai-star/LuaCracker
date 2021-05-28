package com.sink.hooklua;

import android.app.Activity;
import android.os.Bundle;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    int i = 0;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                ClassLoader classLoader = activity.getClassLoader();
               try {
                   Class<?> state = classLoader.loadClass("com.luajava.LuaState");
                   XposedHelpers.findAndHookMethod("com.luajava.LuaState",
                           classLoader, "_LloadFile", long.class, String.class, new XC_MethodHook() {
                               @Override
                               protected void beforeHookedMethod(MethodHookParam param2) throws Throwable {
                                   if (i == 0){
                                       i++;
                                       new SinkNative().dumpAll(param2.thisObject, lpparam.packageName , (long) param2.args[0]);
                                   }else {
                                       XposedBridge.log("dumping：" + param2.args[1]);
                                   }
                                   super.beforeHookedMethod(param2);
                               }
                           });
               }catch (Exception e){
                   XposedBridge.log(lpparam.packageName + "貌似不是lua写的");
               }
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
