# How does Sound System Schedule Generator work?
The A<sub>sf</sub> of each member is calculated right before the program chooses one for a role. Depending on
the members' rank, the "best" will be the member with the highest A<sub>sf</sub>. In the cases when there are multiple
members with equal A<sub>sf</sub> values, then a member will be chosen randomly. The program assumes that there are 5
main roles (6 when a congregation has 2nd hall presentations):

 1. Stage
 2. 1st round mic rotation (left)
 3. 1st round mic rotation (right)
 4. 2nd round mic rotation (left)
 5. 2nd round mic rotation (right)
 6. Second hall (optional)

The reason why the "Second hall" role is added sometimes is because some congregations conduct student presentations
in their 2nd hall and the elder assigned to comment on the students' presentation(s) may need a member of the sound
system to help him with things like timing the presentations, adjusting mic-stand height, etc.... The option to
tell the program that the congregation conducts second hall presentations is currently not included but hopefully the
next release will (you don't have to wait till then. Go ahead and make your changes).

### The Stage Policy
One thing I should mention is that the program is designed to never assign the member who is managing the stage on a
given day to another role on that same day because he could get really busy; especially on mid-week meeting days. I have
decided to name this decision **The Stage Policy**.

### The math involved
The algorithm uses a mathematical formula to help it decide the next member it should assign to a role. The formula is:

![Formula for "Assignment Factor"](/docs/asf_equation.png)

| **Symbol** | **Representation** |
|---|---|
| A<sub>sf</sub> | Assignment factor (my fancy synonym for rank) |
| q | The member qualifies for the givens role or not |
| ψ | The member is occupied today or not |
| e<sub>x</sub> | The member has a role exception |
| d | Number of days since the member's last appearance on a given role |
| n<sub>b</sub> | Number of times the member has appeared on a given role before |
| n<sub>t</sub> | Number of roles the member is assigned to on a given day |

### Variable control options
It is possible to control how the value of n<sub>b</sub> is calculated. By default n<sub>b</sub> is counted from the current
role, i.e. the role which is being populated. For example if the program is populating the stage column and the the A<sub>sf</sub>
of a member called Steve is being calculated, the program will count how many times Steve has appeared on the stage column only.
The other option tells the program to find Steve's appearance not just in the stage column but in all the columns. Doing the
latter, makes the schedule more evenly distributed but those members that qualify for a relatively less number of roles will
appear on the roles they qualify for quite often (only for the first 4 to 5 meeting days).

Another control option the program provides has to do with populating the 2nd hall column. By default, the program chooses a member
who is already on that day's 1st round mic-rotation shift. But that can be changed so that the program chooses any available member
who qualifies for that role either from those members who are unassigned on that day or, if there are'nt any, from those members different
from the one assigned to manage the stage on that same day (see [StagePolicy](#The Stage Policy)).

The above options can be found in "`File`->`Preferences...`"

### Role Exceptions (e<sub>x</sub>)
Currently the only role exception a member can have is the "Sunday Stage Exception". If a member has this exception, he won't be
assigned to manage the stage on Sundays (duh!). Other exceptions will , hopefully, be added in a future release.
