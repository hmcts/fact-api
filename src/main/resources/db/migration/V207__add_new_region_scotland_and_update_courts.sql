-- FACT-1951
-- add new region Scotland to region table and update scottish courts to filter by this region for frontend

-- add new region for scotland
INSERT INTO
  search_region
(name, country)
SELECT
  'Scotland', 'Scotland'
  WHERE NOT EXISTS (
	SELECT 1
	FROM search_region
  WHERE name = 'Scotland'
);
-- cte for which courts to pick for update
WITH courts_to_update AS (
  SELECT *
  FROM search_court sc WHERE
    ("name" LIKE '%Aberdeen%' OR
     "name" LIKE '%Ayr%' OR
     "name" LIKE '%Dundee%' OR
     "name" LIKE '%Edinburgh%' OR
     "name" LIKE '%Glasgow%' OR
     "name" LIKE '%Hamilton%' OR
     "name" LIKE '%Inverness%' OR
     "name" LIKE '%Stirling%')
     AND region_id IS NULL -- make sure we dont pick one that is in a region already
)
--perform update using cte and grab new region id without needing to know new region id
UPDATE search_court
SET region_id = (SELECT id FROM search_region sr WHERE "name" = 'Scotland' LIMIT 1)
WHERE "name" IN (SELECT "name" FROM courts_to_update);
