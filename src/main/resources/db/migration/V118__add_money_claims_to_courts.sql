--- FACT-842, FAC-843 ---
INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
VALUES((SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
       (SELECT id FROM public.search_court WHERE slug = 'taunton-crown-county-and-family-court'),
       false
      ),
      ((SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
       (SELECT id FROM public.search_court WHERE slug = 'isle-of-wight-combined-court'),
       false
      );
