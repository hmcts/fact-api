ALTER TABLE search_courtemail
	drop CONSTRAINT "search_courtemail_email_id_3471f8536ca454ba_fk_search_email_id";

ALTER TABLE search_courtemail
	ADD CONSTRAINT "search_courtemail_email_id_3471f8536ca454ba_fk_search_email_id"
	FOREIGN KEY (email_id)
	REFERENCES public.search_email(id)
	ON DELETE CASCADE ON UPDATE NO ACTION;

UPDATE public.search_court
SET info = ''
WHERE slug = 'civil-money-claims-service-centre';

UPDATE public.search_email
SET description = 'Enquiries'
WHERE
      description = 'County Court'
	  AND id IN (
		SELECT email_id
		FROM public.search_courtemail
		INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
		WHERE slug = 'liverpool-civil-and-family-court'
	  );


DELETE FROM public.search_email
WHERE
		description = 'Enquiries'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'worthing-county-court-and-family-court'
);

INSERT INTO public.search_email (id, description, address, explanation, explanation_cy, description_cy)
VALUES (DEFAULT, 'Family queries', 'worthingfamily@justice.gov.uk', NULL, NULL, 'Ymholiadau Teulu');

INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
VALUES (DEFAULT,
        (SELECT id FROM public.search_court WHERE slug = 'worthing-county-court-and-family-court'),
        (SELECT id FROM public.search_email ORDER BY id DESC LIMIT 1),
        1
);

INSERT INTO public.search_email (id, description, address, explanation, explanation_cy, description_cy)
VALUES (DEFAULT, 'County Court', 'worthingcivil@justice.gov.uk', NULL, NULL, 'Llys Sirol');

INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
VALUES (DEFAULT,
        (SELECT id FROM public.search_court WHERE slug = 'worthing-county-court-and-family-court'),
        (SELECT id FROM public.search_email ORDER BY id DESC LIMIT 1),
        1
       );
