import java.sql.SQLException;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

class Populate {
    private final int[][] scheduleGrid;
    private final HashMap<Integer, Double> ID_ASF_ALL;
    private final HashMap<Integer, Double> ID_ASF_ROUND1;
    private List<Member> allMembers;
    private List<Member> firstRoundMembers;

    private final int STAGE       = 0;
    private final int ROUND1_ROW1 = 1;
    private final int ROUND1_ROW2 = 2;
    private final int HALL2       = 5;

    Populate(int weeks) {
        int membersCount = 0;
        try {
            membersCount = Member.getDao().queryForAll().size();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        /* Initially all members start with equal Asf values. To fill ID_ASF_ALL
         with memberID_Asf pairs, all members must be fetched first */
        try {
            allMembers = Member.getDao().queryForAll();
            if (allMembers.size() == 0) {
                System.exit(0);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        firstRoundMembers = new ArrayList<>(2);
        /* ID_ASF_ALL = new HashMap<>(memberCount) is not initializing ID_ASF_ALL with 'membersCount'.
         It's size remains 0 (I still don't know why). To avoid that problem I've set the stop condition for
         the for "loop" to be the count of members */
        ID_ASF_ALL    = new HashMap<>();
        ID_ASF_ROUND1 = new HashMap<>();
        for (int i = 0; i < membersCount; i++) {
            ID_ASF_ALL.put(allMembers.get(i).getId(), 1.0);
        }
        scheduleGrid = new int[2 * weeks][6];
    }

    // ***************************** column populating method *****************************

    private void fillRole (int role, HashMap<Integer, Double> ID_ASF_PAIR) {
        double qualify, occupied, roleException = 1, distance, numberBefore, numberToday, asf;
        List<Member> memberList = allMembers;

        for (int day = 0; day < scheduleGrid.length; day++) {
            if (role == HALL2 && day % 2 != 0) continue;

            if (role == HALL2) {
                firstRoundMembers.clear();
                ID_ASF_ROUND1.clear();
                ID_ASF_ROUND1.put(scheduleGrid[day][ROUND1_ROW1], null);
                ID_ASF_ROUND1.put(scheduleGrid[day][ROUND1_ROW2], null);
                try {
                    firstRoundMembers.add(Member.getDao().queryForId(scheduleGrid[day][ROUND1_ROW1]));
                    firstRoundMembers.add(Member.getDao().queryForId(scheduleGrid[day][ROUND1_ROW2]));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                memberList = firstRoundMembers;
            }

            for (Member member : memberList) {
                switch (role) {
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
                distance     = distance(member.getId(), day, role);
                numberBefore = numberOfTimesBefore(member.getId(), day, role);
                numberToday  = numberOfTimesToday(member.getId(), day);
                asf          = asf(qualify, roleException, occupied, distance, numberBefore, numberToday);
                ID_ASF_PAIR.replace(member.getId(), asf);
            }
            scheduleGrid[day][role] = keyFromValue(Collections.max(ID_ASF_PAIR.values()), ID_ASF_PAIR);
        }
    }

    // ***************************** variable calculating methods *****************************

    private double qualify (boolean qualify) {
        return qualify ? 1 : 0;
    }

    private double isOccupied (int day, int memberID) {
        boolean occupied = false;
        for (int role = STAGE; role <= HALL2; role++) {
            occupied = occupied || (scheduleGrid[day][role] == memberID);
            /* The schedule generator is built around what I would like to call 'The Stage Policy'.
             The Stage Policy: is the idea that a person who is assigned to manage the stage that day
             should not play any other role as he will be busy (especially on mid-week meeting days).
             Therefore, 'isOccupied' must return the least possible value in such cases */
            if (role == STAGE && occupied)
                return 0;
        }
        return occupied ? 1 : 2;
    }

    private double roleException (int day, boolean exception) {
        return ( day % 2 != 0 && exception ) ? 1 : 2;
    }

    private double distance (int memberID, int day, int role) {
        double distance = 0;
        // the maximum attainable distance is outside of the array
        if (day == 0) return scheduleGrid.length;
        else
            do {
            ++distance;
            if (scheduleGrid[day - 1][role] == memberID)
                break;
            day -= 1;
        } while (day > 0);
        /* if day equals -1 it means that a member with id 'memberID' has not been found
           till the beginning of the array. Hence, day will go past the first day being -1. */
        if (day == -1) return scheduleGrid.length;
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

    /* In the case where the number of members is too small (five and less), every member ranks the same
       after being assigned once to a role. That opens a door to violate 'The Stage Policy' (read the
       comment under the 'isOccupied' method). To solve that, a variable that represents the number of times
       a person has appeared in a day must be included in the formula */
    private double numberOfTimesToday (int memberID, int day) {
        double count = 0;
        for (int role = STAGE; role <= HALL2; role++) {
            if (scheduleGrid[day][role] == memberID)
                ++count;
        }
        return count;
    }

    private double asf (double qualify, double exception, double occupied, double distance, double numberOfTimesBefore, double numberOfTimesToday) {
        return qualify * exception * occupied * distance / ( (numberOfTimesBefore + 1) * (numberOfTimesToday + 1) );
    }

    // ***************************** other methods *****************************

    private int keyFromValue (double maxRank, HashMap<Integer, Double> ID_ASF_PAIR) {
        Random randomKey = new Random();
        ArrayList<Integer> maxValuedKeys = new ArrayList<>();
        for (Integer key: ID_ASF_PAIR.keySet()) {
            if (ID_ASF_PAIR.get(key) == maxRank)
                maxValuedKeys.add(key);
        }
        Collections.shuffle(maxValuedKeys);
        return maxValuedKeys.get(randomKey.nextInt(maxValuedKeys.size()));
    }

    String[][] getNameGrid() {
        final int ROUND2_ROW1 = 3, ROUND2_ROW2 = 4;
        String[][] nameGrid = new String[scheduleGrid.length][scheduleGrid[0].length];

        fillRole(STAGE, ID_ASF_ALL);
        fillRole(ROUND1_ROW1, ID_ASF_ALL);
        fillRole(ROUND1_ROW2, ID_ASF_ALL);
        fillRole(ROUND2_ROW1, ID_ASF_ALL);
        fillRole(ROUND2_ROW2, ID_ASF_ALL);
        fillRole(HALL2, ID_ASF_ROUND1);

        for (int day = 0; day < scheduleGrid.length; day++)
            for (int role = STAGE; role <= HALL2; role++)
                try {
                    /* to avoid an NPE the id values for Sunday's 2nd Hall assignment,
                     which are 0, must be skipped */
                    if (scheduleGrid[day][role] == 0)
                        continue;
                    nameGrid[day][role] = Member.getDao()
                            .queryForId(scheduleGrid[day][role])
                            .getFirstName();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
        return nameGrid;
    }
}
