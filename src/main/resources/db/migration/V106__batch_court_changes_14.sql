--- FACT-549 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, number, explanation,explanation_cy, in_leaflet, contact_type_id, fax)
		VALUES(
			      DEFAULT,
			      '0191 201 2964',
			      'Accessibility needs',
			      '',
			      TRUE,
			      215,
			      FALSE
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


--- FACT-503 ---
UPDATE public.search_email
SET address = 'brighton.breathingspace@justice.gov.uk'
WHERE address = 'horsham.breathingspace@justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'horsham-county-court-and-family-court'
);

--- FACT-580 ---
UPDATE public.search_court
SET alert = 'Please note that the lift is temporarily out of service – engineers have ordered a new part and we await its arrival. If you need assistance with access arrangements please telephone 01253 757 015. We apologise for any inconvenience.'
WHERE slug = 'blackpool-family-court';

--- FACT-522 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_contact
		WHERE number = '01603 728 241'
		  AND id IN (
			SELECT cc.contact_id
			FROM public.search_courtcontact cc
	        INNER JOIN search_court c ON c.id = cc.court_id
			WHERE c.slug = 'norwich-combined-court-centre'
		)
		RETURNING id into temp_id;

		DELETE FROM public.search_courtcontact cc
		WHERE contact_id = temp_id;
	END
$$;
