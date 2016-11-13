package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;
import static de.pscom.pietsmiet.util.RssUtil.parseHtml;

public class UploadplanPresenter extends MainPresenter {
    public static final int MAX_COUNT = 1;
    private static String uploadplanUrl;

    public UploadplanPresenter() {
        super(UPLOAD_PLAN);
        if (SecretConstants.rssUrl == null) {
            PsLog.w("No rssUrl specified");
            return;
        }
        uploadplanUrl = SecretConstants.rssUrl;

        parseUploadplan();
    }

    /**
     * Loads the latest uploadplan URLS and parses them
     */
    private void parseUploadplan() {
        Observable.defer(() -> Observable.just(loadRss(uploadplanUrl)))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMap(Observable::from)
                .take(MAX_COUNT)
                .doOnNext(element -> {
                    post = new Post();
                    post.setDatetime(element.getPubDate());
                    post.setTitle(element.getTitle());
                })
                .map(element -> element.getLink().toString())
                .doOnNext(link -> post.setUrl(link))
                .flatMap(link -> Observable.defer(() -> Observable.just(parseHtml(link)))
                        .subscribeOn(Schedulers.io())
                        .onBackpressureBuffer())
                .filter(content -> content != null)
                .subscribe(uploadplan -> {
                    post.setDescription(uploadplan);
                    post.setPostType(UPLOAD_PLAN);
                    posts.add(post);
                }, Throwable::printStackTrace, this::finished);
    }


}
