package com.raiiiden.taczblueprints.attachment;

import java.util.Set;

public interface IGunUnlocks {

    void unlockGun(String gunId);

    boolean isUnlocked(String gunId);

    void clearAll();

    Set<String> getUnlockedGuns();

    void setUnlockedGuns(Set<String> guns);
}
