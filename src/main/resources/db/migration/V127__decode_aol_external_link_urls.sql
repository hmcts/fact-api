-- FACT-848, FACT-171
UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/divorce'
WHERE name = 'Divorce';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/evicting-tenants'
WHERE name = 'Housing possession';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/courts-tribunals/employment-tribunal'
WHERE name = 'Employment';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/tribunal/sscs'
WHERE name = 'Social security';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/end-civil-partnership'
WHERE name = 'Civil partnership';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/make-court-claim-for-money'
WHERE name = 'Money claims';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/tax-tribunal'
WHERE name = 'Tax';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/child-adoption'
WHERE name = 'Adoption';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/apply-for-bankruptcy'
WHERE name = 'Bankruptcy';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/wills-probate-inheritance'
WHERE name = 'Probate';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/apply-forced-marriage-protection-order'
WHERE name = 'Forced marriage';

UPDATE public.search_areaoflaw
SET external_link = 'https://www.gov.uk/tribunal/iac'
WHERE name = 'Immigration';
