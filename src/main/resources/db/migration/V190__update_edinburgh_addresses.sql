--- Fact 1383 ---
UPDATE search_courtaddress SET address = 'George House
126 George Street', postcode = 'EH2 4HH', town_name = 'Edinburgh',
                               county_id = (SELECT id from search_county WHERE name = 'City of Edinburgh' LIMIT 1)
                           WHERE court_id = (SELECT id FROM search_court WHERE slug = 'edinburgh-social-security-and-child-support-tribunal' LIMIT 1)
                           and address_type_id = (SELECT id FROM search_addresstype WHERE name= 'Visit us' LIMIT 1);


UPDATE search_courtaddress SET address = '52 Melville Street', postcode = 'EH3 7HF', town_name = 'Edinburgh',
                               county_id = (SELECT id from search_county WHERE name = 'City of Edinburgh' LIMIT 1),
                             address_type_id = (SELECT id FROM search_addresstype WHERE name= 'Visit us' LIMIT 1)
                            WHERE court_id = (SELECT id FROM search_court WHERE slug = 'edinburgh-upper-tribunal-administrative-appeals-chamber' LIMIT 1)
                            and address_type_id = (SELECT id FROM search_addresstype WHERE name= 'Visit or contact us' LIMIT 1);


INSERT INTO search_courtaddress(address, postcode, address_type_id, court_id, town_name, county_id, sort_order)
values ('George House
126 George Street','EH2 4HH',(SELECT id FROM search_addresstype WHERE name= 'Write to us' LIMIT 1),
  (SELECT id FROM search_court WHERE slug = 'edinburgh-upper-tribunal-administrative-appeals-chamber' LIMIT 1),
                'Edinburgh',(SELECT id from search_county WHERE name = 'City of Edinburgh' LIMIT 1) ,1);                                                                                 )
