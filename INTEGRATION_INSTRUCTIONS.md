# Embedded Social SDK Integration
## Adding Embedded Social to the project
To add the Embedded Social SDK to an Android project, update the build.gradle of your application module to include the SDK.  The EmbeddedSocial SDK supports a minSdkVerion of 15 and higher.  Currently, the project only has Maven artifacts for pre-release purposes on Bintray.
1.	Update your repositories to include the Bintray endpoint: http://dl.bintray.com/acrowntest/test 
```xml
repositories {
    jcenter()
    maven {
        // for testlib
        url  "http://dl.bintray.com/acrowntest/test"
    }
}
```
2.	Update the dependencies to include the SDK library currently called “sdk” as `com.acrowntest.test:sdk:<version>:release@aar` with transitive dependencies enabled
```xml
dependencies {
  // application dependencies
  ...
  
  // Embedded Social
    compile('com.acrowntest.test:sdk:0.0.23:release@aar') {
        transitive = true;
    }
}
```
##	Configuration File
The Embedded Social SDK requires the application to have a JSON configuration file. This file should be kept as a raw resource of your application (stored in res/raw folder). This file is used during SDK initialization at runtime.
Here is an example of such a file:
```xml
{
	"application" : {
	  	"serverUrl" : "https://ppe.embeddedsocial.microsoft.com/",
	  	"appKey" : "<appKey>",
		"numberOfCommentsToShow" : 20,
		"numberOfRepliesToShow" : 20,
		"searchEnabled" : "true",
		"showGalleryView" : "true"
	},
	"socialNetworks" : {
		"facebook" : {
			"loginEnabled" : "true",
			"clientId" : "<clientId>"
		},
		"google" : {
			"loginEnabled" : "true",
			"clientId" : "<clientId>"
		},
		"microsoft" : {
			"loginEnabled" : "true",
			"clientId" : "<clientId>"
		},
		"twitter" : {
			"loginEnabled" : "false"
		}
	},
	"theme" : {
		"accentColor" : "FF4CAF50",
		"name" : "light"
	}
}
```
The configuration file consists of the following sections:
* application – contains information about your application such as application handle, application token, number of comments and replies to show
* socialNetworks – contains information describing which social networks are enabled as identity providers to support login, and holds application id for corresponding social networks. At leasts one social network must be enabled.  Supported social networks are:
  *	Facebook
  *	Google
  *	Microsoft
*	theme – contains application theme parameters such as accent color.
##	Application Manifest
The Embedded Social SDK requires that the following permissions be granted in your applications manifest.
```
android.permission.INTERNET
android.permission.READ_EXTERNAL_STORAGE
android.permission.WRITE_EXTERNAL_STORAGE
android.permission.CAMERA
```

The internet permissions allows the SDK to interact with our server APIs.  The others enable users to take and post pictures.

##	Search
In order to use search suggestions in Embedded Social SDK, you have to perform the following steps:
1.	Add a content provider subclassed from search suggestion content provider supplied by the SDK: `AbstractEmbeddedSocialSearchSuggestionProvider`. Implementation of this class is not needed, you just have to subclass it.
2.	Register this content provider in the manifest and add unique provider authority consisting of app  package name and a postfix “.embeddedsocial_searchprovider” (e.g. “com.microsoft.testapp.embeddedsocial_searchprovider” for an app with “com.microsoft.testapp” package).
3.	Add XML metadata file called “embeddedsocial_searchable.xml” to res/xml folder. XML file contents can be found in the app module on the SDK GitHub.
4.	Update the data in XML metadata file to reference the unique provider authority mentioned above

##	Using the SDK
This section describes how the SDK itself is used in 3rd party applications.
###	General SDK initialization
SDK initialization must be performed in your application’s class onCreate() method. Use the `EmbeddedSocial.init()` method of the SDK to pass the reference to your raw JSON configuration resource and the reference to your application instance. The initialization method validates the configuration before applying, so if the configuration is not valid, a runtime exception will be thrown.
Here is how your application’s class might look:
```java
@Override
public void onCreate() {
  super.onCreate();
  EmbeddedSocial.init(this, R.raw.embedded_social_config);
}
```
###	Embedding Social features
The SDK provides a number of methods to embed social functionality into your existing activities.  These methods can be called using `EmbeddedSocial.<method>` from within your application.  In order to use SDK fragments in your application’s activities, you currently need to have that activity extend EmbeddedSocialActivity. 
###	Navigation Drawer
There are a few options for handling navigation in the application.  You can create your own navigation drawer and integrate social items as necessary or you can use the included Navigation Drawer. 
####	SDK navigation initialization
To merge the existing Embedded Social SDK navigation capabilities with your existing navigation drawer, you have to call the `EmbeddedSocial.initDrawer()` method in your application’s onCreate() – after you call EmbeddedSocial.init(). The most important parameters to pass to this method are your navigation fragment factory and drawer display mode. Currently 2 drawer display modes are supported – switcher mode and tabs mode.
If you don’t call EmbeddedSocial.initDrawer() method, your side menu won’t be shown and only Embedded Social SDK menu items will be visible in the navigation drawer.
####	Navigation fragment factory
The navigation fragment factory produces fragments that are integrated into the Embedded Social navigation drawer and are shown along with Embedded Social navigation items.
```
<<interface>> INavigationDrawerFactory
+createMenuFragment(): Fragment
```
The interface has a single method that is called when the Embedded Social SDK shows its navigation drawer. This method should produce the navigation fragment that later is used by Embedded Social navigation drawer.
####	Using SDK navigation
Any class extending the EmbeddedSocialActivity class will use the SDK’s navigation drawer. Here is how Embedded Social navigation drawer looks:
 
The “Studio” section on this picture is a user-supplied navigation fragment integrated into the Embedded Social navigation drawer. Here is the look of the navigation fragment itself (from a test application):
 
###	SDK debug logs
The SDK has an independent logging system that is activated if build configuration is set to debug. The logs are written to logcat and to a file located at default external storage. The folder for those logs is called “debug_logs”, and log file name corresponds to the package name of the 3rd party application with “.txt” extension.
