--- FACT-471 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'westyorkshireenforcement@justice.gov.uk',
			       '',
			       '',
			       16
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'west-yorkshire-enforcement-office'),
			       temp_id,
			       3
		       );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, name_cy, number, explanation, in_leaflet, explanation_cy, contact_type_id, fax)
		VALUES(
			      DEFAULT,
			      '',
			      '',
			      '0113 307 6600',
			      'Extension 3 or 4',
			      TRUE,
			      '',
		          215,
		          FALSE
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'west-yorkshire-enforcement-office')
		      );
	END
$$;

--- FACT-473 ---
UPDATE public.search_facility
SET description = '<p>Portable units are available.</p>'
WHERE name = 'Loop Hearing'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
    INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'havant-justice-centre'
);

UPDATE public.search_facility
SET description = '<p>Assistance dogs are welcome.</p>'
WHERE name = 'Assistance dogs'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'havant-justice-centre'
);

DELETE FROM public.search_courtareaoflaw
WHERE id IN (
	SELECT caol.id
	FROM public.search_courtareaoflaw caol
		     INNER JOIN search_areaoflaw aol ON aol.id = caol.area_of_law_id
		     INNER JOIN search_court c ON c.id = caol.court_id
	WHERE aol.name = 'Children'
	  AND c.slug = 'havant-justice-centre'
);

--- FACT-464 ---
UPDATE public.search_email
SET address = 'contactprobate@justice.gov.uk'
WHERE address = 'londonprobate@justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'london-probate-department'
);

--- FACT-487 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'family.northampton.countycourt@justice.gov.uk',
			       'Including C100 applications',
			       '',
			       18
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'northampton-magistrates-court'),
			       temp_id,
			       3
		       );
	END
$$;

--- FACT-496 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES(
		  DEFAULT,
		  '',
		  '',
		  'centrallondon.breathingspace@justice.gov.uk',
		  '',
		  '',
		  4
		)
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'central-london-county-court-bankruptcy'),
			      temp_id,
			      3
		      );
	END
$$;

--- FACT-393 ---
UPDATE search_courtaddress
SET address = 'Courts of Justice
			   Deansleigh Road',
    town_name = 'Bournemouth',
    postcode = 'BH7 7DS'
WHERE address_type_id = 5881
AND court_id IN (
	SELECT id
	FROM search_court
	WHERE slug = 'poole-magistrates-court'
);

UPDATE public.search_facility
SET description = '<p>Support for witnesses is available from Citizens Advice - Witness Service.</p>'
WHERE name = 'Witness service'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'poole-magistrates-court'
);

--- FACT-504 ---
UPDATE public.search_email
SET address = 'humberandsouthyorkshireenforcement@justice.gov.uk'
WHERE address = 'sheffieldresulting/enf@justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'sheffield-magistrates-court'
);

--- FACT-506 ---
UPDATE public.search_email
SET address = 'humberandsouthyorkshireenforcement@justice.gov.uk'
WHERE address = 'hu-hullenforcement@Justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'hull-and-holderness-magistrates-court-and-hearing-centre'
);

--- FACT-465 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_contact
		WHERE contact_type_id = 215
		AND number = '0191  201 2000'
	    AND id IN (
			SELECT cc.contact_id
			FROM public.search_courtcontact cc
			INNER JOIN search_court c ON c.id = cc.court_id
			WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
		)
		RETURNING id into temp_id;

		DELETE FROM public.search_courtcontact cc
		WHERE contact_id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_contact
		WHERE contact_type_id = 215
		  AND number = '0300 332 1000'
		  AND id IN (
			SELECT cc.contact_id
			FROM public.search_courtcontact cc
			INNER JOIN search_court c ON c.id = cc.court_id
			WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
		)
		RETURNING id into temp_id;

		DELETE FROM public.search_courtcontact cc
		WHERE contact_id = temp_id;
	END
$$;

UPDATE public.search_courtcontact
SET sort_order = 4
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
	INNER JOIN search_contact co ON co.id = cc.contact_id
	INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 212
	AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_courtcontact
SET sort_order = 5
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
	INNER JOIN search_contact co ON co.id = cc.contact_id
	INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 207
    AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_courtcontact
SET sort_order = 6
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
	INNER JOIN search_contact co ON co.id = cc.contact_id
	INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 223
    AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_courtcontact
SET sort_order = 7
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
    INNER JOIN search_contact co ON co.id = cc.contact_id
    INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 207
    AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_courtcontact
SET sort_order = 8
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
		     INNER JOIN search_contact co ON co.id = cc.contact_id
		     INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 225
	  AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET contact_type_id = 225
WHERE contact_type_id = 215
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
	INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_courtcontact
SET sort_order = 2
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
	INNER JOIN search_contact co ON co.id = cc.contact_id
	INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 225
    AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_courtcontact
SET sort_order = 3
WHERE id IN (
	SELECT cc.id
	FROM public.search_courtcontact cc
	INNER JOIN search_contact co ON co.id = cc.contact_id
	INNER JOIN search_court c on cc.court_id = c.id
	WHERE co.contact_type_id = 231
	AND c.slug = 'newcastle-upon-tyne-combined-court-centre'
);
