#! /usr/bin/python3
from pyfcm import FCMNotification
from firebase import firebase
import time

firebase_fcm = FCMNotification(api_key="thisissecretyo")
firebase_db = firebase.FirebaseApplication('https://pietsmiet-de5ff.firebaseio.com/', None)


def write(text):
    with open("latest.txt", "w+") as text_file:
        print(text, file=text_file)


def read():
    try:
        with open("latest.txt", "r") as text_file:
            return text_file.read().rstrip()
    except Exception:
        print("No file created yet? ENOENT")


def sendFCM(message):
    result = firebase_fcm.notify_topic_subscribers(message_body=message, topic_name="uploadplan")


while (1):
    result = firebase_db.get('/uploadplan', "title")
    old = read()

    if (result != old):
        write(result)
        print("New: \"" + result + "\"")
        sendFCM(result)
    time.sleep(300)
