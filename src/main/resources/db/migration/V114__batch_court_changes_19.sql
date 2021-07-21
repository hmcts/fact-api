--- FACT-751, FAC-752, FACT-823 ---
INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
VALUES((SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
       (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
       false
      ),
      ((SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
       (SELECT id FROM public.search_court WHERE slug = 'staines-county-court-and-family-court'),
       false
      ),
      ((SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
       (SELECT id FROM public.search_court WHERE slug = 'middlesbrough-county-court-at-teesside-combined-court'),
       false
      );

--- FACT-447 ---
INSERT INTO public.search_additionallink(url, description, description_cy, location_id)
VALUES ('https://www.judiciary.uk/you-and-the-judiciary/going-to-court/high-court/courts-of-the-chancery-division/the-business-and-property-courts-in-birmingham',
        'Business and Property',
        'Busnes ac Eiddo',
        (SELECT id FROM public.admin_sidebarlocation where name = 'This location handles')
       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       );

--- Add Financial Remedy email type ---
INSERT INTO public.admin_emailtype(description, description_cy)
VALUES ('Financial Remedy',
        'Rhwymedi Ariannol'
       );
