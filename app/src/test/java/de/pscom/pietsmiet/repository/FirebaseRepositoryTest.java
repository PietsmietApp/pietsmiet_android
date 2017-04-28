package de.pscom.pietsmiet.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.TestUtils;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.firebaseApi.FirebaseApiInterface;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SettingsHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class FirebaseRepositoryTest extends MainTestPresenter {

    @Override
    public FirebaseRepository preparePresenter() throws Exception {
        SettingsHelper.boolCategoryPietsmietVideos = true;
        SettingsHelper.boolCategoryPietsmietNews = true;
        SettingsHelper.boolCategoryPietsmietUploadplan = true;
        SettingsHelper.boolCategoryPietcast = true;
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(TestUtils.getResource("firebase_response.json")));
        FirebaseRepository presenter = new FirebaseRepository(mMockContext);

        presenter.apiInterface = getRetrofit(mockWebServer).create(FirebaseApiInterface.class);
        return presenter;
    }

    @Test
    public void fetchPostsSinceObservable() throws Exception {
        FirebaseRepository presenter = preparePresenter();

        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        presenter.fetchPostsSinceObservable(new Date(), 50)
                .map(Post.PostBuilder::build)
                .subscribe(testSubscriber);
        List<Post> list = testSubscriber.getOnNextEvents();
        for (Throwable tr : testSubscriber.getOnErrorEvents()) {
            System.out.println(tr.getMessage());
        }

        Post example1 = new Post.PostBuilder(PostType.NEWS)
                .title("Build Wars #6 - Wählt den Loser")
                .url("http://www.pietsmiet.de/news/allgemein/1351-build-wars-6-waehlt-den-loser")
                .date(new Date(1491123600000L))
                .description("\n<p>Es kann nur einen Loser geben!</p>\n \n<p>Loser der letzten Ausgabe: <strong>Sep! </strong>Sein Verlierer-Gedicht dazu:</p>\n \n<p>Kox, Nutten, Heiterkeit<br/>wer ist denn da gemeint?<br/>Oh genau ihr wisst...  <a href=\"http://www.pietsmiet.de/news/allgemein/1351-build-wars-6-waehlt-den-loser\">Auf pietsmiet.de weiterlesen →</a>")
                .build();

        Post example2 = new Post.PostBuilder(PostType.PIETCAST)
                .title("Peter heißt Podcast #35 – Wer ist eigentlich Sven?!")
                .url("http://www.pietsmiet.de/pietcast/php-35/")
                .date(new Date(1488377128000L))
                .description("Heute wartet ein großer Enthüllungspodcast auf euch: Wir klären endlich, wer hinter der Persona „Sven“ steckt. Exklusiv beim PeterHeißtPodcast leaken wir die ersten Infos über den sympathischen Bartträger. Hier bekommt ihr die ganze Story! Außerdem wieder ein wildes Potpourri: Karneval, Jugendsünden, Klamotten, Armbrüche. Wir haben wirklich nichts ausgelassen – wie schaffen wir das bloß jedes … <a href=\"http://www.pietsmiet.de/pietcast/php-35/\" class=\"more-link\"><span class=\"screen-reader-text\">Peter heißt Podcast #35 – Wer ist eigentlich Sven?!</span> weiterlesen <span class=\"meta-nav\">→</span></a>")
                .build();

        assertTrue(list.contains(example1));
        assertTrue(list.contains(example2));
    }

}
