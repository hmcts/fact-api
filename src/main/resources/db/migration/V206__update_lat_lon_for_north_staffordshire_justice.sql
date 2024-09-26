-- FACT-1999
-- update court location for North Staffordshire Justice Centre

UPDATE search_court sc
SET lat = 53.013532, lon = -2.230253
WHERE "name" = 'North Staffordshire Justice Centre';
