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
import rx.Observable;

import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class UploadplanPresenter extends MainPresenter {
    private static final String KEY_DATE = "date";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCRIPTION = "desc";

    public UploadplanPresenter(MainActivity view) {
        super(view);
    }

    private Observable<Post.PostBuilder> parseUploadplanFromDb(String scope) {
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child(scope);
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Observable.just(dataSnapshot.getChildren())
                        .map(snapshots -> {
                            postBuilder = new Post.PostBuilder(UPLOADPLAN);
                            for (DataSnapshot snapshot :
                                    snapshots) {
                                String value = (String) snapshot.getValue();
                                switch (snapshot.getRef().getKey()) {
                                    case KEY_DATE:
                                        postBuilder.date(new Date(value));
                                        break;
                                    case KEY_TITLE:
                                        postBuilder.title(value);
                                        break;
                                    case KEY_LINK:
                                        postBuilder.url(value);
                                        break;
                                    case KEY_DESCRIPTION:
                                        postBuilder.description(value);
                                    default:
                                        break;
                                }
                            }
                            return postBuilder;
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null)
                    PsLog.e("Database loading failed because: " + databaseError.toString());
                view.showError("Typ" + scope + " konnte nicht geladen werden");
            }
        });
        return null; //fixme
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore) {
        return parseUploadplanFromDb("uploadplan");

    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        return parseUploadplanFromDb("uploadplan");
        //fixme richtig?
    }
}
