-- FACT-1405
-- OLD 55.9323574619519, -3.24959272725545
-- NEW 55.93194563254789, -3.2511289046474214

UPDATE search_court sc
SET lat = 55.93194563254789, lon = -3.2511289046474214
WHERE "name" = 'Edinburgh Social Security and Child Support Tribunal';
