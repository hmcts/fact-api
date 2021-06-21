--- FACT-472 ---
DO $$
	DECLARE temp_id integer;
		DECLARE temp_id2 integer;
	BEGIN
		INSERT INTO public.search_areaoflaw(id, name, display_name_cy)
		VALUES(DEFAULT, 'High Court', 'Uchel Lys')
		RETURNING id into temp_id;

		INSERT INTO public.search_areaoflaw(id, name, display_name_cy)
		VALUES(DEFAULT, 'Court Of Appeal', 'Llys Apêl')
		RETURNING id into temp_id2;

		INSERT INTO public.search_courtareaoflaw(id, area_of_law_id, court_id, single_point_of_entry)
		VALUES
		(DEFAULT, temp_id, (SELECT id FROM public.search_court WHERE slug = 'royal-courts-of-justice'), FALSE),
		(DEFAULT, temp_id2, (SELECT id FROM public.search_court WHERE slug = 'royal-courts-of-justice'), FALSE);
	END
$$;

--- FACT-463 ---
UPDATE search_facility
SET description = '<p>Information for disabled visitors is available by contacting the relevant Listing Office or main reception.</p>'
WHERE name = 'Disabled access'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET description = '<p>Assistance dogs are welcome.</p>'
WHERE name = 'Assistance dogs'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET name = 'Loop hearing'
WHERE name = 'Loop Hearing'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET description = '<p>The cafeteria is currently closed.</p>'
WHERE name = 'Refreshments'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET name = 'Interview rooms'
WHERE name = 'Interview room'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET name = 'Baby changing facilities',
    description = '<p>There are baby changing facilities situated within the disabled toilets off the main hall and Queen''s Building ground floor.</p>'
WHERE name = 'Baby changing facility'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET description = '<p>Court/hearing room video conferencing facilities and prison-to-court video link facilities are avilable (by prior arrangement).</p>'
WHERE name = 'Video facilities'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET description = '<p>Wi-Fi is available to professional court users and court visitors. In-court facilities will be available for parties own IT equipment, e.g. electronic presentation of evidence.</p>'
WHERE name = 'Wireless network connection'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

UPDATE search_facility
SET description = '<p>Support Through Courtis available remotely to provide assistance. Please call 0300 081 0006.</p>'
WHERE name = 'Witness service'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'royal-courts-of-justice'
);

--- FACT-523 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, name_cy, number, explanation,explanation_cy, in_leaflet, contact_type_id, fax)
		VALUES(
			      DEFAULT,
			      'DX',
			      '',
			      'DX 44658 Hayes (Middlesex)',
			      '',
			      '',
			      FALSE,
			      NULL,
			      FALSE
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'uxbridge-county-court-and-family-court')
		      );
	END
$$;

--- FACT-510 ---
UPDATE search_facility
SET description = '<p>There is very limited free public parking at or nearby this court. Alternative parking can be found at Isleworth Train Station or Osterley Underground station for a fee. The court is 0.5 miles walk from each car park.</p>'
WHERE name = 'Parking'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
    INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'isleworth-crown-court'
);

--- FACT-590 ---
DELETE FROM search_courtaddress
WHERE court_id IN (
	SELECT id
	FROM search_court
	WHERE slug = 'barnsley-law-courts'
);

INSERT INTO search_courtaddress(address, address_cy, town_name, town_name_cy, postcode, court_id, address_type_id)
VALUES(
  'The Court House
   Westgate
   Barnsley',
  '',
  'South Yorkshire',
  '',
  'S70 2HW',
  (SELECT id FROM search_court WHERE slug = 'barnsley-law-courts'),
  5882
);
