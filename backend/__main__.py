#! /usr/bin/python3
import time
import datetime
import sys

sys.path.append("/home/pi");

from backend.firebase_util import send_fcm, put_feed_into_db
from backend.reddit_util import submit_to_reddit
from backend.scrape_util import format_text
from backend.rss_util import parse_feed
from backend.scopes import SCOPE_NEWS, SCOPE_UPLOADPLAN, SCOPE_PIETCAST


def in_between_time(start_hour, end_hour):
    now = int(datetime.datetime.now().hour)
    if start_hour <= end_hour:
        return start_hour <= now < end_hour
    else:  # over midnight e.g., 23-04
        return start_hour <= now or now < end_hour


def write(text, filename):
    with open(filename, "w+") as text_file:
        print(text, file=text_file)


def read(filename):
    try:
        with open(filename, "r") as text_file:
            return text_file.read().rstrip()
    except Exception:
        print("No file created yet? ENOENT")


def check_for_update(scope):
    new_feed = parse_feed(scope)
    new_title = new_feed.title
    old_title = read(filename=scope)
    if new_title != old_title:
        print("New: \"" + new_title + "\"")
        write(new_title, scope)
        put_feed_into_db(new_feed)
        send_fcm(new_feed)
        if scope == SCOPE_UPLOADPLAN:
            submit_to_reddit(new_feed.title, format_text(new_feed))
        elif scope == SCOPE_NEWS:
            submit_to_reddit("Neuer Post auf pietsmiet.de: " + new_feed.title, format_text(new_feed))
        return True
    return False


fetched_today = False

while 1:
    # Check for updates:
    # 1) Every 2.5 hours for PietCast
    # 2) Every half hour between 9am and 1pm for Uploadplan
    # 3) Every 10 hours for news on pietsmiet.de
    # (I'm two lazy to do it asynchronous)
    i = 0
    if in_between_time(9, 13):
        if not fetched_today:
            fetched_today = check_for_update(SCOPE_UPLOADPLAN)
    else:
        fetched_today = False
    if (i == 4) or (i == 9) or (i == 14) or (i == 19):
        check_for_update(SCOPE_PIETCAST)
    if i == 19:
        check_for_update(SCOPE_NEWS)
        i = 0

    i += 1

    time.sleep(1800)
