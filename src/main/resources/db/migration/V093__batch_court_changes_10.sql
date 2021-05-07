--- FACT-430 ---
UPDATE public.search_facility
SET description = '<p>Court 1 has a fixed hearing loop. Contact front of house on 01752 677475.</p>'
WHERE name = 'Loop Hearing'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'plymouth-magistrates-court'
);


--- FACT-416 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Transcript requests',
			      '',
			      'transcripts.newcastle.countycourt@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-civil-family-courts-and-tribunals-centre'),
			      temp_id,
			      3
		      );
	END
$$;

--- FACT-459 ---
UPDATE public.search_facility
SET description = '<p>A cafeteria is operational on premises. It is open Monday to Friday (except Public and Bank Holidays) from 8:30am to 2:30pm.</p>'
WHERE name = 'Refreshments'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_facility
SET description = '<p>Interview rooms are available on site.</p>'
WHERE name = 'Interview room'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_facility
SET description = '<p>Baby changing facilities can be found on the ground floor next to reception.</p>'
WHERE name = 'Baby changing facility'
  AND id IN (
	SELECT cf.facility_id
	FROM public.search_courtfacility cf
		     INNER JOIN public.search_court c ON cf.court_id = c.id
	WHERE c.slug = 'newcastle-upon-tyne-combined-court-centre'
);

--- FACT-497 ---
UPDATE search_court
SET alert = 'Due to work being undertaken on the water supply by United Utilities, this court will be closed during the morning of Monday 10/05/21. The site will reopen at 14:00hrs. All parties have been notified and we apologise for any inconvenience caused.'
WHERE slug = 'blackpool-magistrates-and-civil-court';
