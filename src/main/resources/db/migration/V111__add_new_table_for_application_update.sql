CREATE TABLE public.search_applicationupdate (
    id integer PRIMARY KEY NOT NULL,
    type character varying(255) NOT NULL,
    type_cy character varying(255),
    email character varying(255),
    external_link character varying(255),
    external_link_desc character varying(255),
    external_link_desc_cy character varying(255)
);

CREATE SEQUENCE public.search_applicationupdate_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_applicationupdate_id_seq OWNED BY public.search_applicationupdate.id;
ALTER TABLE ONLY public.search_applicationupdate ALTER COLUMN id SET DEFAULT nextval('public.search_applicationupdate_id_seq'::regclass);

CREATE TABLE public.search_courtapplicationupdate (
    id integer PRIMARY KEY NOT NULL,
    court_id integer NOT NULL,
    application_update_id integer NOT NULL,
    sort integer NOT NULL
);

CREATE SEQUENCE public.search_courtapplicationupdate_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_courtapplicationupdate_id_seq OWNED BY public.search_courtapplicationupdate.id;
ALTER TABLE ONLY public.search_courtapplicationupdate ALTER COLUMN id SET DEFAULT nextval('public.search_courtapplicationupdate_id_seq'::regclass);


INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Online money claims',
        'Hawliadau arian ar-lein',
        '',
        'https://money-claim-queries.form.service.justice.gov.uk',
        'Contact HMCTS about a money claim',
        'Cysylltwch â HMCTS ynghylch hawliad arian'
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-money-claims-centre-ccmcc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'civil-money-claims-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('County court money claims',
        'Hawliadau arian llys sirol',
        'ccmcccustomerenquiries@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-money-claims-centre-ccmcc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        1
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Money claims online',
        'Hawliadau arian ar-lein',
        'CCBC@Justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-money-claims-centre-ccmcc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        2
       );


INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'sjs@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'single-justice-procedures-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'contactsscs@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'social-security-and-child-support-appeals-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update (Scotland)',
        'Cael diweddariad (Yr Alban)',
        'SSCSA-Glasgow@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'social-security-and-child-support-appeals-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        1
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Fee paid applications',
        'Ceisiadau am dâl ffioedd',
        'ccbcfees@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-business-centre-ccbc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Centralised Attachment of Earnings (CAPS)',
        'System Ganolog Atafaelu Enillion (CAPS)',
        'caps@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-business-centre-ccbc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        1
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Traffic Enforcement Centre (TEC)',
        'Canolfan Gorfodi Rheoliadau Traffig (TEC)',
        'tec@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-business-centre-ccbc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        2
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('County Court Business Centre (CCBC)',
        'Canolfan Fusnes y Llys Sirol (CCBC)',
        'ccbc@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'county-court-business-centre-ccbc'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        3
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'newportdivorce@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'newport-south-wales-regional-divorce-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'NWRegionalDivorce@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'north-west-regional-divorce-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'sw-region-divorce@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'south-west-regional-divorce-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'divorceunitbse@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'bury-st-edmunds-regional-divorce-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'divorcecase@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'divorce-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'contactia@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'immigration-and-asylum-appeals-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );

INSERT INTO public.search_applicationupdate(type, type_cy, email, external_link, external_link_desc, external_link_desc_cy)
VALUES ('Get an update',
        'Cael diweddariad',
        'contactprobate@justice.gov.uk',
        '',
        '',
        ''
       );

INSERT INTO public.search_courtapplicationupdate(court_id, application_update_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'probate-service-centre'),
        (SELECT id FROM public.search_applicationupdate ORDER BY id DESC LIMIT 1),
        0
       );
