-- FACT-1228
CREATE TABLE public.search_courtsecondaryaddresstype
(
    id             integer PRIMARY KEY NOT NULL,
    address_id     integer             NOT NULL,
    area_of_law_id integer,
    court_type_id  integer
);

CREATE SEQUENCE public.search_courtsecondaryaddresstype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.search_courtsecondaryaddresstype_id_seq OWNED BY public.search_courtsecondaryaddresstype.id;
ALTER TABLE ONLY public.search_courtsecondaryaddresstype ALTER COLUMN id SET DEFAULT nextval('public.search_courtsecondaryaddresstype_id_seq'::regclass);

-- Remove description and description cy columns from address table
ALTER TABLE ONLY public.search_courtaddress DROP COLUMN description;
ALTER TABLE ONLY public.search_courtaddress DROP COLUMN description_cy;

DO $$

BEGIN
  INSERT INTO public.search_courtsecondaryaddresstype (address_id, area_of_law_id, court_type_id)
  VALUES ((
    SELECT id FROM public.search_courtaddress
    WHERE court_id = (
      SELECT id
      FROM public.search_court
      WHERE slug = 'newcastle-civil-family-courts-and-tribunals-centre'
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
