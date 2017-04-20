package de.pscom.pietsmiet.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.TestUtil;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.generic.Post.PostBuilder;
import de.pscom.pietsmiet.model.firebaseApi.FirebaseApiInterface;
import de.pscom.pietsmiet.util.PostType;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FirebasePresenterTest {
    @Mock
    MainActivity mMockContext;

    public FirebasePresenter prepareFirebasePresenter(MockWebServer mockWebServer){
        FirebasePresenter presenter = new FirebasePresenter(mMockContext);
        Retrofit retrofit = TestUtil.createRetrofit(mockWebServer);

        presenter.apiInterface = retrofit.create(FirebaseApiInterface.class);
        return presenter;
    }

    @Test
    public void test() {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody("{\"news\":{\"BuildWars6WhltdenLoser1491123600000\":{\"date\":1491123600000,\"desc\":\"\\n<p>Es kann nur einen Loser geben!</p>\\n \\n<p>Loser der letzten Ausgabe: <strong>Sep! </strong>Sein Verlierer-Gedicht dazu:</p>\\n \\n<p>Kox, Nutten, Heiterkeit<br/>wer ist denn da gemeint?<br/>Oh genau ihr wisst...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1351-build-wars-6-waehlt-den-loser\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1351-build-wars-6-waehlt-den-loser\",\"scope\":\"news\",\"title\":\"Build Wars #6 - Wählt den Loser\"},\"BuildWars7WhltdenLoser1491724800000\":{\"date\":1491724800000,\"desc\":\"\\n<p>Es kann nur zwei Loser geben!</p>\\n \\n<p>Loser der letzten Ausgabe: <strong>Sep!</strong></p>\\n \\n<p>Heute läuft Minecraft Battle Build Wars ein wenig anders ab als sonst. Spielleiter Brammen hat sich überlegt, dass Jay...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1402-build-wars-7-waehlt-den-loser\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1402-build-wars-7-waehlt-den-loser\",\"scope\":\"news\",\"title\":\"Build Wars #7 - Wählt den Loser\"},\"BuildWars8WhltdenLoser1492419600000\":{\"date\":1492419600000,\"desc\":\"\\n<p>Es kann nur einen Loser geben!</p>\\n \\n<p>Die heutige Ausgabe Build Wars steht ganz unter dem Zeichen \\\"ACTION!\\\". Wer kann sich unsere Jungs bitte nicht in \\\"Stirb langsam\\\" oder \\\"Rambo\\\" vorstellen? Apropos Rambo: Genau...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1454-build-wars-8-waehlt-den-loser\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1454-build-wars-8-waehlt-den-loser\",\"scope\":\"news\",\"title\":\"Build Wars #8 - Wählt den Loser\"},\"DuellderProfiMaschinen1491296400000\":{\"date\":1491296400000,\"desc\":\"\\n<p>Ihr habt die Qual der Wahl!</p>\\n \\n<p>Ihr kennt das: Es wurde sich in der Arena duelliert und ein Gewinner steht fest. Was aber nicht feststeht, ist die Frage, wer den Beauty-Award gewinnt!</p>\\n...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1371-profi-voting-4-4\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1371-profi-voting-4-4\",\"scope\":\"news\",\"title\":\"Duell der Profi-Maschinen\"},\"PROFIWerbautdencoolstenPodracer1492509600000\":{\"date\":1492509600000,\"desc\":\"\\n<p>Eure Meinung ist gefragt! </p>\\n \\n \\n<p>Eine neue Woche, eine neue Runde P.RO.FI! Wieder toben sich die Jungs aus, um sich mit den abgefahrensten Fahrzeugen herauszufordern. Heute steht alles im Zeichen von \\\"Star...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1462-profi-maschinen-voting-9\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1462-profi-maschinen-voting-9\",\"scope\":\"news\",\"title\":\"P.RO.FI - Wer baut den coolsten Podracer?\"},\"WeristdercoolsteBatmanPROFI1491818400000\":{\"date\":1491818400000,\"desc\":\"\\n<p>Eure Meinung ist gefragt!</p>\\n \\n<p>PietSmiet mag jeder, Superhelden mag auch jeder. Ergo kombinieren wir das Ganze heute und lassen die Jungs das Batmobil bauen. Dabei kamen wieder die wildesten Bauten heraus....  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1410-profi-maschinen-voting8\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1410-profi-maschinen-voting8\",\"scope\":\"news\",\"title\":\"Wer ist der coolste Batman-P.RO.FI?\"},\"WeristdercoolsteProfi1490603400000\":{\"date\":1490603400000,\"desc\":\"\\n<p>Eure Meinung ist gefragt!</p>\\n \\n<p>Die Jungs haben sich wieder zusammengesetzt, um Maschinen aus der Hölle zu kreieren. Gut, so dramatisch wird es auch nicht, aber schauen wir uns mal die Gefährten an!</p>\\n...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1311-profi-maschinen-voting6\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1311-profi-maschinen-voting6\",\"scope\":\"news\",\"title\":\"Wer ist der coolste Profi?\"},\"YayoderNayVoting1491469200000\":{\"date\":1491469200000,\"desc\":\"\\n<p>Ihr habt es in der Hand!</p>\\n \\n<p>Ihr kennt das Prinzip: Chris stellt euch drei Spiele vor und ihr dürft voten. Hieß früher Early Access, jetzt 'Jay oder Nay'. Ich will gar nicht lange labern - ab zu den...  <a href=\\\"http://www.pietsmiet.de/news/allgemein/1382-yay-nay-voting\\\">Auf pietsmiet.de weiterlesen →</a>\",\"link\":\"http://www.pietsmiet.de/news/allgemein/1382-yay-nay-voting\",\"scope\":\"news\",\"title\":\"Yay oder Nay - Voting\"}},\"pietcast\":{\"PeterheitPodcast34HobbysundHaustierediewirnichthatten1487182674000\":{\"date\":1487182674000,\"desc\":\"Hobbys. Haustiere. Hobbys und Haustiere. Klingt erstmal komisch, lässt sich aber wunderbar in einem Podcast vereinen. Und eigentlich geht es auch um Hobbys, die wir gerne mal ausprobiert hätten, aber nicht haben. Auch um Haustiere, die wir haben wollten, aber nicht durften. Ihr merkt, worauf das hinausläuft, oder? Ein Podcast vieler nicht wahrgenommener Chancen. Wer … <a href=\\\"http://www.pietsmiet.de/pietcast/php-34/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">Peter heißt Podcast #34 – Hobbys und Haustiere, die wir nicht hatten</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/php-34/\",\"scope\":\"pietcast\",\"title\":\"Peter heißt Podcast #34 – Hobbys und Haustiere, die wir nicht hatten\"},\"PeterheitPodcast35WeristeigentlichSven1488377128000\":{\"date\":1488377128000,\"desc\":\"Heute wartet ein großer Enthüllungspodcast auf euch: Wir klären endlich, wer hinter der Persona „Sven“ steckt. Exklusiv beim PeterHeißtPodcast leaken wir die ersten Infos über den sympathischen Bartträger. Hier bekommt ihr die ganze Story! Außerdem wieder ein wildes Potpourri: Karneval, Jugendsünden, Klamotten, Armbrüche. Wir haben wirklich nichts ausgelassen – wie schaffen wir das bloß jedes … <a href=\\\"http://www.pietsmiet.de/pietcast/php-35/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">Peter heißt Podcast #35 – Wer ist eigentlich Sven?!</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/php-35/\",\"scope\":\"pietcast\",\"title\":\"Peter heißt Podcast #35 – Wer ist eigentlich Sven?!\"},\"PeterheitPodcast36FahrschuleundFhrerschein1489509767000\":{\"date\":1489509767000,\"desc\":\"Autos haben vier Räder und können fahren. Diese und weitere Fakten präsentieren wir im aktuellen PeterHeißtPodcast! Quark, wird natürlich viel tiefgründiger! Wir reden über unsere Erfahrungen in der Fahrschule, Fails am Lenkrad und was im Auto so gehört wird. Hier und da schweifen wir natürlich ab und die Stimmung ist leicht aufgeheizt. Hat alles seine … <a href=\\\"http://www.pietsmiet.de/pietcast/php-36/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">Peter heißt Podcast #36 – Fahrschule und Führerschein</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/php-36/\",\"scope\":\"pietcast\",\"title\":\"Peter heißt Podcast #36 – Fahrschule und Führerschein\"},\"PeterheitPodcast37DerPhPPersnlichkeitstest1490724950000\":{\"date\":1490724950000,\"desc\":\"Manchmal hat man richtig gute Ideen. Und manchmal hat man dann sowas: Wir hielten es für intelligent, Persönlichkeitstest im Podcast zu machen. Nun, auf dem Papier schien es echt cool. Aber macht euch doch einfach selbst ein Bild davon. Wir sind ja echte Entertainer, die auch eine Betriebsanleitung vortragen können!\",\"link\":\"http://www.pietsmiet.de/pietcast/php-37/\",\"scope\":\"pietcast\",\"title\":\"Peter heißt Podcast #37 – Der PhP-Persönlichkeitstest\"},\"PietCast102FrauenquoteundPewDiePie1487349795000\":{\"date\":1487349795000,\"desc\":\"Folge 102 startet mit einer riesen Diskussion über PewDiePie und ob er ein Antisemit ist oder einfach nur eine Linie überschritten hat. Die große Frage: Was darf Satire? Wo ist die Grenze? Und direkt im Anschluss gab es eine hitzige Debatte über die Frauenquote. Sollten Männer und Frauen anhand eines Stellenprofils angestellt werden, oder sollte … <a href=\\\"http://www.pietsmiet.de/pietcast/pietcast102/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">PietCast #102 – Frauenquote und PewDiePie</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast102/\",\"scope\":\"pietcast\",\"title\":\"PietCast #102 – Frauenquote und PewDiePie\"},\"PietCast103DasneueZeldaDerSinndesLebens1487941241000\":{\"date\":1487941241000,\"desc\":\"Ladies and Gentlemen, fast wären wir in dieser Ausgabe nur zu zweit gewesen und dann auch noch zu 50% mit stetig absteigender Stimme. Aber dann hat sich nicht nur ein Dhalucard erbarmt, es kam auch ein Christian im Laufe der Zeit hinzu. Wir reden über meine Switch und vor allem The Legend of Zelda: Breath … <a href=\\\"http://www.pietsmiet.de/pietcast/pietcast103/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">PietCast #103 – Das neue Zelda & Der Sinn des Lebens</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast103/\",\"scope\":\"pietcast\",\"title\":\"PietCast #103 – Das neue Zelda & Der Sinn des Lebens\"},\"PietCast104BrammensEssgewohnheitenSwitchLaunch1488544640000\":{\"date\":1488544640000,\"desc\":\"Salve ihr guten Menschen! Der Tag des Switch-Launches ist gekommen und natürlich müssen wir kurz darüber reden. Und nochmal über Zelda, jetzt wo ich mehr dazu sagen darf. Und über 1-2-Switch. Aber wir reden auch fernab von Videospielen über unsere Reisegewohnheiten, unfreundliche Mitreisende und Brammens sonderbare Essgewohnheiten. Ein durch und durchiger #PietCast also. Viel Spaß! … <a href=\\\"http://www.pietsmiet.de/pietcast/pietcast104/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">PietCast #104 – Brammens Essgewohnheiten & Switch-Launch</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast104/\",\"scope\":\"pietcast\",\"title\":\"PietCast #104 – Brammens Essgewohnheiten & Switch-Launch\"},\"PietCast105Erwachsenwerdennervtoder1489146042000\":{\"date\":1489146042000,\"desc\":\"Nervt es erwachsen zu werden? Oder ist die Freiheit viel geiler als der Nachteil der Pflichten? Darüber reden wir kurz zu Beginn. Es geht weiter, wie die snobbigen Leute von PietSmiet (nicht der auf dem Boden gebliebene Peter) sich in der VIP Lounge von Borussia Dortmund bedienen lassen. Und so tröpfelt der PietCast vor sich … <a href=\\\"http://www.pietsmiet.de/pietcast/pietcast105/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">PietCast #105 – Erwachsen werden nervt, oder?</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast105/\",\"scope\":\"pietcast\",\"title\":\"PietCast #105 – Erwachsen werden nervt, oder?\"},\"PietCast106GutedeutscheAutobahnen1489757971000\":{\"date\":1489757971000,\"desc\":\"Wir beginnen diesen Podcast ganz normal mit dem großen Internet-Diss über Mass Effect: Andromeda. Und irgendwann driften wir ab. Auf Autobahnen. Keine Ahnung warum aber man kann super lange darüber reden. Hätte ich vorher auch nicht gedacht. Aber man lernt ja nie aus. Außerdem reden darüber, wie viel auf Youtube geschauspielert wird. Spoiler: Geht so. … <a href=\\\"http://www.pietsmiet.de/pietcast/pietcast106/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">PietCast #106 – Gute deutsche Autobahnen</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast106/\",\"scope\":\"pietcast\",\"title\":\"PietCast #106 – Gute deutsche Autobahnen\"},\"PietCast107AlteAutosneueTypen1490370666000\":{\"date\":1490370666000,\"desc\":\"Wir sind ein bisschen enttäuscht von Mass Effect: Andromeda und mögen Playunkown’s Battleground. Vor allem reden wir aber erneut über Autobahnlöcher und alte Autos für junge Menschen :D Viel Spaß! Pedda\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast107/\",\"scope\":\"pietcast\",\"title\":\"PietCast #107 – Alte Autos & neue Typen\"},\"PietCast108ReiseReiseReiseReiseGeburtstag1490953910000\":{\"date\":1490953910000,\"desc\":\"Mensch was waren wir unterwegs! Sep war in Los Angeles, Jay in London, Brammen bei der CEO von Youtube und ich in Berlin bei der Kanzlerin. Unglaubliches hat sich zugetragen und wir berichten davon! Außerdem geht’s um geile Loop-Systeme, das Samsung Galaxy S8, die fiese Sprache der Retter und viel mehr! Viel Spaß! Pedda\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast108/\",\"scope\":\"pietcast\",\"title\":\"PietCast #108 – Reise, Reise, Reise, Reise, Geburtstag\"},\"PietCast109Graffiti1491590411000\":{\"date\":1491590411000,\"desc\":\"Wieder ist eine Woche um, wieder finden wir uns in geselliger Runde zusammen. Brammen erzählt von seinen Eskapaden im Ritz, wir diskutieren über Destiny 2 und Call of Duty im zweiten Weltkrieg. Außerdem ganz wichtige Themen diese Woche: Heißluftfritteusen und Graffiti. Ist ja schon so ne kontroverse Nummer, diese Fritteusen. Viel Spaß, Pedda\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast109/\",\"scope\":\"pietcast\",\"title\":\"PietCast #109 – Graffiti\"},\"PietCast110BVBundReligion1492164515000\":{\"date\":1492164515000,\"desc\":\"Die große Frage ist heute, ob nicht religiöse Menschen auch religiöse Feiertage in Anspruch nehmen sollen und was wir alle zu Ostern machen. Wird Donald Trump bald einen Krieg mit Nordkorea entfachen und wenn ja, wer hat die größte Arschkarte dabei gezogen? Und unsere Jungs reden viel über Fussball und den Anschlag auf den Mannschaftsbus … <a href=\\\"http://www.pietsmiet.de/pietcast/pietcast110/\\\" class=\\\"more-link\\\"><span class=\\\"screen-reader-text\\\">PietCast #110 – BVB und Religion</span> weiterlesen <span class=\\\"meta-nav\\\">→</span></a>\",\"link\":\"http://www.pietsmiet.de/pietcast/pietcast110/\",\"scope\":\"pietcast\",\"title\":\"PietCast #110 – BVB und Religion\"}},\"uploadplan\":{\"UploadPlanam120420171491975790000\":{\"date\":1491975790000,\"desc\":\"\\n<p>Was wollt ihr? Videos? Okay! </p>\\n \\n<p>Guten Morgen! Langsam geht es auf Ostern zu und alle fangen an, ihre Eier zu verstecken. Diesen Satz lasse ich jetzt einfach mal unkommentiert, was ihr daraus macht ist eure Sache! Was allerdings nicht unkommentiert bleiben sollte, ist der heutige Upload-Plan: </p>\\n \\n<p><strong>Upload-Plan am 12.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> Golf It<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT):</strong> Start Up Monday<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> Worms<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> Mario Kart<br/><strong>17:00 Uhr (PS.de)/19:00 Uhr (YT): </strong>PietCast<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> For the King<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1426-upload-plan-am-12-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 12.04.2017\"},\"UploadPlanam130420171492061674000\":{\"date\":1492061674000,\"desc\":\"\\n<p>Die Welt braucht Battles...</p>\\n \\n<p>Guten Morgen! Heute wird wieder Wissen und Geschick abgefragt. Also genau das, wofür die Jungs schon seit Jahren stehen. Ob das alles funktioniert? Unterhaltend wird es auf jeden Fall sein! </p>\\n \\n<p><strong>Upload-Plan am 13.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> CSGO<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT):</strong> Der Dümmste ist raus<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> Schlag den Raab<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> TTT<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> Factorio<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1430-upload-plan-am-13-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 13.04.2017\"},\"UploadPlanam140420171492150699000\":{\"date\":1492150699000,\"desc\":\"\\n<p>Ein Upload-Plan zum Verlieben</p>\\n \\n<p>Guten Morgen! Das lange Wochenende wird eingeläutet, aber nicht ohne unsere Videos! Daher gibt es heute das volle Programm an Unterhaltung, genauso wie morgen, übermorgen und sonst eigentlich auch!</p>\\n \\n<p>Upload-Plan am 14.04.2017:</p>\\n<p><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> Uno<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> Wer bin ich?<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> Mario Kart 8<br/><strong>17:00 Uhr (PS.de)/19:00 Uhr (YT):</strong> Trashnight<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> For the King (verschiebt sich auf Samstag Früh wegen Seps Rücksreise aus Frankreich)<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1438-upload-plan-am-14-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 14.04.2017\"},\"UploadPlanam150420171492236000000\":{\"date\":1492236000000,\"desc\":\"\\n<p>Ein Plan sie alle zu knechten</p>\\n \\n<p>Guten Morgen! Hoch die Hände, Wochenende! Dieser Samstag scheint wieder äußerst spaßig, kommen doch wieder sehr äußerst spannende Projekte auf den Schirm. </p>\\n \\n<p><strong>Upload-Plan am 15.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> Deceit<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT): </strong>Der Dümmste ist Raus<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> F1 Bahrain<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> TTT<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> Factorio<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1442-upload-plan-am-15-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 15.04.2017\"},\"UploadPlanam160420171492322400000\":{\"date\":1492322400000,\"desc\":\"\\n<p>Upload-Plan: Jetzt erst Recht</p>\\n \\n<p>Guten Morgen! Wir haben zwar Sonntag, aber immerhin ist morgen auch noch frei. Welch wunderbarer Einfall, diese Feiertage! Damit euch nicht langweilig wird, bekommt ihr hier eure Übersicht des Tages!</p>\\n \\n<p><strong>Upload-Plan am 16.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> Bumms den Brammen<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT):</strong> Trials<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> F1<br/><strong>16:30 Uhr (PS.de)/18:00 Uhr (YT):</strong> Mario Kart 8 (verzögert sich heute etwas)<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> For the King<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1446-upload-plan-am-16-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 16.04.2017\"},\"UploadPlanam170420171492408800000\":{\"date\":1492408800000,\"desc\":\"\\n<p>The Dark Upload-Plan returns!</p>\\n \\n<p>Guten Morgen! Eigentlich wollte ich heute aufstehen, aber dann sagte mir mein Kopf: FEIERTAG! Sehr praktisch, kann ich einfach liegen bleiben. Schlafen geht aber erst, wenn ich euch mit den Videos des Tages versorgt habe. Bitte sehr: </p>\\n \\n<p><strong>Upload-Plan am 17.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> Battle for One<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT):</strong> Minecraft Battle Build Wars<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> Schlag den Raab<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> TTT<br/><strong>17:00 Uhr (PS.de)/19:00 Uhr (YT): </strong>PES<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> Factorio<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1450-upload-plan-am-17-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 17.04.2017\"},\"UploadPlanam180420171492495200000\":{\"date\":1492495200000,\"desc\":\"\\n<p>Good Morning Upload-Plan...</p>\\n \\n<p>Guten Morgen! Ich hoffe, ihr habt die Feiertage alle ausgiebig zur Erholung genutzt. Wir haben heute nämlich wieder zahlreiche Videos, die ihr auch frisch und gut gelaunt ansehen solltet! Ich biete euch folgendes an: </p>\\n \\n<p><strong>Upload-Plan am 18.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> BÄÄÄÄÄNG<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT):</strong> GTA<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT):</strong> Profi<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> Mario Kart 8<br/><strong>17:00 Uhr (PS.de)/19:00 Uhr (YT):</strong> PietCast<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> For the King<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1458-upload-plan-am-18-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 18.04.2017\"},\"UploadPlanam190420171492582234000\":{\"date\":1492582234000,\"desc\":\"\\n<p>Yippie Yah Yei Upload-Plan!</p>\\n \\n<p>Guten Morgen! Die Woche ist zur Hälfte rum, aber nicht unser Vorrat an Videos! Der ist unerschöpflich und wird noch mindestens bis 3049 halten! Heute haben wir folgendes rausgepickt:</p>\\n \\n<p><strong>Upload-Plan am 19.04.2017:</strong><br/><strong>12:00 Uhr (PS.de)/14:00 Uhr (YT):</strong> Wer bin ich?<br/><strong>13:00 Uhr (PS.de)/15:00 Uhr (YT):</strong> Mount your friends<br/><strong>14:00 Uhr (PS.de)/16:00 Uhr (YT): </strong>Worms<br/><strong>16:00 Uhr (PS.de)/18:00 Uhr (YT):</strong> TTT<br/><strong>18:00 Uhr (PS.de)/20:00 Uhr (YT):</strong> Frag PietSmiet<br/><strong>21:00 Uhr (PS.de):</strong> Factorio<br/></p> \",\"link\":\"http://www.pietsmiet.de/news/uploadplan/1466-upload-plan-am-19-04-2017\",\"scope\":\"uploadplan\",\"title\":\"Upload-Plan am 19.04.2017\"}},\"video\":{\"AUFDIEFRESSEBattleforOne101492416000000\":{\"date\":1492416000000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25118-battle-for-one-10-auf-die-fresse\",\"scope\":\"video\",\"title\":\"AUF DIE FRESSE \uD83C\uDFAE Battle for One #10\"},\"BILDERMERKENSchlagdenRaab51492423200000\":{\"date\":1492423200000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/23797-schlag-den-raab-5\",\"scope\":\"video\",\"title\":\"BILDER MERKEN \uD83C\uDFAE Schlag den Raab #5\"},\"BVBUNDRELIGIONPietCast1101492520402000\":{\"date\":1492520402000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25206-pietcast110\",\"scope\":\"video\",\"title\":\"BVB UND RELIGION \uD83C\uDFAE PietCast #110\"},\"DERBESTEMINECRAFTSEASONTROLLFragPietSmiet7741492437600000\":{\"date\":1492437600000,\"link\":\"http://www.pietsmiet.de/gallery/categories/8-frag-pietsmiet/25178-fps-774\",\"scope\":\"video\",\"title\":\"DER BESTE MINECRAFT SEASON TROLL? \uD83C\uDFAE Frag PietSmiet #774\"},\"DERLICHForTheKing71492362001000\":{\"date\":1492362001000,\"link\":\"http://www.pietsmiet.de/gallery/categories/37-pietsmietde/25146-der-lich-for-the-king-7\",\"scope\":\"video\",\"title\":\"DER LICH \uD83C\uDFAE For The King #7\"},\"DIEBESTESPIELEVERFILMUNGFragPietSmiet7731492351200000\":{\"date\":1492351200000,\"link\":\"http://www.pietsmiet.de/gallery/categories/8-frag-pietsmiet/25106-fps-773\",\"scope\":\"video\",\"title\":\"DIE BESTE SPIELE-VERFILMUNG? \uD83C\uDFAE Frag PietSmiet #773\"},\"EINENWEGSTECKENMarioKart82691492345800000\":{\"date\":1492345800000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25158-mk8-269\",\"scope\":\"video\",\"title\":\"EINEN WEGSTECKEN \uD83C\uDFAE Mario Kart 8 #269\"},\"EUERLIEBLINGSMUSIKINTERPRETFragPietSmiet7751492524000000\":{\"date\":1492524000000,\"link\":\"http://www.pietsmiet.de/gallery/categories/8-frag-pietsmiet/25194-fps-775\",\"scope\":\"video\",\"title\":\"EUER LIEBLINGS-MUSIKINTERPRET? \uD83C\uDFAE Frag PietSmiet #775\"},\"FNFTLETZTEFOLGEMarioKart82701492516800000\":{\"date\":1492516800000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/24950-lets-play-mario-kart-8-folge-270\",\"scope\":\"video\",\"title\":\"FÜNFT LETZTE FOLGE \uD83C\uDFAE Mario Kart 8 #270\"},\"INDENTODFactorioMultiplayer81492448400000\":{\"date\":1492448400000,\"link\":\"http://www.pietsmiet.de/gallery/categories/37-pietsmietde/24651-factorio-8\",\"scope\":\"video\",\"title\":\"IN DEN TOD! \uD83C\uDFAE Factorio Multiplayer #8\"},\"IRGENDWASMITBAUENMinecraftBattleBuildWars81492419602000\":{\"date\":1492419602000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25170-irgendwas-mit-bauen-minecraft-battle-built-wars-8\",\"scope\":\"video\",\"title\":\"IRGENDWAS MIT BAUEN \uD83C\uDFAE Minecraft Battle Build Wars #8\"},\"KENNICHNICHTWerbinich331492588800000\":{\"date\":1492588800000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/24778-wbi-33\",\"scope\":\"video\",\"title\":\"KENN ICH NICHT! \uD83C\uDFAE Wer bin ich? #33\"},\"KOPFANKOPFTTT4001492430402000\":{\"date\":1492430402000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25150-kopf-an-kopf-ttt-trouble-in-terrorist-town-400\",\"scope\":\"video\",\"title\":\"KOPF AN KOPF \uD83C\uDFAE TTT #400\"},\"LUFTNICHTTrialsFusion621492333201000\":{\"date\":1492333201000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25122-lauft-nicht-trials-fusion-62\",\"scope\":\"video\",\"title\":\"LÄUFT NICHT \uD83C\uDFAE Trials Fusion #62\"},\"MITFEUERSPIELTMANNICHTWormsClanWarsS221492596000000\":{\"date\":1492596000000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/24990-lets-play-clan-worms-season-2-folge-2\",\"scope\":\"video\",\"title\":\"MIT FEUER SPIELT MAN NICHT \uD83C\uDFAE Worms Clan Wars S2 #2\"},\"NeueMODIGrandTheftAutoOnline1621492506000000\":{\"date\":1492506000000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25198-gta-162\",\"scope\":\"video\",\"title\":\"Neue MODI! \uD83C\uDFAE Grand Theft Auto Online #162\"},\"NeulichimSignalIdunaParkDerChampionsLeagueVlog1492412400000\":{\"date\":1492412400000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25166-bvb-asm\",\"scope\":\"video\",\"title\":\"Neulich im Signal Iduna Park \uD83C\uDFAE Der Champions League Vlog\"},\"ORDER67Bahrain22F12016631492336800000\":{\"date\":1492336800000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25038-f1-2016-s2-bahrain-2\",\"scope\":\"video\",\"title\":\"ORDER 67 | Bahrain 2/2 \uD83C\uDFAE F1 2016 #63\"},\"PIETDERTRAITORCAPTAINTTT4011492603201000\":{\"date\":1492603201000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25210-piet-der-traitor-captain-ttt-trouble-in-terrorist-town-401\",\"scope\":\"video\",\"title\":\"PIET DER TRAITOR CAPTAIN \uD83C\uDFAE TTT #401\"},\"POTTRENNERPROFI121492509600000\":{\"date\":1492509600000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25190-profi-12\",\"scope\":\"video\",\"title\":\"POTTRENNER \uD83C\uDFAE P.RO.FI. #12\"},\"RICHTIGANDIEEIERMountyourFriends221492592401000\":{\"date\":1492592401000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25214-richtig-an-die-eier-mount-your-friends-22\",\"scope\":\"video\",\"title\":\"RICHTIG AN DIE EIER \uD83C\uDFAE Mount your Friends #22\"},\"RICHTIGEINLOCHENBummsdenBr4mm3n41492329601000\":{\"date\":1492329601000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25138-richtig-einlochen-bumms-den-brammen-4\",\"scope\":\"video\",\"title\":\"RICHTIG EINLOCHEN \uD83C\uDFAE Bumms den Br4mm3n #4\"},\"SECRETHITLERUNDMUNCHKININEINEMSPIELBang11492502400000\":{\"date\":1492502400000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25182-bang-01-secret-hitler-und-munchkin-in-einem-spiel\",\"scope\":\"video\",\"title\":\"SECRET HITLER UND MUNCHKIN IN EINEM SPIEL \uD83C\uDFAE Bang! #1\"},\"SEPWIRDGEOPFERTFactorioMultiplayer71492275600000\":{\"date\":1492275600000,\"desc\":\"Buch: http://pietsmiet.de/buch | Shirts: http://pietsmiet.de/shop | PCs: http://...\",\"link\":\"http://www.pietsmiet.de/gallery/categories/37-pietsmietde/24631-factorio-7\",\"scope\":\"video\",\"title\":\"SEP WIRD GEOPFERT \uD83C\uDFAE Factorio Multiplayer #7\"},\"WIEDERGEBURTForTheKing81492534800000\":{\"date\":1492534800000,\"link\":\"http://www.pietsmiet.de/gallery/categories/37-pietsmietde/25226-ftk-8\",\"scope\":\"video\",\"title\":\"WIEDERGEBURT \uD83C\uDFAE For The King #8\"},\"WirsindabsolutePESProfisPESLeague1492434000000\":{\"date\":1492434000000,\"link\":\"http://www.pietsmiet.de/gallery/categories/9-pietsmiet/25066-pes-turnier\",\"scope\":\"video\",\"title\":\"Wir sind absolute PES Profis | PES League\"}}}"));

        FirebasePresenter presenter = prepareFirebasePresenter(mockWebServer);

        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        presenter.fetchPostsSinceObservable(new Date())
                .map(PostBuilder::build)
                .subscribe(testSubscriber);
        List<Post> list = testSubscriber.getOnNextEvents();

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