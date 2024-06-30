package com.kandclay.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

public class MyAssetManager implements Disposable {
    private static MyAssetManager instance;
    private final AssetManager assetManager;

    /**
     * Private constructor to prevent instantiation
     */
    private MyAssetManager() {
        assetManager = new AssetManager();
    }

    /**
     * Thread-safe method to get the singleton instance
     * @return the singleton instance of MyAssetManager
     */
    public static synchronized MyAssetManager getInstance() {
        if (instance == null) {
            instance = new MyAssetManager();
        }
        return instance;
    }


    public synchronized void load(String asset, Class<?> type) {
        if (!assetManager.isLoaded(asset)) {
            assetManager.load(asset, type);
        }
    }

    // Get a loaded asset
    public synchronized <T> T get(String asset, Class<T> type) {
        if (assetManager.isLoaded(asset)) {
            return assetManager.get(asset, type);
        } else {
            throw new RuntimeException("Asset not loaded: " + asset);
        }
    }

    // Finish loading all assets
    public void finishLoading() {
        assetManager.finishLoading();
    }

    /**
     * Update the asset manager
     * @return true if the asset manager is done loading
     */
    public boolean update() {
        return assetManager.update();
    }

    /**
     * Get the progress of the asset manager
     * @return the progress of the asset manager
     */
    public float getProgress() {
        return assetManager.getProgress();
    }

    // Dispose of the asset manager and all its assets
    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
