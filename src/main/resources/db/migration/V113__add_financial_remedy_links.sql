CREATE TABLE public.admin_sidebarlocation (
    id integer PRIMARY KEY NOT NULL,
    name character varying(255) NOT NULL
);

CREATE SEQUENCE public.admin_sidebarlocation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_sidebarlocation_id_seq OWNED BY public.admin_sidebarlocation.id;
ALTER TABLE ONLY public.admin_sidebarlocation ALTER COLUMN id SET DEFAULT nextval('public.admin_sidebarlocation_id_seq'::regclass);

INSERT INTO public.admin_sidebarlocation(name)
VALUES ('This location handles'),
       ('Find out more about');


-- Add additional 'location_id' column to search_additionallink table for the sidebar location
ALTER TABLE public.search_additionallink
    ADD COLUMN location_id integer NULL;

UPDATE public.search_additionallink
SET description = 'Support Through Court (Independent charity)',
    description_cy = 'Support Through Court (Elusen annibynnol)',
    location_id = (SELECT id FROM public.admin_sidebarlocation where name = 'Find out more about')
WHERE description = 'Support through court (Independent charity)';

-- Not-in-person courts
INSERT INTO public.search_additionallink(url, description, description_cy, location_id)
VALUES ('https://www.gov.uk/money-property-when-relationship-ends',
        'If you are making an application to settle your finances following a divorce (Financial Remedy), please refer to the guidance found here',
        'Os ydych chi’n gwneud cais i setlo eich materion ariannol yn dilyn ysgariad (Rhwymedi Ariannol), cyfeiriwch at y cyfarwyddyd sydd ar gael yma',
        (SELECT id FROM public.admin_sidebarlocation where name = 'Find out more about')
       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'bury-st-edmunds-regional-divorce-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'divorce-service-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-regional-divorce-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'north-west-regional-divorce-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'south-west-regional-divorce-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       );

-- In-person courts
INSERT INTO public.search_additionallink(url, description, description_cy, location_id)
VALUES ('https://www.gov.uk/money-property-when-relationship-ends',
        'Financial Remedy',
        'Rhwymedi Ariannol',
        (SELECT id FROM public.admin_sidebarlocation where name = 'This location handles')
       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'central-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'birmingham-civil-and-family-justice-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'nottingham-county-court-and-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-county-court-and-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'swansea-civil-justice-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'liverpool-civil-and-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'sheffield-combined-court-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'newcastle-civil-family-courts-and-tribunals-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'leeds-combined-court-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'medway-county-court-and-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'manchester-civil-justice-centre-civil-and-family-courts'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'peterborough-combined-court-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'oxford-combined-court-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'bristol-civil-and-family-justice-centre'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'plymouth-combined-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'wrexham-county-and-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       ),
       ((SELECT id FROM public.search_court WHERE slug = 'preston-crown-court-and-family-court-sessions-house'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       );
