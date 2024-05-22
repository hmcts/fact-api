-- FACT_1775
-- add Cath link to Oxford Combined Court Centre

INSERT INTO public.search_additionallink(url, description, description_cy)
VALUES ('https://pip-frontend.demo.platform.hmcts.net/summary-of-publications?locationId=3',
        'Information about cases at this location',
        '');

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court where slug = 'oxford-combined-court-centre'),
        (SELECT id FROM public.search_additionallink where description = 'Information about cases at this location'),
        0);





