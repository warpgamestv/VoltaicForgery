package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.Constants;
import com.warpgames.voltaicforgery.platform.services.IPlatformHelper;
import com.warpgames.voltaicforgery.platform.services.IVoltaicCapabilities;
import com.warpgames.voltaicforgery.platform.services.IVoltaicRegistrar;

import java.util.ServiceLoader;

// Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
// environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
// is swapped out for the platform specific implementation at runtime.
public class Services {

    // Provides information about the active loader and environment.
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * Platform-specific content registration (blocks, items, block entities).
     * Common code declares what to register; loader code decides how.
     */
    public static final IVoltaicRegistrar REGISTRAR = load(IVoltaicRegistrar.class);

    /**
     * Platform-specific capability / transfer wiring (energy + fluid exposure).
     * Common code owns the actual storage implementation.
     */
    public static final IVoltaicCapabilities CAPABILITIES = load(IVoltaicCapabilities.class);

    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform. For
    // Each loader resource points to its implementation class.
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}

