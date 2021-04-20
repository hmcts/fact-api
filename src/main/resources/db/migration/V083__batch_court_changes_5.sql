--- FACT-423 ---
DO $$
DECLARE temp_id integer;
BEGIN
	DELETE FROM public.search_contact
	WHERE name = 'Fax'
	  AND id IN (
		SELECT cc.contact_id
		FROM public.search_courtcontact cc
			     INNER JOIN search_court c ON c.id = cc.court_id
		WHERE c.slug = 'basingstoke-county-court-and-family-court'
	)
	RETURNING id into temp_id;

	DELETE FROM public.search_courtcontact cc
	WHERE contact_id = temp_id;
END $$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
		       DEFAULT,
		       'Possession enquiries',
		       'Ymholiadau meddiant',
		       'Basingstoke.Poss@justice.gov.uk',
		       '',
		       ''
        )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
		       DEFAULT,
		       (SELECT id FROM public.search_court WHERE slug = 'basingstoke-county-court-and-family-court'),
		       temp_id,
		       2
        );
END $$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space enquiries',
			      'Ymholiadau am ofod anadlu',
			      'Basingstoke.Breath@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'basingstoke-county-court-and-family-court'),
			      temp_id,
			      3
		      );
END $$;

UPDATE search_facility
SET description = '<p>There are no parking facilities at this building, however, paid offsite parking is available within 500 metres in the Red Lion Car park.</p>'
WHERE name = 'No parking'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'basingstoke-county-court-and-family-court'
);

--- FACT-426 ---
UPDATE search_facility
SET description = '<p>This court has Prison Video Link facilities which is also available for conferences and legal visits. To book a booth email Enquiries8@justice.gov.uk, subject heading Video Link Booking or telephone 01793 690530.</p>'
WHERE name = 'Video facilities'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'swindon-combined-court'
);

--- FACT-427 ---
UPDATE search_facility
SET description = '<p>There is no parking at the court. The closest public parking is in the car park at the Pavilions Leisure Centre in Hurst Road.</p>'
WHERE name = 'Parking'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'horsham-county-court-and-family-court'
);

--- FACT-428 ---
UPDATE search_facility
SET description = '<p>There is no parking at the court. The closest public parking is in the car park at the Pavilions Leisure Centre in Hurst Road.</p>'
WHERE name = 'Parking'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'horsham-magistrates-court'
);

--- FACT-442 ---
UPDATE search_court
SET alert = ''
WHERE slug = 'manchester-civil-justice-centre-civil-and-family-courts';

--- FACT-429 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_contact
		WHERE name = 'Bailiffs fax'
		AND id IN (
			SELECT cc.contact_id
			FROM public.search_courtcontact cc
				     INNER JOIN search_court c ON c.id = cc.court_id
			WHERE c.slug = 'plymouth-combined-court'
		)
		RETURNING id into temp_id;

		DELETE FROM public.search_courtcontact cc
		WHERE contact_id = temp_id;
	END
$$;
