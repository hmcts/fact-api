--- FACT-418 ---
UPDATE search_court
SET alert = 'We’re aware of fraudulent calls (mimicking Inner London Crown Court’s phone number) claiming to be from HMRC/Government Departments. If you receive a call like this please report it to Action Fraud (0300 123 2040) and HMRC immediately.'
WHERE slug = 'inner-london-crown-court';

--- FACT-403 ---
UPDATE search_facility
SET description = '<p>Assistance dogs are welcome</p>'
WHERE name = 'Assistance dogs'
AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'bradford-combined-court-centre'
);

--- FACT-406 ---
UPDATE search_facility
SET description = '<p>Assistance dogs are welcome</p>'
WHERE name = 'Assistance dogs'
AND id IN (
SELECT cf.facility_id
FROM search_courtfacility cf
	     INNER JOIN search_court c ON cf.court_id = c.id
WHERE c.slug = 'bradford-and-keighley-magistrates-court-and-family-court'
);

--- FACT-413 ---
UPDATE search_facility
SET description = '<p>Assistance dogs are welcome</p>'
WHERE name = 'Assistance dogs'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'harrogate-justice-centre'
);

--- FACT-414 ---
UPDATE search_facility
SET description = '<p>Assistance dogs are welcome</p>'
WHERE name = 'Assistance dogs'
  AND id IN (
	SELECT cf.facility_id
	FROM search_courtfacility cf
		     INNER JOIN search_court c ON cf.court_id = c.id
	WHERE c.slug = 'blackburn-family-court'
);

--- FACT-400 ---
UPDATE search_contact
SET number = '07583 675059'
WHERE name = 'Jury service'
AND id IN (
	SELECT cc.contact_id
	FROM search_courtcontact cc
	INNER JOIN search_court c ON cc.court_id = c.id
	WHERE c.slug = 'bournemouth-crown-court'
);

UPDATE search_contact
SET number = '07583 675059'
WHERE name = 'Jury service'
  AND id IN (
	SELECT cc.contact_id
	FROM search_courtcontact cc
		     INNER JOIN search_court c ON cc.court_id = c.id
	WHERE c.slug = 'bournemouth-and-poole-county-court-and-family-court'
);

UPDATE search_contact
SET number = '01202 502 836'
WHERE name = 'Enquiries'
AND explanation = 'General enquiries'
  AND id IN (
	SELECT cc.contact_id
	FROM search_courtcontact cc
		     INNER JOIN search_court c ON cc.court_id = c.id
	WHERE c.slug = 'bournemouth-crown-court'
);

UPDATE search_contact
SET number = '01202 502 836'
WHERE name = 'Crown Court'
AND id IN (
	SELECT cc.contact_id
	FROM search_courtcontact cc
		     INNER JOIN search_court c ON cc.court_id = c.id
	WHERE c.slug = 'bournemouth-and-poole-county-court-and-family-court'
);
