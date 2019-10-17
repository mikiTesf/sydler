
# Sydler
This Java program is intended to help the brothers who work on the sound system of a JW congregation generate efficient
timely schedules. Once a schedule is generated it will be saved as a formatted excel document.

|   |   |
|:---:|:---:|
|![Program Tab](/docs/screenshots/program_tab.png "Program Tab")|![Member Tab](/docs/screenshots/member_tab.png "Member Tab")|


One thing I should mention is that the names of the days of the week and the months in a year are all in the Amharic language. You must change the values of the appropriate keys in the `Properties` file located in resources (`UITexts.properties`) to their equivalent meaning in your language. Due to [this](https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle) reason, I had to put the encoded conversions of the day/month names in the file. You may have to check first if it applies to your language too. Here's a snippet of the sections you have to change:

```java
# days in a week
day.1=\u1230\u129e
day.2=\u121b\u12ad\u1230\u129e
day.3=\u12d5\u122e\u1265
day.4=\u1210\u1219\u1235
day.5=\u12a0\u122d\u1265
day.6=\u1245\u12f3\u121c
day.7=\u12a5\u1201\u12f5
# months in a year
month.1=\u1218\u1235\u12a8\u1228\u121d
month.2=\u1325\u1245\u121d\u1275
month.3=\u1205\u12f3\u122d
month.4=\u1273\u1205\u1233\u1225
month.5=\u1325\u122d
month.6=\u12e8\u12ab\u1272\u1275
month.7=\u1218\u130b\u1262\u1275
month.8=\u121a\u12eb\u12dd\u12eb
month.9=\u130d\u1295\u1266\u1275
month.10=\u1230\u1294
month.11=\u1210\u121d\u120c
month.12=\u1290\u1210\u1234
```

Read the [*doc*](/docs/HOW_IT_WORKS.md) for details.
