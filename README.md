# Sound System Schedule Generator
This java program is intended to help the brothers who work on the sound system of a congregation generate efficient
timely schedules. It saves the schedule in a formatted excel document.

The program assumes that there are 5 main roles (6 when a congregation has 2nd hall presentations):

 1. Stage
 2. 1st round mic rotation (left)
 3. 1st round mic rotation (right)
 4. 2nd round mic rotation (left)
 5. 2nd round mic rotation (right)
 6. Second hall (optional)

The reason why the "Second hall" role is added sometimes is because some congregations conduct student presentations
in their 2nd hall and the elder assigned to do that job may need a member of the sound system to help him with things
like timing presentations, opening/closing the room's curtains, etc.... The option to do so is currently not included
in the program but hopefully the next release will (you don't have to wait till then. Go ahead and make your changes
if you want).

One thing I should mention is that the program is designed to never assign the brother who is managing the stage on a
given day to another role on that same day because he could get really busy.

The algorithm uses a mathematical formula to help it decide the member it should assign to a role next. The formula is:

![Formula for "Assignment Factor"](http://www.sciweavers.org/upload/Tex2Img_1548944487/render.png)

|symbol|meaning  |
|--|--|
| A<sub>sf<sub/> | Assignment factor (its like a rank) |
| q |The member qualifies for a role|
| ψ | The member is occupied today |
| e<sub>x<sub/> | The member has a role exception |
| d | Number of days since the member's last appearance |
| n<sub>b<sub/> | Number of times the member has appeared before |
| n<sub>t<sub/> | Number of times the member has appeared today |

The A<sub>sf</sub> (rank) of each member is calculated right before the program chooses one for a role. Depending on
the members' rank, the "best" will be the member with the highest A<sub>sf</sub>. In the cases when there are multiple
members with equal A<sub>sf</sub> values, then a member will be chosen randomly.

Currently the only role exception a member can have is the "Sunday Stage Exception". If a member has this
exception, he won't be assigned to manage the stage on Sundays (duh!).
