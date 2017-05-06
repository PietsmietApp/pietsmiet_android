# PietSmiet-App für Android [![version](https://img.shields.io/github/release/l3d00m/pietsmiet_android.svg)](https://github.com/l3d00m/pietsmiet_android/releases/latest)

`develop`: [![Build Status](https://travis-ci.org/l3d00m/pietsmiet_android.svg?branch=develop)](https://travis-ci.org/l3d00m/pietsmiet_android)  

<a href="https://play.google.com/store/apps/details?id=de.pscom.pietsmiet">
    <img alt="Jetzt bei Google Play"
        height="80"
        src="https://play.google.com/intl/de_de/badges/images/generic/de_badge_web_generic.png" />
</a>

## Funktionen

* Schau Dir alle Neuigkeiten vom Team Pietsmiet an - gesammelt in einer App!  
 Neben den neusten Tweets, Facebook-Posts und Youtube-Videos findest du ebenfalls die Uploadpläne, Videos, News und Pietcasts von Pietsmiet.de

* Bekomme Push-Benachrichtigungen, sobald es etwas Neues auf Pietsmiet.de gibt: News, Uploadpläne, Videos oder auch der Pietcast - alles einzeln einstellbar.

* Verpackt in ein totschickes, modernes Design, aufgebaut auf den Google Material Design Richtlinien.

## Information & goal

This app is focused on performance and clean code. This is achieved by using very helpful libraries such as RxJava & Retrofit. Development follows the Model View Presenter design pattern. We are using some JUnit tests with Mockito to verify that there are no major logic bugs.  

SQLite is used to cache objects. The app communicates with the Twitter, Youtube & Facebook API for getting it's content. It also communicates with a Firebase Database where the data of pietsmiet.de is stored (Pietcast, Uploadplan, News, Videos). 

The backend of the app (RSS fetching and HTML scraping of pietsmiet.de and storing in Firebase DB) is coded with python ([code here](https://github.com/l3d00m/pietsmiet_xposter)).

## Git Workflow
**[`develop`](https://github.com/l3d00m/pietsmiet_android/tree/develop) branch:** Stable and buildable code. Travis automatically uploads a signed APK to Github releases when a new tag is pushed.  
**feature branches:** When a feature requires a lot of work, it has a separate branch that is merged (via PR) into `develop` as soon as the feature is complete  
