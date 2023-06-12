-- FACT-1405
-- OLD 55.9323574619519	-3.24959272725545
-- NEW 55.931945, -3.251131

UPDATE search_court sc
SET lat = 55.931945, lon = -3.251131
WHERE "name" = 'Edinburgh Social Security and Child Support Tribunal';
