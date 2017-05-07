package de.pscom.pietsmiet.repository;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.pscom.pietsmiet.TestUtils;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.json_model.twitterApi.TwitterApiInterface;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;

public class TwitterRepositoryTest extends MainTestPresenter {
    @Override
    public TwitterRepository preparePresenter() throws Exception {
        SecretConstants.twitterSecret = "s";
        SettingsHelper.stringTwitterBearer = "totallyValidBearerYo";
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(TestUtils.getResource("twitter_response.json")));
        TwitterRepository presenter = new TwitterRepository(mMockContext);
        presenter.apiInterface = getRetrofit(mockWebServer).create(TwitterApiInterface.class);
        return presenter;
    }

    @Test
    public void fetchPostsSinceObservable() throws Exception {
        TwitterRepository presenter = preparePresenter();
        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        presenter.fetchPostsSinceObservable(new Date(), 50)
                .map(Post.PostBuilder::build)
                .subscribe(testSubscriber);
        List<Post> list = testSubscriber.getOnNextEvents();
        for (Throwable tr : testSubscriber.getOnErrorEvents()) {
            System.out.println(tr.getMessage());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        Post example1 = new Post.PostBuilder(PostType.TWITTER)
                .title("Piet")
                .url("https://twitter.com/PietSmiet/status/855132476380938240")
                .date(dateFormat.parse("Thu Apr 20 18:54:04 +0000 2017"))
                .description("Lol https://t.co/J6LSnSKrxZ")
                .build();

        Post example2 = new Post.PostBuilder(PostType.TWITTER)
                .title("Sep")
                .url("https://twitter.com/kessemak2/status/855292827059666945")
                .date(dateFormat.parse("Fri Apr 21 05:31:15 +0000 2017"))
                .description("Sammy schl\u00e4ft noch eine Runde \ud83d\ude0d\nGuten Morgen! \ud83e\udd17 https://t.co/9VFhTcZeqW")
                .build();

        assertTrue(list.contains(example1));
        assertTrue(list.contains(example2));
    }

}