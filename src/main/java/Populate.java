import java.sql.SQLException;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

class Populate {
    private final int[][] scheduleGrid;
    private final HashMap<Integer, Double> ID_ASF;
    private List<Member> allMembers;

    private final int STAGE = 0;
    private final int HALL2 = 5;

    Populate(int month) {
        int membersCount = 0;
        try {
            membersCount = Member.getDao().queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // initially all members start with an Asf of +Infinity
        // to fill ID_ASF with memberID_Asf pairs, all members must be fetched first
        try {
            allMembers = Member.getDao().queryForAll();
            if (allMembers.size() == 0) {
                System.out.println("\nDatabase empty. Program will terminate now...");
                System.exit(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // ID_ASF = new HashMap<>(memberCount) is not initializing ID_ASF with membersCount.
        // It's size remains 0. Why? To avoid that problem I've set the stop condition for
        // the for loop to be the number of members count
        ID_ASF = new HashMap<>();
        // the ID of each member will be matched with +Infinity
        for (int i = 0; i < membersCount; i++)
            ID_ASF.put(allMembers.get(i).getId(), Double.POSITIVE_INFINITY);
        scheduleGrid = new int[month * 30][6];
    }

    // ***************************** column populating method *****************************

    private void fillRole(int roundRole) {
        double roleException = 1, distance, numberBefore, qualify, occupied, asf;

        for (int day = 0; day < scheduleGrid.length; day++) {
            if (roundRole == HALL2 && day % 2 != 0) continue;
            for (Member member : allMembers) {
                switch (roundRole) {
                    case STAGE:
                        qualify       = qualify(member.canBeStage());
                        roleException = roleException(day, member.hasSundayException());
                        break;
                    case HALL2:
                        qualify = qualify(member.canBeSecondHall());
                        break;
                    default:
                        qualify = qualify(member.canRotateMic());
                        break;
                }
                occupied     = isOccupied(day, member.getId());
                distance     = distance(member.getId(), day, roundRole);
                numberBefore = numberOfTimesBefore(member.getId(), day, roundRole);
                asf          = asf(qualify, roleException, occupied, distance, numberBefore);
                ID_ASF.replace(member.getId(), asf);
            }
            scheduleGrid[day][roundRole] = keyFromValue(Collections.max(ID_ASF.values()));
        }
    }

    // ***************************** variable calculating methods *****************************

    private double qualify (boolean available) {
        return available ? 1 : 0;
    }

    private double isOccupied(int day, int memberID) {
        boolean occupied = false;
        for (int role = STAGE; role <= HALL2; role++)
            occupied = occupied || (scheduleGrid[day][role] == memberID);
        return occupied ? 0 : 1;
    }

    private double roleException(int day, boolean exception) {
        return ( day % 2 != 0 && exception ) ? 0 : 1;
    }

    private double distance (int memberID, int day, int role) {
        double distance = 0;

        if (day == 0) return Double.POSITIVE_INFINITY;
        else
            do {
            ++distance;
            if (scheduleGrid[day - 1][role] == memberID)
                break;
            day -= 1;
        } while (day > 0);
        if (day == -1) return Double.POSITIVE_INFINITY;
        return distance;
    }

    private double numberOfTimesBefore (int memberId, int day, int role) {
        double count = 0;
        int skip = 1;
        if (role == HALL2)
            skip = 2;
        day -= skip; // the counting must start before 'day' as no member is assigned on the current day/role
        for (; day > -1; day -= skip)
            if (scheduleGrid[day][role] == memberId)
                ++count;
        return count;
    }

    private double asf(double qualify, double exception, double occupied, double distance, double numberOfTimesBefore) {
        if (qualify == 0 || exception == 0 || occupied == 0)
            return 0;
        if (numberOfTimesBefore == 0)
            return Double.POSITIVE_INFINITY;
        else
            return (distance / numberOfTimesBefore);
    }

    // ***************************** other methods *****************************

    private int keyFromValue (double value) {
        Random randomKey = new Random();
        ArrayList<Integer> keys = new ArrayList<>();
        for (Integer key: ID_ASF.keySet())
            if (ID_ASF.get(key) == value)
                keys.add(key);
        return keys.get(randomKey.nextInt(keys.size()));
    }

    String[][] getNameGrid() {
        final int ROUND1_ROW1 = 1, ROUND1_ROW2 = 2, ROUND2_ROW1 = 3, ROUND2_ROW2 = 4;
        String[][] nameGrid = new String[scheduleGrid.length][scheduleGrid[0].length];

        fillRole(STAGE);
        fillRole(ROUND1_ROW1);
        fillRole(ROUND1_ROW2);
        fillRole(ROUND2_ROW1);
        fillRole(ROUND2_ROW2);
        fillRole(HALL2);

        for (int day = 0; day < scheduleGrid.length; day++)
            for (int role = STAGE; role <= HALL2; role++)
                try {
                    // to avoid a java.lang.NullPointerException the id values for Sunday's 2nd Hall
                    // assignment, which are 0, must be skipped
                    if (scheduleGrid[day][role] == 0)
                        continue;
                    nameGrid[day][role] = Member.getDao()
                            .queryForId(scheduleGrid[day][role])
                            .getFirstName();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        return nameGrid;
    }
}
