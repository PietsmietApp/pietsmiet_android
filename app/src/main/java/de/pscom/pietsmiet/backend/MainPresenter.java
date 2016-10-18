package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;

import static de.pscom.pietsmiet.util.CardType.ItemTypeAll;

public class MainPresenter {
    @ItemTypeAll
    private int cardItemType;
    private MainActivity view;
    Post post;

    MainPresenter(@ItemTypeAll int cardItemType) {
        this.cardItemType = cardItemType;
    }

    /**
     * Publishes the current cardItem to the specified activity
     */
    void publish() {
        if (view != null) {
            if (post != null) {
                post.setCardItemType(cardItemType);
                view.addNewCard(post);
            } else {
                view.showError("Typ" + Integer.toString(cardItemType) + " konnte nicht geladen werden :(");
            }
        }
    }

    /**
     * Add a "callback" activity to this class
     * @param view Activity to call back
     */
    public void onTakeView(MainActivity view) {
        this.view = view;
    }
}
