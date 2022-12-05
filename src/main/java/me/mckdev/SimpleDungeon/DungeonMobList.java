package me.mckdev.SimpleDungeon;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class DungeonMobList {
    private List<DungeonMob> mobs;

    public DungeonMobList() {
        this.mobs = new ArrayList<>();
    }

    public void addMob(DungeonMob mob) {
        mobs.add(mob);
    }

    public boolean containsMob(Entity entity) {
        for (DungeonMob mob : mobs) {
            if (mob.getSpawned() == entity) {
                return true;
            }
        }
        return false;
    }

    public List<DungeonMob> getMobs() {
        return mobs;
    }
}
