##74
SELECT id,CASE WHEN has_internet = 1 THEN 'YES' ELSE 'NO' END AS has_internet FROM Rooms;

##56
DELETE FROM Trip WHERE town_from = 'Moscow';

##114
SELECT DISTINCT p.name FROM Flights f JOIN Pilots p ON f.second_pilot_id = p.pilot_id WHERE f.destination = 'New York' AND f.flight_date BETWEEN '2023-08-01' AND '2023-08-31';

##19
SELECT DISTINCT fm.status FROM FamilyMembers fm JOIN Payments p ON fm.member_id = p.family_member JOIN Goods g ON p.good = g.good_id WHERE g.good_name = 'potato';

##21
SELECT g.good_name FROM Goods g INNER JOIN Payments p ON g.good_id = p.good GROUP BY g.good_id, g.good_name HAVING COUNT(p.payment_id) > 1;
