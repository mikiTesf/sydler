package view;

class TitlesAndLabels {
    // **************************** GeneratorGUI dialog *****************************
    // GeneratorGUI frame title
    static final String GENERATOR_FRAME_TITLE = "Sydler";
    // program tab labels and titles
    static final String PROGRAM_TAB_TITLE = "Program";
    static final String REQUIREMENT_DETAILS_LABEL = "<html> Using the first three<br/>" +
            "components, insert the:" +
            "<ul>" +
            "<li> Day </li>" +
            "<li> Month and </li>" +
            "<li> Year </li>" +
            "</ul>" +
            "of the \"Monday\" in the week you<br/>" +
            "want the schedule to begin from </html>";
    static final String DAY_SPINNER_LABEL = "Day:";
    static final String MONTH_DROPDOWN_LABEL = "Month:";
    static final String YEAR_SPINNER_LABEL = "Year:";
    static final String SERVICE_MEETING_DROPDOWN_LABEL = "<html>Midweek<br/>Meeting:</html>";
    static final String SUNDAY_MEETING_DROPDOWN_LABEL = "<html>Weekend<br/>Meeting:</html>";
    static final String OTHER_SUNDAY_MEETING_CHECKBOX_LABEL = "other";
    static final String HOW_MANY_WEEKS_SPINNER_LABEL = "Weeks?";
    static final String GENERATE_BUTTON_TEXT = "Prepare";
    // member tab labels and titles
    static final String MEMBER_TAB_TITLE = "Member";
    static final String MEMBER_BORDER_TITLE = "Add";
    static final String FIRST_NAME_TEXTFIELD_LABEL = "Name";
    static final String LAST_NAME_TEXTFIELD_LABEL = "<html>Last<br/>Name</html>";
    static final String STAGE_CHECKBOX_LABEL = "Stage";
    static final String MICROPHONE_CHECKBOX_LABEL = "Mic";
    static final String SECOND_HALL_CHECKBOX_LABEL = "<html>Second<br/>Hall</html>";
    static final String SUNDAY_EXCEPTION_CHECKBOX_LABEL = "<html>Sunday<br/>Exception</html>";
    static final String ADD_BUTTON_TEXT = "Add";
    static final String MEMBER_TABLE_BORDER_TITLE = "Update or Remove";
    // members table column headers
    static final String ID_COLUMN = "#";
    static final String FULL_NAME_COLUMN = "Full Name";
    static final String STAGE_COLUMN = "Stage";
    static final String MIC_COLUMN = "Mic";
    static final String HALL2_COLUMN = "2nd Hall";
    static final String SUNDAY_EXCEPTION_COLUMN = "Sunday Exception";
    static final String UPDATE_BUTTON_TITLE = "Update";
    static final String REMOVE_BUTTON_TITLE = "Remove";
    // **************************** Preference dialog *****************************
    // Preference frame title
    static final String PREFERENCES_FRAME_TITLE = "Settings";
    // other titles and labels
    static final String PREFERENCES_DETAILS_LABEL = "<html> <ul> <li> የመጀመሪያውን አመልካች ሳጥን መጠቀም ሁሉም ሚናዎች ላይ<br/>" +
            " ያልተመደቡትን የድምጽ ክፍሉ አባላት \"በመጀመሪያዎቹ\" 3 ወይም<br/>4 ስብሰባዎች ወቅት በተደጋጋሚ አንዲደርሳቸው ቢያደርግም፤<br/>" +
            " ድልድሉ ግን ፍትሀዊ አንዲሆን ያደርጋል </li><br/><li>ሁለተኛውን አመልካች ሳጥን መጠቀም በአገልግሎት ስብሰባ ወቅት<br/>" +
            " በሁለተኛው አዳራሽ የሚደረገውን ስብሰባ የሚመራውን ወንድም<br/>እንዲያግዝ የሚመደበው የድምጽ ክፍሉ አባል በዚያው ቀን በመጀመሪያው<br/>" +
            " ዙር የድምጽ ማጉያ ሲያዞሩ ከነበሩት ወንድሞች መሀከል አንዲመረጥ<br/>ደርጋል። ይህን አመልካች ሳጥን አለመጠቀም ይህ ወንድም በዚያ ቀን<br/>" +
            " ምንም ሚና ላይ ካልተመደቡት ወይም ከ2ቱ ዙሮች በአንዱ ላይ<br/>የድምጽ ማጉያ ሲያዞሩ ከነበሩት አባላት መሀከል አንዲመረጥ ያደርጋል። </li>" +
            "</ul> <html/>";
    static final String CURRENT_SETTINGS_LABEL = "Current Settings";
    static final String COUNT_PREFERENCE_CHECKBOX_LABEL = "<html> Count from all<br/>roles to determine<br/>" +
            "how many times a member<br/>appeared before </html>";
    static final String HALL2_PREFERENCE_CHECK_BOX_LABEL = "<html> Select the member<br/>to be assigned<br/>" +
            "to the 2nd hall from<br/>the members<br/>who rotated the mic<br/>during the first round </html>";
    // about form labels
    static final String NAME_AND_VERSION_LABEL = "Sydler v1.0.4";
    static final String BUILD_DATE_LABEL = "Built on November 29, 2018";
    static final String DEVELOPER_EMAIL_LABEL = "(mickyasTesfamichael@gmail.com)";
    static final String DEVELOPER_NAME_LABEL = "Developed by: Mikyas Tesfamichael";
    static final String WHATS_NEW_TAB_TITLE = "Whats new in " + NAME_AND_VERSION_LABEL;
    static final String WHATS_NEW_IN_SYDLER_LIST = "<html> <ul>" +
            "<li> Added settings that control the way a schedule <br/>" +
            "gets generated. The program persists the <br/>" +
            "settings using a JSON file </li> <br/>" +
            "<li> An option to change the weekend meeting day <br/>" +
            "added (some congregations may decide to do <br/>" +
            "it on Saturdays) </li> <br/>" +
            "<li> Error or success messages associated with the <br/>" +
            "generation of the Excel document are properly <br/>" +
            "communicated to the user </li> </ul> </html>";

}
