--- FACT-904 ---
DELETE FROM search_courtareaoflaw
WHERE court_id = (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre')
  AND area_of_law_id = (SELECT id FROM public.search_areaoflaw WHERE name = 'Single justice procedure');
