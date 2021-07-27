INSERT INTO public.search_courtaddress(address, postcode, address_type_id, court_id, town_name)
VALUES(E'Manchester address',
       'M1 7EP',
       (SELECT id FROM public.search_addresstype WHERE name = 'Write to us'),
       (SELECT id FROM public.search_court WHERE slug = 'ipswich-county-court-and-family-hearing-centre'),
       'Manchester'
      );
