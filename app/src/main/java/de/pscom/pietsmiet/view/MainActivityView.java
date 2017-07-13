package de.pscom.pietsmiet.view;

public interface MainActivityView {
    void freshLoadingCompleted();

    void loadingNextCompleted(int startPosition, int itemCount);

    void loadingNewCompleted(int itemCount);

    void noNetworkError();

    void loadingStarted();

    void loadingFailed(String message, boolean fetchDirectionDown);

    void showMessage(String message);
}
