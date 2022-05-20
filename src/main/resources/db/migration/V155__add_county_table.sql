CREATE TABLE public.search_county
(
	id             integer PRIMARY KEY NOT NULL,
	name           character varying(250),
	country        character varying(250)
);

CREATE SEQUENCE public.search_county_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_county_id_seq OWNED BY public.search_county.id;

ALTER TABLE ONLY public.search_county
	ALTER COLUMN id SET DEFAULT nextval('public.search_county_id_seq'::regclass);


COPY public.search_county (name, country) FROM stdin;
Bedfordshire	England
Berkshire	England
Bristol	England
Buckinghamshire	England
Cambridgeshire	England
City of London	England
Cheshire	England
Cornwall	England
County Durham	England
Cumbria	England
Derbyshire	England
Devon	England
Dorset	England
East Riding of Yorkshire	England
East Sussex	England
Essex	England
Gloucestershire	England
Greater London	England
Greater Manchester	England
Hampshire	England
Herefordshire	England
Hertfordshire	England
Isle of Wight	England
Kent	England
Lancashire	England
Leicestershire	England
Lincolnshire	England
Merseyside	England
Norfolk	England
North Yorkshire	England
Northamptonshire	England
Northumberland	England
Nottinghamshire	England
Oxfordshire	England
Rutland	England
Shropshire	England
Somerset	England
South Yorkshire	England
Staffordshire	England
Suffolk	England
Surrey	England
Tyne & Wear	England
Warwickshire	England
West Sussex	England
West Midlands	England
West Yorkshire	England
Wiltshire	England
Worcestershire	England
Aberdeen city	Scotland
Aberdeenshire	Scotland
Angus	Scotland
Argyll & Bute	Scotland
City of Edinburgh	Scotland
Clackmannanshire	Scotland
Dumfries & Galloway	Scotland
Dundee City	Scotland
East Ayrshire	Scotland
East Dunbartonshire	Scotland
East Lothian	Scotland
East Renfrewshire	Scotland
Falkirk	Scotland
Fife	Scotland
Glasgow City	Scotland
Highland	Scotland
Inverclyde	Scotland
Modlothian	Scotland
Moray	Scotland
Nah-Eileanan Siar (Western Isles)	Scotland
North Ayrshire	Scotland
North Lanarkshire	Scotland
Orkney	Scotland
Perth & Kinross	Scotland
Renfrewshire	Scotland
Scottish Borders	Scotland
Shetland	Scotland
South Ayrshire	Scotland
South Lanarkshire	Scotland
Stirling	Scotland
West Dunbartonshire	Scotland
West Lothian	Scotland
Blaenau Gwent	Wales
Bridgend	Wales
Caerphilly	Wales
Cardiff	Wales
Carmarthenshire	Wales
Ceredigion	Wales
Conwy	Wales
Denbighshire	Wales
Flintshire	Wales
Gwynedd	Wales
Isle of Anglesey	Wales
Merthyr Tydfil	Wales
Monmouthshire	Wales
Neath Port Talbot	Wales
Newport	Wales
Pembrokeshire	Wales
Powys	Wales
Rhondda Cynon Taff	Wales
Swansea	Wales
Torfaen	Wales
Vale of Glamorgan	Wales
Wrexham	Wales
Antrim	Northern Ireland
Armagh	Northern Ireland
Down	Northern Ireland
Fermanagh	Northern Ireland
Londonderry	Northern Ireland
Tyrone	Northern Ireland
\.
