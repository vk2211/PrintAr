

#ifndef __EASYAR_SAMPLE_COMMON_H__
#define __EASYAR_SAMPLE_COMMON_H__

#define _STR(str)                                   #str
#define STR(str)                                    _STR(str)

#define JNI_DATA_PACKAGE                            com/yipai/printar/bean/

#define JNI_CLASS(packet, cls)                      STR(packet) STR(cls)
#define JNI_CLASS_PARAM(packet, cls)                STR(L) JNI_CLASS(packet, cls) ";"

bool jstringToString(JNIEnv* env, jstring jstr, std::string &stlStr);

#endif
