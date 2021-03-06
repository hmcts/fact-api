DROP SEQUENCE public.search_inperson_id_seq;

CREATE SEQUENCE public.search_inperson_id_seq
	START WITH 19
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_inperson_id_seq OWNED BY public.search_inperson.id;

ALTER TABLE ONLY public.search_inperson
	ALTER COLUMN id SET DEFAULT nextval('public.search_inperson_id_seq'::regclass);

DELETE
FROM public.search_inperson
WHERE court_id in (
	SELECT id
	from public.search_court
	WHERE name in ('Darlington Magistrates'' Court and Family Court',
				   'Aldershot Justice Centre',
				   'Central Family Court',
				   'North Staffordshire Justice Centre',
				   'Birmingham Civil and Family Justice Centre'
		));

INSERT INTO public.search_inperson (is_in_person, court_id, access_scheme)
SELECT true, id, true
FROM public.search_court
WHERE name in
	  ('Aberystwyth Justice Centre',
	   'Aldershot Justice Centre',
	   'Amersham Law Courts',
	   'Aylesbury Crown Court',
	   'Barkingside  Magistrates'' Court',
	   'Barnet Civil and Family Courts Centre',
	   'Barnsley Law Courts',
	   'Barnstaple Magistrates'', County and Family Court',
	   'Barrow-in-Furness County Court and Family Court',
	   'Basildon Combined Court',
	   'Basildon Magistrates'' Court and Family Court',
	   'Basingstoke Magistrates'' Court',
	   'Bath Law Courts (Civil, Family and Magistrates)',
	   'Bedford County Court and Family Court',
	   'Bexley Magistrates'' Court',
	   'Birkenhead County Court',
	   'Birmingham Civil and Family Justice Centre',
	   'Birmingham Crown Court',
	   'Birmingham Magistrates'' Court',
	   'Blackburn Family Court',
	   'Blackburn Magistrates'' Court',
	   'Blackpool  Family Court',
	   'Blackpool Magistrates'' and Civil Court',
	   'Blackwood Civil and Family Court',
	   'Bodmin Law Courts',
	   'Bolton Magistrates'' Court',
	   'Boston County Court and Family Court',
	   'Bournemouth Combined Court',
	   'Bradford and Keighley Magistrates'' Court and Family Court',
	   'Bradford Combined Court Centre',
	   'Bradford Tribunal Hearing Centre',
	   'Brentford County and Family Court',
	   'Brighton County Court',
	   'Brighton-Hearing Centre',
	   'Brighton Magistrates'' Court',
	   'Bristol Civil and Family Justice Centre',
	   'Bristol Crown Court',
	   'Bristol Magistrates'' Court and Tribunals Hearing Centre',
	   'Bromley County Court and Family Court',
	   'Bromley Magistrates'' Court',
	   'Burnley Combined Court Centre',
	   'Burnley Magistrates'' Court',
	   'Cambridge County Court and Family Court',
	   'Cambridge Crown Court',
	   'Cambridge Magistrates'' Court',
	   'Cannock Magistrates'' Court',
	   'Canterbury Combined Court Centre',
	   'Canterbury Magistrates'' Court',
	   'Cardiff Social Security and Child Support Tribunal',
	   'Carlisle Magistrates'' Court',
	   'Carmarthen County Court and Family Court',
	   'Central Criminal Court',
	   'Central Family Court',
	   'Central London Employment Tribunal',
	   'Chelmsford Crown Court',
	   'Chelmsford Justice Centre',
	   'Chelmsford Magistrates'' Court and Family Court',
	   'Cheltenham Magistrates'' Court',
	   'Chester Civil and Family Justice Centre',
	   'Chester Crown Court',
	   'Chester Magistrates'' Court',
	   'Chesterfield County Court',
	   'City of London Magistrates'' Court',
	   'Clerkenwell and Shoreditch County Court and Family Court',
	   'Colchester Magistrates'' Court and Family Court',
	   'Coventry Combined Court Centre',
	   'Coventry Magistrates'' Court',
	   'Crewe (South Cheshire) Magistrates'' Court',
	   'Croydon County Court and Family Court',
	   'Croydon Magistrates'' Court',
	   'Cwmbran Magistrates'' Court',
	   'Darlington County Court and Family Court',
	   'Darlington Magistrates'' Court and Family Court',
	   'Dartford County Court and Family Court',
	   'Derby Combined Court Centre',
	   'Derby Magistrates'' Court',
	   'Doncaster Justice Centre South',
	   'Doncaster Justice Centre North',
	   'Dudley Magistrates'' Court',
	   'Durham Crown Court',
	   'Durham County Court and Family Court',
	   'Ealing Magistrates'' Court',
	   'East London Tribunal Hearing Centre',
	   'Edmonton County Court and Family Court',
	   'Exeter Law Courts',
	   'Gloucester and Cheltenham County  and Family Court',
	   'Gloucester Crown Court',
	   'Great Grimsby Combined Court Centre',
	   'Great Yarmouth Magistrates'' Court and Family Court',
	   'Grimsby Magistrates'' Court and Family Court',
	   'Guildford Crown Court',
	   'Guildford Magistrates'' Court and Family Court',
	   'Guildford County Court and Family Court',
	   'Harmondsworth Tribunal Hearing Centre',
	   'Harrogate Justice Centre',
	   'Harrow Crown Court',
	   'Hastings County Court and Family Court',
	   'Hatton Cross Tribunal Hearing Centre',
	   'Haverfordwest County Court and Family Court',
	   'Hendon Magistrates'' Court',
	   'Hereford Crown Court',
	   'Hereford Justice Centre',
	   'Hertford County Court and Family Court',
	   'High Wycombe County Court and Family Court',
	   'Highbury Corner Magistrates'' Court',
	   'Horsham Magistrates'' Court',
	   'Hove Trial Centre',
	   'Huddersfield County Court and Family Court',
	   'Hull and Holderness Magistrates'' Court and Hearing Centre',
	   'Inner London Crown Court',
	   'Ipswich County Court and Family Hearing Centre',
	   'Isleworth Crown Court',
	   'Kidderminster Magistrates'' Court',
	   'King''s Lynn Crown Court',
	   'King''s Lynn Magistrates'' Court and Family Court',
	   'Kingston upon Thames County Court and Family Court',
	   'Kingston-upon-Hull Combined Court Centre',
	   'Kirklees (Huddersfield) Magistrates'' Court and Family Court',
	   'Lancaster Courthouse',
	   'Lavender Hill Magistrates'' Court',
	   'Leeds Combined Court Centre',
	   'Leeds Magistrates'' Court and Family Court',
	   'Leicester Crown Court',
	   'Leicester Magistrates'' Court',
	   'Leicester Tribunal Hearing Centre',
	   'Lewes Combined Court Centre',
	   'Leyland Family Hearing Centre',
	   'Lincoln County Court and Family Court',
	   'Lincoln Crown Court',
	   'Lincoln Magistrates'' Court',
	   'Liverpool & Knowsley Magistrates'' Court',
	   'Liverpool Civil and Family Court',
	   'Liverpool Crown Court',
	   'Liverpool District Probate Registry',
	   'Liverpool Social Security and Child Support Tribunal',
	   'Liverpool Youth Court',
	   'Llandrindod Wells Magistrates'' and Family Court',
	   'Llandudno Magistrates'' Court',
	   'Llanelli Law Courts',
	   'London (South) Employment Tribunal',
	   'Loughborough Magistrates'' Court',
	   'Luton and South Bedfordshire Magistrates'' Court and Family Court',
	   'Luton Crown Court',
	   'Luton Justice Centre',
	   'Maidstone Combined Court Centre',
	   'Manchester Civil Justice Centre (Civil and Family Courts)',
	   'Manchester Crown Court (Crown Square)',
	   'Manchester Employment Tribunal',
	   'Manchester Magistrates'' Court',
	   'Manchester Crown Court (Minshull St)',
	   'Manchester Tribunal Hearing Centre',
	   'Mansfield Magistrates'' and County Court',
	   'Margate Magistrates'' Court',
	   'Medway County Court and Family Court',
	   'Medway Magistrates'' Court and Family Court',
	   'Milton Keynes County Court and Family Court',
	   'Milton Keynes Magistrates'' Court and Family Court',
	   'Mold Justice Centre',
	   'Newcastle Civil & Family Courts and Tribunals Centre',
	   'Newcastle Moot Hall',
	   'Newcastle upon Tyne Combined Court Centre',
	   'Newport (South Wales) Immigration and Asylum Tribunal',
	   'Newport (South Wales) Magistrates'' Court',
	   'Newton Abbot Magistrates'' Court',
	   'Newton Aycliffe Magistrates'' Court',
	   'North Staffordshire Justice Centre',
	   'North Tyneside Magistrates'' Court',
	   'Northampton Crown, County and Family Court',
	   'Northampton Magistrates'' Court',
	   'Norwich Combined Court Centre',
	   'Nottingham Crown Court',
	   'Nottingham Magistrates'' Court',
	   'Nottingham Social Security and Child Support Tribunal',
	   'Nuneaton Magistrates'' Court',
	   'Oxford and Southern Oxfordshire Magistrates'' Court',
	   'Oxford Combined Court Centre',
	   'Peterborough Combined Court Centre',
	   'Plymouth Combined Court',
	   'Plymouth Magistrates'' Court',
	   'Pontypridd County Court and Family Court',
	   'Poole Magistrates'' Court',
	   'Port Talbot Justice Centre',
	   'Portsmouth Combined Court Centre',
	   'Portsmouth Magistrates'' Court',
	   'Prestatyn Justice Centre',
	   'Preston Combined Court Centre',
	   'Preston Crown Court and Family Court (Sessions House)',
	   'Preston Magistrates'' Court',
	   'Reading County Court and Family Court',
	   'Reading Crown Court',
	   'Redditch Magistrates'' Court',
	   'Reedley Family Hearing Centre',
	   'Residential Property London Region - First Tier Tribunal (Property Chamber)',
	   'Romford County Court and Family Court',
	   'Romford Magistrates'' Court (formerly Havering Magistrates'' Court)',
	   'Salisbury Law Courts',
	   'Scarborough Justice Centre',
	   'Sefton Magistrates'' Court',
	   'Sheffield Combined Court Centre',
	   'Sheffield Magistrates'' Court',
	   'Shrewsbury Crown Court',
	   'Slough County Court and Family Court',
	   'Snaresbrook Crown Court',
	   'South Tyneside Magistrates'' Court',
	   'Southampton Combined Court Centre',
	   'Southwark Crown Court',
	   'St Albans Crown Court',
	   'St Albans Magistrates'' Court',
	   'St Helens County Court and Family Court',
	   'Stafford Combined Court Centre',
	   'Staines County Court and Family Court',
	   'Stevenage Magistrates'' Court',
	   'Stockport Magistrates'' Court',
	   'Stockport County Court and Family Court',
	   'Stoke-on-Trent Combined Court',
	   'Stratford Magistrates'' Court',
	   'Sunderland County, Family, Magistrates’ and Tribunal Hearings',
	   'Sutton Social Security and Child Support Tribunal',
	   'Swindon Combined Court',
	   'Swindon Magistrates'' Court',
	   'Tameside Magistrates'' Court',
	   'Taunton Crown, County and Family Court',
	   'Taunton Magistrates'' Court, Tribunals and Family Hearing Centre',
	   'Taylor House Tribunal Hearing Centre',
	   'Teesside Combined Court Centre',
	   'Teesside Magistrates'' Court',
	   'Telford Magistrates'' Court',
	   'Thames Magistrates'' Court',
	   'Torquay and Newton Abbot County and Family Court',
	   'Truro Combined Court',
	   'Truro Magistrates'' Court',
	   'Uxbridge County Court and Family Court',
	   'Uxbridge Magistrates'' Court',
	   'Wakefield Civil and Family Justice Centre',
	   'Walsall County and Family Court',
	   'Walsall Magistrates'' Court',
	   'Wandsworth County Court and Family Court',
	   'Warrington Crown Court',
	   'Warrington Magistrates'' Court',
	   'Warwick Combined Court',
	   'Watford County Court and Family Court',
	   'Wellingborough Magistrates'' Court',
	   'Welshpool Magistrates'' Court',
	   'West Cumbria Courthouse',
	   'West London Family Court',
	   'Weymouth Combined Court',
	   'Wigan and Leigh Magistrates'' Court',
	   'Willesden County Court and Family Court',
	   'Willesden Magistrates'' Court',
	   'Wimbledon Magistrates'' Court',
	   'Winchester Combined Court Centre',
	   'Wirral Magistrates'' Court',
	   'Wolverhampton Combined Court Centre',
	   'Wolverhampton Magistrates'' Court',
	   'Wood Green Crown Court',
	   'Worcester Justice Centre',
	   'Worcester Magistrates'' Court',
	   'Worthing Magistrates'' Court',
	   'Yeovil County, Family and Magistrates'' Court',
	   'York County Court and Family Court',
	   'York Crown Court',
	   'York Magistrates'' Court and Family Court');
