--FACT-2524
--Remove the entry in search_serviceareacourt that points to traffic enforcement centre tec for money claims
--CNBC will remain and system will point there instead

WITH tec_id AS (
  SELECT id FROM search_court WHERE slug IN ('traffic-enforcement-centre-tec') LIMIT 1
)
DELETE FROM search_serviceareacourt ssac
WHERE ssac.court_id = (SELECT id FROM tec_id)
AND ssac.catchment_type = 'national';
