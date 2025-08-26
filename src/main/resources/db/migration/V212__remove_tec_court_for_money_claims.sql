--FACT-2524
--Remove the entry in search_serviceareacourt that points to traffic enforcement centre tec for money claims
--CNBC will remain and system will point there instead

WITH
tec_id AS (
  SELECT id FROM search_court WHERE slug = 'traffic-enforcement-centre-tec' LIMIT 1
),
sa_id AS (
SELECT id FROM search_servicearea WHERE name = 'Money claims' LIMIT 1
)
DELETE FROM search_serviceareacourt ssac
WHERE ssac.court_id = (SELECT id FROM tec_id) AND
  ssac.servicearea_id = (SELECT id FROM sa_id) AND
  ssac.catchment_type = 'national';
