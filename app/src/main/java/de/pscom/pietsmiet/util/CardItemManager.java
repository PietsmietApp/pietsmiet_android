package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.adapters.CardItem;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.CardType.*;

public class CardItemManager {
    private List<CardItem> currentCards = new ArrayList<>();
    private List<CardItem> allCards = new ArrayList<>();
    private MainActivity mView;
    @ItemTypeDrawer
    private int currentlyDisplayedType = TYPE_DISPLAY_ALL;

    public CardItemManager(MainActivity view) {
        mView = view;
    }

    public void addCard(CardItem cardItem) {
        allCards.add(cardItem);
        Collections.sort(allCards);

        //noinspection WrongConstant
        if (cardItem.getCardItemType() == currentlyDisplayedType
                || currentlyDisplayedType == TYPE_DISPLAY_ALL) {
            currentCards.add(cardItem);
            Collections.sort(currentCards);
        }
        if (mView != null) mView.updateAdapter();
    }

    public List<CardItem> getAllCardItems() {
        return currentCards;
    }

    public void displayAllCards() {
        currentlyDisplayedType = TYPE_DISPLAY_ALL;
        currentCards.clear();
        currentCards.addAll(allCards);
        if (mView != null) mView.updateAdapter();
    }

    public void displayOnlyCardsFromType(@ItemTypeDrawer int cardItemType) {
        currentlyDisplayedType = cardItemType;
        Observable.just(allCards)
                .flatMap(Observable::from)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(this::isAllowedType)
                .toList()
                .subscribe(cards -> {
                    currentCards.clear();
                    currentCards.addAll(cards);
                    if (mView != null) mView.updateAdapter();
                });
    }

    private boolean isAllowedType(CardItem cardItem) {
        int cardItemType = cardItem.getCardItemType();
        if (currentlyDisplayedType == TYPE_DISPLAY_ALL) return true;
        else if (currentlyDisplayedType == TYPE_DISPLAY_SOCIAL) {
            if (cardItemType == TYPE_TWITTER
                    || cardItemType == TYPE_FACEBOOK) {
                return true;
            }
        } else {
            //noinspection WrongConstant
            if (cardItemType == currentlyDisplayedType) return true;
        }
        return false;
    }


}
