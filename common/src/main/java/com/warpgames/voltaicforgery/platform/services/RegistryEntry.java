package com.warpgames.voltaicforgery.platform.services;

import net.minecraft.resources.Identifier;

public interface RegistryEntry<T> {

    Identifier id();

    T get();
}

