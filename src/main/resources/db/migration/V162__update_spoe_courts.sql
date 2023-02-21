CREATE TEMPORARY TABLE temp_table
(
    courtSlug     character varying(250)
);

COPY temp_table (courtSlug) FROM stdin;
newcastle-civil-family-courts-and-tribunals-centre
leeds-combined-court-centre
york-county-court-and-family-court
sheffield-combined-court-centre
kingston-upon-hull-combined-court-centre
cardiff-civil-and-family-justice-centre
newport-south-wales-county-court-and-family-court
swansea-civil-justice-centre
caernarfon-justice-centre
wrexham-county-and-family-court
bristol-civil-and-family-justice-centre
gloucester-and-cheltenham-county-and-family-court
swindon-combined-court
exeter-law-courts
bournemouth-and-poole-county-court-and-family-court
plymouth-combined-court
truro-county-court-and-family-court
portsmouth-combined-court-centre
southampton-combined-court-centre
basingstoke-county-court-and-family-court
liverpool-civil-and-family-court
manchester-civil-justice-centre-civil-and-family-courts
carlisle-combined-court
barrow-in-furness-county-court-and-family-court
birmingham-civil-and-family-justice-centre
coventry-combined-court-centre
derby-combined-court-centre
leicester-county-court-and-family-court
lincoln-county-court-and-family-court
nottingham-county-court-and-family-court
stoke-on-trent-combined-court
wolverhampton-combined-court-centre
worcester-combined-court
central-family-court
east-london-family-court
west-london-family-court
luton-justice-centre
watford-county-court-and-family-court
medway-county-court-and-family-court
norwich-combined-court-centre
chelmsford-county-and-family-court
brighton-hearing-centre
guildford-county-court-and-family-court
oxford-combined-court-centre
milton-keynes-county-court-and-family-court
reading-county-court-and-family-court
preston-crown-court-and-family-court-sessions-house
teesside-combined-court-centre
peterborough-combined-court-centre
taunton-crown-county-and-family-court
northampton-crown-county-and-family-court
\.


INSERT INTO public.search_courtcourttype (court_id, court_type_id)
SELECT  court.id, courttype.id
FROM (select court.id from temp_table as temp
      INNER JOIN public.search_court as court
        ON court.slug = temp.courtSlug) court,
     (select * from search_courttype where name='Family Court') courttype
WHERE Not EXISTS (SELECT 1
                  FROM public.search_courtcourttype
                  WHERE court_type_id = (select id from search_courttype where name='Family Court')
                    AND court_id = court.id);

