DELETE
FROM search_serviceareacourt
where id = 15;

DELETE
FROM search_serviceareacourt
where id = 5;

update search_servicearea
set name        = 'Single Justice Procedure',
	description = 'T.V. Licensing and minor traffic offences such as speeding'
where slug = 'minor-criminal-offences';

update search_servicearea
set name        = 'Other criminal offences',
	description = 'Criminal cases at a Crown or Magistrates'' Court'
where slug = 'major-criminal-offences';

update search_service
set description = 'Single Justice Procedure and other criminal cases at a Crown or Magistrates’ Court'
where slug = 'crime';
