package com.sink.hooklua;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class SinkNative {
    static {
        System.load("/data/local/tmp/lua_dump.so");
    }
    public void dumpAll(Object L, String packageName , long ptr){
        try {
            File parentFile = new File("/mnt/sdcard/Sink/FuckLua/");
            if (!parentFile.isDirectory()){
                parentFile.mkdirs();
            }
            Class<?> stateClass = L.getClass();
            File file = new File( "/data/data/" + packageName + "/files/");
            File[] luaFiles = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".lua");
                }
            });
            for (File lua : luaFiles){
                Method loadFileMethod = stateClass.getDeclaredMethod("LloadFile", String.class);
                Method setTopMethod = stateClass.getDeclaredMethod("setTop", int.class);
                setTopMethod.invoke(L, 0);
                loadFileMethod.invoke(L, lua.getAbsolutePath());
                dump(ptr, "/mnt/sdcard/Sink/FuckLua/" + lua.getName());
            }
        } catch (Exception e) {
            XposedBridge.log(e);
            e.printStackTrace();
        }
    }
    public native void dump(long ptr, String filename);
}
