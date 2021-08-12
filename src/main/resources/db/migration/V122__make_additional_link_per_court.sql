-- Drop the records for Breathing Space

DELETE FROM public.search_courtadditionallink
WHERE additional_link_id IN (
    SELECT id FROM public.search_additionallink
    WHERE description = 'Breathing Space'
);

DELETE FROM public.search_additionallink
WHERE description = 'Breathing Space';


-- Re-add the records for Breathing Space so the Additional Link record is unique for each court

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
                    0
                   );
        END LOOP;
    END;
$$;
