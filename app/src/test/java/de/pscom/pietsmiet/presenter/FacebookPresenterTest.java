package de.pscom.pietsmiet.presenter;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.facebookApi.FacebookApiInterface;
import de.pscom.pietsmiet.repository.FacebookRepository;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SecretConstants;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;

public class FacebookPresenterTest extends MainTestPresenter {
    @Override
    public FacebookRepository preparePresenter() {
        SecretConstants.facebookToken = "s";
        MockWebServer mockWebServer = new MockWebServer();
        //todo load from file (fck facebook)
        mockWebServer.enqueue(new MockResponse().setBody("[{\"code\":200,\"body\":\"{\\\"data\\\":[{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-20T17:09:18+0000\\\",\\\"message\\\":\\\"Ich habe mal wieder eine Runde The Witcher 3 gestartet - diesmal erstmals mit Mods. Welche Mods ich benutze, zeige ich euch im neuen Video :) https:\\\\\\/\\\\\\/www.youtube.com\\\\\\/watch?v=lioDf6uCyMk\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/external.xx.fbcdn.net\\\\\\/safe_image.php?d=AQBDx-vt5hN8G8wJ&w=360&h=360&url=https\\\\u00253A\\\\u00252F\\\\u00252Fi.ytimg.com\\\\u00252Fvi\\\\u00252FlioDf6uCyMk\\\\u00252Fhqdefault.jpg&cfs=1&sx=120&sy=0&sw=360&sh=360&_nc_hash=AQC3H1naJ4KuOYUs\\\",\\\"id\\\":\\\"174416892612899_1280991308622113\\\"},{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-16T13:11:39+0000\\\",\\\"message\\\":\\\"#ghetto #part2\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/p720x720\\\\\\/17904366_1276378622416715_1107658455421765321_n.jpg?oh=70d7005d687f3a8f38bb584dcd1ad311&oe=594FC228\\\",\\\"id\\\":\\\"174416892612899_1276378622416715\\\"},{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-16T07:10:09+0000\\\",\\\"message\\\":\\\"\\\\ud83d\\\\udc23\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/p720x720\\\\\\/17951889_1276136459107598_60586929566864002_n.jpg?oh=47f0e370c49a2da4f29968e206772613&oe=5999C0FC\\\",\\\"id\\\":\\\"174416892612899_1276136459107598\\\"},{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-15T14:08:02+0000\\\",\\\"message\\\":\\\"#ghetto\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17861745_1275275475860363_5645715023741210011_n.jpg?oh=df90f2198e770de1b959ed91bb3f24a2&oe=598FDEFE\\\",\\\"id\\\":\\\"174416892612899_1275275475860363\\\"},{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-14T08:51:57+0000\\\",\\\"message\\\":\\\"Typisch? \\\\ud83d\\\\ude04\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t15.0-10\\\\\\/s720x720\\\\\\/17967070_1273955432659034_38777537433698304_n.jpg?oh=9f1f3efe4e730897ac0efaa90eb476fc&oe=594E9004\\\",\\\"id\\\":\\\"174416892612899_1273955112659066\\\"},{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-14T05:19:20+0000\\\",\\\"message\\\":\\\"\\\\ud83d\\\\udc4d\\\\ud83c\\\\udffb\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t15.0-10\\\\\\/s720x720\\\\\\/17950616_1273741832680394_2727872285334568960_n.jpg?oh=144959a1187eb4b4b8927db04d5e601b&oe=5981CD10\\\",\\\"id\\\":\\\"174416892612899_1273741596013751\\\"},{\\\"from\\\":{\\\"name\\\":\\\"PietSmiet\\\",\\\"id\\\":\\\"174416892612899\\\"},\\\"created_time\\\":\\\"2017-04-14T02:48:19+0000\\\",\\\"message\\\":\\\"Guten Morgen :D\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t15.0-10\\\\\\/17950564_1273625549358689_4981907814180978688_n.jpg?oh=892006468966c07078b5a62662b7e3a5&oe=59943D6C\\\",\\\"id\\\":\\\"174416892612899_1273625216025389\\\"}],\\\"paging\\\":{\\\"cursors\\\":{\\\"before\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXhOelEwTVRZANE9USTJNVEk0T1RrNk5UZAzVPREUzT0RjM05UUTBPVFl4TVRJek5ROE1ZAWEJwWDNOMGIzSjVYMmxrRHlBeE56UTBNVFk0T1RJMk1USTRPVGxmTVRJNE1EazVNVE13T0RZAeU1qRXhNdzhFZAEdsdFpRWlkrT3MrQVE9PQZDZD\\\",\\\"after\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXhOelEwTVRZANE9USTJNVEk0T1RrNkxUVTVOVGd5T1RVNE9Ua3hORFkxTXpVMU13OE1ZAWEJwWDNOMGIzSjVYMmxrRHlBeE56UTBNVFk0T1RJMk1USTRPVGxmTVRJM016WXlOVEl4TmpBeU5UTTRPUThFZAEdsdFpRWlk4RGh6QVE9PQZDZD\\\"}}}\"},{\"code\":200,\"body\":\"{\\\"data\\\":[{\\\"from\\\":{\\\"name\\\":\\\"Chris\\\",\\\"id\\\":\\\"276775629094183\\\"},\\\"created_time\\\":\\\"2017-04-17T11:38:55+0000\\\",\\\"message\\\":\\\"Letzte Woche waren wir gegen Monaco im Stadion und ich hab mal die Kamera mitgenommen ^^\\\\n\\\\nhttps:\\\\\\/\\\\\\/youtu.be\\\\\\/I5nbGv7k6VU\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/external.xx.fbcdn.net\\\\\\/safe_image.php?d=AQCT1nD3twYCR1H1&w=360&h=360&url=https\\\\u00253A\\\\u00252F\\\\u00252Fi.ytimg.com\\\\u00252Fvi\\\\u00252FI5nbGv7k6VU\\\\u00252Fhqdefault.jpg&cfs=1&sx=50&sy=0&sw=360&sh=360&_nc_hash=AQDz7D00HkxDaMRK\\\",\\\"id\\\":\\\"276775629094183_1130596360378768\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Chris\\\",\\\"id\\\":\\\"276775629094183\\\"},\\\"created_time\\\":\\\"2017-04-16T14:15:39+0000\\\",\\\"message\\\":\\\"Sch\\\\u00f6ne freie Tage!\\\\nMario Kart kommt erst um halb 5. Hat der Osterbeagle vor mir versteckt :p\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17952466_1129539887151082_3203607389701310735_n.jpg?oh=2287c673cbda641338b70340dfa66d61&oe=598CECA9\\\",\\\"id\\\":\\\"276775629094183_1129539887151082\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Chris\\\",\\\"id\\\":\\\"276775629094183\\\"},\\\"created_time\\\":\\\"2017-04-14T16:35:22+0000\\\",\\\"message\\\":\\\"Star Wars Trailer tiiimeee!\\\\nSieht gut aus :>\\\\n\\\\nhttps:\\\\\\/\\\\\\/youtu.be\\\\\\/zB4I68XVPzQ\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/external.xx.fbcdn.net\\\\\\/safe_image.php?d=AQCVRIEulIsZGsns&w=720&h=720&url=https\\\\u00253A\\\\u00252F\\\\u00252Fi.ytimg.com\\\\u00252Fvi\\\\u00252FzB4I68XVPzQ\\\\u00252Fmaxresdefault.jpg&cfs=1&_nc_hash=AQCqyM8nEwAego0y\\\",\\\"id\\\":\\\"276775629094183_1127292734042464\\\"}],\\\"paging\\\":{\\\"cursors\\\":{\\\"before\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5UXlOelkzTnpVMk1qa3dPVFF4T0RNNkxUWXdPVGMzTmpRNE9ERXlNekkwT0RFM016VVBER0ZA3YVY5emRHOXllVjlwWkE4ZA01qYzJOemMxTmpJNU1EazBNVGd6WHpFeE16QTFPVFl6TmpBek56ZAzNOamdQQkhScGJXVUdXUFNwVHdFPQZDZD\\\",\\\"after\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5UXlOelkzTnpVMk1qa3dPVFF4T0RNNkxUSTVNall4TWpZANU9UYzNNVFUzTVRreE5EUVBER0ZA3YVY5emRHOXllVjlwWkE4ZA01qYzJOemMxTmpJNU1EazBNVGd6WHpFeE1qY3lPVEkzTXpRd05ESTBOalFQQkhScGJXVUdXUEQ2U2dFPQZDZD\\\"}}}\"},{\"code\":200,\"body\":\"{\\\"data\\\":[{\\\"from\\\":{\\\"name\\\":\\\"Der Jay\\\",\\\"id\\\":\\\"275192789211423\\\"},\\\"created_time\\\":\\\"2017-04-19T08:50:00+0000\\\",\\\"message\\\":\\\"Bock auf Worms? Dann w\\\\u00fcrde ich ja heute mal Pietsmiet.de und YouTube im Auge behalten! :D Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1466-upload-plan-am-19-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/18055824_1557499570980732_1054245996833398710_o.jpg?oh=ede298a53b5da19ca2879c96a0e36692&oe=59795F2C\\\",\\\"id\\\":\\\"275192789211423_1557499570980732\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Der Jay\\\",\\\"id\\\":\\\"275192789211423\\\"},\\\"created_time\\\":\\\"2017-04-14T09:20:00+0000\\\",\\\"message\\\":\\\"\\\\\\\"Wer bin ich?\\\\\\\" Das haben wir uns doch alle schon mal gefragt! :D Heute kommt eine neue Folge :) Zum Upload-Plan: \\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1438-upload-plan-am-14-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17814614_1552162618181094_5866092818218521554_o.jpg?oh=1869246868fba8a236ebf5814937137d&oe=598BB062\\\",\\\"id\\\":\\\"275192789211423_1552162618181094\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Der Jay\\\",\\\"id\\\":\\\"275192789211423\\\"},\\\"created_time\\\":\\\"2017-04-13T07:48:46+0000\\\",\\\"message\\\":\\\"Heute kommt was - wie ich finde -sehr unterhaltsames! :D Bin gespannt, was ihr dazu sagt! \\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1430-upload-plan-am-13-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17814436_1550921351638554_6018123902755975883_o.jpg?oh=513fa8e6a2af00772c32bc70962efe0a&oe=598DB3E6\\\",\\\"id\\\":\\\"275192789211423_1550921351638554\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Der Jay\\\",\\\"id\\\":\\\"275192789211423\\\"},\\\"created_time\\\":\\\"2017-04-12T16:08:08+0000\\\",\\\"message\\\":\\\"Danke \\\\u0040konami, dass ich so oft diesen wundervollen Fu\\\\u00dfballverein im Stadion lautstark unterst\\\\u00fctzen darf! #bvbasm\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/p720x720\\\\\\/17904015_1550061698391186_8934033929054014750_n.jpg?oh=5f8886984e0beb014a8f27f265aeb900&oe=594F0852\\\",\\\"id\\\":\\\"275192789211423_1550061698391186\\\"}],\\\"paging\\\":{\\\"cursors\\\":{\\\"before\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXlOelV4T1RJM09Ea3lNVEUwTWpNNk5qSTBPREF4TWpReU5qWXlPREEwTXpFMk1BOE1ZAWEJwWDNOMGIzSjVYMmxrRHlBeU56VXhPVEkzT0RreU1URTBNak5mTVRVMU56UTVPVFUzTURrNE1EY3pNZAzhFZAEdsdFpRWlk5eVM0QVE9PQZDZD\\\",\\\"after\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5UXlOelV4T1RJM09Ea3lNVEUwTWpNNkxUSXhNamN6T0RRMk5UZAzRPRE14TWpjM05qTVBER0ZA3YVY5emRHOXllVjlwWkE4ZA01qYzFNVGt5TnpnNU1qRXhOREl6WHpFMU5UQXdOakUyT1Rnek9URXhPRFlQQkhScGJXVUdXTzVRNkFFPQZDZD\\\"}}}\"},{\"code\":200,\"body\":\"{\\\"data\\\":[{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-20T08:33:20+0000\\\",\\\"message\\\":\\\"So langsam neigt sich Mario Kart 8 dem Ende. Aber Deluxe steht ja schon in den Startl\\\\u00f6chern! :) Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1470-upload-plan-am-20-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/18055957_1502219859819228_5720977984493428783_o.jpg?oh=d5cdb8262bebf7b9a74d2e3fcf2d1d90&oe=59948B86\\\",\\\"id\\\":\\\"411585615549330_1502219859819228\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-19T19:05:00+0000\\\",\\\"message\\\":\\\"Wir bauen wieder F\\\\u00f6rderb\\\\u00e4nder! Und bauen Zeug ab! Und...ach, viel Spa\\\\u00df mit Factorio! :D\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/gallery\\\\\\/categories\\\\\\/37-pietsmietde\\\\\\/24671-factorio-9\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/q84\\\\\\/s720x720\\\\\\/18055683_1500848816622999_8638972286272931251_o.jpg?oh=bd712c0a6aae3d66513f14e758cd140a&oe=594D84F7\\\",\\\"id\\\":\\\"411585615549330_1500848816622999\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-19T13:37:46+0000\\\",\\\"message\\\":\\\"Behind the Scenes vom UNGL\\\\u00dcCKSRAD \\\\ud83d\\\\ude0e Grade eine neue Folge aufgenommen! \\\\ud83d\\\\ude0a\\\\nTeilen w\\\\u00fcrde mich freuen damit es viele sehen \\\\nHoffe euch gef\\\\u00e4llt es!\\\\ud83d\\\\udc4d\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t15.0-10\\\\\\/s720x720\\\\\\/18036313_1501179729923241_6436371022655520768_n.jpg?oh=9f1e5254ee83bedf384c5b08285eb6d9&oe=598AE9BD\\\",\\\"id\\\":\\\"411585615549330_1501168506591030\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-19T08:29:29+0000\\\",\\\"message\\\":\\\"Who are i am! Heute gibts ne neue Folge :) Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1466-upload-plan-am-19-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/18055656_1500833533291194_4587045236983820369_o.jpg?oh=8b690d606de20d430e7d679171bbbbde&oe=594F6348\\\",\\\"id\\\":\\\"411585615549330_1500833533291194\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-18T08:16:14+0000\\\",\\\"message\\\":\\\"Die Jungs haben was spannendes aufgenommen, mal schauen was ihr davon haltet! :D Zum Upload-Plan: \\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1458-upload-plan-am-18-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17990614_1499313376776543_4728946104623470283_o.jpg?oh=5847e44c27215b8f9091aac4183b3e3d&oe=599658D7\\\",\\\"id\\\":\\\"411585615549330_1499313376776543\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-16T16:56:39+0000\\\",\\\"message\\\":\\\"#Schwerelos #Abgehoben \\\\nZeit f\\\\u00fcr lustige Memes \\\\ud83d\\\\ude02\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/p720x720\\\\\\/17990901_1497267396981141_5286687261773392639_n.jpg?oh=d7a179aafc9aa5044db89dbc84693b95&oe=597B50B5\\\",\\\"id\\\":\\\"411585615549330_1497267396981141\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-16T12:59:34+0000\\\",\\\"message\\\":\\\"#SniperSep 25\\\\\\/25 \\\\ud83d\\\\ude0e\\\\nDer Freundin erstmal einen pinken Kackhaufen geschossen \\\\ud83d\\\\udca9\\\\ud83d\\\\udc9c\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t15.0-10\\\\\\/17996597_1497033717004509_8440124341714157568_n.jpg?oh=4d66904e91c1884f5d02183f30ee2391&oe=594DC59D\\\",\\\"id\\\":\\\"411585615549330_1497033030337911\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-16T10:09:26+0000\\\",\\\"message\\\":\\\"Frohe Ostern! \\\\ud83d\\\\udc30\\\\ud83e\\\\udd5a\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17951569_1496864937021387_4298762851251341655_n.jpg?oh=177eafb7c825665f491635ad7f594d81&oe=5995980C\\\",\\\"id\\\":\\\"411585615549330_1496864937021387\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-15T14:48:25+0000\\\",\\\"message\\\":\\\"Es war sau geil!\\\\ud83d\\\\ude80\\\\ud83c\\\\udf0d \\\\nSo f\\\\u00fchlen sich also die Charaktere in #masseffectandromeda \\\\ud83d\\\\ude0e #abgehoben #schwerelos\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-0\\\\\\/p480x480\\\\\\/17904488_1495681573806390_7682828674431149328_n.jpg?oh=424d648b78fde7f4b7f85b0f20e5322b&oe=5978844A\\\",\\\"id\\\":\\\"411585615549330_1495681573806390\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-14T12:02:34+0000\\\",\\\"message\\\":\\\"Es war einfach unbeschreiblich \\\\u00fcberw\\\\u00e4ltigend! #ZeroG \\\\nDas Video wird awesome! \\\\nDanke nochmal an #masseffect\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/p720x720\\\\\\/17904404_1493952507312630_161231800175011698_n.jpg?oh=563c792ea4120be06083118eafd027d7&oe=597C811D\\\",\\\"id\\\":\\\"411585615549330_1493952507312630\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-14T12:01:57+0000\\\",\\\"message\\\":\\\"Gleich geht's los! Es wird geil!!! #panik #aufgeregt #masseffect\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17523235_1493875297320351_8070178570377604143_n.jpg?oh=f1a3d201c132ef096ba4ede62934d0cc&oe=599AE95E\\\",\\\"id\\\":\\\"411585615549330_1493875297320351\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-14T08:57:53+0000\\\",\\\"message\\\":\\\"Voller Tag heute wieder, wir beginnen am besten mal mit UNO! :D Zum Upload-Plan: \\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1438-upload-plan-am-14-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17814376_1493802977327583_3046619924126534621_o.jpg?oh=9e9e0297be0a64e831c56bfd0264cbb2&oe=5997410C\\\",\\\"id\\\":\\\"411585615549330_1493802977327583\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-13T19:00:00+0000\\\",\\\"message\\\":\\\"Mit Br4mm3n geht es weiter in Factorio! :) Hier der Link zu PietSmiet.de: \\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/gallery\\\\\\/categories\\\\\\/37-pietsmietde\\\\\\/24659-factorio-6\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/q81\\\\\\/s720x720\\\\\\/17917833_1492298207478060_437877504089625531_o.jpg?oh=6439601e899220a0eb4130370ba2461e&oe=5982288D\\\",\\\"id\\\":\\\"411585615549330_1492298207478060\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-13T15:07:49+0000\\\",\\\"message\\\":\\\"Bordeaux erkunden\\\\ud83d\\\\ude0e echt eine sch\\\\u00f6ne Altstadt!\\\\nMorgen hei\\\\u00dft es \\\\\\\"Schwerelos\\\\\\\" mit #masseffectandromeda \\\\ud83d\\\\ude80\\\\ud83c\\\\udf0d #vorfreude\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17883608_1492779650763249_2211987764976353878_n.jpg?oh=3cab473f58fe82d3199a2cceda87b239&oe=59926EA0\\\",\\\"id\\\":\\\"411585615549330_1492779650763249\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-13T05:49:24+0000\\\",\\\"message\\\":\\\"Da g\\\\u00f6nnt sich der #Septonaut \\\\ud83d\\\\ude0b \\\\nAuf dem Weg nach Bordeaux zum #Parabelflug \\\\ud83c\\\\udf0d\\\\ud83d\\\\ude80 #masseffectandromeda\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/p720x720\\\\\\/17883618_1492198177488063_2221574655183301038_n.jpg?oh=a6a27541b3d8aeacac244fe32bc0d517&oe=5981DB16\\\",\\\"id\\\":\\\"411585615549330_1492198177488063\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-12T18:55:30+0000\\\",\\\"message\\\":\\\"Was ein Spiel! Ein auf und ab.. \\\\nTrotzdem toll gek\\\\u00e4mpft und das nach dem Anschlag gestern! \\\\ud83d\\\\udc9b Tolle Kulisse! Danke an \\\\u0040Konami \\\\ud83c\\\\udfae\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17862765_1491660490875165_8801114002834846839_n.jpg?oh=01e11308ef8cb897610ed7674bd76292&oe=59836402\\\",\\\"id\\\":\\\"411585615549330_1491660490875165\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-12T08:02:59+0000\\\",\\\"message\\\":\\\"Wir spielen heute wieder mit unseren kleinen W\\\\u00fcrmern. Also... ihr wisst schon! Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1426-upload-plan-am-12-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17917445_1490984324276115_2811673702510891672_o.jpg?oh=662f3c95b67da73af087133248917f86&oe=598D5955\\\",\\\"id\\\":\\\"411585615549330_1490984324276115\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-11T17:29:32+0000\\\",\\\"message\\\":\\\"Am Samstag Abend schon den Platz f\\\\u00fcr heute gesichert! \\\\nHeute besonders Anfeuern!\\\\ud83c\\\\udf89\\\\nDanke, dass ihr mich rein gelassen habt Pro Evolution Soccer \\\\ud83d\\\\ude48 #bvbasm\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-0\\\\\\/p180x540\\\\\\/17862285_1490128247695056_4357566538845329221_n.jpg?oh=f247400c26e13f8cfa750318ebb9f923&oe=5999BF75\\\",\\\"id\\\":\\\"411585615549330_1490128247695056\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Sep Pietsmiet\\\",\\\"id\\\":\\\"411585615549330\\\"},\\\"created_time\\\":\\\"2017-04-11T14:08:05+0000\\\",\\\"message\\\":\\\"Neues #StartUpMonday Projekt! \\\\nHab sogar ein eigenes TV-Studio gebaut! Lasst gerne Feedback da :)\\\\n>> https:\\\\\\/\\\\\\/www.youtube.com\\\\\\/watch?v=cUhZ3orLBnM\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/s720x720\\\\\\/17883794_1489856767722204_4083161633654692096_n.jpg?oh=34f3b24266e2a7009569e5aebd1d62dd&oe=5987BB6D\\\",\\\"id\\\":\\\"411585615549330_1489856767722204\\\"}],\\\"paging\\\":{\\\"cursors\\\":{\\\"before\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TTBNVEUxT0RVMk1UVTFORGt6TXpBNkxURTNOelkyTnpRek16ZAzFNak0zTWpNNU9ROE1ZAWEJwWDNOMGIzSjVYMmxrRHlBME1URTFPRFUyTVRVMU5Ea3pNekJmTVRVd01qSXhPVGcxT1RneE9USXlPQThFZAEdsdFpRWlkrSEpRQVE9PQZDZD\\\",\\\"after\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5STBNVEUxT0RVMk1UVTFORGt6TXpBNk5qWXpOalU0TWpBM05UYzJNalk0T1RNMER3eGhjR2xmYzNSdmNubGZAhV1FQSURReE1UVTROVFl4TlRVME9UTXpNRjh4TkRnNU9EVTJOelkzTnpJeU1qQTBEd1IwYVcxbEJsanM0MFVC\\\"}}}\"},{\"code\":200,\"body\":\"{\\\"data\\\":[{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-20T09:00:00+0000\\\",\\\"message\\\":\\\"Lobet den K\\\\u00f6nig! ... Ich sollte h\\\\u00e4ufiger mit Headset und Szepter rumlaufen :D Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1470-upload-plan-am-20-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/q82\\\\\\/s720x720\\\\\\/17973882_1534573516585153_7421038397656654555_o.jpg?oh=eef95d43915bf0306444532b09acd7b8&oe=597B6CE7\\\",\\\"id\\\":\\\"298837886825395_1534573516585153\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-19T09:00:00+0000\\\",\\\"message\\\":\\\"Der Snob macht sich am Steuer seiner Yacht eigentlich ganz gut :D Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1466-upload-plan-am-19-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17966568_1533079696734535_2742575458062335791_o.jpg?oh=8f92976e51a6498de4345c1e9319ef3a&oe=598660BE\\\",\\\"id\\\":\\\"298837886825395_1533079696734535\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-18T10:54:08+0000\\\",\\\"message\\\":\\\"B\\\\u00c4M alter da gibt es #Neuwahlen auf der Insel. Ob das nochmal den #brexit kippen k\\\\u00f6nnte? \\\\n\\\\nIch w\\\\u00fcrde es mir w\\\\u00fcnschen\\\\n\\\\nAuch mit ihren Fehlern die EU ist gut. Und ja man kann viel verbessern. Aber unterm Strich ist sie ein Zugewinn. Und gerade wir Deutschen meckern ja gerne auch bei guten Sachen gerne nur \\\\u00fcber das schlechte. \\\\nAber die Freiheit und Frieden und Wohlstand (Und ja ich sage Wohlstand verglichen mit L\\\\u00e4nder wie Syrien wo dir der Arsch weggebombt wird, oder L\\\\u00e4ndern in Afrika, wo du nichts zu fressen hast und und und) zwischen den EU L\\\\u00e4ndern auf dauer ist es einfach wert, ein nicht perfektes System zu verbessern und daran zu arbeiten, statt es zu verlassen.\\\\n\\\\nMeine Hoffnung kommt wieder. Ich mag die Briten\\\",\\\"id\\\":\\\"298837886825395_1531713083537863\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-16T21:05:57+0000\\\",\\\"message\\\":\\\"Das #Referendum geht mit \\\\\\\"Ja\\\\\\\" aus... Find ich pers\\\\u00f6nlich nicht gut, das Demokratie in jeglicher Form untergraben wird. \\\\nEin starker F\\\\u00fchrer kann nie richtig sein. Glaubt mir. Wir Deutschen wissen das genau...\\\",\\\"id\\\":\\\"298837886825395_1529757330400105\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-14T19:50:13+0000\\\",\\\"message\\\":\\\"Fuck Yeah.\\\\n\\\\nVor drei Stunden rausgekommen und ich muss Ihn gucken. Ich will Ihn sehen. Und ja ich hype den Total. Ist mir egal. \\\\nICH WILL IHN JETZT. \\\\n\\\\nUnd nochmal den Trailer gucken... \\\\nnoch so lange :(\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t15.0-10\\\\\\/s720x720\\\\\\/17967137_1526874404021731_1574438124841336832_n.jpg?oh=6863710a22e8bc02beaeb8de321fa252&oe=5998D04B\\\",\\\"id\\\":\\\"298837886825395_1526871017355403\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-14T09:50:03+0000\\\",\\\"message\\\":\\\"Ein Freitag ohne Trashnight ist kein Freitag. Also technisch gesehen schon, aber ihr wisst was ich meine! :D Zum Upload-Plan: \\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1438-upload-plan-am-14-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17854813_1526210964088075_8255716860529889070_o.jpg?oh=4e05353e7adcada845e64c1895a997e6&oe=59902F78\\\",\\\"id\\\":\\\"298837886825395_1526210964088075\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-13T12:58:48+0000\\\",\\\"message\\\":\\\"Versteh ich nicht xD\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/17904326_1524918767550628_4117246994854322743_n.png?oh=c6a769c1b7573365119a25755cb94f4b&oe=594CC3BD\\\",\\\"id\\\":\\\"298837886825395_1524918767550628\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-13T08:20:00+0000\\\",\\\"message\\\":\\\"Dieses diabolische Lachen bekommt auch nur ein Der Jay hin :D So ein Schlingel! Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1430-upload-plan-am-13-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17880304_1524645097577995_2975430855116916151_o.jpg?oh=91c389355226c3b9194c8c393fedf290&oe=597C4640\\\",\\\"id\\\":\\\"298837886825395_1524645097577995\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-12T08:30:00+0000\\\",\\\"message\\\":\\\"Dieses nervenaufreibende Golf It wird irgendwann unser aller Untergang! :D Zum Upload-Plan:\\\\nhttp:\\\\\\/\\\\\\/www.pietsmiet.de\\\\\\/news\\\\\\/uploadplan\\\\\\/1426-upload-plan-am-12-04-2017\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t31.0-8\\\\\\/s720x720\\\\\\/17880743_1523256711050167_6093651975911818621_o.jpg?oh=f2df59905acdb181fba6e3284b48b3dd&oe=5981187E\\\",\\\"id\\\":\\\"298837886825395_1523256711050167\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-11T18:20:24+0000\\\",\\\"message\\\":\\\"Gerade mitbekommen, dass in Dortmund am Vereinsbus des BVB eine Bombe explodiert ist. \\\\nMehr Infos gibt es dann von offizieller Seite. Und hier auch nochmal von der Polizei NRW bzw. Polizei Dortmund, dass doch bitte keine Ger\\\\u00fcchte in Umlauf gebracht werden sollen. Das wollte ich dazu mal teilen. \\\\nUnsere Jungs von PietSmiet sind in Ordnung, die sich gerade im Stadion befanden.\\\\n\\\\nAnsonsten echt mal wieder Spasten auf der Welt, die nichts besseres zu tun haben als so einen schei\\\\u00df zu veranstalten...\\\\n\\\\nIch verstehe das bis heute nicht, wie man sowas machen kann. Scheint etwas zu sein, f\\\\u00fcr das ich einfach zu Dumm bin. Und selbst wenn ich zu Dumm daf\\\\u00fcr bin das zu verstehen, habe ich f\\\\u00fcr solche Menschen nichts als Verachtung \\\\u00fcbrig. Und egal wer es war, hoffentlich werden die gefangen und dann hinter Gitter und Schl\\\\u00fcssel weg. \\\\nSolche Menschen brauch ich echt nicht. Das aller Letzte\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/scontent.xx.fbcdn.net\\\\\\/v\\\\\\/t1.0-9\\\\\\/17884069_1522515731124265_1597486922171715637_n.png?oh=46c4ed0630c4eb5cfe199f247708e89f&oe=598EC962\\\",\\\"id\\\":\\\"298837886825395_1522515731124265\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-11T07:50:15+0000\\\",\\\"message\\\":\\\"3 Tage kommt die Grippe, 3 Tage hast du sie, 3 Tage geht sie wieder. Sagte Oma wieder. \\\\n\\\\n#Tag1\\\\nDas wird ein schei\\\\u00df Wochenende :D\\\",\\\"id\\\":\\\"298837886825395_1521895424519629\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-10T22:31:03+0000\\\",\\\"message\\\":\\\"Br4mm3n kommt wie Blanka.\\\\n\\\\nKeine Ahnung was das hei\\\\u00dft, aber geilste Punchline\\\",\\\"id\\\":\\\"298837886825395_1521353041240534\\\"},{\\\"from\\\":{\\\"name\\\":\\\"Br4mm3n\\\",\\\"id\\\":\\\"298837886825395\\\"},\\\"created_time\\\":\\\"2017-04-10T22:25:08+0000\\\",\\\"message\\\":\\\"Da bin ich abends nochmal auf Twitch unterwegs. Und h\\\\u00e4nge bei nem Typen fest, der noch schlechter Rapt als ich xD\\\\n\\\\nUnfassbar geiler Chat gerade\\\\n\\\\nhttps:\\\\\\/\\\\\\/www.twitch.tv\\\\\\/iamegon\\\",\\\"full_picture\\\":\\\"https:\\\\\\/\\\\\\/external.xx.fbcdn.net\\\\\\/safe_image.php?d=AQCde84KKaXtw3Ea&w=300&h=300&url=https\\\\u00253A\\\\u00252F\\\\u00252Fscontent.xx.fbcdn.net\\\\u00252Ft45.1600-4\\\\u00252F17508280_6111768799213_838149428861730816_n.png&cfs=1&sx=0&sy=0&sw=300&sh=300&_nc_hash=AQDvG7ktUNQQHtCO\\\",\\\"id\\\":\\\"298837886825395_1521349701240868\\\"}],\\\"paging\\\":{\\\"cursors\\\":{\\\"before\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXlPVGc0TXpjNE9EWTRNalV6T1RVNk16RXlNRFkyT0RrME5qRXdPVEEzTURrM05ROE1ZAWEJwWDNOMGIzSjVYMmxrRHlBeU9UZAzRNemM0T0RZANE1qVXpPVFZAmTVRVek5EVTNNelV4TmpVNE5URTFNdzhFZAEdsdFpRWlkrSGlRQVE9PQZDZD\\\",\\\"after\\\":\\\"Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5UXlPVGc0TXpjNE9EWTRNalV6T1RVNkxURTFNRE0zTmpZAeU5ERXpNams1TXpreE1UY1BER0ZA3YVY5emRHOXllVjlwWkE4ZA01qazRPRE0zT0RnMk9ESTFNemsxWHpFMU1qRXpORGszTURFeU5EQTROamdQQkhScGJXVUdXT3dHUkFFPQZDZD\\\"}}}\"}]"));
        FacebookRepository presenter = new FacebookRepository(mMockContext);
        presenter.apiInterface = getRetrofit(mockWebServer).create(FacebookApiInterface.class);
        return presenter;
    }

