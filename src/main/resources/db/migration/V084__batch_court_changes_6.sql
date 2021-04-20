--- FACT-438 ---
UPDATE public.search_court
SET alert = ''
WHERE slug = 'bradford-and-keighley-magistrates-court-and-family-court';

--- FACT-410 ---
UPDATE public.search_facility
SET description = '<p>Access to the Internet via WiFi is available at this court free of charge. This service will advertise itself on your device as GovWifi.</p>'
WHERE name = 'Wireless network connection'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
    INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'bristol-civil-and-family-justice-centre'
);

UPDATE public.search_facility
SET description = '<p>Assistance and Guide dogs are welcome at this court.</p>'
WHERE name = 'Assistance dogs'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
    INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'bristol-civil-and-family-justice-centre'
);

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_email
		WHERE address = 'bristolclinneg@justice.gov.uk'
		  AND id IN (
			SELECT ce.email_id
			FROM public.search_courtemail ce
	        INNER JOIN public.search_court c ON c.id = ce.court_id
			WHERE c.slug = 'bristol-civil-and-family-justice-centre'
			  AND ce."order" = 3
		)
		RETURNING id into temp_id;

		DELETE FROM public.search_courtemail cc
		WHERE email_id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
		      DEFAULT,
		      '',
		      '',
		      'bristolclinneg@justice.gov.uk',
		      '',
		      ''
	      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
		      DEFAULT,
		      (SELECT id FROM public.search_court WHERE slug = 'bristol-civil-and-family-justice-centre'),
		      temp_id,
		      12
	      );
	END
$$;

UPDATE public.search_openingtime o
SET hours = 'Monday to Friday 9am to 4pm for civil and family queries.'
WHERE type = 'Telephone enquiries answered'
  AND o.id IN (
	SELECT co.opening_time_id
	FROM public.search_courtopeningtime co
    INNER JOIN public.search_court c ON c.id = co.court_id
	WHERE c.slug = 'bristol-civil-and-family-justice-centre'
);

UPDATE public.search_email
SET description = 'Court of Protection'
WHERE description = 'High Court'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'bristol-civil-and-family-justice-centre'
);

UPDATE public.search_email
SET description = 'Support Through Court'
WHERE description = 'Personal support unit'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'bristol-civil-and-family-justice-centre'
);


