import praw
from backend.api_keys import reddit_client_secret, reddit_client_refresh

subreddit = "pietsmiet"

reddit_auth = praw.Reddit(user_agent="X-Poster by l3d00m")
reddit_auth.set_oauth_app_info(client_id="eoAG6V7plEDeAA", client_secret=reddit_client_secret,
                               redirect_uri="http://127.0.0.1")


def submit_to_reddit(title, text):
    """
    Posts a link to the given subreddit
    :param title: Title of the reddit post
    :param text: Text to add to the reddit self post
    """

    if (text == '') or (title == ''):
        print("Not submitting to reddit, null text or title")
        return

    # use the refresh token to get new access information regularly (at least every hour):
    reddit_auth.refresh_access_information(reddit_client_refresh)
    # Submit the post
    reddit_auth.submit(subreddit, title, text=text)
