
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

DO $$
	DECLARE temp_id integer;
	BEGIN
		INSERT INTO public.search_email(id, description, description_cy, address, explanation, explanation_cy)
		VALUES(
			      DEFAULT,
			      'Breathing space notifications',
			      'Hysbysiadau lle anadlu',
			      'luton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bedford-county-court-and-family-court'),
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
			      'birkenhead.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'birkenhead-county-court'),
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
			      'birmingham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'),
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
			      'blackpool.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'blackpool-family-court'),
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
			      'blackwood.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'blackwood-civil-and-family-court'),
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
			      'bodmin.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bodmin-county-court-and-family-court'),
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
			      'boston.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'boston-county-court-and-family-court'),
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
			      'bournmouth.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'),
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
			      'bradford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bradford-combined-court-centre'),
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
			      'brentford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'brentford-county-and-family-court'),
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
			      'brighton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'brighton-county-court'),
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
			      'bristol.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bristol-civil-and-family-justice-centre'),
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
			      'bromley.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bromley-county-court-and-family-court'),
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
			      'burnley.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'burnley-combined-court-centre'),
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
			      'burystedmunds.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'bury-st-edmunds-county-court-and-family-court'),
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
			      'caernarfon.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'caernarfon-justice-centre'),
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
			      'peterborough.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
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
			      'kent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'canterbury-combined-court-centre'),
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
			      'cardiff.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'cardiff-civil-and-family-justice-centre'),
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
			      'carlisle.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'carlisle-combined-court'),
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
			      'llanelli.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'carmarthen-county-court-and-family-court'),
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
			      'centrallondon.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'central-london-county-court'),
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
			      'chelmsford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'chelmsford-county-and-family-court'),
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
			      'chester.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'chester-civil-and-family-justice-centre'),
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
			      'chesterfield.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'chesterfield-county-court'),
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
			      'clerkenwellandshoreditch.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'clerkenwell-and-shoreditch-county-court-and-family-court'),
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
			      'coventry.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'coventry-combined-court-centre'),
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
			      'crewe.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'crewe-county-court-and-family-court'),
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
			      'croydon.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'croydon-county-court-and-family-court'),
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
			      'darlington.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'darlington-county-court-and-family-court'),
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
			      'kent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'dartford-county-court-and-family-court'),
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
			      'derby.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'derby-combined-court-centre'),
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
			      'doncaster.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'doncaster-justice-centre-north'),
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
			      'dudley.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'dudley-county-court-and-family-court'),
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
			      'durham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'durham-county-court-and-family-court'),
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
			      'edmonton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'edmonton-county-court-and-family-court'),
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
			      'exeter.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'exeter-combined-court-centre'),
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
			      'gateshead.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'gateshead-county-court-and-family-court'),
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
			      'gloucester.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'gloucester-and-cheltenham-county-and-family-court'),
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
			      'greatgrimsby.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'great-grimsby-combined-court-centre'),
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
			      'guilford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'guildford-county-court-and-family-court'),
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
			      'harrogate.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'harrogate-justice-centre'),
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
			      'hastings.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'hastings-county-court-and-family-court'),
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
			      'haverfordwest.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'haverfordwest-county-court-and-family-court'),
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
			      'hertford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'hertford-county-court-and-family-court'),
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
			      'highwycombe.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'high-wycombe-county-court-and-family-court'),
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
			      'horsham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'horsham-county-court-and-family-court'),
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
			      'huddersfield.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'huddersfield-county-court-and-family-court'),
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
			      'ipswich.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'ipswich-county-court-and-family-hearing-centre'),
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
			      'kingstonuponhull.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'kingston-upon-hull-combined-court-centre'),
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
			      'kingstonuponthamescounty.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'kingston-upon-thames-county-court-and-family-court'),
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
			      'lancaster.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'lancaster-courthouse'),
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
			      'leeds.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'leeds-combined-court-centre'),
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
			      'leicester.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'leicester-county-court-and-family-court'),
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
			      'lewes.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'leicester-county-court-and-family-court'),
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
			      'lincoln.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'lincoln-county-court-and-family-court'),
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
			      'liverpool.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'liverpool-civil-and-family-court'),
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
			      'llanelli.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'llanelli-law-courts'),
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
			      'luton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'luton-justice-centre'),
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
			      'kent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'maidstone-combined-court-centre'),
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
			      'manchester.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'manchester-civil-justice-centre-civil-and-family-courts'),
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
			      'mansfield.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'mansfield-magistrates-and-county-court'),
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
			      'mayorsandcity.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'mayors-and-city-of-london-court'),
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
			      'kent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'medway-county-court-and-family-court'),
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
			      'merthyrtydfil.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'merthyr-tydfil-combined-court-centre'),
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
			      'miltonkeynes.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'milton-keynes-county-court-and-family-court'),
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
			      'wrexham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'mold-justice-centre'),
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
			      'newcastleupontyne.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre'),
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
			      'newportgwent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-county-court-and-family-court'),
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
			      'newportiow.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'isle-of-wight-combined-court'),
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
			      'northshields.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'north-shields-county-court-and-family-court'),
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
			      'northampton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'northampton-crown-county-and-family-court'),
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
			      'norwich.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'norwich-combined-court-centre'),
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
			      'nottingham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'nottingham-county-court-and-family-court'),
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
			      'nuneaton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'nuneaton-county-court'),
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
			      'oxford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'oxford-combined-court-centre'),
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
			      'peterborough.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
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
			      'plymouth.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'plymouth-combined-court'),
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
			      'pontyprid.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'pontypridd-county-court-and-family-court'),
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
			      'porttalbot.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'port-talbot-justice-centre'),
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
			      'portsmouth.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'portsmouth-combined-court-centre'),
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
			      'prestatyn.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'prestatyn-justice-centre'),
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
			      'preston.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'preston-crown-court-and-family-court-sessions-house'),
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
			      'reading.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'reading-county-court-and-family-court'),
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
			      'romford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'romford-county-court-and-family-court'),
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
			      'salisbury.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'salisbury-law-courts'),
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
			      'scarborough.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'scarborough-justice-centre'),
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
			      'sheffield.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'sheffield-combined-court-centre'),
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
			      'skipton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'skipton-county-court-and-family-court'),
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
			      'slough.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'slough-county-court-and-family-court'),
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
			      'southshields.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'south-tyneside-county-court-and-family-court'),
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
			      'southampton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'southampton-combined-court-centre'),
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
			      'southend.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'southend-county-court-and-family-court'),
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
			      'sthelens.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'st-helens-county-court-and-family-court'),
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
			      'stafford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'stafford-combined-court-centre'),
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
			      'guildford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'staines-county-court-and-family-court'),
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
			      'stockport.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'stockport-county-court-and-family-court'),
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
			      'stokeontrent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'stoke-on-trent-combined-court'),
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
			      'sunderland.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'sunderland-county-family-magistrates-and-tribunal-hearings'),
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
			      'swansea.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'swansea-civil-justice-centre'),
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
			      'swindon.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'swindon-combined-court'),
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
			      'taunton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'taunton-crown-county-and-family-court'),
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
			      'teesside.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'teesside-combined-court-centre'),
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
			      'telford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'telford-county-court-and-family-court'),
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
			      'kent.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'thanet-county-court-and-family-court'),
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
			      'torquayandnewtonabbot.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'torquay-and-newton-abbot-county-court-and-family-court'),
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
			      'truro.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'truro-county-court-and-family-court'),
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
			      'uxbridge.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'uxbridge-county-court-and-family-court'),
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
			      'wakefield.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'wakefield-civil-and-family-justice-centre'),
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
			      'walsall.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'walsall-county-and-family-court'),
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
			      'wandsworth.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'wandsworth-county-court-and-family-court'),
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
			      'warwick.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'warwick-combined-court'),
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
			      'watford.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'watford-county-court-and-family-court'),
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
			      'wrexham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'wrexham-county-and-family-court'),
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
			      'westcumbria.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'west-cumbria-courthouse'),
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
			      'westonsupermare.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'weston-super-mare-county-court-and-family-court'),
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
			      'weymouth.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'weymouth-combined-court'),
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
			      'wigan.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'wigan-county-court-and-family-court'),
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
			      'willesden.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'willesden-county-court-and-family-court'),
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
			      'winchester.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'winchester-combined-court-centre'),
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
			      'wolverhampton.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'wolverhampton-combined-court-centre'),
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
			      'worcester.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'worcester-combined-court'),
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
			      'worthing.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'worthing-county-court-and-family-court'),
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
			      'wrexham.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'wrexham-county-and-family-court'),
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
			      'yeovil.breathingspace@justice.gov.uk',
			      '',
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
			      'york.breathingspace@justice.gov.uk',
			      '',
			      ''
		      )
		RETURNING id into temp_id;

		INSERT INTO public.search_courtemail(id, court_id, email_id, "order")
		VALUES(
			      DEFAULT,
			      (SELECT id FROM public.search_court WHERE slug = 'york-county-court-and-family-court'),
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
			      (SELECT id FROM public.search_court WHERE slug = 'civil-money-claims-service-centre'),
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
			      (SELECT id FROM public.search_court WHERE slug = 'civil-money-claims-service-centre'),
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
			      (SELECT id FROM public.search_court WHERE slug = 'civil-money-claims-service-centre'),
			      temp_id,
			      3
		      );
	END
$$;
