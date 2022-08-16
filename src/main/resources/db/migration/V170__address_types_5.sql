DO $$

BEGIN

  -- telford-county-court-and-family-court
  DELETE FROM search_courtsecondaryaddresstype
  WHERE address_id = (
        SELECT sca.id
        FROM public.search_court AS sc
        JOIN public.search_courtaddress AS sca
        JOIN public.search_addresstype AS sat
        ON sca.address_type_id = sat.id
        ON sc.id = sca.court_id
        WHERE sc.slug = 'telford-county-court-and-family-court'
        AND sca.postcode = 'ST1 3BP'
        AND sat.id = (
          SELECT id
          FROM public.search_addresstype
          WHERE name = 'Write to us'
        )
  );

  INSERT INTO public.search_courtsecondaryaddresstype (address_id, area_of_law_id, court_type_id)
  VALUES (
  (
    SELECT id FROM public.search_courtaddress
    WHERE id = (
                                      SELECT sca.id
      FROM public.search_court AS sc
      JOIN public.search_courtaddress AS sca
      JOIN public.search_addresstype AS sat
      ON sca.address_type_id = sat.id
      ON sc.id = sca.court_id
      WHERE sc.slug = 'telford-county-court-and-family-court'
      AND sca.postcode = 'ST1 3BP'
      AND sat.id = (
        SELECT id
        FROM public.search_addresstype
        WHERE name = 'Write to us'
      )
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
    WHERE name = 'Civil'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;
