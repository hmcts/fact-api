--- FACT-591 ---
DELETE FROM public.search_courtareaoflaw
WHERE area_of_law_id = 34254
AND court_id IN (
    SELECT court.id
    FROM public.search_court court
	WHERE slug = 'middlesbrough-county-court-at-teesside-combined-court'
);

--- FACT-543 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, name_cy, number, explanation,explanation_cy, in_leaflet, contact_type_id, fax)
		VALUES(
			      DEFAULT,
			      'DX',
			      '',
			      'DX 97840 Brentford 2',
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
			      (SELECT id FROM public.search_court WHERE slug = 'brentford-county-and-family-court')
		      );
	END
$$;

--- FACT-592 ---
INSERT INTO public.search_courtareaoflaw(id, area_of_law_id, court_id, single_point_of_entry)
VALUES (
        DEFAULT,
        34254,
		(SELECT court.id FROM public.search_court court WHERE slug = 'sunderland-county-family-magistrates-and-tribunal-hearings'),
        FALSE
);

--- FACT-603 ---
DELETE FROM search_courtaddress
WHERE court_id IN (
	SELECT id
	FROM search_court
	WHERE slug = 'medway-county-court-and-family-court'
);

INSERT INTO search_courtaddress(address, address_cy, town_name, town_name_cy, postcode, court_id, address_type_id)
VALUES (
  'Medway County and Family Court
   9-11 The Brook',
  '',
  'Chatham',
  '',
  'ME4 4JZ',
  (SELECT id FROM search_court WHERE slug = 'medway-county-court-and-family-court'),
  5881
);

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_courtfacility
		WHERE id IN (
			SELECT cf.id
			FROM public.search_courtfacility cf
	        INNER JOIN public.search_facility f ON f.id = cf.facility_id
	        INNER JOIN public.search_court c ON c.id = cf.court_id
			WHERE f.name = 'Interview room'
		    AND c.slug = 'medway-county-court-and-family-court'
		)
		RETURNING facility_id into temp_id;

		DELETE FROM public.search_facility
		WHERE id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_courtfacility
		WHERE id IN (
			SELECT cf.id
			FROM public.search_courtfacility cf
				     INNER JOIN public.search_facility f ON f.id = cf.facility_id
				     INNER JOIN public.search_court c ON c.id = cf.court_id
			WHERE f.name = 'Interview room'
			  AND c.slug = 'medway-county-court-and-family-court'
		)
		RETURNING facility_id into temp_id;

		DELETE FROM public.search_facility
		WHERE id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_courtfacility
		WHERE id IN (
			SELECT cf.id
			FROM public.search_courtfacility cf
				     INNER JOIN public.search_facility f ON f.id = cf.facility_id
				     INNER JOIN public.search_court c ON c.id = cf.court_id
			WHERE f.name = 'Children''s waiting room'
			  AND c.slug = 'medway-county-court-and-family-court'
		)
		RETURNING facility_id into temp_id;

		DELETE FROM public.search_facility
		WHERE id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_courtfacility
		WHERE id IN (
			SELECT cf.id
			FROM public.search_courtfacility cf
				     INNER JOIN public.search_facility f ON f.id = cf.facility_id
				     INNER JOIN public.search_court c ON c.id = cf.court_id
			WHERE f.name = 'Video facilities'
			  AND c.slug = 'medway-county-court-and-family-court'
		)
		RETURNING facility_id into temp_id;

		DELETE FROM public.search_facility
		WHERE id = temp_id;
	END
$$;

--- FACT-674 ---
UPDATE search_facility
SET description = '<p>Disabled access and toilet available. Disabled parking for jurors/witness on request.</p>',
	description_cy = '<p>Mae mynediad a toiledau i bobol anabl ar gael. Parcio i’r anabl ar gyfer rheithwyr/tystion ar gais.</p>'
WHERE name = 'Disabled access'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
    INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'swansea-crown-court'
);
