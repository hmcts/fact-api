TRUNCATE TABLE public.search_additionallink;
TRUNCATE TABLE public.search_courtadditionallink;

ALTER TABLE public.search_additionallink
ADD COLUMN type CHARACTER VARYING(500);

--- Support Through Court ---

INSERT INTO public.search_additionallink(url, description, description_cy, location_id)
VALUES ('https://www.supportthroughcourt.org',
        'Support Through Court (Independent charity)',
        'Support Through Court (Elusen annibynnol)',
        (SELECT id FROM public.admin_sidebarlocation where name = 'Find out more about')
       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'west-london-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       );

--- Financial Remedy (Not in-person courts) ---

DO $$
    DECLARE
        temp_additional_link_id integer;
    BEGIN
        FOR i IN 1..5
        LOOP
            INSERT INTO public.search_additionallink(url, description, description_cy, location_id, type)
            VALUES ('https://www.gov.uk/money-property-when-relationship-ends',
                    'If you are making an application to settle your finances following a divorce (Financial Remedy), please refer to the guidance found here',
                    'Os ydych chi’n gwneud cais i setlo eich materion ariannol yn dilyn ysgariad (Rhwymedi Ariannol), cyfeiriwch at y cyfarwyddyd sydd ar gael yma',
                    (SELECT id FROM public.admin_sidebarlocation where name = 'Find out more about'),
                    'Financial Remedy'
                   );
        END LOOP;

        SELECT MIN(id) INTO temp_additional_link_id
        FROM public.search_additionallink
        WHERE url = 'https://www.gov.uk/money-property-when-relationship-ends';

        INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
        VALUES ((SELECT id FROM public.search_court WHERE slug = 'bury-st-edmunds-regional-divorce-centre'),
                temp_additional_link_id,
                0
               ),
                ((SELECT id FROM public.search_court WHERE slug = 'divorce-service-centre'),
                temp_additional_link_id + 1,
                0
               ),
                ((SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-regional-divorce-centre'),
                temp_additional_link_id + 2,
                0
               ),
                ((SELECT id FROM public.search_court WHERE slug = 'north-west-regional-divorce-centre'),
                temp_additional_link_id + 3,
                0
               ),
                ((SELECT id FROM public.search_court WHERE slug = 'south-west-regional-divorce-centre'),
                temp_additional_link_id + 4,
                0
               );

    END
$$;

--- Financial Remedy (In-person courts) ---

DO $$
    DECLARE
        temp_additional_link_id integer;
    BEGIN
        FOR i IN 1..18
        LOOP
            INSERT INTO public.search_additionallink(url, description, description_cy, location_id, type)
            VALUES ('https://www.gov.uk/money-property-when-relationship-ends',
                    'Financial Remedy',
                    'Rhwymedi Ariannol',
                    (SELECT id FROM public.admin_sidebarlocation where name = 'This location handles'),
                    'Financial Remedy'
                   );
        END LOOP;

        SELECT MIN(id) INTO temp_additional_link_id
        FROM public.search_additionallink
        WHERE description = 'Financial Remedy';

        INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
        VALUES ((SELECT id FROM public.search_court WHERE slug = 'central-family-court'),
                temp_additional_link_id,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'),
                temp_additional_link_id + 1,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'nottingham-county-court-and-family-court'),
                temp_additional_link_id + 2,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-county-court-and-family-court'),
                temp_additional_link_id + 3,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'swansea-civil-justice-centre'),
                temp_additional_link_id + 4,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'liverpool-civil-and-family-court'),
                temp_additional_link_id + 5,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'sheffield-combined-court-centre'),
                temp_additional_link_id + 6,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'newcastle-civil-family-courts-and-tribunals-centre'),
                temp_additional_link_id + 7,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'leeds-combined-court-centre'),
                temp_additional_link_id + 8,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'medway-county-court-and-family-court'),
                temp_additional_link_id + 9,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'manchester-civil-justice-centre-civil-and-family-courts'),
                temp_additional_link_id + 10,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
                temp_additional_link_id + 11,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'oxford-combined-court-centre'),
                temp_additional_link_id + 12,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'bristol-civil-and-family-justice-centre'),
                temp_additional_link_id + 13,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'),
                temp_additional_link_id + 14,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'plymouth-combined-court'),
                temp_additional_link_id + 15,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'wrexham-county-and-family-court'),
                temp_additional_link_id + 16,
                0
               ),
               ((SELECT id FROM public.search_court WHERE slug = 'preston-crown-court-and-family-court-sessions-house'),
                temp_additional_link_id + 17,
                0
               );
    END
$$;

--- Business and Property ---

INSERT INTO public.search_additionallink(url, description, description_cy, location_id, type)
VALUES ('https://www.judiciary.uk/you-and-the-judiciary/going-to-court/high-court/courts-of-the-chancery-division/the-business-and-property-courts-in-birmingham',
        'Business and Property',
        'Busnes ac Eiddo',
        (SELECT id FROM public.admin_sidebarlocation where name = 'This location handles'),
        'Business and Property'
       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        1
       );

--- Breathing Space ---

DO $$
    DECLARE
        temp_court_id integer;
    BEGIN
        -- Add breathing space additional link to all active County Courts
        FOR temp_court_id IN SELECT c.id FROM public.search_court c
                                 INNER JOIN public.search_courtcourttype cct ON c.id = cct.court_id
                                 INNER JOIN public.search_courttype ct ON cct.court_type_id = ct.id
                             WHERE c.displayed = true AND ct.name = 'County Court'
                                 AND NOT c.id IN (SELECT court_id FROM public.search_inperson
                                                  WHERE is_in_person = false)
        LOOP
            INSERT INTO public.search_additionallink(url, description, description_cy, location_id)
            VALUES ('https://www.gov.uk/guidance/debt-respite-breathing-space-scheme-creditors-responsibilities-to-the-court',
                    'Breathing Space',
                    'Gofod Anadlu',
                    (SELECT id FROM public.admin_sidebarlocation WHERE name = 'Find out more about')
                   );

            INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
            VALUES (temp_court_id,
                    (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
                    0);
        END LOOP;
    END;
$$;
