-- FACT-1538
-- update Bury St Edmunds Regional Divorce Centre

INSERT INTO public.search_servicecentre
(court_id, intro_paragraph, id)
VALUES (
         (
           SELECT id
           FROM public.search_court
           WHERE slug = 'bury-st-edmunds-regional-divorce-centre'
         ),
          '<p>This location services all of England and Wales for civil partnership, divorce before 6th April 2022 and paper cases only case numbers will start with BV, LV, EZ, BD. Please see financial remedy contact pages for information on where to send your financial documents. We do not provide an in-person service.</p>
<p>If you have completed a divorce application after 6th April 2022 or your case number started with ZZ then contact CTSC Stoke Courts and Tribunals Service Centre HMCTS Divorce and Dissolution Service, PO Box 13226, Harlow, CM20 9UG</p>',
         DEFAULT
       );


INSERT INTO public.search_additionallink
(id, url, description)
VALUES (
      DEFAULT,
      'https://www.gov.uk/government/publications/hmcts-financial-remedy-centres/regional-family-centre-addresses-in-england-and-wales',
      'For financial remedy contested you need to contact the FRC you filed the application with'
  );

INSERT INTO public.search_courtadditionallink
(id, court_id, additional_link_id, sort)
VALUES (
      DEFAULT,
  (
  SELECT id
  FROM public.search_court
  WHERE slug = 'bury-st-edmunds-regional-divorce-centre'
  ),
  (
  SELECT id
  FROM public.search_additionallink
  WHERE description = 'For financial remedy contested you need to contact the FRC you filed the application with'
  ),
  0
  );


UPDATE public.search_court
SET alert = 'Please do not chase the Court for an update on your application, the Court will contact you with updates as and when required'
where slug = 'bury-st-edmunds-regional-divorce-centre';

DELETE FROM public.search_courtapplicationupdate
where court_id = (
  SELECT id
  FROM public.search_court
  WHERE slug = 'bury-st-edmunds-regional-divorce-centre'
);

DELETE FROM public.search_applicationupdate
where email= 'divorceunitbse@justice.gov.uk';
