-- Copy existing display_name and display_name_cy data into the alt_name and alt_name_cy columns so these columns will be used to display for in-person court in the front-end

UPDATE public.search_areaoflaw
SET alt_name = display_name
WHERE display_name IS NOT NULL
  AND alt_name IS NULL;

UPDATE public.search_areaoflaw
SET alt_name_cy = display_name_cy
WHERE display_name_cy IS NOT NULL
  AND alt_name_cy IS NULL;

--- Add Financial Remedy to areaoflaw and courtareaoflaw tables

INSERT INTO public.search_areaoflaw(name, external_link, alt_name_cy, display_name, display_name_cy)
VALUES ('Financial Remedy',
        'https://www.gov.uk/money-property-when-relationship-ends',
        'Rhwymedi Ariannol',
        'If you are making an application to settle your finances following a divorce (Financial Remedy), please refer to the guidance found here',
        'Os ydych chi’n gwneud cais i setlo eich materion ariannol yn dilyn ysgariad (Rhwymedi Ariannol), cyfeiriwch at y cyfarwyddyd sydd ar gael yma'
       );

DO $$
    DECLARE
        temp_area_of_law_id integer;
    BEGIN
        SELECT id INTO temp_area_of_law_id
        FROM public.search_areaoflaw
        ORDER BY id DESC LIMIT 1;

        INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
        VALUES (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'central-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'nottingham-county-court-and-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-county-court-and-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'swansea-civil-justice-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'liverpool-civil-and-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'sheffield-combined-court-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'newcastle-civil-family-courts-and-tribunals-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'leeds-combined-court-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'medway-county-court-and-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'manchester-civil-justice-centre-civil-and-family-courts'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'oxford-combined-court-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'bristol-civil-and-family-justice-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'plymouth-combined-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'wrexham-county-and-family-court'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'preston-crown-court-and-family-court-sessions-house'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'bury-st-edmunds-regional-divorce-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'divorce-service-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-regional-divorce-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'north-west-regional-divorce-centre'), false),
               (temp_area_of_law_id, (SELECT id FROM public.search_court WHERE slug = 'south-west-regional-divorce-centre'), false);
    END
$$;

--- Add Business and Property to areaoflaw and courtareaoflaw tables

INSERT INTO public.search_areaoflaw(name, external_link, alt_name_cy, display_name_cy)
VALUES ('Business and Property',
        'https://www.judiciary.uk/you-and-the-judiciary/going-to-court/high-court/courts-of-the-chancery-division/the-business-and-property-courts-in-birmingham',
        'Busnes ac Eiddo',
        'Busnes ac Eiddo'
       );

INSERT INTO public.search_courtareaoflaw(area_of_law_id, court_id, single_point_of_entry)
VALUES ((SELECT id FROM public.search_areaoflaw ORDER BY id DESC LIMIT 1),
        (SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'),
        false
       );

-- Remove Financial Remedy and Business and Property from additional link tables

DELETE FROM public.search_courtadditionallink
WHERE additional_link_id IN (
    SELECT id FROM public.search_additionallink
    WHERE url = 'https://www.gov.uk/money-property-when-relationship-ends'
        OR description = 'Business and Property'
    );

DELETE FROM public.search_additionallink
WHERE url = 'https://www.gov.uk/money-property-when-relationship-ends'
   OR description = 'Business and Property';


