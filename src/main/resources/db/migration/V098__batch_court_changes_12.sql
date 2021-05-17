--- FACT-512 ---
UPDATE public.search_email
SET explanation = 'Enquiries',
    explanation_cy = 'Ymholiadau'
WHERE address = 'enquiries.newcastle.crowncourt@justice.gov.uk'
AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_email
SET explanation = 'Crown court',
    explanation_cy = 'Llys y goron',
    admin_email_type_id = 25
WHERE address = 'listing.newcastle.crowncourt@justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_email
SET explanation = 'Crown court',
    explanation_cy = 'Llys y goron',
    admin_email_type_id = 37
WHERE address = 'newcastleupontyne.cc@cawitnessservice.cjsm.net'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_email
SET explanation = 'Enquiries',
    explanation_cy = 'Ymholiadau'
WHERE address = 'no-newcastle@Justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_email
SET explanation = 'Magistrates court',
    explanation_cy = 'Llys ynadon',
    admin_email_type_id = 25
WHERE address = 'NO-Listings@Justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_email
SET explanation = 'Fine queries'
WHERE address = 'cdnenforcement@Justice.gov.uk'
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
    INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET explanation = 'Enquiries',
    explanation_cy = 'Ymholiadau'
WHERE number = '0191 201 2000'
AND contact_type_id = 212
AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
    INNER JOIN public.search_court court ON court.id = cc.court_id
    WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET explanation = 'Enquiries',
    explanation_cy = 'Ymholiadau',
    number = '0191 201 2950 '
WHERE number = '0191 201 2031'
  AND contact_type_id = 226
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
    INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET explanation = 'Magistrates court',
    explanation_cy = 'Llys ynadon',
    contact_type_id = 215
WHERE number = '0191 440 7203'
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
    INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET explanation = 'Fine queries'
WHERE number = '0191 298 2280'
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
    INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET explanation = 'Witness service',
    explanation_cy = 'Gwasanaeth i dystion'
WHERE number = '0300 332 1000'
AND contact_type_id = 207
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
    INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

UPDATE public.search_contact
SET explanation = 'Witness service',
    explanation_cy = 'Gwasanaeth i dystion'
WHERE number = '0300 332 1000'
  AND contact_type_id = 207
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
		     INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_openingtime
		WHERE opening_type_id = 49
		  AND id IN (
			SELECT cot.opening_time_id
			FROM public.search_courtopeningtime cot
				     INNER JOIN public.search_court court ON court.id = cot.court_id
			WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
		)
		RETURNING id into temp_id;

		DELETE FROM search_courtopeningtime
	    WHERE opening_time_id = temp_id;
	END
$$;

UPDATE public.search_openingtime
SET hours = 'The Crown Court counter is currently closed due to the COVID situation.'
WHERE opening_type_id = 44
  AND id IN (
	SELECT cot.opening_time_id
	FROM public.search_courtopeningtime cot
    INNER JOIN public.search_court court ON court.id = cot.court_id
	WHERE slug = 'newcastle-upon-tyne-combined-court-centre'
);

--- FACT-502 ---
INSERT INTO public.search_courtcourttype(id, court_id, court_type_id)
VALUES (
        DEFAULT,
        (SELECT id FROM public.search_court WHERE slug = 'lewes-combined-court-centre'),
        (SELECT id FROM public.search_courttype WHERE name = 'County Court')
);

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
				DEFAULT,
				'Breathing space notifications',
				'Hysbysiadau lle anadlu',
				'brighton.breathingspace@justice.gov.uk',
				'',
				'',
				4
        )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
				DEFAULT,
				(SELECT id FROM public.search_court WHERE slug = 'lewes-combined-court-centre'),
				temp_id,
				3
        );
	END
$$;

