package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.adapters.CardItem;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.CardTypes.*;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_DEFAULT;

public class CardItemManager {

    private List<CardItem> currentCards = new ArrayList<>();
    private List<CardItem> allCards = new ArrayList<>();
    private MainActivity mView;

    @ItemTypeDrawer
    private int currentDisplayMethod = TYPE_DEFAULT;

    public CardItemManager(MainActivity view) {
        mView = view;
    }

    public void addCard(CardItem cardItem) {
        allCards.add(cardItem);
        Collections.sort(allCards);

        //noinspection WrongConstant
        if (cardItem.getCardItemType() == currentDisplayMethod || currentDisplayMethod == TYPE_DEFAULT) {
            currentCards.add(cardItem);
            Collections.sort(currentCards);
        }
        mView.updateAdapter();
    }

    public List<CardItem> getAllCardItems() {
        return currentCards;
    }

    public void displayAllCards() {
        currentCards.clear();
        currentCards.addAll(allCards);
        mView.updateAdapter();
    }

    public void displayOnlyCardsFromType(@ItemTypeDrawer int cardItemType) {
        currentDisplayMethod = cardItemType;
        Observable.just(allCards)
                .flatMap(Observable::from)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(cardItem -> {
                    if (cardItemType == TYPE_DEFAULT) return true;
                    else {
                        //noinspection WrongConstant
                        if (cardItem.getCardItemType() == cardItemType) return true;
                    }
                    return false;
                })
                .toList()
                .subscribe(cards -> {
                    currentCards.clear();
                    currentCards.addAll(cards);
                    mView.updateAdapter();
                });
    }


}
