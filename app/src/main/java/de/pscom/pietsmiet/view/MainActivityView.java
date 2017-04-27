package de.pscom.pietsmiet.view;

public interface MainActivityView {
    void loadingCompleted();

    void noNetworkError();

    void loadingStarted();

    void loadingFailed(String message);

    void showMessage(String message);
}
