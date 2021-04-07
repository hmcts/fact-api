INSERT INTO search_contact(id, name, name_cy, number, sort_order, explanation, explanation_cy, in_leaflet)
VALUES (
	       DEFAULT,
	       'General enquiries',
	       'Ymholiadau',
	       '01865 264200',
	       8,
	       '',
	       '',
	       TRUE
       );
INSERT INTO search_courtcontact(id, contact_id, court_id)
VALUES (
	       DEFAULT,
	       (SELECT id FROM public.search_contact ORDER BY id DESC LIMIT 1),
	       (SELECT id FROM public.search_court WHERE slug = 'oxford-combined-court-centre')
       );

INSERT INTO search_contact(id, name, name_cy, number, sort_order, explanation, explanation_cy, in_leaflet)
VALUES (
	       DEFAULT,
	       'Warrant of Control South East',
	       '',
	       '01865 264273',
	       8,
	       '',
	       '',
	       TRUE
       );
INSERT INTO search_courtcontact(id, contact_id, court_id)
VALUES (
	       DEFAULT,
	       (SELECT id FROM public.search_contact ORDER BY id DESC LIMIT 1),
	       (SELECT id FROM public.search_court WHERE slug = 'oxford-combined-court-centre')
       );


INSERT INTO search_contact(id, name, name_cy, number, sort_order, explanation, explanation_cy, in_leaflet)
VALUES (
	       DEFAULT,
	       'Enquiries',
	       'Ymholiadau',
	       '01872 267460',
	       8,
	       'Civil and Family Enquiries',
	       '',
	       TRUE
       );
INSERT INTO search_courtcontact(id, contact_id, court_id)
VALUES (
	       DEFAULT,
	       (SELECT id FROM public.search_contact ORDER BY id DESC LIMIT 1),
	       (SELECT id FROM public.search_court WHERE slug = 'truro-county-court-and-family-court')
       );


UPDATE search_email
SET address = 'taxappeals@justice.gov.uk'
WHERE description = 'Enquiries'
  AND id IN (
	SELECT email_id
	FROM search_courtemail ce
		     INNER JOIN search_court c ON ce.court_id = c.id
	WHERE c.slug = 'tax-chamber-first-tier-tribunal'
);


INSERT INTO search_email(id, description, description_cy, address, explanation, explanation_cy)
VALUES(
	      DEFAULT,
	      'Possession enquiries',
	      '',
	      'scarborough.possession@justice.gov.uk',
	      '',
	      ''
      );
INSERT INTO search_courtemail
VALUES(
	      DEFAULT,
	      (SELECT id FROM public.search_court WHERE slug = 'scarborough-justice-centre'),
	      (SELECT id FROM public.search_email ORDER BY id DESC LIMIT 1),
	      10
      );


UPDATE search_contact
SET explanation = 'Switchboard'
WHERE name = 'Business and Property Courts'
  AND
		id IN (
		SELECT contact_id
		FROM search_courtcontact cc
			     INNER JOIN search_court c ON cc.court_id = c.id
		WHERE c.slug = 'birmingham-civil-and-family-justice-centre'
	);


UPDATE search_email
SET address = 'NY-Crime.enquiries@justice.gov.uk'
WHERE description = 'Criminal queries'
  AND
		id IN (
		SELECT email_id
		FROM search_courtemail ce
			     INNER JOIN search_court c ON ce.court_id = c.id
		WHERE c.slug = 'scarborough-justice-centre'
	);
