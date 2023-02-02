--1479504	pocock-street-tribunal-hearing-centre
--1479780	clerkenwell-and-shoreditch-county-court-and-family-court
--1479939	norwich-combined-court-centre

--34254	Money claims
--34263	Tax
--34261	Probate

insert into public.search_courtpostcode (postcode, court_id) values ('IP22', 1479504);
insert into public.search_courtpostcode (postcode, court_id) values ('IP222HF', 1479504);

insert into public.search_serviceareacourt (servicearea_id, court_id, catchment_type) values (1,1479504,'national');

insert into public.search_courtareaoflaw (area_of_law_id, court_id) values (34254,1479504);
insert into public.search_courtareaoflaw (area_of_law_id, court_id) values (34263,1479504);
insert into public.search_courtareaoflaw (area_of_law_id, court_id) values (34261,1479504);
