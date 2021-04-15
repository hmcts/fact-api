-- FACT-393 content changes (Bournemouth Crow Court and Weymouth Combined Court only)

INSERT INTO public.search_facility (id, name, description, image, image_description, image_file_path, name_cy)
VALUES (DEFAULT,
        'Witness service',
        '<p>Support for witnesses is available from Citizens Advice - Witness Service.</p>',
        '',
        'Witness service icon',
        'uploads/facility/image_file/69/Witness_service.png',
        'Gwasanaeth tystion'
       );

INSERT INTO public.search_courtfacility(id, court_id, facility_id)
VALUES (DEFAULT,
        (SELECT id FROM public.search_court WHERE slug = 'weymouth-combined-court'),
        (SELECT id FROM public.search_facility ORDER BY id DESC LIMIT 1)
    );

INSERT INTO public.search_facility (id, name, description, image, image_description, image_file_path, name_cy)
VALUES (DEFAULT,
        'Witness service',
        '<p>Support for witnesses is available from Citizens Advice - Witness Service.</p>',
        '',
        'Witness service icon',
        'uploads/facility/image_file/69/Witness_service.png',
        'Gwasanaeth tystion'
       );

INSERT INTO public.search_courtfacility(id, court_id, facility_id)
VALUES (DEFAULT,
        (SELECT id FROM public.search_court WHERE slug = 'bournemouth-crown-court'),
        (SELECT id FROM public.search_facility ORDER BY id DESC LIMIT 1)
    );


-- FACT-397 content changes

UPDATE public.search_facility
SET description = '<p>The Flying Aubergine Cafe on the ground floor is open from 9am until 3pm</p>'
WHERE id IN (
    SELECT facility_id FROM public.search_courtfacility
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'southampton-combined-court-centre'))
  AND name = 'Refreshments';

