DELETE FROM search_serviceareacourt
WHERE servicearea.id = (select id from search_servicearea where name = 'Divorce') AND catchment_type = 'regional';
