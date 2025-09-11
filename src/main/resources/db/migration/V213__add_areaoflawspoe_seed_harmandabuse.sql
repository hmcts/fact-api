-- 1480128 / Pontypool County Court /	pontypool-county-court
-- FGM
INSERT INTO search_courtareaoflawspoe (court_id, area_of_law_id)
SELECT 1480128, 34265
  WHERE NOT EXISTS (
  SELECT 1 FROM search_courtareaoflawspoe
  WHERE court_id = 1480128 AND area_of_law_id = 34265
);

-- Forced marriage
INSERT INTO search_courtareaoflawspoe (court_id, area_of_law_id)
SELECT 1480128, 34264
  WHERE NOT EXISTS (
  SELECT 1 FROM search_courtareaoflawspoe
  WHERE court_id = 1480128 AND area_of_law_id = 34264
);

-- Domestic violence
INSERT INTO search_courtareaoflawspoe (court_id, area_of_law_id)
SELECT 1480128, 34251
  WHERE NOT EXISTS (
  SELECT 1 FROM search_courtareaoflawspoe
  WHERE court_id = 1480128 AND area_of_law_id = 34251
);
