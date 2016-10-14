package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.adapters.CardItem;

import static de.pscom.pietsmiet.util.CardType.ItemTypeAll;

public class MainPresenter {
    @ItemTypeAll
    private int cardItemType;
    private MainActivity view;
    CardItem cardItem;

    MainPresenter(@ItemTypeAll int cardItemType) {
        this.cardItemType = cardItemType;
    }

    void publish() {
        if (view != null) {
            if (cardItem != null) {
                cardItem.setCardItemType(cardItemType);
                view.addNewCard(cardItem);
            } else {
                view.showError("Typ" + Integer.toString(cardItemType) + " konnte nicht geladen werden :(");
            }
        }
    }

    public void onTakeView(MainActivity view) {
        this.view = view;
    }
}
