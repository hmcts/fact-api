--- FACT-454 ---
UPDATE public.search_court
SET displayed = FALSE
WHERE slug = 'newcastle-upon-tyne-magistrates-court';


--- FACT-453 ---
UPDATE public.search_court
SET name = 'Newcastle upon Tyne Crown Court and Magistrates'' Court'
WHERE slug = 'newcastle-upon-tyne-combined-court-centre';

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_openingtime(id, hours, type)
		VALUES(
			      DEFAULT,
			      '9:00am to 5:00pm',
			      'Crown Court open'
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtopeningtime(id, court_id, opening_time_id, sort)
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre'),
			      temp_id,
			      1
		      );
	END
$$;

UPDATE public.search_openingtime o
SET hours = 'The counter is currently closed due to the covid situation.'
WHERE type = 'Counter open'
  AND o.id IN (
	SELECT co.opening_time_id
	FROM public.search_courtopeningtime co
		     INNER JOIN public.search_court c ON c.id = co.court_id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Magistrates court Enquiries',
			      '',
			      'no-newcastle@Justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre'),
			      temp_id,
			      1
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Magistrates Fine queries',
			      '',
			      'cdnenforcement@Justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre'),
			      temp_id,
			      1
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Magistrates court Listing',
			      '',
			      'NO-Listings@Justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre'),
			      temp_id,
			      1
		      );
	END
$$;

UPDATE search_email
SET description = 'Crown court Enquiries',
    explanation = '',
    explanation_cy = ''
WHERE description = 'Enquiries'
  AND explanation = 'Criminal Queries'
  AND id IN (
	SELECT email_id
	FROM search_courtemail ce
		     INNER JOIN search_court c ON ce.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE search_email
SET description = 'Crown court Listing'
WHERE description = 'Listing'
  AND id IN (
	SELECT email_id
	FROM search_courtemail ce
		     INNER JOIN search_court c ON ce.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE search_email
SET description = 'Crown court witness service Enquiries',
    explanation = '',
    explanation_cy = ''
WHERE description = 'Enquiries'
  AND explanation = 'Witness Service'
  AND id IN (
	SELECT email_id
	FROM search_courtemail ce
		     INNER JOIN search_court c ON ce.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Crown court Enquiries',
			      '0191 201 2000',
			      1,
			      '',
		          TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre')
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Magistrates court enquiries',
			      '0191 201 2031',
			      1,
			      '',
			      TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre')
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Magistrates court listing',
			      '0191 440 7203',
			      1,
			      '',
			      TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre')
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Fine Queries',
			      '0191 298 2280',
			      1,
			      '',
			      TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre')
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Citizens advice witness service',
			      '0300 332 1000',
			      1,
			      '',
			      TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre')
		      );
	END
$$;

UPDATE public.search_facility
SET description = '<p>interview rooms are available on site.</p>'
WHERE name = 'Interview room'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);
