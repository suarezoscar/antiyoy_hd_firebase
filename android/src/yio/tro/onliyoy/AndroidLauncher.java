package yio.tro.onliyoy;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import yio.tro.onliyoy.net.shared.NetSignInData;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YioGdxGame.platformType = PlatformType.android;

        YioGdxGame yioGdxGame = new YioGdxGame();
        yioGdxGame.signInManager = new StubSignInManager();
        yioGdxGame.billingManager = new StubBillingManager();

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;
        initialize(yioGdxGame, configuration);
    }

    // Stubs hasta implementar Firebase / Google Play Games.
    static class StubSignInManager implements ISignInManagerYio {
        @Override
        public void apply(NetSignInData netSignInData) {
        }

        @Override
        public boolean isReady() {
            return true;
        }
    }

    static class StubBillingManager implements IBillingManagerYio {
        @Override public void launch() { }
        @Override public void finish() { }
        @Override public void showPurchaseDialog(String productId) { }
        @Override public void onProductConsumed(String token) { }
        @Override public void restorePurchases() { }
        @Override public void launchAndRestorePurchases() { }
    }
}