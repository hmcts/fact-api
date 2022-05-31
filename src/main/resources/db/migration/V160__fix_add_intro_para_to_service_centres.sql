DELETE FROM public.search_servicecentre ssc
WHERE court_id IN (SELECT id FROM public.search_court where slug IN ('divorce-service-centre', 'immigration-and-asylum-appeals-service-centre',
  'civil-money-claims-service-centre', 'probate-service-centre',
  'single-justice-procedures-service-centre', 'social-security-and-child-support-appeals-service-centre',
  'cleveland-durham-northumbria-and-north-yorkshire-central-enforcement-unit',
  'devon-and-cornwall-central-finance-unit', 'greater-manchester-central-accounts-and-enforcement-unit',
  'humberside-enforcement-unit', 'london-regional-confiscation-unit',
  'midlands-regional-confiscation-unit', 'north-east-regional-confiscation-unit', 'north-west-regional-confiscation-unit',
  'south-east-regional-confiscation-unit', 'south-yorkshire-enforcement-unit', 'wales-and-south-west-confiscation-unit',
  'staffordshire-central-finance-and-enforcement-unit', 'north-yorkshire-magistrates-courts-central-finance-unit'));

--- FACT-1151 ---
INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'divorce-service-centre'),
        'This location services all of England and Wales for civil partnership, divorce and if you are making an ' ||
        'application to settle your finances following a divorce (financial remedy), please refer to the guidance found ' ||
        'here. We do not provide an in-person service.',
        'Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer partneriaeth sifil, ysgariad and os ydych ' ||
        'chi’n gwneud cais i setlo eich materion ariannol yn dilyn ysgariad (rhwymedi ariannol), cyfeiriwch at y ' ||
        'cyfarwyddyd sydd ar gael yma. Nid ydym yn darparu gwasanaeth wyneb yn wyneb.');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'immigration-and-asylum-appeals-service-centre'),
        'This location services all of England and Wales for immigration and asylum. We do not provide an in-person ' ||
        'service.',
        'Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer mewnfudo. Nid ydym yn darparu gwasanaeth ' ||
        'wyneb yn wyneb.');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'civil-money-claims-service-centre'),
        'This location services all of England and Wales for money claims. We do not provide an in-person service.',
        'Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer hawliadau am arian. Nid ydym yn darparu ' ||
        'gwasanaeth wyneb yn wyneb.');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'probate-service-centre'),
        'This location services all of England and Wales for probate. We do not provide an in-person service.',
        'Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer profiant. Nid ydym yn darparu gwasanaeth ' ||
        'wyneb yn wyneb.');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'single-justice-procedures-service-centre'),
        'This location services all of England and Wales for crime and single justice procedure. We do not provide an ' ||
        'in-person service.',
        'Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer troseddu a y weithdrefn un ynad. Nid ydym ' ||
        'yn darparu gwasanaeth wyneb yn wyneb.');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'social-security-and-child-support-appeals-service-centre'),
        'This location services all of England and Wales for benefits. We do not provide an in-person service.',
        'Mae’r lleoliad hwn yn gwasanaethu Cymru a Lloegr i gyd ar gyfer budd-daliadau. Nid ydym yn darparu ' ||
        'gwasanaeth wyneb yn wyneb.');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'cleveland-durham-northumbria-and-north-yorkshire-central-enforcement-unit'),
        '',
        '');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'devon-and-cornwall-central-finance-unit'),
        '',
        '');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'greater-manchester-central-accounts-and-enforcement-unit'),
        '',
        '');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services Hull, Grimsby, Beverley and Bridlington for fine enforcement orders We do not provide ' ||
       'an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'humberside-enforcement-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'humberside-enforcement-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services London for confiscation orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'london-regional-confiscation-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'london-regional-confiscation-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services the Midlands for confiscation orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'midlands-regional-confiscation-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'midlands-regional-confiscation-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services the Crown Courts of Bradford, Doncaster, Durham, Great Grimsby, Kingston-upon-Hull, ' ||
       'Leeds, Newcastle-upon-Tyne, Sheffield, Teesside and York for confiscation orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'north-east-regional-confiscation-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'north-east-regional-confiscation-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services the Crown Courts of Bolton, Burnley, Carlisle, Chester, Lancaster, Liverpool, Manchester,' ||
       ' Preston and  Warrington for confiscation orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'north-west-regional-confiscation-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'north-west-regional-confiscation-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'north-yorkshire-magistrates-courts-central-finance-unit'),
        '',
        '');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services Bedfordshire, Cambridgeshire, Essex, Hertfordshire, Kent, Norfolk, Suffolk, Surrey, ' ||
       'Sussex and Thames Valley for confiscation orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'south-east-regional-confiscation-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'south-east-regional-confiscation-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services Sheffield, Barnsley and Doncaster for fine enforcement orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'south-yorkshire-enforcement-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'south-yorkshire-enforcement-unit' );

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'staffordshire-central-finance-and-enforcement-unit'),
        '',
        '');

INSERT INTO public.search_servicecentre (court_id, intro_paragraph, intro_paragraph_cy)
SELECT court.id,
       'This location services South Wales, Dyfed Powys, Gwent, North Wales, Devon and Cornwall, Avon and Somerset, ' ||
       'Hampshire, Isle of Wight, Gloucestershire, Dorset and Wiltshire for confiscation orders. We do not provide an in-person service.',
       ''
FROM (select * from public.search_court WHERE slug = 'wales-and-south-west-confiscation-unit') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'wales-and-south-west-confiscation-unit' );

