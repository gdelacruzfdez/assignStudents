package uniovi.assign.greedy;

import uniovi.assign.model.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Support class for the greedy algorithm that contains a boolean matrix representing the collisions between
 * all the groups in the planification.
 */
public class CollisionMatrix {
    Map<String, List<String>> mandatoryCollisions;
    Map<String, List<String>> collisionExceptions;
    List<Group> groups;
    Map<Group, List<Group>> collisionMap = new HashMap<>();
    boolean[][] collisionMatrix;

    /**
     * Default constructor for the collision matrix.
     *
     * @param groups              list of all groups included in the planification.
     * @param collisionExceptions list of collision exceptions provided by the user.
     * @param mandatoryCollisions list of mandatory collisions provided by the user.
     */
    public CollisionMatrix(List<Group> groups, Map<String, List<String>> collisionExceptions, Map<String, List<String>> mandatoryCollisions) {
        this.groups = groups;
        this.collisionMatrix = new boolean[groups.size()][groups.size()];
        this.collisionExceptions = collisionExceptions;
        this.mandatoryCollisions = mandatoryCollisions;
        calculateCollisions();
    }

    /**
     * Method that generate the collision matrix taking into account the timetable of each group,
     * the collision exceptions and the mandatory collisions.
     */
    private void calculateCollisions() {
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            List<String> exceptions = collisionExceptions.get(group.getGroupId());
            List<String> mandatoryColls = mandatoryCollisions.get(group.getGroupId());
            List<Group> groupsCollided = new ArrayList<>();
            for (int j = 0; j < groups.size(); j++) {
                Group groupToTest = groups.get(j);
                if (exceptions != null && exceptions.contains(groupToTest.getGroupId())) {
                    collisionMatrix[i][j] = false;
                } else if (mandatoryColls != null && mandatoryColls.contains(groupToTest.getGroupId())) {
                    collisionMatrix[i][j] = true;
                    groupsCollided.add(groupToTest);
                } else {
                    if (group.collide(groupToTest)) {
                        collisionMatrix[i][j] = true;
                        groupsCollided.add(groupToTest);
                    }
                }
            }
            collisionMap.put(group, groupsCollided);
        }
    }

    /**
     * Method that returns the complete list of groups with whom a determined group collides.
     *
     * @param group group from which we want to obtain all the groups that collide with him.
     * @return list of groups that collide with the group passed as parameter.
     */
    public List<Group> getCollidedGroups(Group group) {
        return collisionMap.get(group);
    }

}
