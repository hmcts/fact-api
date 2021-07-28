--- FACT-602 ---
DELETE FROM search_courtareaoflaw
WHERE court_id = (SELECT id FROM public.search_court WHERE slug = 'middlesbrough-county-court-at-teesside-combined-court')
  AND area_of_law_id = (SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims');
