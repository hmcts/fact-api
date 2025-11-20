-- Addressing local courts that have missing regions; searched and allocated through lon/lat search
WITH court_region_map (court_name, region_id) AS (
  VALUES
    ('Barkingside  Magistrates'' Court', 7),
    ('Birmingham Social Security and Child Support Tribunal', 8),
    ('Cardiff  Magistrates'' Court', 12),
    ('Cardiff Social Security and Child Support Tribunal', 12),
    ('Central London County Court (Bankruptcy)', 7),
    ('Cleveland, Durham, Northumbria and North Yorkshire Central Enforcement Unit', 3),
    ('Court of Protection', 7),
    ('Criminal Fines Collection and Enforcement Contact Centre - Wales, South West, South East, London, East Midlands', 10),
    ('Employment Appeal Tribunal (England and Wales)', 7),
    ('Exeter Law Courts', 11),
    ('King''s Bench Division', 7),
    ('Leeds District Magistrates'' Court and Family Court', 9),
    ('London (Central) Social Security and Child Support Tribunal', 7),
    ('Loughborough Court', 6),
    ('Oxford Magistrates Court', 4),
    ('Residential Property Eastern Region - First-tier Tribunal (Property Chamber)', 1),
    ('Residential Property London Region - First Tier Tribunal (Property Chamber)', 7),
    ('Residential Property Midlands Region - First Tier Tribunal (Property Chamber)', 8),
    ('Residential Property Northern Region - First Tier Tribunal (Property Chamber)', 2),
    ('Residential Property Southern Region - First Tier Tribunal (Property Chamber)', 4),
    ('Southampton (West Hampshire) Magistrates'' Court', 4),
    ('Special Educational Needs and Disability - SEND (First-tier Tribunal)', 3),
    ('Upper Tribunal (Administrative Appeals Chamber)', 7),
    ('Upper Tribunal (Tax and Chancery Chamber)', 7),
    ('Wrexham County and Family  Court', 2)
)
UPDATE search_court c
SET region_id = m.region_id
  FROM court_region_map m
WHERE c.name = m.court_name;

-- Addressing prod courts that have missing regions; searched and allocated through lon/lat search
WITH court_slug_region_map (court_slug, region_id) AS (
  VALUES
    ('agricultural-land-and-drainage-first-tier-tribunal-property-chamber', 2),
    ('belmarsh-magistrates-court', 7),
    ('birmingham-social-security-and-child-support-tribunal', 8),
    ('bristol-district-probate-registry', 12),
    ('cardiff-social-security-and-child-support-tribunal', 12),
    ('care-standards-tribunal', 3),
    ('central-london-county-court-bankruptcy', 7),
    ('cheshire-and-merseyside-central-payment-and-enforcement-centre', 2),
    ('civil-national-business-centre-cnbc', 1),
    ('county-court-money-claims-centre-ccmcc', 2),
    ('court-of-protection', 7),
    ('employment-appeal-tribunal-england-and-wales', 7),
    ('general-regulatory-chamber', 6),
    ('hereford-crown-court', 8),
    ('hmcts-wales-and-the-south-west-enforcement-contact-centre', 12),
    ('ipswich-district-probate-registry', 1),
    ('kent-surrey-and-sussex-enforcement-business-centre', 7),
    ('land-registration', 7),
    ('london-collection-and-compliance-centre', 7),
    ('maintenance-enforcement-business-centre-wales', 10),
    ('maintenance-enforcement-centre-london', 7),
    ('medway-county-court-and-family-court', 7),
    ('mental-health-tribunal', 6),
    ('midlands-and-wales-regional-fixed-penalty-office', 6),
    ('money-claim-online-mcol', 1),
    ('newport-south-wales-regional-divorce-centre', 12),
    ('north-regional-fixed-penalty-office', 9),
    ('north-west-regional-divorce-centre', 2),
    ('residential-property-eastern-region-first-tier-tribunal-property-chamber', 1),
    ('residential-property-london-region-first-tier-tribunal-property-chamber', 7),
    ('residential-property-midlands-region-first-tier-tribunal-property-chamber', 8),
    ('residential-property-northern-region-first-tier-tribunal-property-chamber', 2),
    ('residential-property-southern-region-first-tier-tribunal-property-chamber', 4),
    ('south-regional-fixed-penalty-office', 7),
    ('special-educational-needs-and-disability-first-tier-tribunal', 3),
    ('tax-chamber-first-tier-tribunal', 8),
    ('traffic-enforcement-centre-tec', 1),
    ('upper-tribunal-administrative-appeals-chamber', 7),
    ('upper-tribunal-tax-and-chancery-chamber', 7),
    ('wales-enforcement-office', 10),
    ('war-pensions-armed-forces-compensation-chamber', 7)
)
UPDATE search_court c
SET region_id = m.region_id
  FROM court_slug_region_map m
WHERE c.slug = m.court_slug;
