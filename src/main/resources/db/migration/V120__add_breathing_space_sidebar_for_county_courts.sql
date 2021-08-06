INSERT INTO public.search_additionallink(url, description, description_cy, location_id)
VALUES ('https://www.gov.uk/guidance/debt-respite-breathing-space-scheme-creditors-responsibilities-to-the-court',
        'Breathing Space',
        'Gofod Anadlu',
        (SELECT id FROM public.admin_sidebarlocation WHERE name = 'Find out more about')
       );

DO $$
    DECLARE
        temp_additional_link_id integer;
        temp_court_id integer;
    BEGIN
        SELECT id INTO temp_additional_link_id
        FROM public.search_additionallink
        ORDER BY id DESC LIMIT 1;

        -- Add breathing space additional link to all active County Courts. If the court already has
        -- an existing additional link, set the existing sort order to 1
        FOR temp_court_id IN SELECT c.id FROM public.search_court c
                             INNER JOIN public.search_courtcourttype cct
                                 ON c.id = cct.court_id
                             INNER JOIN public.search_courttype ct
                                 ON cct.court_type_id = ct.id
                             WHERE c.displayed = true AND ct.name = 'County Court'
                             AND NOT c.id IN (SELECT court_id FROM public.search_inperson
                                              WHERE is_in_person = false)
        LOOP
            UPDATE public.search_courtadditionallink
            SET sort = 1
            WHERE court_id = temp_court_id
                AND additional_link_id IN (
                    SELECT al.id FROM public.search_additionallink al
                    INNER JOIN public.admin_sidebarlocation sl
                        ON al.location_id = sl.id
                    WHERE sl.name = 'Find out more about'
                );

            INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
            VALUES (temp_court_id,
                    temp_additional_link_id,
                    0);
        END LOOP;
    END;
$$;
