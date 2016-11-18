#! /usr/bin/python3
from pyfcm import FCMNotification
from firebase import firebase
import time
import praw
import datetime
from bs4 import BeautifulSoup
from urllib.request import Request, urlopen
import api_keys
import html2text

firebase_fcm = FCMNotification(api_key=api_keys.fcm_key)
firebase_db = firebase.FirebaseApplication('https://pietsmiet-de5ff.firebaseio.com/', authentication=api_keys.fb_db_key)

reddit_auth = praw.Reddit(user_agent="Twitter X-Poster by l3d00m")
reddit_auth.set_oauth_app_info(client_id="eoAG6V7plEDeAA", client_secret=api_keys.reddit_client_secret,
                               redirect_uri="http://127.0.0.1")
subreddit = "pietsmiet"

SCOPE_UPLOADPLAN = "uploadplan"
SCOPE_PIETCAST = "pietcast"


def submit_to_reddit(text):
    """
    Posts a link to the given subreddit
    :param url: Url to add to the reddit link post
    """

    if text == '':
        return

    now = datetime.datetime.now()
    title = 'Uploadplan vom ' + str(now.day) + "." + str(now.month) + "." + str(now.year)

    # use the refresh token to get new access information regularly (at least every hour):
    reddit_auth.refresh_access_information(api_keys.reddit_client_refresh)
    # Submit the post
    reddit_auth.submit(subreddit, title, text=text)


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


def formatText(text, link):
    text = html2text.html2text(text)
    text += '\n[Link zum aktuellen Uploadplan](' + link + ')\n\n--- \n[Code vom Bot](https://github.com/l3d00m/pietsmiet_android/blob/develop/scripts/firebase_to_fcm.py) | by /u/l3d00m'
    return text


def check_for_update(scope):
    new = get_thing_from_db(scope, "title")
    old = read(filename=scope)

    if new != old:
        write(new, scope)
        print("New: \"" + new + "\"")
        send_fcm(new)
        if scope == SCOPE_UPLOADPLAN:
            link = get_thing_from_db(SCOPE_UPLOADPLAN, "link")
            put_desc_into_db(link)
            time.sleep(10)
            submit_to_reddit(formatText(get_thing_from_db(SCOPE_UPLOADPLAN, "desc"), link))


def get_thing_from_db(scope, thing):
    return firebase_db.get('/' + scope, thing)


def put_desc_into_db(link):
    try:
        content = scrape_site(link)
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
