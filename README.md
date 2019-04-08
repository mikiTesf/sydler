
# Sydler
This Java program is intended to help the brothers who work on the sound system of a JW congregation generate efficient
timely schedules. It saves the schedule in a formatted excel document.

|   |   |
|:---:|:---:|
|![Program Tab](/docs/screenshots/program_tab.png "Program Tab")|![Member Tab](/docs/screenshots/member_tab.png "Member Tab")|


One thing I should mention is that the names of the days of the week and the months in a year are all in the Amharic language. You must
change the values of the Strings in the following arrays in the _view.MessagesAndTitles_ class to their equivalent meaning in your language.

```java
static final String[] MIDWEEK_MEETING_DAYS = {"ሰኞ", "ማክሰኞ", "ዕሮብ", "ሐሙስ", "አርብ", "ቅዳሜ"};
static final String[] WEEKEND_MEETING_DAYS = {"እሁድ", "ሰኞ", "ማክሰኞ", "ዕሮብ", "ሐሙስ", "አርብ", "ቅዳሜ"};
static final String[] MONTHS = {
				"መስከረም", "ጥቅምት", "ህዳር", "ታህሳሥ", "ጥር", "የካቲት",
				"መጋቢት", "ሚያዝያ", "ግንቦት", "ሰኔ", "ሐምሌ", "ነሐሴ"
		   	       };
```

Read the [*doc*](/docs/HOW_IT_WORKS.md) for details.
