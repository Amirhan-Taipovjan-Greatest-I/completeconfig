package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;

@ConfigEntries(includeAll = true)
public class EntriesContainerWithTransientField implements ConfigContainer {

    private transient boolean entriesContainerWithTransientFieldField;

}