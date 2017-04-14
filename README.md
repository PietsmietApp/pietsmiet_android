# PietSmiet-App für Android [![version](https://img.shields.io/github/release/l3d00m/pietsmiet_android.svg)](https://github.com/l3d00m/pietsmiet_android/releases/latest) [![license](https://img.shields.io/github/license/l3d00m/pietsmiet_android.svg)]()

`develop`: [![Build Status](https://travis-ci.org/l3d00m/pietsmiet_android.svg?branch=develop)](https://travis-ci.org/l3d00m/pietsmiet_android)  
`release`: [![Build Status](https://travis-ci.org/l3d00m/pietsmiet_android.svg?branch=release)](https://travis-ci.org/l3d00m/pietsmiet_android)  

<a href="https://play.google.com/store/apps/details?id=de.pscom.pietsmiet">
    <img alt="Jetzt bei Google Play"
        height="80"
        src="https://play.google.com/intl/de_de/badges/images/generic/de_badge_web_generic.png" />

## Features

* A beautiful and intuitive interface, based on material design.

* Last tweets and facebook posts from team PietSmiet

* Latest Uploadplan & Pietcast from pietsmiet.de

* Notifications for new Uploadplan, new PietCast, new pietmiet.de videos & pietsmiet.de News

## Information & goal

This app is focused on performance and clean code. This is achieved by using very helpful libraries such as RxJava & Retrofit. I'm trying to use a very basic Model-View-Presenter pattern for developing this.  
SQLite is used to cache objects. The app communicates with the Twitter & Facebook API for getting it's content. It also communicates with a Firebase Database where the Uploadplan & News is stored. The Pietcast is fetched via RSS.  
The RSS fetching and HTML scraping of pietsmiet.de and storing in Firebase is done with python ([code here](https://github.com/l3d00m/pietsmiet_xposter)).

*[Here](https://github.com/l3d00m/pietsmiet_android/blob/develop/ressources/ps_app_overview.png) is a professional overview over the classes made with paint ;)*

## Git Workflow
**[`release`](https://github.com/l3d00m/pietsmiet_android/tree/release) branch:** `develop` is merged into this branch when a new version is released. Travis automatically uploads a signed APK to Github releases when a new tag is pushed to this branch  
**[`develop`](https://github.com/l3d00m/pietsmiet_android/tree/develop) branch:** mostly stable and buildable code, there may be exceptions  
**feature branches:** When a feature requires a lot of work, it has a separate branch that is merged into `develop` as soon as the feature is complete  
