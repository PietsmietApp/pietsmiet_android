package de.pscom.pietsmiet.backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import de.pscom.pietsmiet.MainActivity;
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

    public UploadplanPresenter(MainActivity view) {
        super(view, UPLOAD_PLAN);
        if (SecretConstants.rssUrl == null) {
            PsLog.w("No rssUrl specified");
            return;
        }
        uploadplanUrl = SecretConstants.rssUrl;

        parseUploadplanFromDb();
    }

    private void parseUploadplanFromDb() {
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("uploadplan");

        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postBuilder = new Post.PostBuilder(UPLOAD_PLAN);
                Observable.just(dataSnapshot.getChildren())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .onBackpressureBuffer()
                        .subscribe(snapshots -> {
                            for (DataSnapshot snapshot :
                                    snapshots) {
                                String value = (String) snapshot.getValue();
                                switch (snapshot.getRef().getKey()) {
                                    case "date":
                                        postBuilder.date(new Date(value));
                                        break;
                                    case "title":
                                        postBuilder.title(value);
                                        break;
                                    case "link":
                                        postBuilder.url(value);
                                        break;
                                    case "desc":
                                        postBuilder.description(value);
                                    default:
                                        break;
                                }
                            }
                            Post post = postBuilder.build();
                            if (post == null || post.getDescription() == null) {
                                PsLog.i("Falling back to fetching uploadplan directly; " +
                                        "Database loading failed because a value was empty");
                                parseUploadplan();
                            } else {
                                posts.add(post);
                                finished();
                                PsLog.v("added uploadplan from firebase db");
                            }
                        }, (throwable) -> {
                            throwable.printStackTrace();
                            view.showError("Uploadplan from DB Error");
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null)
                    PsLog.i("Falling back to fetching uploadplan directly;" +
                            " Database loading failed because: " + databaseError.toString());
                view.showError("Uploadplan from DB Error");
                parseUploadplan();
            }
        });
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
                    postBuilder = new Post.PostBuilder(UPLOAD_PLAN);
                    postBuilder.date(element.getPubDate());
                    postBuilder.title(element.getTitle());
                })
                .map(element -> element.getLink().toString())
                .doOnNext(link -> postBuilder.url(link))
                .flatMap(link -> Observable.defer(() -> Observable.just(parseHtml(link)))
                        .subscribeOn(Schedulers.io())
                        .onBackpressureBuffer())
                .filter(content -> content != null)
                .subscribe(uploadplan -> {
                    postBuilder.description(uploadplan);
                    posts.add(postBuilder.build());
                }, (throwable) -> {
                    throwable.printStackTrace();
                    view.showError("Uploadplan parsing Error");
                }, this::finished);
    }


    @Override
    public void getNewPosts() {

    }

    @Override
    public void getPostsAfter() {

    }
}
