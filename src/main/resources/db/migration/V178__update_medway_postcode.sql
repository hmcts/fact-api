UPDATE search_courtaddress SET postcode = 'ME4 4AR' WHERE court_id = (
  SELECT id FROM search_court WHERE slug = 'medway-county-court-and-family-court' LIMIT 1
);
