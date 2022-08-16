DO $$

BEGIN
  DELETE FROM search_courtsecondaryaddresstype;

  INSERT INTO public.search_courtsecondaryaddresstype (address_id, area_of_law_id, court_type_id)
  VALUES ((
    SELECT id FROM public.search_courtaddress
    WHERE court_id = (
      SELECT sc.id
      FROM public.search_court AS sc
      JOIN public.search_courtaddress AS sca
      ON sc.id = sca.court_id
      WHERE sc.slug = 'newcastle-civil-family-courts-and-tribunals-centre'
      AND sca.postcode = 'NE1 8QF'
    )
    AND address_type_id = (
      SELECT id
      FROM public.search_addresstype
      WHERE name = 'Write to us'
    )
  ),
  (
    SELECT id
    FROM public.search_areaoflaw
    WHERE name = 'Money claims'
  ),
  null) ON CONFLICT DO NOTHING;

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;
