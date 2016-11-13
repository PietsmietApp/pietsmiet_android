# PietSmiet-App für Android [![Build Status](https://travis-ci.org/l3d00m/pietsmiet_android.svg?branch=master)](https://travis-ci.org/l3d00m/pietsmiet_android)

Still under active development and pre-alpha.

## Features

* A beatiful and intuitive interface, based on material design.

* Last tweets and facebook posts from Team Pietsmiet

* Latest Uploadplan & Pietcast

* Latest pietsmiet.de videos (WIP)

* Notifications for new Uploadplan and pietsmiet.de videos (WIP)

## Information & goal

This app is focused on performance and clean code. This is achieved by using very helpful libraries such as RxJava & Retrofit. I'm trying to use Model-View-Presenter pattern for developing this.  
SQLite is used to cache objects. The app communicates with the Twitter & Facebook API for getting it's content. It also uses RSS in combination with JSOUP for scraping content from pietsmiet.de

[Here](https://github.com/l3d00m/pietsmiet_android/blob/develop/ressources/ps_app_overview.png) is a professional overview over the classes made with paint ;)

## Git Workflow
**[`release`](https://github.com/l3d00m/pietsmiet_android/tree/release) branch:** `develop` is merged into this branch when a new version is released  
**[`develop`](https://github.com/l3d00m/pietsmiet_android/tree/develop) branch:** mostly stable and buildable code, there may be exceptions  
**feature branches:** When a feature requires a lot of work, it has a separate branch that is merged (via a PR) into `develop` as soon as the feature is complete  


