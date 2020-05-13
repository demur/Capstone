# Jersey City On-Street Permit Parking Helper
[Udacity Grow with Google](https://www.udacity.com/grow-with-google) [Android Developer Nanodegree Program](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801)

## Overview
This application helps to eliminate frustration and cut down time wasted on search for parking spots in downtown Jersey City, NJ. Hunt for an on-street spot can be quite a disappointing challenge equally for residents and visitors. Remembering the location of the vehicle and keeping in mind allowed parking time can be rather exhausting too. The app provides easy and comprehensive way to accomplish these tasks and avoid getting tickets for prohibited parking.

## Screenshots
<p align="center">
    <img src="screenshots/Screenshot_1.png?raw=true" width=275 />
    <img src="screenshots/Demo.gif?raw=true" width=275 />
</p>

## Project Requirements

### Common
- [x] App conforms to common standards found in the [Android Nanodegree General Project Guidelines](http://udacity.github.io/android-nanodegree-guidelines/core.html)
- [x] App is written solely in the Java Programming Language
- [x] App utilizes stable release versions of all libraries, Gradle, and Android Studio

### Core Platform Development
- [x] App integrates third-party libraries
- [x] App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash
- [x] App includes support for accessibility. That includes content descriptions, navigation using a D-pad
- [x] App keeps all strings in a `strings.xml` file and enables RTL layout switching on all layouts
- [x] App provides a widget to provide relevant information to the user on the home screen

### Google Play Services
- [x] App integrates two or more Google services. Google service integrations can be a part of Google Play Services or Firebase
- [x] Each service imported in the `build.gradle` is used in the app
- [x] App customizes the user’s experience by using the device's location
- [x] App creates only one analytics instance
- [x] Map provides relevant information to the user

### Material Design
- [x] App theme extends `AppCompat`
- [x] App uses an app bar and associated toolbars
- [x] App uses standard and simple transitions between activities

### Building
- [x] App builds from a clean repository checkout with no additional configuration
- [x] App builds and deploys using the `installRelease` `Gradle` task
- [x] All app dependencies are managed by `Gradle`

### Data Persistence
- [x] App stores data using Cloud Firestore
- [x] LiveData and ViewModel are used to fetch and process all data changes in Cloud Firestore
- [x] No unnecessary calls to Cloud Firestore are made

## Libraries
* [AndroidX](https://developer.android.com/jetpack/androidx/) previously known as *'Android support Library'*
    * [ConstraintLayout](https://developer.android.com/training/constraint-layout) allows to create large and complex layouts
    * [DataBinding](https://developer.android.com/topic/libraries/data-binding/) allows to bind UI components in layouts to data sources in app
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) the class designed to store and manage UI-related data in a lifecycle conscious way
    * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) an observable data holder class
* [Firebase](https://firebase.google.com/docs/android/setup) a Backend-as-a-Service app-development platform on Google Cloud Platform
    * [Cloud Firestore](https://firebase.google.com/docs/firestore) flexible, scalable NoSQL cloud database to store and sync data for client- and server-side development
    * [Crashlytics](https://firebase.google.com/docs/crashlytics) clear, actionable insight into app issues with this powerful crash reporting solution
    * [Analytics](https://firebase.google.com/docs/analytics) an app measurement solution that provides insight on app usage and user engagement
    * [Authentication](https://firebase.google.com/docs/auth) easy-to-use services to authenticate users to an app
    * [Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) a messaging solution that lets reliably send notifications at no cost
* [Google Maps](https://developers.google.com/maps/documentation/android-sdk) adds maps based on Google Maps data to an application
* [Maps SDK Utility Library](https://github.com/googlemaps/android-maps-utils) contains various utilities that are useful for applications that are using Google Maps API
* [SublimePicker](https://github.com/vikramkakkar/SublimePicker) a material-styled view for picking of date and time at ease
* [ScrollableNumberPicker](https://github.com/michaelmuenzer/ScrollableNumberPicker) a user-friendly numerical input interface