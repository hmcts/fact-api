--- FACT-411 ---

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'aberystwyth.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'aberystwyth-justice-centre'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'aldershot.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'aldershot-justice-centre'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'barnet.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'barnet-civil-and-family-courts-centre'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'barnsley.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'barnsley-law-courts'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'barnstaple.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'barnstaple-magistrates-county-and-family-court'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'barrowinfurness.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'barrow-in-furness-county-court-and-family-court'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'basildon.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'basildon-combined-court'),
			      temp_id,
			      3
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'basingstoke.breathingspace@justice.gov.uk',
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
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'bath.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bath-law-courts-civil-family-and-magistrates'),
			      temp_id,
			      3
		      );
	END
$$;


--- FACT-417 ---

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Claims issue',
			      '',
			      'ccmccISSUE.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'county-court-money-claims-centre-ccmcc'),
			      temp_id,
			      3
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Charging Orders',
			      '',
			      'ccmccCO.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'county-court-money-claims-centre-ccmcc'),
			      temp_id,
			      3
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Attachment of Earnings',
			      '',
			      'ccmccAE.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'county-court-money-claims-centre-ccmcc'),
			      temp_id,
			      3
		      );
	END
$$;

DELETE FROM public.search_email
WHERE description IN (
                      'Claims issue', 'Charging Orders', 'Attachment of Earnings'
	)
  AND id IN (
	SELECT email_id
	FROM public.search_courtemail
		     INNER JOIN public.search_court court ON court.id = search_courtemail.court_id
	WHERE slug = 'civil-money-claims-service-centre'
);

--- FACT-482 ---
UPDATE search_court
SET alert = ''
WHERE slug = 'central-london-employment-tribunal';

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_openingtime(id, hours, type, opening_type_id)
		VALUES(
			      DEFAULT,
			      '9:00am to 5:00pm',
			      'Tribunal open',
		          '43'
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtopeningtime(id, court_id, opening_time_id, sort)
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'central-london-employment-tribunal'),
			      temp_id,
			      1
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_openingtime(id, hours, type, opening_type_id)
		VALUES(
			      DEFAULT,
			      '9:00am to 5:00pm',
			      'Counter open',
			      '44'
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtopeningtime(id, court_id, opening_time_id, sort)
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'central-london-employment-tribunal'),
			      temp_id,
			      1
		      );
	END
$$;

--- FACT-488 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      '',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'gateshead-magistrates-court-and-family-court'),
			      temp_id,
			      3
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, name_cy, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      '03308 084 407',
			      1,
			      'Crime customer service centre',
			      TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'gateshead-magistrates-court-and-family-court')
		      );
	END
$$;

--- FACT-489 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      '',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'south-tyneside-magistrates-court'),
			      temp_id,
			      3
		      );
	END
$$;

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_contact(id, name, name_cy, number, sort_order, explanation, in_leaflet, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      '03308 084 407',
			      1,
			      'Crime customer service centre',
			      TRUE,
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtcontact(id, contact_id, court_id)
		VALUES(
			      DEFAULT,
			      temp_id,
			      (SELECT id FROM public.search_court WHERE slug = 'south-tyneside-magistrates-court')
		      );
	END
$$;

--- FACT-481 ---
INSERT INTO search_courtareaoflaw(id, area_of_law_id, court_id, single_point_of_entry)
VALUES (
   DEFAULT,
   (SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
   (SELECT id FROM public.search_court WHERE slug = 'romford-county-court-and-family-court'),
   FALSE
);


