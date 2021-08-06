--- FACT-717 ---
UPDATE public.search_courtaddress
SET address = E'The Court House\r\nThe Brook',
    town_name = 'Chatham',
    postcode = 'ME4 4JZ'
WHERE id IN (
    SELECT sca.id
    FROM public.search_court court
    JOIN public.search_courtaddress sca
    ON court.id = sca.court_id
    JOIN public.search_addresstype sat
    ON sca.address_type_id = sat.id
    WHERE slug = 'maidstone-combined-court-centre'
    AND sat.name = 'Write to us');

--- FACT-721 FACT-722 ---
UPDATE public.search_court
SET gbs = 'Y683'
WHERE slug = 'yeovil-county-family-and-magistrates-court';

UPDATE public.search_court
SET gbs = 'Y412'
WHERE slug = 'barnstaple-magistrates-county-and-family-court';

--- FACT-772 ---
INSERT INTO public.search_courtfacility(id, court_id, facility_id)
VALUES (DEFAULT,
        (SELECT id FROM public.search_court WHERE slug = 'nuneaton-county-court'),
        2379236
       );

--- FACT-856 ---
DELETE FROM public.search_courtareaoflaw
WHERE area_of_law_id = 34249
  AND court_id IN (
    SELECT court.id
    FROM public.search_court court
    WHERE slug = 'northampton-magistrates-court'
);

--- FACT-860 and FACT-716 ---
INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
VALUES(
    (SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
    (SELECT id FROM public.search_court WHERE slug = 'wolverhampton-combined-court-centre'),
    false
    );

INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
VALUES(
          (SELECT id FROM public.search_areaoflaw WHERE name = 'Adoption'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      ),
      (
          (SELECT id FROM public.search_areaoflaw WHERE name = 'Bankruptcy'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      ),
      (
          (SELECT id FROM public.search_areaoflaw WHERE name = 'Divorce'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      ),
      (
          (SELECT id FROM public.search_areaoflaw WHERE name = 'High Court District Registry'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      ),
      (
          (SELECT id FROM public.search_areaoflaw WHERE name = 'Housing possession'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      ),
      (
          (SELECT id FROM public.search_areaoflaw WHERE name = 'Social security'),
          (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
          false
      );
