-- Add new Civil AOL
INSERT INTO public.search_areaoflaw(name, alt_name_cy, display_name, display_name_cy)
VALUES ('Civil',
        'Rhwymedi Ariannol',
        'Making an application to settle finances following a divorce or ending a civil partnership',
        'Gwneud cais i setlo materion ariannol yn dilyn ysgariad neu ddiweddu partneriaeth sifil'
       );

-- newcastle-civil-family-courts-and-tribunals-centre
DO $$

BEGIN
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
      WHERE sc.slug = 'newcastle-civil-family-courts-and-tribunals-centre'
      AND sca.postcode = 'BD3 7BH'
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
    WHERE name = 'Immigration'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

-- telford-county-court-and-family-court
DO $$

BEGIN
  UPDATE public.search_courtsecondaryaddresstype
  SET area_of_law_id = (
    SELECT id
    FROM public.search_areaoflaw
    WHERE name = 'Civil'
  )
  WHERE address_id = (
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
  ));

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;
