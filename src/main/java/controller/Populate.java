package controller;

import domain.Member;

import java.sql.SQLException;
import java.util.*;

class Populate {
    private final int[][] scheduleGrid;
    private final HashMap<Integer, Double> ID_RANK_ALL;
    private final HashMap<Integer, Double> ID_RANK_ROUND1;
    private final int STAGE       = 0;
    private final int ROUND1_ROW1 = 1;
    private final int ROUND1_ROW2 = 2;
    private final int HALL2       = 5;
    private List<Member> allMembers;
    private final List<Member> firstRoundMembers;
    // calculation settings
    private final boolean COUNT_FROM_ALL_ROLES;
    private final boolean CHOOSE_FROM_1ST_ROUND;
    // conventional values representing a member's qualification for a criteria
    private final double LEAST  = 0;
    private final double NORMAL = 1;
    private final double BEST   = 2;

    Populate(int weeks) {
        try {
            /* Initially all members start with equal ranks. To fill ID_RANK_ALL
               with memberID:rank pairs, all members must be fetched first */
            allMembers = Member.memberDao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        firstRoundMembers = new ArrayList<>();
        ID_RANK_ALL       = new HashMap<>();
        ID_RANK_ROUND1    = new HashMap<>();
        for (Member member : Objects.requireNonNull(allMembers)) {
            ID_RANK_ALL.put(member.getId(), NORMAL);
        }
        /* the 2 below is the number of meeting days
           in a week and the 6 is the number of roles */
        scheduleGrid = new int[2 * weeks][6];
        // the preferences set by the user must be read from the settings file before proceeding
        COUNT_FROM_ALL_ROLES  = SettingInitializer.KEY_COUNT_FROM_ALL;
        CHOOSE_FROM_1ST_ROUND = SettingInitializer.KEY_CHOOSE_FROM_1ST_ROUND;
    }

    /********************************* column populating method *********************************/

    private void fillRole(int role) {
        double qualify, occupied, sundayException = 1, distance, numberBefore, numberToday, rank;
        List<Member> memberList = allMembers;
        HashMap<Integer, Double> ID_RANK_PAIR = ID_RANK_ALL;

        for (int day = 0; day < scheduleGrid.length; day++) {
            if (role == HALL2) {
                if (isSunday(day)) continue;

                if (CHOOSE_FROM_1ST_ROUND) {
                    firstRoundMembers.clear();
                    ID_RANK_ROUND1.clear();
                    ID_RANK_ROUND1.put(scheduleGrid[day][ROUND1_ROW1], null);
                    ID_RANK_ROUND1.put(scheduleGrid[day][ROUND1_ROW2], null);
                    try {
                        firstRoundMembers.add(Member.memberDao.queryForId(scheduleGrid[day][ROUND1_ROW1]));
                        firstRoundMembers.add(Member.memberDao.queryForId(scheduleGrid[day][ROUND1_ROW2]));
                    } catch (SQLException e) { e.printStackTrace(); }
                    memberList   = firstRoundMembers;
                    ID_RANK_PAIR = ID_RANK_ROUND1;
                }
            }

            for (Member member : memberList) {
                switch (role) {
                    case STAGE:
                        qualify         = qualify(member.canBeStage());
                        sundayException = sundayException(day, member.hasSundayException());
                        break;
                    case HALL2:
                        qualify = qualify(member.canBe2ndHall());
                        break;
                    default:
                        qualify = qualify(member.canRotateMic());
                        break;
                }
                occupied     = isOccupied(day, member.getId());
                distance     = distance(member.getId(), day, role);
                numberBefore = numberOfTimesBefore(member.getId(), day, role);
                numberToday  = numberOfTimesToday(member.getId(), day);
                rank         = rank(qualify, sundayException, occupied, distance, numberBefore, numberToday);
                ID_RANK_PAIR.replace(member.getId(), rank);
            }
            scheduleGrid[day][role] = getHighestRankingMember(ID_RANK_PAIR);
        }
    }

    /******************************* variable calculating methods *******************************/

    private double qualify(boolean qualify) {
        return qualify ? NORMAL : LEAST;
    }

    private double isOccupied(int day, int memberID) {
        boolean occupied = false;
        for (int role = STAGE; role <= HALL2; role++) {
            occupied = occupied || (scheduleGrid[day][role] == memberID);
            /* The schedule generator is built around what I would like to call 'The Stage Policy'.
               The Stage Policy: A a person who is assigned to manage the stage that day
               should not play any other role as he will be busy (especially on mid-week meeting days).
               Therefore, 'isOccupied' must return the least possible value in such cases */
            if ((role == STAGE) && occupied)
                return LEAST;
        }
        return occupied ? NORMAL : BEST;
    }

    private double sundayException(int day, boolean exception) {
        return (isSunday(day) && exception) ? NORMAL : BEST;
    }

    private double distance(int memberID, int day, int role) {
        // the maximum attainable distance is outside of the array
        if (day == 0) return scheduleGrid.length;
        double distance = 0;
        do {
            ++distance;
            if (scheduleGrid[day - 1][role] == memberID)
                break;
            day -= 1;
        } while (day > 0);
        /* if day equals -1 it means that a member with ID 'memberID' has not been found till
		   the beginning of the array. Hence, 'day' will go past the first day becoming -1 */
        return (day == -1) ? scheduleGrid.length : distance;
    }

    private double numberOfTimesBefore(int memberId, int day, int role) {
        double count = 0;
        if (COUNT_FROM_ALL_ROLES) {
            for (int[] IDArray : scheduleGrid) {
                for (int ID : IDArray) {
                    if (ID == memberId) {
                        ++count;
                    }
                }
            }
        } else { /* count from current role only */
            int skip = (role == HALL2) ? 2 : 1;
            day -= skip; // counting must start before 'day' as no member is assigned on the current day/role
            for (; day > -1; day -= skip) {
                if (scheduleGrid[day][role] == memberId)
                    ++count;
            }
        }
        return count;
    }

    /* In the case where the number of members is too small (five and less), every member ranks the same
       after being assigned once to a role. That opens a door to violate 'The Stage Policy' (read the
       comment under the 'isOccupied' method). To solve that, a variable that represents the number of times
       a person has appeared in a day must be included in the formula */
    private double numberOfTimesToday(int memberID, int day) {
        double count = 0;
        for (int role = STAGE; role <= HALL2; role++) {
            if (scheduleGrid[day][role] == memberID)
                ++count;
        }
        return count;
    }

    private double rank(double qualify, double exception, double occupied, double distance, double numberOfTimesBefore, double numberOfTimesToday) {
        return qualify * exception * occupied * distance / ((numberOfTimesBefore + 1) * (numberOfTimesToday + 1));
    }

    /************************************** other methods **************************************/

    private boolean isSunday(int day) {
        return (day % 2) != 0;
    }

    private int getHighestRankingMember(HashMap<Integer, Double> ID_RANK_PAIR) {
        double maxRank = Collections.max(ID_RANK_PAIR.values());
        Random random  = new Random();
        ArrayList<Integer> maxValuedIDs = new ArrayList<>();

        for (int memberID : ID_RANK_PAIR.keySet()) {
            if (ID_RANK_PAIR.get(memberID) == maxRank)
                maxValuedIDs.add(memberID);
        }
        Collections.shuffle(maxValuedIDs, random);
        return maxValuedIDs.get(random.nextInt(maxValuedIDs.size()));
    }

    String[][] getNameGrid() {
        if (allMembers.isEmpty()) return null;

        final int ROUND2_ROW1 = 3, ROUND2_ROW2 = 4;
        String[][] nameGrid = new String[scheduleGrid.length][scheduleGrid[0].length];

        fillRole(STAGE);
        fillRole(ROUND1_ROW1);
        fillRole(ROUND1_ROW2);
        fillRole(ROUND2_ROW1);
        fillRole(ROUND2_ROW2);
        fillRole(HALL2);

        for (int day = 0; day < scheduleGrid.length; day++) {
            for (int role = STAGE; role <= HALL2; role++) {
                try {
                    /* to avoid an NPE the ID values for Sunday's 2nd Hall assignment,
                       which are 0, must be skipped */
                    if (scheduleGrid[day][role] == 0) continue;

                    Member member = Member.memberDao.queryForId(scheduleGrid[day][role]);
                    nameGrid[day][role] = member.hasDuplicateFirstName() ?
                            member.getFirstName() + "\n" + member.getLastName() : member.getFirstName();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return nameGrid;
    }
}
