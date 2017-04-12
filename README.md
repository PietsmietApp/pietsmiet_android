# PietSmiet-App für Android

`develop`: [![Build Status](https://travis-ci.org/l3d00m/pietsmiet_android.svg?branch=develop)](https://travis-ci.org/l3d00m/pietsmiet_android)  
`release`: [![Build Status](https://travis-ci.org/l3d00m/pietsmiet_android.svg?branch=release)](https://travis-ci.org/l3d00m/pietsmiet_android)  

Still under active development. It isn't released to the play store yet.

## Features

* A beautiful and intuitive interface, based on material design.

* Last tweets and facebook posts from team PietSmiet

* Latest Uploadplan & Pietcast from pietsmiet.de

* Notifications for new Uploadplan, new PietCast and new YouTube videos

## Information & goal

This app is focused on performance and clean code. This is achieved by using very helpful libraries such as RxJava & Retrofit. I'm trying to use a very basic Model-View-Presenter pattern for developing this.  
SQLite is used to cache objects. The app communicates with the Twitter & Facebook API for getting it's content. It also communicates with a Firebase Database where the Uploadplan & Pietcast is stored.  
The RSS fetching and HTML scraping of pietsmiet.de and storing in Firebase is done with python (code in the `backend` folder).

[Here](https://github.com/l3d00m/pietsmiet_android/blob/develop/ressources/ps_app_overview.png) is a professional overview over the classes made with paint ;)

## Git Workflow
**[`release`](https://github.com/l3d00m/pietsmiet_android/tree/release) branch:** `develop` is merged into this branch when a new version is released  
**[`develop`](https://github.com/l3d00m/pietsmiet_android/tree/develop) branch:** mostly stable and buildable code, there may be exceptions  
**feature branches:** When a feature requires a lot of work, it has a separate branch that is merged (via a PR) into `develop` as soon as the feature is complete  
