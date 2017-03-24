# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

include_directories( ${CMAKE_SOURCE_DIR}/src/main/cpp/include )
set(LIBS_DIR ${CMAKE_SOURCE_DIR}/libs)

add_library(EasyAR SHARED IMPORTED )
set_target_properties( # Specifies the target library.
						EasyAR

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       "${LIBS_DIR}/${ANDROID_ABI}/libEasyAR.so" )

add_library( # Sets the name of the library.
             ar

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/arvideo.cc
             src/main/cpp/renderer.cc
             src/main/cpp/ar.cc
             src/main/cpp/jniar.cc
             src/main/cpp/Common.cpp
             src/main/cpp/VideoData.cpp
          )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log
           )

find_library( # Sets the name of the path variable.
              gl

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              GLESv2
           )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
						ar android log

                       # Links the target library to the log library
                       # included in the NDK.
                       EasyAR
                       ${log-lib}
                       ${gl}
                    )
