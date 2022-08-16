
-- norwich-magistrates-court-and-family-court
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
      WHERE sc.slug = 'norwich-magistrates-court-and-family-court'
      AND sca.postcode = 'NR3 1UR'
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
  null,
    (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  ));

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

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
      WHERE sc.slug = 'norwich-magistrates-court-and-family-court'
      AND sca.postcode = 'NR3 1UP'
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
    WHERE name = 'Crime'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

-- kings-lynn-magistrates-court-and-family-court
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
      WHERE sc.slug = 'kings-lynn-magistrates-court-and-family-court'
      AND sca.postcode = 'NR3 1UP'
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
    WHERE name = 'Crime'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;


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
      WHERE sc.slug = 'kings-lynn-magistrates-court-and-family-court'
      AND sca.postcode = 'NR3 1UR'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

-- great-yarmouth-magistrates-court-and-family-court
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
      WHERE sc.slug = 'great-yarmouth-magistrates-court-and-family-court'
      AND sca.postcode = 'NR3 1UP'
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
    WHERE name = 'Crime'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;


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
      WHERE sc.slug = 'great-yarmouth-magistrates-court-and-family-court'
      AND sca.postcode = 'NR3 1UR'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

-- lancaster-courthouse
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
      WHERE sc.slug = 'lancaster-courthouse'
      AND sca.postcode = 'PR1 2QT'
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
    WHERE name = 'Crime'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;


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
      WHERE sc.slug = 'lancaster-courthouse'
      AND sca.postcode = 'LA1 1XZ'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;


-- loughborough-court
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
      WHERE sc.slug = 'loughborough-court'
      AND sca.postcode = 'LE1 6HG'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Crown Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

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
      WHERE sc.slug = 'loughborough-court'
      AND sca.postcode = 'LE1 6HG'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

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
      WHERE sc.slug = 'loughborough-court'
      AND sca.postcode = 'LE1 6BT'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Magistrates'' Court'
  )
  );
--
EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

-- telford-county-court-and-family-court
--DO $$
--
--BEGIN
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;


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
      WHERE sc.slug = 'telford-county-court-and-family-court'
      AND sca.postcode = 'WV1 3LQ'
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
  null,
      (
    SELECT id
    FROM public.search_courttype
    WHERE name = 'Family Court'
  )
  );

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;

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
      AND sca.postcode = 'NE1 8QF'
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
    WHERE name = 'Money claims'
  ),
  null);

EXCEPTION WHEN OTHERS THEN
  raise notice 'The row has not been added, as the address is not present';
  raise notice '% %', SQLERRM, SQLSTATE;
END;
$$;
