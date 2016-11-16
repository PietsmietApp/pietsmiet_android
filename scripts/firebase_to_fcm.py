#! /usr/bin/python3
from pyfcm import FCMNotification
from firebase import firebase
import time
from bs4 import BeautifulSoup
from urllib.request import Request, urlopen
from api_keys import fcm_key, fb_db_key

firebase_fcm = FCMNotification(api_key=fcm_key)
firebase_db = firebase.FirebaseApplication('https://pietsmiet-de5ff.firebaseio.com/', authentication=fb_db_key)

SCOPE_UPLOADPLAN = "uploadplan"
SCOPE_PIETCAST = "pietcast"


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
    try:
        firebase_fcm.notify_topic_subscribers(message_body=message, topic_name="uploadplan")
    except Exception:
        print("Error making new fcm")


def check_for_update(scope):
    new = get_thing_from_db(scope, "title")
    old = read(filename=scope)

    if new != old:
        write(new, scope)
        print("New: \"" + new + "\"")
        send_fcm(new)
        if scope == "uploadplan":
            put_desc_into_db()


def get_thing_from_db(scope, thing):
    return firebase_db.get('/' + scope, thing)


def put_desc_into_db():
    try:
        content = scrape_site(get_thing_from_db(SCOPE_UPLOADPLAN, "link"))
        if content is not None:
            firebase_db.put(url="/uploadplan", name="desc", data=content)
    except Exception:
        print('Error putting desc into fb db')


def scrape_site(url):
    try:
        hdr = {
            'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
            'Accept-Encoding': 'none',
            'Accept-Language': 'en-US,en;q=0.8',
            'Connection': 'keep-alive'}
        req = Request(url, headers=hdr)
        r = urlopen(req).read()
        results = BeautifulSoup(r, 'html.parser').find(itemprop="articleBody")
        to_return = ""
        for thing in results:
            to_return += str(thing)
        return to_return
    except Exception:
        return None


while (1):
    check_for_update(SCOPE_UPLOADPLAN)
    check_for_update(SCOPE_PIETCAST)

    time.sleep(300)
