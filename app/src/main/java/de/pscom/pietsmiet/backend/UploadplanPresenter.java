package de.pscom.pietsmiet.backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;
import static de.pscom.pietsmiet.util.RssUtil.parseHtml;

public class UploadplanPresenter extends MainPresenter {
    private static final int DEFAULT_MAX = 1;
    private static String uploadplanUrl;

    public UploadplanPresenter() {
        super(UPLOAD_PLAN);
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
                Post post = new Post();
                post.setPostType(UPLOAD_PLAN);
                PsLog.v(dataSnapshot.toString());
                Observable.just(dataSnapshot.getChildren())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .onBackpressureBuffer()
                        .map(snapshots -> {
                            for (DataSnapshot snapshot :
                                    snapshots) {
                                String value = (String) snapshot.getValue();
                                PsLog.v(value);
                                PsLog.v(snapshot.getRef().getKey() + " \n<<<<<<<<");
                                switch (snapshot.getRef().getKey()) {
                                    case "date":
                                        post.setDatetime(new Date(value));
                                        break;
                                    case "title":
                                        post.setTitle(value);
                                        break;
                                    case "link":
                                        return value;
                                    default:
                                        break;
                                }
                            }
                            return null;
                        })
                        .filter(link -> link != null)
                        .flatMap(link -> Observable.just(parseHtml(link))
                                .subscribeOn(Schedulers.io())
                                .onBackpressureBuffer())
                        .subscribe(description -> {
                            if (post.getTitle() == null || post.getDate() == null || description.isEmpty()) {
                                PsLog.i("Falling back to fetching uploadplan directly; " +
                                        "Database loading failed because a value was empty");
                                parseUploadplan();
                            } else {
                                post.setDescription(description);
                                posts.add(post);
                                finished();
                                PsLog.v("added uploadplan from firebase db");
                            }
                        }, Throwable::printStackTrace);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null)
                    PsLog.i("Falling back to fetching uploadplan directly;" +
                            " Database loading failed because: " + databaseError.toString());
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
                .doOnNext(element -> {
                    post = new Post();
                    post.setDatetime(element.getPubDate());
                    post.setTitle(element.getTitle());
                })
                .map(element -> element.getLink().toString())
                .take(DEFAULT_MAX)
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
