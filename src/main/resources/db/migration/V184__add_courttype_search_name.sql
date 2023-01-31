ALTER TABLE public.search_courttype
  ADD COLUMN search character varying(255);

update public.search_courttype
set search = 'Magistrates'
where name = 'Magistrates'' Court';

update public.search_courttype
set search = 'Family'
where name = 'Family Court';

update public.search_courttype
set search = 'Tribunal'
where name = 'Tribunal';

update public.search_courttype
set search= 'County'
where name = 'County Court';

update public.search_courttype
set search= 'Crown'
where name = 'Crown Court';