--- FACT-468 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_email
		WHERE address = 'norwich.court@justice.gov.uk'
		  AND id IN (
			SELECT ce.email_id
			FROM public.search_courtemail ce
				     INNER JOIN public.search_court court ON court.id = ce.court_id
			WHERE slug = 'kings-lynn-magistrates-court-and-family-court'
		)
		RETURNING id into temp_id;

		DELETE FROM search_courtemail
		WHERE email_id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'norfolkcmt@justice.gov.uk',
			       'Enquiries',
			       'Ymholiadau',
			       26
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'kings-lynn-magistrates-court-and-family-court'),
			       temp_id,
			       3
		       );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'family.norwich.countycourt@justice.gov.uk',
			       'Enquiries',
			       'Ymholiadau',
			       18
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'kings-lynn-magistrates-court-and-family-court'),
			       temp_id,
			       3
		       );
	END
$$;

--- FACT-469 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_openingtime
		WHERE opening_type_id = 5
		  AND id IN (
			SELECT cot.opening_time_id
			FROM public.search_courtopeningtime cot
	        INNER JOIN public.search_court court ON court.id = cot.court_id
			WHERE slug = 'southampton-combined-court-centre'
		)
		RETURNING id into temp_id;

		DELETE FROM search_courtopeningtime
		WHERE opening_time_id = temp_id;
	END
$$;

--- FACT-470 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_email
		WHERE address = 'hu-grfamily@Justice.gov.uk'
		  AND id IN (
			SELECT ce.email_id
			FROM public.search_courtemail ce
				     INNER JOIN public.search_court court ON court.id = ce.court_id
			WHERE slug = 'grimsby-magistrates-court-and-family-court'
		)
		RETURNING id into temp_id;

		DELETE FROM search_courtemail
		WHERE email_id = temp_id;
	END
$$;

--- FACT-466 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_email
		WHERE address = 'norwich.court@justice.gov.uk'
		  AND id IN (
			SELECT ce.email_id
			FROM public.search_courtemail ce
				     INNER JOIN public.search_court court ON court.id = ce.court_id
			WHERE slug = 'norwich-magistrates-court-and-family-court'
		)
		RETURNING id into temp_id;

		DELETE FROM search_courtemail
		WHERE email_id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'norfolkcmt@justice.gov.uk',
			       'Enquiries',
			       'Ymholiadau',
			       26
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'norwich-magistrates-court-and-family-court'),
			       temp_id,
			       3
		       );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'family.norwich.countycourt@justice.gov.uk',
			       'Enquiries',
			       'Ymholiadau',
			       18
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'norwich-magistrates-court-and-family-court'),
			       temp_id,
			       3
		       );
	END
$$;

--- FACT-467 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_email
		WHERE address = 'norwich.court@justice.gov.uk'
		  AND id IN (
			SELECT ce.email_id
			FROM public.search_courtemail ce
				     INNER JOIN public.search_court court ON court.id = ce.court_id
			WHERE slug = 'great-yarmouth-magistrates-court-and-family-court'
		)
		RETURNING id into temp_id;

		DELETE FROM search_courtemail
		WHERE email_id = temp_id;
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'norfolkcmt@justice.gov.uk',
			       'Enquiries',
			       'Ymholiadau',
			       26
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'great-yarmouth-magistrates-court-and-family-court'),
			       temp_id,
			       3
		       );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy, admin_email_type_id)
		VALUES (
			       DEFAULT,
			       '',
			       '',
			       'family.norwich.countycourt@justice.gov.uk',
			       'Enquiries',
			       'Ymholiadau',
			       18
		       )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES (
			       DEFAULT,
			       (SELECT id FROM public.search_court WHERE slug = 'great-yarmouth-magistrates-court-and-family-court'),
			       temp_id,
			       3
		       );
	END
$$;

--- FACT-444 ---
UPDATE public.search_contact
SET number = '0330 808 4458'
WHERE contact_type_id = 215
  AND id IN (
	SELECT cc.contact_id
	FROM public.search_courtcontact cc
		     INNER JOIN public.search_court court ON court.id = cc.court_id
	WHERE slug = 'war-pensions-armed-forces-compensation-chamber'
);
