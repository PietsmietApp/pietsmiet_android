package de.pscom.pietsmiet.presenter;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.youtubeApi.YoutubeApiInterface;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SecretConstants;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;

public class YoutubePresenterTest extends MainTestPresenter {
    @Override
    public YoutubePresenter preparePresenter() {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody("{ \"items\": [ { \"id\": { \"videoId\": \"lioDf6uCyMk\" }, \"snippet\": { \"publishedAt\": \"2017-04-20T14:05:00.000Z\", \"title\": \"Coole Mods für The Witcher 3\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/lioDf6uCyMk/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"za6L4iqDjKo\" }, \"snippet\": { \"publishedAt\": \"2017-04-20T13:05:00.000Z\", \"title\": \"KINDER ODER KARRIERE \uD83C\uDFAE Spiel des Lebens #1\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/za6L4iqDjKo/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"VKY1PtaKdYI\" }, \"snippet\": { \"publishedAt\": \"2017-04-20T12:05:00.000Z\", \"title\": \"BRAMMEN HAT DIE AUFNAHME \uD83C\uDFAE Counterstrike: Global Offensive #216\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/VKY1PtaKdYI/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"mlr7l_iIFIc\" }, \"snippet\": { \"publishedAt\": \"2017-04-19T16:05:00.000Z\", \"title\": \"PIET DER TRAITOR CAPTAIN \uD83C\uDFAE TTT #401\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/mlr7l_iIFIc/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"nrJp4n73eM0\" }, \"snippet\": { \"publishedAt\": \"2017-04-19T14:05:00.000Z\", \"title\": \"MIT FEUER SPIELT MAN NICHT \uD83C\uDFAE Worms Clan Wars S2 #2\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/nrJp4n73eM0/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"DPz6_jWWuAo\" }, \"snippet\": { \"publishedAt\": \"2017-04-19T13:05:00.000Z\", \"title\": \"RICHTIG AN DIE EIER \uD83C\uDFAE Mount your Friends #22\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/DPz6_jWWuAo/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"M8v6rGq-lnw\" }, \"snippet\": { \"publishedAt\": \"2017-04-19T12:05:00.000Z\", \"title\": \"KENN ICH NICHT! \uD83C\uDFAE Wer bin ich? #33\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/M8v6rGq-lnw/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"WRu2TV9YUos\" }, \"snippet\": { \"publishedAt\": \"2017-04-18T17:05:00.000Z\", \"title\": \"BVB UND RELIGION \uD83C\uDFAE PietCast #110\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/WRu2TV9YUos/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"-mBw2Oc7oFQ\" }, \"snippet\": { \"publishedAt\": \"2017-04-18T16:05:00.000Z\", \"title\": \"FÜNFT LETZTE FOLGE \uD83C\uDFAE Mario Kart 8 #270\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/-mBw2Oc7oFQ/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"XeGeQIMlDt4\" }, \"snippet\": { \"publishedAt\": \"2017-04-18T14:05:00.000Z\", \"title\": \"POTTRENNER \uD83C\uDFAE P.RO.FI. #12\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/XeGeQIMlDt4/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"NGGmTTbUhOk\" }, \"snippet\": { \"publishedAt\": \"2017-04-18T13:16:25.000Z\", \"title\": \"Neue MODI! \uD83C\uDFAE Grand Theft Auto Online #162\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/NGGmTTbUhOk/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"h2c-hV0mSoo\" }, \"snippet\": { \"publishedAt\": \"2017-04-18T12:05:00.000Z\", \"title\": \"SECRET HITLER UND MUNCHKIN IN EINEM SPIEL \uD83C\uDFAE Bang! #1\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/h2c-hV0mSoo/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"_47EJR7gIi4\" }, \"snippet\": { \"publishedAt\": \"2017-04-17T17:05:00.000Z\", \"title\": \"Wir sind absolute PES Profis | PES League\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/_47EJR7gIi4/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"sjZHjROQ3lw\" }, \"snippet\": { \"publishedAt\": \"2017-04-17T16:05:00.000Z\", \"title\": \"KOPF AN KOPF \uD83C\uDFAE TTT #400\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/sjZHjROQ3lw/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"70X3DD4Y6RI\" }, \"snippet\": { \"publishedAt\": \"2017-04-17T14:05:00.000Z\", \"title\": \"BILDER MERKEN \uD83C\uDFAE Schlag den Raab #5\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/70X3DD4Y6RI/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"ZvPtQ_5cGvs\" }, \"snippet\": { \"publishedAt\": \"2017-04-17T13:04:00.000Z\", \"title\": \"IRGENDWAS MIT BAUEN \uD83C\uDFAE Minecraft Battle Build Wars #8\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/ZvPtQ_5cGvs/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"sRanmqYZ0oY\" }, \"snippet\": { \"publishedAt\": \"2017-04-17T12:05:00.000Z\", \"title\": \"AUF DIE FRESSE \uD83C\uDFAE Battle for One #10\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/sRanmqYZ0oY/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"I5nbGv7k6VU\" }, \"snippet\": { \"publishedAt\": \"2017-04-17T10:05:00.000Z\", \"title\": \"Neulich im Signal Iduna Park \uD83C\uDFAE Der Champions League Vlog\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/I5nbGv7k6VU/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"5DKAKZw6XjU\" }, \"snippet\": { \"publishedAt\": \"2017-04-16T16:05:00.000Z\", \"title\": \"EINEN WEGSTECKEN \uD83C\uDFAE Mario Kart 8 #269\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/5DKAKZw6XjU/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"3HY6muB7CvY\" }, \"snippet\": { \"publishedAt\": \"2017-04-16T14:05:00.000Z\", \"title\": \"ORDER 67 | Bahrain 2/2 \uD83C\uDFAE F1 2016 #63\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/3HY6muB7CvY/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"mE6g6zWLjCc\" }, \"snippet\": { \"publishedAt\": \"2017-04-16T13:05:00.000Z\", \"title\": \"LÄUFT NICHT \uD83C\uDFAE Trials Fusion #62\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/mE6g6zWLjCc/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"AfILCb7AUx4\" }, \"snippet\": { \"publishedAt\": \"2017-04-16T12:05:00.000Z\", \"title\": \"RICHTIG EINLOCHEN \uD83C\uDFAE Bumms den Br4mm3n #4\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/AfILCb7AUx4/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"biKujh6b0bA\" }, \"snippet\": { \"publishedAt\": \"2017-04-15T16:05:00.000Z\", \"title\": \"DER WAL FICKT DHALU \uD83C\uDFAE TTT #399\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/biKujh6b0bA/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"iGq54zqGU_A\" }, \"snippet\": { \"publishedAt\": \"2017-04-15T14:05:00.000Z\", \"title\": \"SCHLECHT GEFAHREN | Bahrain 1/2 \uD83C\uDFAE F1 2016 #62\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/iGq54zqGU_A/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"H_2SBwg4mGs\" }, \"snippet\": { \"publishedAt\": \"2017-04-15T13:05:01.000Z\", \"title\": \"WER GEWINNT? \uD83C\uDFAE Der Dümmste ist raus! #2\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/H_2SBwg4mGs/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"Q0nxvJzEQD0\" }, \"snippet\": { \"publishedAt\": \"2017-04-15T12:05:00.000Z\", \"title\": \"DAS HORROR TTT \uD83C\uDFAE Deceit #1\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/Q0nxvJzEQD0/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"Zwn0T60akzk\" }, \"snippet\": { \"publishedAt\": \"2017-04-14T17:05:00.000Z\", \"title\": \"MAXIMUM ABFUCK \uD83C\uDFAE Trashnight #178\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/Zwn0T60akzk/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"Xwq5IFvRsoY\" }, \"snippet\": { \"publishedAt\": \"2017-04-14T16:05:00.000Z\", \"title\": \"UNGLÜCKS-PETER \uD83C\uDFAE Mario Kart 8 #268\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/Xwq5IFvRsoY/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"S3SBBpTQll8\" }, \"snippet\": { \"publishedAt\": \"2017-04-14T14:05:00.000Z\", \"title\": \"ASI SHOWS \uD83C\uDFAE Wer bin ich? #32\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/S3SBBpTQll8/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"cieie0cTjM4\" }, \"snippet\": { \"publishedAt\": \"2017-04-14T12:05:00.000Z\", \"title\": \"NEUES DREAMTEAM? \uD83C\uDFAE Uno #11\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/cieie0cTjM4/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"j4P3Lm4TJBI\" }, \"snippet\": { \"publishedAt\": \"2017-04-13T16:05:00.000Z\", \"title\": \"RICHTIG GEILE TRAITOR RUNDEN \uD83C\uDFAE TTT #398\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/j4P3Lm4TJBI/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"70_NEd2Gfqk\" }, \"snippet\": { \"publishedAt\": \"2017-04-13T15:05:00.000Z\", \"title\": \"NEUER TRAILER \uD83C\uDFAE Transformers 5 PietSmiet React #15\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/70_NEd2Gfqk/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"pvw2ONI5_zw\" }, \"snippet\": { \"publishedAt\": \"2017-04-13T13:01:41.000Z\", \"title\": \"MIT (fast) ALLEN VON PIETSMIET \uD83C\uDFAE Der Dümmste ist raus! #1\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/pvw2ONI5_zw/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"y1_5TTIBZms\" }, \"snippet\": { \"publishedAt\": \"2017-04-13T12:05:01.000Z\", \"title\": \"GEWISSENLOSE PEDDAS \uD83C\uDFAE Counterstrike: Global Offensive #215\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/y1_5TTIBZms/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"-ZHxFj1vmII\" }, \"snippet\": { \"publishedAt\": \"2017-04-12T17:05:00.000Z\", \"title\": \"GRAFFITI \uD83C\uDFAE PietCast #109\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/-ZHxFj1vmII/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"Vzi7I8LMSOI\" }, \"snippet\": { \"publishedAt\": \"2017-04-12T16:05:00.000Z\", \"title\": \"EINE TRÖTE SIE ZU KNECHTEN \uD83C\uDFAE Mario Kart 8 #267\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/Vzi7I8LMSOI/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"eP1Amzy0lBI\" }, \"snippet\": { \"publishedAt\": \"2017-04-12T14:05:00.000Z\", \"title\": \"DAS BESSERE WORMS IST ZURÜCK \uD83C\uDFAE Worms Clan Wars S2 #1\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/eP1Amzy0lBI/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"qScSGzfZEnM\" }, \"snippet\": { \"publishedAt\": \"2017-04-12T13:05:00.000Z\", \"title\": \"Es war mal verboten...\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/qScSGzfZEnM/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"4EPwrH-lmjg\" }, \"snippet\": { \"publishedAt\": \"2017-04-12T12:05:00.000Z\", \"title\": \"RETURN OF THE FASS \uD83C\uDFAE Golf it! #17\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/4EPwrH-lmjg/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"Bkop6xuncj0\" }, \"snippet\": { \"publishedAt\": \"2017-04-11T17:02:00.000Z\", \"title\": \"Was ist ... \uD83C\uDFAE Yooka Laylee ?\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/Bkop6xuncj0/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"yvUJolZSXQA\" }, \"snippet\": { \"publishedAt\": \"2017-04-11T16:05:00.000Z\", \"title\": \"TERRITORIEN \uD83C\uDFAE TTT #397\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/yvUJolZSXQA/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"I2mDlT730mg\" }, \"snippet\": { \"publishedAt\": \"2017-04-11T14:05:00.000Z\", \"title\": \"MÄNNLICHES MINIGOLF \uD83C\uDFAE Battle for One #9\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/I2mDlT730mg/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"cUhZ3orLBnM\" }, \"snippet\": { \"publishedAt\": \"2017-04-11T13:05:00.000Z\", \"title\": \"Es dreht sich! \uD83C\uDFAE UNGLÜCKSRAD #1\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/cUhZ3orLBnM/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"f-VrHqfezv0\" }, \"snippet\": { \"publishedAt\": \"2017-04-10T14:05:00.000Z\", \"title\": \"NA NA NA NA NA NA NA NA \uD83C\uDFAE P.RO.FI. #11\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/f-VrHqfezv0/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"bpnCeMqgu9U\" }, \"snippet\": { \"publishedAt\": \"2017-04-02T14:05:00.000Z\", \"title\": \"INTERNE DUELLE | Australien 2/2 \uD83C\uDFAE F1 2016 #60\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/bpnCeMqgu9U/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"mnRyYlLoXZA\" }, \"snippet\": { \"publishedAt\": \"2017-04-01T13:05:00.000Z\", \"title\": \"DAS ANDERE RECHTS \uD83C\uDFAE PlayerUnknown's Battlegrounds #3\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/mnRyYlLoXZA/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"P1RQ1iixJPE\" }, \"snippet\": { \"publishedAt\": \"2017-03-28T12:05:00.000Z\", \"title\": \"DRIVE-BY \uD83C\uDFAE PlayerUnknown's Battlegrounds #2\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/P1RQ1iixJPE/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"T1q9LBlhtG4\" }, \"snippet\": { \"publishedAt\": \"2017-02-19T17:05:00.000Z\", \"title\": \"DIE FOLGE DES PECHS \uD83C\uDFAE Mario Kart 8 #241\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/T1q9LBlhtG4/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"SxEctV-HVI0\" }, \"snippet\": { \"publishedAt\": \"2017-01-09T17:05:00.000Z\", \"title\": \"PEDDA WIRD ABGESCHLEPPT \uD83C\uDFAE Modern Warfare 2 #373\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/SxEctV-HVI0/mqdefault.jpg\" } } } }, { \"id\": { \"videoId\": \"-hcI9ZIDxiM\" }, \"snippet\": { \"publishedAt\": \"2016-12-18T15:05:00.000Z\", \"title\": \"MAXIMALE SPANNUNG | Ungarn 2/2 \uD83C\uDFAE F1 2016 #36\", \"thumbnails\": { \"medium\": { \"url\": \"https://i.ytimg.com/vi/-hcI9ZIDxiM/mqdefault.jpg\" } } } } ] } "));

        YoutubePresenter presenter = new YoutubePresenter(mMockContext);
        presenter.apiInterface = getRetrofit(mockWebServer).create(YoutubeApiInterface.class);
        return presenter;
    }

    @Test
    public void fetchPostsSinceObservable() throws Exception {
        SecretConstants.youtubeAPIkey = "s";
        YoutubePresenter presenter = preparePresenter();

        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        presenter.fetchPostsSinceObservable(new Date())
                .map(Post.PostBuilder::build)
                .subscribe(testSubscriber);
        List<Post> list = testSubscriber.getOnNextEvents();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        Post example1 = new Post.PostBuilder(PostType.YOUTUBE)
                .title("Coole Mods für The Witcher 3")
                .url("http://www.youtube.com/watch?v=" + "lioDf6uCyMk")
                .date(dateFormat.parse("2017-04-20T14:05:00.000Z"))
                .build();

        Post example2 = new Post.PostBuilder(PostType.YOUTUBE)
                .title("Es war mal verboten...")
                .url("http://www.youtube.com/watch?v=" + "qScSGzfZEnM")
                .date(dateFormat.parse("2017-04-12T13:05:00.000Z"))
                .build();

        assertTrue(list.contains(example1));
        assertTrue(list.contains(example2));
    }

}
