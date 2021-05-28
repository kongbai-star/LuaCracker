//
// Created by DELL on 2021/2/8.
//

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <lapi.h>
#include <lua.h>
#include <lauxlib.h>


char *jstring2String(JNIEnv *env, jstring jstr);
static int writer(lua_State *L, const void *b, size_t size, void *B) {
    FILE * file = (FILE* )B;
    fwrite(b, sizeof(b[0]), size / sizeof(b[0]), file);
    return 0;
}
JNIEXPORT void JNICALL
 Java_com_sink_hooklua_SinkNative_dump(JNIEnv *env, jobject thiz,jlong ptr, jstring filename) {
    lua_State *L = (lua_State *) ptr;
    luaL_checktype(L, -1, LUA_TFUNCTION);
    FILE* file = fopen(jstring2String(env, filename), "wb");
    if (file){
        lua_dump(L, writer, file, 0);
        fclose(file);
    }
    lua_pop(L, 1);

}

char *jstring2String(JNIEnv *env, jstring jstr)
{
    char* rtn = NULL;
    jstring strencode = (*env)->NewStringUTF(env, "utf-8");
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    jmethodID m_getByteID = (*env)->GetMethodID(env, strClass, "getBytes",
                                                "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, m_getByteID, strencode);
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = malloc(alen+1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
    return rtn;
}