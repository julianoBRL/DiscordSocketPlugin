package io.github.julianobrl.botplugins.interfaces;

import net.dv8tion.jda.internal.entities.ActivityImpl;

public class CustomActivityImpl extends ActivityImpl {

    public CustomActivityImpl(String name, String url, ActivityType type) {
        super(name, url, type);
    }

}