    @Test
    public void fetchPostsSinceObservable() throws Exception {
        FacebookRepository presenter = preparePresenter();
        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        presenter.fetchPostsSinceObservable(new Date())
                .map(Post.PostBuilder::build)
                .subscribe(testSubscriber);
        List<Post> list = testSubscriber.getOnNextEvents();
        List<Throwable> kl = testSubscriber.getOnErrorEvents();
        for (Throwable tr : kl) {
            System.out.println(tr.getMessage());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Post example1 = new Post.PostBuilder(PostType.FACEBOOK)
                .title("Der Jay")
                .url("http://www.facebook.com/" + "275192789211423_1550921351638554")
                .date(dateFormat.parse("2017-04-13T07:48:46+0000"))
                .description("Heute kommt was - wie ich finde -sehr unterhaltsames! :D Bin gespannt, was ihr dazu sagt! \nhttp://www.pietsmiet.de/news/uploadplan/1430-upload-plan-am-13-04-2017")
                .build();

        Post example2 = new Post.PostBuilder(PostType.FACEBOOK)
                .title("Sep Pietsmiet")
                .url("http://www.facebook.com/" + "411585615549330_1499313376776543")
                .date(dateFormat.parse("2017-04-18T08:16:14+0000"))
                .description("Die Jungs haben was spannendes aufgenommen, mal schauen was ihr davon haltet! :D Zum Upload-Plan: \nhttp://www.pietsmiet.de/news/uploadplan/1458-upload-plan-am-18-04-2017")
                .build();

        assertTrue(list.contains(example1));
        assertTrue(list.contains(example2));
    }

}
