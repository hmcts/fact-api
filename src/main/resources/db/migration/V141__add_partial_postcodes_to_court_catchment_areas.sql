INSERT INTO search_courtpostcode(postcode, court_id)
VALUES ('GU147', (select id from search_court where slug = 'aldershot-justice-centre')),
       ('GU146', (select id from search_court where slug = 'aldershot-justice-centre')),
       ('GU111', (select id from search_court where slug = 'aldershot-justice-centre')),
       ('GU126', (select id from search_court where slug = 'aldershot-justice-centre')),
       ('GU125', (select id from search_court where slug = 'aldershot-justice-centre')),
       ('RH140', (select id from search_court where slug = 'horsham-county-court-and-family-court')),
       ('RH201', (select id from search_court where slug = 'worthing-county-court-and-family-court')),
       ('GU280', (select id from search_court where slug = 'worthing-county-court-and-family-court'));
