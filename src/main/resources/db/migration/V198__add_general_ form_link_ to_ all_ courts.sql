-- FACT-1529
-- add general enquiry form link to all active courts

DO $$
    DECLARE
temp_court_id integer;
BEGIN

FOR temp_court_id IN SELECT c.id FROM public.search_court c
                     WHERE c.displayed = true
                       AND NOT c.id IN (SELECT court_id FROM public.search_inperson
                                        WHERE is_in_person = false)
                       LOOP
                     INSERT INTO public.search_additionallink(url, description, description_cy)
                     VALUES ('https://hmcts-general-enquiry-eng.form.service.justice.gov.uk/',
                       'General enquiry form',
                       'Ffurflen ymholiad cyffredinol'
                       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES (temp_court_id,
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
  0
  );
END LOOP;
END;
$$;


