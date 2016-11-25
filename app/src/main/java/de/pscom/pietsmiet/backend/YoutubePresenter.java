package de.pscom.pietsmiet.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class YoutubePresenter extends MainPresenter {

    private static final String urlYTAPI = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails%2Cstatus+&playlistId=UUqwGaUvq_l0RKszeHhZ5leA&maxResults=";
    static final int MAX_COUNT = 10;

    public YoutubePresenter(MainActivity view) {
        super(view, VIDEO);
        parsePlaylist();
    }

    /**
     * Parsing upload Playlist from YT
     */
    private void parsePlaylist() {
        Observable.defer(() -> Observable.from(loadJSON()))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .filter(result -> result != null)
                .subscribe(jsonobj -> {
                    this.post = new Post();
                    try {
                        JSONObject jsnipp = jsonobj.getJSONObject("snippet");
                        JSONObject jthumb = jsnipp.getJSONObject("thumbnails").getJSONObject("default");
                        post.setThumbnail(DrawableFetcher.getDrawableFromUrl(jthumb.getString("url")));
                        post.setTitle(jsnipp.getString("title"));
                        post.setDescription(jsnipp.getString("title"));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.GERMANY);
                        post.setDatetime(dateFormat.parse(jsnipp.getString("publishedAt")));
                        post.setPostType(VIDEO);
                        String videoID = jsonobj.getJSONObject("contentDetails").getString("videoId");
                        if (videoID != null && !videoID.isEmpty()) {
                            post.setUrl("http://www.youtube.com/watch?v=" + videoID);
                        }
                    } catch (Exception e) {
                        PsLog.e(e.getMessage());
                    }
                    posts.add(post);
                }, e -> PsLog.e(e.toString()), this::finished);

    }

    private JSONObject[] loadJSON() {
        JSONObject[] items = null;
        try {
            URL uYT = new URL(urlYTAPI + MAX_COUNT + "&key=" + SecretConstants.youtubeAPIkey);
            Scanner sc = new Scanner(uYT.openStream());
            String jsonStr = "";
            while (sc.hasNext()) {
                jsonStr += sc.useDelimiter("\\A").next();
            }
            JSONObject root = new JSONObject(jsonStr);
            JSONArray jitems = root.getJSONArray("items");
            items = new JSONObject[jitems.length()];
            for (int i = 0; i < jitems.length(); i++) {
                items[i] = jitems.getJSONObject(i);
            }

        } catch (Exception e) {
            PsLog.e(e.getMessage());
        }
        return items;
    }

}
