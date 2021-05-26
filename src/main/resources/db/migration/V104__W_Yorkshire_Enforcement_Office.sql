--- FACT-542 ---
DO $$
	DECLARE temp_id integer;
	BEGIN
		DELETE FROM public.search_contact
		WHERE number = '01133 076 600'
		  AND id IN (
			SELECT cc.contact_id
			FROM public.search_courtcontact cc
	        INNER JOIN search_court c ON c.id = cc.court_id
			WHERE c.slug = 'west-yorkshire-enforcement-office'
		)
		RETURNING id into temp_id;

		DELETE FROM public.search_courtcontact cc
		WHERE contact_id = temp_id;
	END
$$;
