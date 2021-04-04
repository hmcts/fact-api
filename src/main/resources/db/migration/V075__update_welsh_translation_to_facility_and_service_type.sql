UPDATE search_service
SET name_cy = 'Cofrestrfeydd dosbarth yr Uchel Lys'
WHERE name = 'High Court district registries';

UPDATE search_facility
SET name_cy = 'Cyfleuster newid cewynnau babanod'
WHERE name = 'Baby changing facility';

UPDATE search_facility
SET name_cy = 'Ystafell weddïo/dawel'
WHERE name = 'Prayer / Quiet room';

UPDATE search_facility
SET name_cy = 'Cyfleusterau fideo'
WHERE name = 'Video facilities';

UPDATE search_facility
SET name_cy = 'Cysylltiad rhwydwaith di-wifr'
WHERE name = 'Wireless network connection';

UPDATE admin_facilitytype
SET name_cy = 'Cyfleuster newid cewynnau babanod'
WHERE name = 'Baby changing facility';

UPDATE admin_facilitytype
SET name_cy              = 'Ystafell weddïo/dawel',
    image_description_cy = 'Eicon ystafell weddïo/dawel'
WHERE name = 'Prayer / Quiet room';

UPDATE admin_facilitytype
SET name_cy = 'Cyfleusterau fideo'
WHERE name = 'Video facilities';

UPDATE admin_facilitytype
SET name_cy = 'Cysylltiad rhwydwaith di-wifr'
WHERE name = 'Wireless network connection';
