ALTER TABLE ONLY search_openingtime ADD type_cy varchar(255);
UPDATE search_openingtime SET type_cy = 'cownter Beiliaid agor' WHERE type = 'Bailiff counter open';
UPDATE search_openingtime SET type_cy = 'Oriau agor swyddfa''r Beiliaid' WHERE type = 'Bailiff office open';
UPDATE search_openingtime SET type_cy = 'Gwasanaeth Ffôn Beilïaid' WHERE type = 'Bailiff telephone service';
UPDATE search_openingtime SET type_cy = 'Swyddfa''r Beilïaid' WHERE type = 'Bailiffs Office';
UPDATE search_openingtime SET type_cy = 'Adeilad ar agor' WHERE type = 'Building open';
UPDATE search_openingtime SET type_cy = 'Cownter sifil ar agor' WHERE type = 'Civil counter open';
UPDATE search_openingtime SET type_cy = 'Gwasanaethau Cownter' WHERE type = 'Counter Services';
UPDATE search_openingtime SET type_cy = 'Oriau agor y cownter' WHERE type = 'Counter open';
UPDATE search_openingtime SET type_cy = 'Gwasaneth cownter drwy apwyntiad yn unig' WHERE type = 'Counter service by appointment only';
UPDATE search_openingtime SET type_cy = 'Cownter Ymholiadau''r Llys Sifil' WHERE type = 'County Court enquiries counter';
UPDATE search_openingtime SET type_cy = 'Swyddfa''r llys ar agor' WHERE type = 'Court Office open';
UPDATE search_openingtime SET type_cy = 'Adeilad llys ar gau' WHERE type = 'Court building closed';
UPDATE search_openingtime SET type_cy = 'Adeilad llys ar agor' WHERE type = 'Court building open';
UPDATE search_openingtime SET type_cy = 'Cownter y llys ar gau' WHERE type = 'Court counter closed';
UPDATE search_openingtime SET type_cy = 'Cownter y llys ar agor' WHERE type = 'Court counter open';
UPDATE search_openingtime SET type_cy = 'Desg Ymholiadau''r Llys ar agor' WHERE type = 'Court enquiries open';
UPDATE search_openingtime SET type_cy = 'Oriau agor y Llys' WHERE type = 'Court open';
UPDATE search_openingtime SET type_cy = 'Ymholiadau Llys y Goron' WHERE type = 'Crown Court enquiries';
UPDATE search_openingtime SET type_cy = 'Swyddfa Llys y Goron ar agor' WHERE type = 'Crown Office opens';
UPDATE search_openingtime SET type_cy = 'Swyddfa Ymholiadau ar agor' WHERE type = 'Enquiry Office open';
UPDATE search_openingtime SET type_cy = 'Cownter Teulu ar agor' WHERE type = 'Family counter open';
UPDATE search_openingtime SET type_cy = 'Llys Ynadon ar agor' WHERE type = 'Magistrates'' Court open';
UPDATE search_openingtime SET type_cy = 'Dim gwasanaeth cownter ar gael' WHERE type = 'No counter service available';
