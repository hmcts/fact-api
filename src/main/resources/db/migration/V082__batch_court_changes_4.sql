--- FACT-425 ---
UPDATE search_court
SET alert = 'This building is closed today - 19/04/21. We apologise for any inconvenience. Parties with hearings are being contacted and remote hearings continue.  Anyone with an urgent enquiry please contact: 07989 658110 . Further updates to follow.'
WHERE slug = 'manchester-civil-justice-centre-civil-and-family-courts';

--- FACT-435 ---
UPDATE search_court
SET alert = 'We are currently experiencing intermittent problems with our telephone systems which we are working hard to repair. If you can’t get through please keep trying or email somersetcrime@justice.gov.uk. Apologies for any inconvenience you may experience.'
WHERE slug = 'yeovil-county-family-and-magistrates-court';

UPDATE search_court
SET alert = 'We are currently experiencing intermittent problems with our telephone systems which we are working hard to repair. If you can’t get through please keep trying or email somersetcrime@justice.gov.uk. Apologies for any inconvenience you may experience.'
WHERE slug = 'north-somerset-magistrates-court';

--- FACT-434 ---
UPDATE search_court
SET alert = 'We are currently experiencing intermittent problems with our telephone systems which we are working hard to repair. If you can’t get through please keep trying or email somersetcrime@justice.gov.uk. Apologies for any inconvenience you may experience.'
WHERE slug = 'taunton-crown-county-and-family-court';

UPDATE search_court
SET alert = 'We are currently experiencing intermittent problems with our telephone systems which we are working hard to repair. If you can’t get through please keep trying or email somersetcrime@justice.gov.uk. Apologies for any inconvenience you may experience.'
WHERE slug = 'taunton-magistrates-court-tribunals-and-family-hearing-centre';

--- FACT-401 ---
UPDATE search_courtaddress
SET address = 'The Law Courts,
              Newcastle Magistrates Court,
              The Quayside',
    town_name = 'Newcastle-upon-Tyne',
    postcode = 'NE1 3LA'
WHERE address_type_id = 5881
  AND court_id IN (
	SELECT id
	FROM search_court
	WHERE slug = 'berwick-upon-tweed-magistrates-court'
);

--- FACT-422 ---
UPDATE search_contact
SET explanation = 'Monday to Friday 9 am until 5 pm'
WHERE name = 'Witness care unit'
  AND id IN (
	SELECT cc.contact_id
	FROM search_courtcontact cc
		     INNER JOIN search_court c ON cc.court_id = c.id
	WHERE c.slug = 'newton-aycliffe-magistrates-court'
);

--- FACT-433 ---
UPDATE search_court
SET cci_code = '554'
WHERE slug = 'brighton-county-court';
