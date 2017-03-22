#include <jni.h>
#include <string>
#include "Log.h"
#include "Common.h"


bool jstringToString(JNIEnv *env, jstring jstr, std::string &stlStr) {
	if (jstr == NULL) {
		stlStr.assign("");
		return true;
	}
	const char *str = env->GetStringUTFChars(jstr, NULL);
	if (str == NULL) {
		LOGI("GetStringUTFChars fail!");
		return false; /* OutOfMemoryError already thrown */
	}
	stlStr.assign(str);
	env->ReleaseStringUTFChars(jstr, str);
	return true;
}
