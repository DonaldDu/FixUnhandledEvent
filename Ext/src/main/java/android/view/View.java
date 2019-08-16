package android.view;

public class View {
    /**
     * build then get dex with only of this, exclude android.view.View & android.view.KeyEvent
     */
    public interface OnUnhandledKeyEventListener {
        boolean onUnhandledKeyEvent(View v, KeyEvent event);
    }
}
