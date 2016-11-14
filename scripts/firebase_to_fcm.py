#! /usr/bin/python3
from pyfcm import FCMNotification
from firebase import firebase
import time

firebase_fcm = FCMNotification(api_key="AIzaSyDCX8fZ8Y5yemDLx54PMhepzGYs76Ar_Os")
firebase_db = firebase.FirebaseApplication('https://pietsmiet-de5ff.firebaseio.com/', None)


def write(text, filename):
    with open(filename, "w+") as text_file:
        print(text, file=text_file)


def read(filename):
    try:
        with open(filename, "r") as text_file:
            return text_file.read().rstrip()
    except Exception:
        print("No file created yet? ENOENT")


def send_fcm(message):
    firebase_fcm.notify_topic_subscribers(message_body=message, topic_name="uploadplan")


def check_for_update(scope):
    new = firebase_db.get('/' + scope, "title")
    old = read(filename=scope)

    if new != old:
        write(new, scope)
        print("New: \"" + new + "\"")
        send_fcm(new)


while (1):
    check_for_update("uploadplan")
    check_for_update("pietcast")
    time.sleep(300)
