To run this app on device must use adb, following next steps:

#First you must to install the app on device, there are three ways to do that

-Install the app on device with Android Studio.
-Build the app with Android Studio and install whit adb command.
-Build the project in debug mode using gradlew and install it with adb command.

To build with gradlew (installed gradlew require), go to project directory and
On Windows
> gradlew assembleDebug

On Mac OS and Linux platforms
$ chmod +x gradlew
$ ./gradlew assembleDebug

After you build the project, the output APK for the app module is located in app/build/outputs/apk/

To install whit adb
adb -d install -r "<your-project-directory>/app/build/outputs/apk/app-debug.apk"

#Then once installed, execute the next command line to run the application in your device:

adb -d shell am start -n "com.tn.webqawall/com.tn.webqawall.WebFullscreenActivity" -a
android.intent.action.MAIN --es URL_FROM_INTENT <url_to_lunch>


-NOTE: If you have different devices connected you must define the device id as following

adb -s <p_device_id> shell am start -n "com.tn.webqawall/com.tn.webqawall.WebFullscreenActivity" -a
android.intent.action.MAIN --es URL_FROM_INTENT <url>



