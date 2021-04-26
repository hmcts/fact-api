--- FACT-458 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'warrington-magistrates-court'),
			      temp_id,
			      3
		      );
	END $$;

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
			      (SELECT id FROM public.search_court WHERE slug = 'warrington-magistrates-court')
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'taunton-magistrates-court-tribunals-and-family-hearing-centre'),
			      temp_id,
			      3
		      );
	END $$;

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
			      (SELECT id FROM public.search_court WHERE slug = 'taunton-magistrates-court-tribunals-and-family-hearing-centre')
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'yeovil-county-family-and-magistrates-court'),
			      temp_id,
			      3
		      );
	END $$;

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
			      (SELECT id FROM public.search_court WHERE slug = 'yeovil-county-family-and-magistrates-court')
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
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
	END $$;

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
			      (SELECT id FROM public.search_court WHERE slug = 'bath-law-courts-civil-family-and-magistrates')
		      );
	END
$$;


DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Enquiries',
			      'Ymholiadau',
			      'contactcrime@justice.gov.uk',
			      'Crime customer service centre',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'north-somerset-magistrates-court'),
			      temp_id,
			      3
		      );
	END $$;

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
			      (SELECT id FROM public.search_court WHERE slug = 'north-somerset-magistrates-court')
		      );
	END
$$;
