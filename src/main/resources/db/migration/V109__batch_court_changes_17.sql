--- FACT-483 ---
UPDATE search_facility
SET description = '<p>Court/hearing room video conferencing facilities and prison-to-court video link facilities are available (by prior arrangement) </p>'
WHERE name = 'Video facilities'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
    INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET description = '<p>Support Through Court is available remotely to provide assistance. Please call 0300 081 0006.</p>'
WHERE name = 'Witness service'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
    INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);


--- FACT-616 -- FACT-628  ---
INSERT INTO public.search_courtareaoflaw(id, area_of_law_id, court_id, single_point_of_entry)
VALUES
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'caernarfon-justice-centre'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'wrexham-county-and-family-court'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'aberystwyth-justice-centre'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'haverfordwest-county-court-and-family-court'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'carmarthen-county-court-and-family-court'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'llanelli-law-courts'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'swansea-civil-justice-centre'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'port-talbot-justice-centre'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'merthyr-tydfil-combined-court-centre'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'cardiff-civil-and-family-justice-centre'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-county-court-and-family-court'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'pontypridd-county-court-and-family-court'), FALSE),
        (DEFAULT, 34251, (SELECT id FROM public.search_court WHERE slug = 'blackwood-civil-and-family-court'), FALSE);
