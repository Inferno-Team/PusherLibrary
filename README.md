
## Badges
[![Static Badge](https://img.shields.io/badge/notifyMe-.com-blue)](https://github.com/Inferno-Team/custom_pusher)

![JitPack](https://img.shields.io/jitpack/version/com.github.Inferno-Team/PusherLibrary)


[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)


# Custom Pusher SDK

The Custom Pusher SDK is an Android Java library designed to simplify integration with our hosted notification system. With this SDK, developers can easily send and manage push notifications from their Android applications.

## Installation

You can integrate the Custom Pusher SDK into your Android project via JitPack.

Add the JitPack repository to your project's settings.gradle or settings.gradle.kts :


```bash
  maven { url 'https://jitpack.io' }
```
Add the SDK dependency to your app module's build.gradle file:

```bash
  implementation 'com.github.Inferno-Team:PusherLibrary:0.9.1-beta-1'
```
## Usage/Examples
To start using the Custom Pusher SDK, you need to add this to you AndroidManifest.xml File
under application tag :

#### All This values can be optanined from your dashboard.
```xml
        <meta-data
            android:name="com.custom_pusher.PROJECT_ID_VALUE"
            android:value="YOUR_PROJET_ID" />
```
This represent your project id

```xml
        <meta-data
            android:name="com.custom_pusher.KEY_VALUE"
            android:value="YOUR_KEY_VALUE" />
```
This represent your account api key

```xml
        <meta-data
            android:name="com.custom_pusher.CLUSTER_VALUE"
            android:value="YOUR_CLUSTER_VALUE" />
```

This represent your websocket cluster


```xml
        <meta-data
            android:name="com.custom_pusher.HOST_VALUE"
            android:value="192.168.1.8" />
```

This represent OUR host must be as same the value
```xml
        <meta-data
            android:name="com.custom_pusher.PORT_VALUE"
            android:value="6001" />
```

This represent OUR port must be as same the value

```xml
        <meta-data
            android:name="com.custom_pusher.USE_TLS_VALUE"
            android:value="false" />
```

This represent OUR TLS must be as same the value

#### After that you need to add this service inside your application tag


```xml
       <service
            android:name="cloud.inferno_team.custom_pusher.services.PusherService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="specialUse">
                <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                    android:value="explanation_for_special_use"/>
        </service>
```

#### And you need to add this four permissions

```xml
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

and you are responsible of handling the request of notification permission request in runtime

#### After that one last thing to run this service you need to call it
you can use any activity or the application class

#### Example :
```java
        Intent intent = new Intent(this, PusherService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);
```

And that is it.
Now a service will be running in background/forground hundling incoming notifications.
## Customize Notification Style
you can customize notification style
by extending PusherService class and overriding createEventNotification method
this method is response of what happen when new Notification detected so you need to handle the whole process if you override it.

## Authors

- [@Inferno-Team](https://www.github.com/Inferno-Team)

