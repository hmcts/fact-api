WITH court_region_map (court_slug, region_id, region_name, country) AS (
  VALUES
    ('ayr-social-security-and-child-support-tribunal', 15, 'Scotland', 'Scotland'),
    ('crime-service-centre', 7, 'London', 'England'),
    ('bury-st-edmunds-regional-divorce-centre', 7, 'London', 'England'),
    ('london-regional-confiscation-unit', 7, 'London', 'England'),
    ('social-security-and-child-support-appeals-service-centre', 7, 'London', 'England'),
    ('maintenance-enforcement-business-centre', 7, 'London', 'England'),
    ('small-claims-mediation-service-scms', 7, 'London', 'England'),
    ('immigration-and-asylum-appeals-service-centre', 7, 'London', 'England'),
    ('humberside-enforcement-unit', 9, 'Yorkshire and the Humber', 'England'),
    ('divorce-service-centre', 7, 'London', 'England'),
    ('south-yorkshire-enforcement-unit', 9, 'Yorkshire and the Humber', 'England'),
    ('single-justice-procedures-service-centre', 7, 'London', 'England'),
    ('north-east-regional-confiscation-unit', 3, 'North East', 'England'),
    ('south-east-regional-confiscation-unit', 4, 'South East', 'England'),
    ('midlands-regional-confiscation-unit', 8, 'West Midlands', 'England'),
    ('online-civil-money-claims-service-centre', 7, 'London', 'England'),
    ('wales-and-south-west-confiscation-unit', 10, 'South Wales West', 'Wales'),
    ('probate-service-centre', 7, 'London', 'England'),
    ('north-west-regional-confiscation-unit', 2, 'North West', 'England')
)
UPDATE public.search_court court
SET region_id = mapping.region_id
FROM court_region_map mapping
JOIN public.search_region region
  ON region.id = mapping.region_id
  AND region.name = mapping.region_name
  AND region.country = mapping.country
WHERE court.slug = mapping.court_slug
  AND court.region_id IS NULL;
