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
Bath and North East Somerset	England
Bedfordshire	England
Berkshire	England
Bristol	England
Buckinghamshire	England
Cambridgeshire	England
Cheshire	England
Cornwall	England
County Durham	England
Cumbria	England
Derbyshire	England
Devon	England
Dorset	England
Sussex	England
Essex	England
Gloucestershire	England
Greater London	England
Greater Manchester	England
Hampshire	England
Herefordshire	England
Hertfordshire	England
Isle of Wight	England
Isles of Scilly	England
Kent	England
Lancashire	England
Leicestershire	England
Lincolnshire	England
Merseyside	England
Norfolk	England
North Somerset	England
Yorkshire	England
Northamptonshire	England
Northumberland	England
Nottinghamshire	England
Oxfordshire	England
Rutland	England
Shropshire	England
Somerset	England
South Gloucestershire	England
South Yorkshire	England
Staffordshire	England
Suffolk	England
Surrey	England
Tyne & Wear	England
Warwickshire	England
West Midlands	England
Wiltshire	England
Worcestershire	England
Aberdeenshire	Scotland
Angus	Scotland
Argyll & Bute	Scotland
Ayrshire	Scotland
Banffshire	Scotland
Berwickshire	Scotland
Borders	Scotland
Caithness	Scotland
Clackmannanshire	Scotland
Dumfries & Galloway	Scotland
Dunbartonshire	Scotland
Lothian	Scotland
East Renfrewshire	Scotland
Fife	Scotland
Highland	Scotland
Inverclyde	Scotland
Kincardineshire	Scotland
Midlothian	Scotland
Moray	Scotland
Lanarkshire	Scotland
Orkney	Scotland
Perth & KInross	Scotland
Renfrewshire	Scotland
Shetland	Scotland
Stirlingshire	Scotland
Western Isles	Scotland
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
Glamorgan	Wales
Wrexham	Wales
Antrim	Northern Ireland
Armagh	Northern Ireland
Down	Northern Ireland
Fermanagh	Northern Ireland
Londonderry	Northern Ireland
Tyrone	Northern Ireland
\.
