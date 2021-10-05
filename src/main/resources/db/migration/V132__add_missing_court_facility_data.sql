--adding missing facility types that exist against courts but were not originally mapped across.

INSERT INTO public.admin_facilitytype(id, name, description, image,image_description, image_file_path, "order", image_description_cy , name_cy)
VALUES(58 , 'First Aid', null ,'first_aid','First aid icon.','uploads/facility/first_aid.png',20,null,null),
      (59 , 'Public pay phone',null, 'public_pay_phone','Public pay phone icon.','uploads/facility/public_pay_phone.png',21,null,null),
      (60 , 'Witness care',null, 'witness_care','Witness care icon.','uploads/facility/witness_care.png',22,null,null);


--removing empty spaces from search facility name column

update public.search_facility
      set name = trim(name);

--adding the missing records to public.search_facilityfacilitytype table

COPY public.search_facilityfacilitytype (facility_id, facility_type_id) FROM stdin;
2379293	43
2379632	43
2378852	43
2379312	43
2378843	43
2381055	43
2378771	43
2379747	43
2379691	43
2379721	43
2379359	43
2379133	43
2378909	43
2381057	43
2378897	43
2379167	43
2381060	43
2379466	43
2381058	43
2378854	43
2379183	43
2380157	43
2379231	43
2379581	43
2380924	58
2379148	58
2379367	58
2380922	58
2378784	58
2379515	58
2379522	58
2379533	58
2379628	58
2379645	58
2380923	58
2380925	58
2380926	58
2379715	59
2379150	59
2378835	60
2379659	60
2379649	60
2381110	60
2379514	60
2379449	60
2378886	60
2379383	60
2378906	60
2379663	60
2378823	60
2379702	60
2382634	49
2382635	49
\.
