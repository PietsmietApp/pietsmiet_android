from backend.api_keys import fcm_key, fb_db_key
from pyfcm import FCMNotification
from firebase import firebase

firebase_fcm = FCMNotification(api_key=fcm_key)
firebase_db = firebase.FirebaseApplication('https://pietsmiet-de5ff.firebaseio.com/', authentication=fb_db_key)


def put_feed_into_db(feed):
    """
    Stores the content of the Feed object in firebase database
    :param feed: the feed object
    :return: None
    """
    scope = feed.scope
    try:
        firebase_db.put(url="/" + scope, name="desc", data=feed.desc)
        firebase_db.put(url="/" + scope, name="link", data=feed.link)
        firebase_db.put(url="/" + scope, name="title", data=feed.title)
        firebase_db.put(url="/" + scope, name="date", data=feed.date)
    except Exception:
        print('Error putting feed into fb db')


def send_fcm(message, topic):
    """
    Sends a firebase cloud messaging message
    :param message: Message to send
    :param topic: Topic to send to
    :return: None
    """
    try:
        firebase_fcm.notify_topic_subscribers(message_body=message, topic_name=topic)
    except Exception:
        print("Error making new fcm")
