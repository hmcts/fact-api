-- FACT-1523
-- make Maintenance Enforcement Business Centre to not in person court

INSERT INTO public.search_inperson
(id, is_in_person, court_id, access_scheme, common_platform)
VALUES
  (
    DEFAULT,
   false,
    (
      SELECT id
      FROM public.search_court
      WHERE slug = 'maintenance-enforcement-business-centre-england' or slug = 'maintenance-enforcement-business-centre'
    ),
    false,
    false
  );
INSERT INTO public.search_servicecentre
(court_id, intro_paragraph, id)
VALUES (
         (
           SELECT id
           FROM public.search_court
           WHERE slug = 'maintenance-enforcement-business-centre-england' or slug = 'maintenance-enforcement-business-centre'
         ),
          'This location services all of England and Wales for Reciprocal Enforcement of Maintenance Orders. We do not provide an in-person service. ',
         DEFAULT
       );


INSERT INTO public.search_additionallink
(id, url, description)
VALUES (
      DEFAULT,
      'https://www.gov.uk/child-maintenance-if-one-parent-lives-abroad/other-partner-lives-abroad',
      'Child maintenance if a parent lives abroad'
  );

INSERT INTO public.search_courtadditionallink
(id, court_id, additional_link_id, sort)
VALUES (
      DEFAULT,
  (
  SELECT id
  FROM public.search_court
  WHERE slug = 'maintenance-enforcement-business-centre-england' or slug = 'maintenance-enforcement-business-centre'
  ),
  (
  SELECT id
  FROM public.search_additionallink
  WHERE description = 'Child maintenance if a parent lives abroad'
  ),
  0
  )
