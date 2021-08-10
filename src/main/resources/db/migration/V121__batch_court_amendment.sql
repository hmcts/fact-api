INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
VALUES(
          (SELECT id FROM public.search_areaoflaw WHERE name = 'Domestic violence'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      );
