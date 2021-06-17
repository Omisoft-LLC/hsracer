#OSMDroid
-keep class microsoft.mappoint.** {*;}
-keep class org.osmdroid.** {*;}
-keep class org.metalev.multitouch.controller.** {*;}
# https://github.com/osmdroid/osmdroid/issues/633 SDK version 26
-dontwarn org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck