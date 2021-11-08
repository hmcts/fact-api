UPDATE search_facilityfacilitytype
SET facility_type_id = (select id from admin_facilitytype where name = 'Loop Hearing')
WHERE facility_type_id = (select id from admin_facilitytype where name LIKE 'Hearing loop%');

UPDATE search_facility SET name = 'Hearing Loop' WHERE name LIKE 'Hearing loop%';
UPDATE search_facility SET name = 'Hearing Loop' WHERE name = 'Loop Hearing';

UPDATE admin_facilitytype SET name = 'Hearing Loop' WHERE name = 'Loop Hearing';
DELETE from admin_facilitytype WHERE name LIKE 'Hearing loop%';
