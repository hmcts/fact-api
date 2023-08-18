-- FACT-1437

UPDATE search_court sc
SET info = CONCAT('<p>The Civil National Business Centre (formerly CCBC) is also in Northampton and is an entirely separate entity to Northampton Crown, County and Family Court. Northampton County and Family Court cannot answer queries on behalf of CNBC. <a class="external-link" title="Follow link" href="https://www.find-court-tribunal.service.gov.uk/courts/county-court-business-centre-ccbc" target="_blank" rel="nofollow noopener">https://www.find-court-tribunal.service.gov.uk/courts/county-court-business-centre-ccbc</a> Opening times - Monday to Friday 08:30 to 17:00.</p>',info)
WHERE slug = 'northampton-crown-county-and-family-court';

