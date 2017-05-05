package de.pscom.pietsmiet.view;

public interface MainActivityView {
    void freshLoadingCompleted();

    void loadingItemRangeInserted(int startPosition, int itemCount);

    void noNetworkError();

    void loadingStarted();

    void loadingFailed(String message);

    void showMessage(String message);
}
