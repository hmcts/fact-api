-- FACT-1534
-- close south west regional divorce centre
UPDATE search_court sc
SET displayed = false
WHERE slug = 'south-west-regional-divorce-centre';

