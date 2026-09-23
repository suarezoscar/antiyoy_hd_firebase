package yio.tro.onliyoy;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import yio.tro.onliyoy.stuff.GraphicsYio;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YioGdxGame.platformType = PlatformType.android;

        YioGdxGame yioGdxGame = new YioGdxGame();

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;
        initialize(yioGdxGame, configuration);

        initSafeArea();
    }

    // Expone los insets reales (notch + barras del sistema + gestos) al core para
    // que la UI respete la zona segura (alignTop/alignBottom).
    private void initSafeArea() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                applyInsets(insets);
                return insets;
            }
        });
        decorView.post(new Runnable() {
            @Override
            public void run() {
                WindowInsets insets = decorView.getRootWindowInsets();
                if (insets != null) applyInsets(insets);
            }
        });
    }

    private void applyInsets(WindowInsets insets) {
        int top = 0;
        int bottom = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.graphics.Insets bars = insets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            top = bars.top;
            bottom = bars.bottom;
        } else {
            top = insets.getSystemWindowInsetTop();
            bottom = insets.getSystemWindowInsetBottom();
        }
        GraphicsYio.safeAreaTop = top;
        GraphicsYio.safeAreaBottom = bottom;
    }
}