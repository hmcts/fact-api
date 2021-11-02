UPDATE search_facility SET name = 'Loop Hearing' WHERE name LIKE 'Hearing Loop%';

UPDATE search_facilityfacilitytype SET facility_type_id =
  (select facility_type_id from search_facilityfacilitytype sfft join search_facility sf on sf.id = sfft.facility_id where name = 'Loop Hearing' )
  WHERE facility_type_id = (select facility_type_id from search_facilityfacilitytype sfft join search_facility sf on sf.id = sfft.facility_id where name LIKE 'Hearing Loop%');

DELETE FROM admin_facilitytype WHERE name LIKE 'Hearing Loop%';
