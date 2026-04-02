-- Update the description. A fix essentially to expedite an urgent change. Another ticket will
-- be raised to address the core issue.
UPDATE search_facility f
SET description = '<a href="https://www.witnessservice.org.uk" rel="nofollow">www.witnessservice.org.uk</a>' from search_courtfacility cf
JOIN search_court sc
ON sc.id = cf.court_id
WHERE f.id = cf.facility_id
  AND sc.slug = 'plymouth-magistrates-court'
  AND f.name = 'Witness service'
  AND (
  SELECT COUNT (*)
  FROM search_courtfacility cf2
  JOIN search_court sc2 on sc2.id = cf2.court_id
  JOIN search_facility f2 on f2.id = cf2.facility_id
  WHERE sc2.slug = 'plymouth-magistrates-court'
  AND f2.name = 'Witness service'
  ) = 1;
