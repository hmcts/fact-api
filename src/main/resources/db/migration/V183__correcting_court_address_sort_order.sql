UPDATE public.search_courtaddress
SET sort_order =
      CASE WHEN address_type_id in (select id FROM public.search_addresstype where name in ('Visit us', 'Visit or contact us')) THEN 0
           ELSE 1 END;


