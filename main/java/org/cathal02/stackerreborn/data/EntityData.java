package org.cathal02.stackerreborn.data;

public abstract class EntityData {
    int numberOfEntities = 0;
    public void increaseEntityCount() {
        numberOfEntities++;
    }

    public void decreaseEntityCount() {
        numberOfEntities--;
    }

    public int getEntityCount()
    {
        return numberOfEntities;
    }
}
