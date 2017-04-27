package de.pscom.pietsmiet.interfaces;

import java.util.List;

import de.pscom.pietsmiet.generic.ViewItem;

public interface MainActivityView {
    void loadingCompleted(List<ViewItem> posts);

    void noNetworkError();

    void loadingStarted();

    void loadingFailed(String message);
}
