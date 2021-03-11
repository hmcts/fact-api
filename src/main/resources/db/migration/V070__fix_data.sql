

/* Replicant of V042 */
UPDATE public.search_court
SET displayed = TRUE
WHERE slug in
      ('probate-service-centre',
       'divorce-service-centre',
       'civil-money-claims-service-centre',
       'immigration-and-asylum-appeals-service-centre',
       'single-justice-procedures-service-centre',
       'criminal-offences-service-centre',
       'social-security-and-child-support-appeals-service-centre');

/* Replicant of V044 */
ALTER TABLE public.search_areaoflaw
    ADD COLUMN display_name CHARACTER VARYING(500);

ALTER TABLE public.search_areaoflaw
    ADD COLUMN display_name_cy CHARACTER VARYING(500);

UPDATE public.search_areaoflaw
SET display_name = 'Domestic abuse',
    display_name_cy = 'Cam-drin domestig'
WHERE name = 'Domestic violence';

UPDATE public.search_areaoflaw
SET display_name = 'Claims against employers',
    display_name_cy = 'Honiadau yn erbyn cyflogwyr'
WHERE name = 'Employment';

UPDATE public.search_areaoflaw
SET display_name = 'High Court cases – serious or high profile criminal or civil law cases',
    display_name_cy = 'Achosion yn yr Uchel Lys – achosion cyfraith droseddol neu gyfraith sifil difrifol neu sy’n cael cryn amlwgrwydd'
WHERE name = 'High Court District Registry';

UPDATE public.search_areaoflaw
SET display_name = 'Childcare arrangements if you separate from your partner',
    display_name_cy = 'Trefniadau gofal plant os ydych yn gwahanu oddi wrth eich partner'
WHERE name = 'Children';

UPDATE public.search_areaoflaw
SET display_name = 'Housing',
    display_name_cy = 'Tai'
WHERE name = 'Housing possession';

UPDATE public.search_areaoflaw
SET display_name = 'Immigration and asylum',
    display_name_cy = 'Mewnfudo'
WHERE name = 'Immigration';

UPDATE public.search_areaoflaw
SET display_name = 'Benefits',
    display_name_cy = 'Budd-daliadau'
WHERE name = 'Social security';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Mabwysiadu'
WHERE name = 'Adoption';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Methdaliad'
WHERE name = 'Bankruptcy';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Partneriaeth sifil'
WHERE name = 'Civil partnership';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Ysgariad'
WHERE name = 'Divorce';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Hawliadau am arian'
WHERE name = 'Money claims';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Profiant'
WHERE name = 'Probate';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Treth'
WHERE name = 'Tax';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Troseddu'
WHERE name = 'Crime';

/* Replicant of V045 */
ALTER TABLE public.search_areaoflaw
    ADD COLUMN display_external_link CHARACTER VARYING(500);

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/child-adoption/applying-for-an-adoption-court-order'
WHERE name = 'Adoption';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/bankruptcy'
WHERE name = 'Bankruptcy';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/employment-tribunals'
WHERE name = 'Employment';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/looking-after-children-divorce'
WHERE name = 'Children';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/applying-for-probate'
WHERE name = 'Probate';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/appeal-benefit-decision'
WHERE name = 'Social security';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/injunction-domestic-violence'
WHERE name = 'Domestic violence';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/immigration-asylum-tribunal'
WHERE name = 'Immigration';

/* Replicant of V047 */
ALTER TABLE public.search_addresstype
    ADD COLUMN name_cy CHARACTER VARYING(255);

UPDATE public.search_addresstype
SET name = 'Visit us',
    name_cy = 'Ymweld â ni'
WHERE id = 5880;

UPDATE public.search_addresstype
SET name = 'Write to us',
    name_cy = 'Ysgrifennwch atom'
WHERE id = 5881;

UPDATE public.search_addresstype
SET name = 'Visit or contact us',
    name_cy = 'Cysylltu â ni'
WHERE id = 5882;

/* Replicant of V050 */
COPY public.search_areaoflaw (name, external_link, external_link_desc, external_link_cy, external_link_desc_cy,
                              display_name) FROM stdin;
Forced marriage	https%3A//www.gov.uk/apply-forced-marriage-protection-order	Information about forced marriage protection orders	https://www.gov.uk/apply-forced-marriage-protection-order	Gwybodaeth ynglŷn â gorchmynion amddiffyn priodas dan orfod	\N
FGM	https://www.gov.uk/government/collections/female-genital-mutilation	\N	https://www.gov.uk/government/collections/female-genital-mutilation	\N	Female Genital Mutilation
Single justice procedure	\N	\N	\N	\N	\N
\.

COPY public.search_courtareaoflaw (court_id, area_of_law_id, single_point_of_entry) FROM stdin;
1479530	34264	FALSE
1479547	34264	FALSE
1479781	34264	FALSE
1479789	34264	FALSE
1479799	34264	FALSE
1479822	34264	FALSE
1479835	34264	FALSE
1479861	34264	FALSE
1479936	34264	FALSE
1479938	34264	FALSE
1479939	34264	FALSE
1479941	34264	FALSE
1479948	34264	FALSE
1480133	34264	FALSE
1479970	34264	FALSE
1479976	34264	FALSE
1479978	34264	FALSE
1480005	34264	FALSE
1479373	34264	FALSE
1479382	34264	FALSE
1479401	34264	FALSE
1479409	34264	FALSE
1479437	34264	FALSE
1479463	34264	FALSE
1480135	34264	FALSE
1480134	34264	FALSE
1479530	34265	FALSE
1479547	34265	FALSE
1479781	34265	FALSE
1479789	34265	FALSE
1479799	34265	FALSE
1479822	34265	FALSE
1479835	34265	FALSE
1479861	34265	FALSE
1479936	34265	FALSE
1479938	34265	FALSE
1479939	34265	FALSE
1479941	34265	FALSE
1479948	34265	FALSE
1480133	34265	FALSE
1479970	34265	FALSE
1479976	34265	FALSE
1479978	34265	FALSE
1480005	34265	FALSE
1479373	34265	FALSE
1479382	34265	FALSE
1479401	34265	FALSE
1479409	34265	FALSE
1479437	34265	FALSE
1479463	34265	FALSE
1480135	34265	FALSE
1480134	34265	FALSE
\.

UPDATE public.search_servicearea
SET area_of_law_id = 34264
WHERE name = 'Forced marriage';

UPDATE public.search_servicearea
SET area_of_law_id = 34265
WHERE name = 'Female Genital Mutilation';

UPDATE public.search_servicearea
SET area_of_law_id = 34266
WHERE name = 'Minor criminal offences';

DELETE
FROM search_courtareaoflaw
WHERE area_of_law_id = 34258;

DELETE
FROM search_areaoflaw
WHERE name = 'Forced marriage and FGM';

/* Replicant of V051 */
COPY public.search_courtareaoflaw (court_id, area_of_law_id, single_point_of_entry) FROM stdin;
1479476	34266	FALSE
1479484	34266	FALSE
1479486	34266	FALSE
1479487	34266	FALSE
1479490	34266	FALSE
1479491	34266	FALSE
1479493	34266	FALSE
1479494	34266	FALSE
1479497	34266	FALSE
1479498	34266	FALSE
1479502	34266	FALSE
1479503	34266	FALSE
1479505	34266	FALSE
1479507	34266	FALSE
1479508	34266	FALSE
1479513	34266	FALSE
1479516	34266	FALSE
1479518	34266	FALSE
1479519	34266	FALSE
1479522	34266	FALSE
1479523	34266	FALSE
1479527	34266	FALSE
1479531	34266	FALSE
1479534	34266	FALSE
1479535	34266	FALSE
1479536	34266	FALSE
1479539	34266	FALSE
1479540	34266	FALSE
1479541	34266	FALSE
1479543	34266	FALSE
1479545	34266	FALSE
1479546	34266	FALSE
1479548	34266	FALSE
1479549	34266	FALSE
1479550	34266	FALSE
1479553	34266	FALSE
1479561	34266	FALSE
1479562	34266	FALSE
1479563	34266	FALSE
1479564	34266	FALSE
1479565	34266	FALSE
1479566	34266	FALSE
1479567	34266	FALSE
1479568	34266	FALSE
1479569	34266	FALSE
1479571	34266	FALSE
1479572	34266	FALSE
1479573	34266	FALSE
1479574	34266	FALSE
1479575	34266	FALSE
1479577	34266	FALSE
1479579	34266	FALSE
1479580	34266	FALSE
1479581	34266	FALSE
1479582	34266	FALSE
1479586	34266	FALSE
1479590	34266	FALSE
1479591	34266	FALSE
1479592	34266	FALSE
1479593	34266	FALSE
1479594	34266	FALSE
1479595	34266	FALSE
1479596	34266	FALSE
1479597	34266	FALSE
1479598	34266	FALSE
1479599	34266	FALSE
1479600	34266	FALSE
1479601	34266	FALSE
1479602	34266	FALSE
1479603	34266	FALSE
1479606	34266	FALSE
1479607	34266	FALSE
1479608	34266	FALSE
1479609	34266	FALSE
1479610	34266	FALSE
1479611	34266	FALSE
1479612	34266	FALSE
1479617	34266	FALSE
1479619	34266	FALSE
1479620	34266	FALSE
1479621	34266	FALSE
1479622	34266	FALSE
1479624	34266	FALSE
1479625	34266	FALSE
1479626	34266	FALSE
1479627	34266	FALSE
1479628	34266	FALSE
1479629	34266	FALSE
1479630	34266	FALSE
1479632	34266	FALSE
1479633	34266	FALSE
1479635	34266	FALSE
1479637	34266	FALSE
1479638	34266	FALSE
1479640	34266	FALSE
1479643	34266	FALSE
1479645	34266	FALSE
1479646	34266	FALSE
1479647	34266	FALSE
1479649	34266	FALSE
1479650	34266	FALSE
1479652	34266	FALSE
1479654	34266	FALSE
1479655	34266	FALSE
1479656	34266	FALSE
1479657	34266	FALSE
1479659	34266	FALSE
1479660	34266	FALSE
1479664	34266	FALSE
1479665	34266	FALSE
1479666	34266	FALSE
1479667	34266	FALSE
1479668	34266	FALSE
1479669	34266	FALSE
1479670	34266	FALSE
1479671	34266	FALSE
1479672	34266	FALSE
1479677	34266	FALSE
1479678	34266	FALSE
1479680	34266	FALSE
1479681	34266	FALSE
1479682	34266	FALSE
1479683	34266	FALSE
1479684	34266	FALSE
1479685	34266	FALSE
1479686	34266	FALSE
1479687	34266	FALSE
1479689	34266	FALSE
1479690	34266	FALSE
1479691	34266	FALSE
1479692	34266	FALSE
1479693	34266	FALSE
1479699	34266	FALSE
1479700	34266	FALSE
1479709	34266	FALSE
1479711	34266	FALSE
1479713	34266	FALSE
1479714	34266	FALSE
1479809	34266	FALSE
1479673	34266	FALSE
1479732	34266	FALSE
1479734	34266	FALSE
1479740	34266	FALSE
1479742	34266	FALSE
1479743	34266	FALSE
1479745	34266	FALSE
1479746	34266	FALSE
1479747	34266	FALSE
1479749	34266	FALSE
1479757	34266	FALSE
1479760	34266	FALSE
1479761	34266	FALSE
1479775	34266	FALSE
1479778	34266	FALSE
1479781	34266	FALSE
1479783	34266	FALSE
1479784	34266	FALSE
1479785	34266	FALSE
1479789	34266	FALSE
1479790	34266	FALSE
1479791	34266	FALSE
1479792	34266	FALSE
1479793	34266	FALSE
1479796	34266	FALSE
1479798	34266	FALSE
1479799	34266	FALSE
1479801	34266	FALSE
1479803	34266	FALSE
1479806	34266	FALSE
1479808	34266	FALSE
1479810	34266	FALSE
1479812	34266	FALSE
1479813	34266	FALSE
1479815	34266	FALSE
1479816	34266	FALSE
1479818	34266	FALSE
1479819	34266	FALSE
1479821	34266	FALSE
1479823	34266	FALSE
1479824	34266	FALSE
1479825	34266	FALSE
1479827	34266	FALSE
1479830	34266	FALSE
1479831	34266	FALSE
1479832	34266	FALSE
1479834	34266	FALSE
1479836	34266	FALSE
1479838	34266	FALSE
1479840	34266	FALSE
1479842	34266	FALSE
1479843	34266	FALSE
1479845	34266	FALSE
1479848	34266	FALSE
1479852	34266	FALSE
1479853	34266	FALSE
1479855	34266	FALSE
1479864	34266	FALSE
1479868	34266	FALSE
1479876	34266	FALSE
1479877	34266	FALSE
1479878	34266	FALSE
1479881	34266	FALSE
1479882	34266	FALSE
1479884	34266	FALSE
1479885	34266	FALSE
1479886	34266	FALSE
1479888	34266	FALSE
1479891	34266	FALSE
1479893	34266	FALSE
1479896	34266	FALSE
1479901	34266	FALSE
1479904	34266	FALSE
1479907	34266	FALSE
1479908	34266	FALSE
1479911	34266	FALSE
1479912	34266	FALSE
1479913	34266	FALSE
1479915	34266	FALSE
1479917	34266	FALSE
1479918	34266	FALSE
1479927	34266	FALSE
1479931	34266	FALSE
1479933	34266	FALSE
1479935	34266	FALSE
1479936	34266	FALSE
1479939	34266	FALSE
1479940	34266	FALSE
1479945	34266	FALSE
1479948	34266	FALSE
1479949	34266	FALSE
1479950	34266	FALSE
1479953	34266	FALSE
1479955	34266	FALSE
1479956	34266	FALSE
1479957	34266	FALSE
1479958	34266	FALSE
1479959	34266	FALSE
1479960	34266	FALSE
1479965	34266	FALSE
1479968	34266	FALSE
1479969	34266	FALSE
1479971	34266	FALSE
1479975	34266	FALSE
1479976	34266	FALSE
1479977	34266	FALSE
1479982	34266	FALSE
1479983	34266	FALSE
1479989	34266	FALSE
1479997	34266	FALSE
1480003	34266	FALSE
1480007	34266	FALSE
1480008	34266	FALSE
1479854	34266	FALSE
1479358	34266	FALSE
1479361	34266	FALSE
1479364	34266	FALSE
1479366	34266	FALSE
1479367	34266	FALSE
1479371	34266	FALSE
1479376	34266	FALSE
1479380	34266	FALSE
1479381	34266	FALSE
1479383	34266	FALSE
1479384	34266	FALSE
1479387	34266	FALSE
1479389	34266	FALSE
1479391	34266	FALSE
1479393	34266	FALSE
1479394	34266	FALSE
1479400	34266	FALSE
1479401	34266	FALSE
1479402	34266	FALSE
1479405	34266	FALSE
1479408	34266	FALSE
1479410	34266	FALSE
1479423	34266	FALSE
1479427	34266	FALSE
1479428	34266	FALSE
1479437	34266	FALSE
1479439	34266	FALSE
1479443	34266	FALSE
1479445	34266	FALSE
1479446	34266	FALSE
1479447	34266	FALSE
1479449	34266	FALSE
1479456	34266	FALSE
1479458	34266	FALSE
1479459	34266	FALSE
1479463	34266	FALSE
1479464	34266	FALSE
1479465	34266	FALSE
1479467	34266	FALSE
1479469	34266	FALSE
1479470	34266	FALSE
1479473	34266	FALSE
1479474	34266	FALSE
1479892	34266	FALSE
1479857	34266	FALSE
1479981	34266	FALSE
1479947	34266	FALSE
1479894	34266	FALSE
1480141	34266	FALSE
1480142	34266	FALSE
\.

/* Replicant of V056 */
UPDATE public.search_areaoflaw
SET display_name_cy = 'Priodas dan orfod'
WHERE name = 'Forced marriage';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Anffurfio Organau Cenhedlu Benywod'
WHERE name = 'FGM';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Y Weithdrefn Un Ynad'
WHERE name = 'Single justice procedure';

/* Replicant of V063 */
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

/* Replicant of V064 */
ALTER TABLE ONLY search_facility ADD name_cy varchar(255);
UPDATE search_facility SET name_cy = 'Cŵn cymorth' WHERE name = 'Assistance dogs';
UPDATE search_facility SET name_cy = 'cyfleuster newid cewynnau babanod' WHERE name = 'Baby changing facility';
UPDATE search_facility SET name_cy = 'Ystafell aros plant' WHERE name = 'Children''s waiting room';
UPDATE search_facility SET name_cy = 'Mynediad i bobl anabl' WHERE name = 'Disabled access';
UPDATE search_facility SET name_cy = 'Toiled anabl' WHERE name = 'Disabled toilet';
UPDATE search_facility SET name_cy = 'Ffilmio a llogi lleoliad' WHERE name = 'Filming and venue hire';
UPDATE search_facility SET name_cy = 'Cymorth Cyntaf' WHERE name = 'First Aid';
UPDATE search_facility SET name_cy = 'Dolen glyw ''T''' WHERE name = 'Hearing loop ''T''';
UPDATE search_facility SET name_cy = 'Ystafell gyfweld' WHERE name = 'Interview room';
UPDATE search_facility SET name_cy = 'Lifft' WHERE name = 'Lift';
UPDATE search_facility SET name_cy = 'Dolen glyw' WHERE name = 'Loop Hearing';
UPDATE search_facility SET name_cy = 'Dim parcio' WHERE name = 'No parking';
UPDATE search_facility SET name_cy = 'Parcio' WHERE name = 'Parking';
UPDATE search_facility SET name_cy = 'Ystafell weddio/dawel' WHERE name = 'Prayer / Quiet room';
UPDATE search_facility SET name_cy = 'Ffôn cyhoeddus' WHERE name = 'Public pay phone';
UPDATE search_facility SET name_cy = 'Toiledau cyhoeddus' WHERE name = 'Public toilets';
UPDATE search_facility SET name_cy = 'Lluniaeth' WHERE name = 'Refreshments';
UPDATE search_facility SET name_cy = 'Bwa diogelwch' WHERE name = 'Security arch';
UPDATE search_facility SET name_cy = 'cyfleusterau fideo' WHERE name = 'Video facilities';
UPDATE search_facility SET name_cy = 'Ystafell aros' WHERE name = 'Waiting Room';
UPDATE search_facility SET name_cy = 'cysylltiad rhwydwaith di-wifr' WHERE name = 'Wireless network connection';
UPDATE search_facility SET name_cy = 'Gwasanaeth gofal' WHERE name = 'Witness care';
UPDATE search_facility SET name_cy = 'Gwasanaeth tystion' WHERE name = 'Witness service';

/* Replicant of V065 */
ALTER TABLE ONLY search_contact ADD name_cy varchar(255);
UPDATE search_contact SET name_cy = 'Ffacs yr Adran Gyfrifon' WHERE name = 'Accounts fax';
UPDATE search_contact SET name_cy = 'Gweinyddol' WHERE name = 'Admin';
UPDATE search_contact SET name_cy = 'Llys Gweinyddol' WHERE name = 'Admin court';
UPDATE search_contact SET name_cy = 'Mabwysiadu' WHERE name = 'Adoption';
UPDATE search_contact SET name_cy = 'Ceisiadau' WHERE name = 'Applications';
UPDATE search_contact SET name_cy = 'apwyntiadau' WHERE name = 'Appointments';
UPDATE search_contact SET name_cy = 'apwyntiadau - Sifil' WHERE name = 'Appointments - Civil';
UPDATE search_contact SET name_cy = 'apwyntiadau - Teulu' WHERE name = 'Appointments - Family';
UPDATE search_contact SET name_cy = 'cymdeithion' WHERE name = 'Associates';
UPDATE search_contact SET name_cy = 'Beilïaid' WHERE name = 'Bailiffs';
UPDATE search_contact SET name_cy = 'Ffacs y Beilïaid' WHERE name = 'Bailiffs fax';
UPDATE search_contact SET name_cy = 'methdaliad' WHERE name = 'Bankruptcy';
UPDATE search_contact SET name_cy = 'Swyddfa Birmingham' WHERE name = 'Birmingham office';
UPDATE search_contact SET name_cy = 'Llysoedd busnes ac eiddo' WHERE name = 'Business and Property Courts';
UPDATE search_contact SET name_cy = 'Swyddfa Caerdydd' WHERE name = 'Cardiff office';
UPDATE search_contact SET name_cy = 'Achosion gofal' WHERE name = 'Care cases';
UPDATE search_contact SET name_cy = 'Ffacs Achosion Gofal' WHERE name = 'Care cases fax';
UPDATE search_contact SET name_cy = 'Hwyluso Achosion' WHERE name = 'Case progression';
UPDATE search_contact SET name_cy = 'Hwyluso Achosion (A)' WHERE name = 'Case progression (A)';
UPDATE search_contact SET name_cy = 'Hwyluso Achosion (B)' WHERE name = 'Case progression (B)';
UPDATE search_contact SET name_cy = 'Hwyluso Achosion (C)' WHERE name = 'Case progression (C)';
UPDATE search_contact SET name_cy = 'Ffacs Hwyluso Achosion' WHERE name = 'Case progression fax';
UPDATE search_contact SET name_cy = 'System Ganolog Atafaelu Enillion (CAPS)' WHERE name = 'Centralised Attachment of Earnings (CAPS)';
UPDATE search_contact SET name_cy = 'Cynhaliaeth plant' WHERE name = 'Child maintenance';
UPDATE search_contact SET name_cy = 'Achosion sy''n ymwneud â phlant' WHERE name = 'Children cases';
UPDATE search_contact SET name_cy = 'Gwasanaeth i Dystion Cyngor ar Bopeth' WHERE name = 'Citizens Advice Witness Service';
UPDATE search_contact SET name_cy = 'Llys Sifil a''r Llys Teulu' WHERE name = 'Civil and family';
UPDATE search_contact SET name_cy = 'Ffacs Materion Sifil' WHERE name = 'Civil fax';
UPDATE search_contact SET name_cy = 'Rhestru - Sifil' WHERE name = 'Civil listing';
UPDATE search_contact SET name_cy = 'Ymholiadau Sifil' WHERE name = 'Civil queries';
UPDATE search_contact SET name_cy = 'Cyflwyno Hawliadau' WHERE name = 'Claims issue';
UPDATE search_contact SET name_cy = 'Ffacs y Clerc' WHERE name = 'Clerk fax';
UPDATE search_contact SET name_cy = 'Clercod' WHERE name = 'Clerks';
UPDATE search_contact SET name_cy = 'Mater cwmni' WHERE name = 'Company issue';
UPDATE search_contact SET name_cy = 'Cwmni yn dirwyn i ben' WHERE name = 'Company winding up';
UPDATE search_contact SET name_cy = 'Atafaelu' WHERE name = 'Confiscation';
UPDATE search_contact SET name_cy = 'Apwyntiadau cownter' WHERE name = 'Counter appointments';
UPDATE search_contact SET name_cy = 'Llys Sirol' WHERE name = 'County Court';
UPDATE search_contact SET name_cy = 'Canolfan Fusnes y Llys Sirol (CCBC)' WHERE name = 'County Court Business Centre (CCBC)';
UPDATE search_contact SET name_cy = 'Ffacs y Llys Sirol' WHERE name = 'County Court fax';
UPDATE search_contact SET name_cy = 'Rhestrau''r Llys Sirol' WHERE name = 'County Court listing';
UPDATE search_contact SET name_cy = 'Adran Sifil y Llys Apêl' WHERE name = 'Court of Appeal Civil Division';
UPDATE search_contact SET name_cy = 'Rhestru - Troseddol' WHERE name = 'Criminal listing';
UPDATE search_contact SET name_cy = 'Ymholiadau Troseddol' WHERE name = 'Criminal queries';
UPDATE search_contact SET name_cy = 'Ffacs ar gyfer Ymholiadau Troseddol' WHERE name = 'Criminal queries fax';
UPDATE search_contact SET name_cy = 'Llys y Goron' WHERE name = 'Crown Court';
UPDATE search_contact SET name_cy = 'Ffacs Llys y Goron' WHERE name = 'Crown Court fax';
UPDATE search_contact SET name_cy = 'Rhestru - Llys y Goron' WHERE name = 'Crown Court listing';
UPDATE search_contact SET name_cy = 'DX' WHERE name = 'DX';
UPDATE search_contact SET name_cy = 'Mynediad i''r anabl' WHERE name = 'Disabled access';
UPDATE search_contact SET name_cy = 'Ysgariad' WHERE name = 'Divorce';
UPDATE search_contact SET name_cy = 'Ffacs Ysgariadau' WHERE name = 'Divorce fax';
UPDATE search_contact SET name_cy = 'Tribiwnlys cyflogaeth' WHERE name = 'Employment tribunal';
UPDATE search_contact SET name_cy = 'Gorfodaeth' WHERE name = 'Enforcement';
UPDATE search_contact SET name_cy = 'Ymholiadau' WHERE name = 'Enquiries';
UPDATE search_contact SET name_cy = 'Ymholiadau Cymraeg' WHERE name = 'Enquiries Welsh language';
UPDATE search_contact SET name_cy = 'Ffacs y Llys Teulu' WHERE name = 'Family fax';
UPDATE search_contact SET name_cy = 'Rhestru - Teulu' WHERE name = 'Family listing';
UPDATE search_contact SET name_cy = 'Cyfraith gyhoeddus - teulu (plant mewn gofal)' WHERE name = 'Family public law (children in care)';
UPDATE search_contact SET name_cy = 'Ymholiadau Teulu' WHERE name = 'Family queries';
UPDATE search_contact SET name_cy = 'Ffacs' WHERE name = 'Fax';
UPDATE search_contact SET name_cy = 'Ffioedd' WHERE name = 'Fees';
UPDATE search_contact SET name_cy = 'Ymholiadau Dirwyon' WHERE name = 'Fine queries';
UPDATE search_contact SET name_cy = 'Ffacs Dirwyon' WHERE name = 'Fines fax';
UPDATE search_contact SET name_cy = 'Cosbau penodedig' WHERE name = 'Fixed penalties';
UPDATE search_contact SET name_cy = 'Ffioedd graddedig' WHERE name = 'Graduated fees';
UPDATE search_contact SET name_cy = 'Uchel Lys' WHERE name = 'High Court';
UPDATE search_contact SET name_cy = 'Mewnfudo a lloches' WHERE name = 'Immigration and asylum';
UPDATE search_contact SET name_cy = 'Mater' WHERE name = 'Issue';
UPDATE search_contact SET name_cy = 'Ffacs Materion' WHERE name = 'Issue fax';
UPDATE search_contact SET name_cy = 'Gwasanaethu ar reithgor' WHERE name = 'Jury service';
UPDATE search_contact SET name_cy = 'Ffacs Gwasanaeth Rheithgor' WHERE name = 'Jury service fax';
UPDATE search_contact SET name_cy = 'Swyddfa Leeds' WHERE name = 'Leeds office';
UPDATE search_contact SET name_cy = 'Cymorth Cyfreithiol' WHERE name = 'Legal aid';
UPDATE search_contact SET name_cy = 'Ffacs Cymorth Cyfreithiol' WHERE name = 'Legal aid fax';
UPDATE search_contact SET name_cy = 'Ffacs Cynrychiolwyr Cyfreithiol' WHERE name = 'Legal representation fax';
UPDATE search_contact SET name_cy = 'Rhestru' WHERE name = 'Listing';
UPDATE search_contact SET name_cy = 'Rhestru (barnwyr cylchdaith)' WHERE name = 'Listing (circuit judges)';
UPDATE search_contact SET name_cy = 'Rhestru (barnwyr rhanbarth)' WHERE name = 'Listing (district judges)';
UPDATE search_contact SET name_cy = 'Ffacs Rhestru' WHERE name = 'Listing fax';
UPDATE search_contact SET name_cy = 'Rhestru - Ynadon' WHERE name = 'Magistrates listing';
UPDATE search_contact SET name_cy = 'Llys Ynadon' WHERE name = 'Magistrates'' court';
UPDATE search_contact SET name_cy = 'Ffacs Llys Ynadon' WHERE name = 'Magistrates'' court fax';
UPDATE search_contact SET name_cy = 'Ffacs Ynadon - ar ôl achos llys' WHERE name = 'Magistrates'' post court fax';
UPDATE search_contact SET name_cy = 'Ffacs Ynadon - cyn achos llys' WHERE name = 'Magistrates'' pre court fax';
UPDATE search_contact SET name_cy = 'Swyddfa Manceinion' WHERE name = 'Manchester office';
UPDATE search_contact SET name_cy = 'Meistri' WHERE name = 'Masters';
UPDATE search_contact SET name_cy = 'Ffacs Meistri' WHERE name = 'Masters fax';
UPDATE search_contact SET name_cy = 'Cyfryngu' WHERE name = 'Mediation';
UPDATE search_contact SET name_cy = 'Minicom' WHERE name = 'Minicom';
UPDATE search_contact SET name_cy = 'Hawliadau am Arian Ar-lein (MCOL)' WHERE name = 'Money Claim Online (MCOL)';
UPDATE search_contact SET name_cy = 'Gorchmynion a chyfrifon' WHERE name = 'Orders and accounts';
UPDATE search_contact SET name_cy = 'Talu dirwy' WHERE name = 'Pay a fine';
UPDATE search_contact SET name_cy = 'Taliadau' WHERE name = 'Payments';
UPDATE search_contact SET name_cy = 'Uned cefnogaeth bersonol' WHERE name = 'Personal Support Unit';
UPDATE search_contact SET name_cy = 'Cynadleddau ffôn' WHERE name = 'Phone conferencing';
UPDATE search_contact SET name_cy = 'Ar ôl achos llys' WHERE name = 'Post court';
UPDATE search_contact SET name_cy = 'Ffacs - Ar ôl achos llys' WHERE name = 'Post court fax';
UPDATE search_contact SET name_cy = 'Cyn achos llys' WHERE name = 'Pre court';
UPDATE search_contact SET name_cy = 'Llinell gymorth profiant' WHERE name = 'Probate helpline';
UPDATE search_contact SET name_cy = 'Mainc y Frenhines' WHERE name = 'Queen''s Bench';
UPDATE search_contact SET name_cy = 'Proses dramor Mainc y Frenhines' WHERE name = 'Queen''s Bench foreign process';
UPDATE search_contact SET name_cy = 'Rhestru - Barnwyr Mainc y Frenhines' WHERE name = 'Queen''s Bench judges listing';
UPDATE search_contact SET name_cy = 'Trethu rhanbarthol' WHERE name = 'Regional taxing';
UPDATE search_contact SET name_cy = 'Gwrandawiadau''r Cofrestrydd' WHERE name = 'Registrar''s hearings';
UPDATE search_contact SET name_cy = 'Cofrestrfa' WHERE name = 'Registry';
UPDATE search_contact SET name_cy = 'Diogelwch' WHERE name = 'Security';
UPDATE search_contact SET name_cy = 'Costau''r Uwch Lysoedd' WHERE name = 'Senior courts costs';
UPDATE search_contact SET name_cy = 'Hawliadau bychain' WHERE name = 'Small claims';
UPDATE search_contact SET name_cy = 'Cyfryngu ar gyfer hawliadau bychain' WHERE name = 'Small claims mediation';
UPDATE search_contact SET name_cy = 'Minicom Nawdd Cymdeithasol a Chynnal Plant' WHERE name = 'Social Security and Child Support Minicom';
UPDATE search_contact SET name_cy = 'Nawdd cymdeithasol a chynnal plant' WHERE name = 'Social security and child support';
UPDATE search_contact SET name_cy = 'Ffacs Nawdd Cymdeithasol a Chynnal Plant' WHERE name = 'Social security and child support fax';
UPDATE search_contact SET name_cy = 'Canolfan Gorfodi Rheoliadau Traffig (TEC)' WHERE name = 'Traffic Enforcement Centre (TEC)';
UPDATE search_contact SET name_cy = 'Tribiwnlysoedd' WHERE name = 'Tribunals';
UPDATE search_contact SET name_cy = 'Ffacs y Tribiwnlysoedd' WHERE name = 'Tribunals fax';
UPDATE search_contact SET name_cy = 'Llinell Gymorth Gymraeg' WHERE name = 'Welsh Language Helpline';
UPDATE search_contact SET name_cy = 'Gwasanaeth gofal uned' WHERE name = 'Witness care unit';
UPDATE search_contact SET name_cy = 'Gwasanaeth tystion' WHERE name = 'Witness service';

/* Replicant of V066 */
ALTER TABLE ONLY search_email ADD description_cy varchar(255);
UPDATE search_email SET description_cy = 'Ffacs yr Adran Gyfrifon' WHERE description = 'Accounts fax';
UPDATE search_email SET description_cy = 'Gweinyddol' WHERE description = 'Admin';
UPDATE search_email SET description_cy = 'Llys Gweinyddol' WHERE description = 'Admin court';
UPDATE search_email SET description_cy = 'Mabwysiadu' WHERE description = 'Adoption';
UPDATE search_email SET description_cy = 'Ceisiadau' WHERE description = 'Applications';
UPDATE search_email SET description_cy = 'apwyntiadau' WHERE description = 'Appointments';
UPDATE search_email SET description_cy = 'apwyntiadau - Sifil' WHERE description = 'Appointments - Civil';
UPDATE search_email SET description_cy = 'apwyntiadau - Teulu' WHERE description = 'Appointments - Family';
UPDATE search_email SET description_cy = 'cymdeithion' WHERE description = 'Associates';
UPDATE search_email SET description_cy = 'Beilïaid' WHERE description = 'Bailiffs';
UPDATE search_email SET description_cy = 'Ffacs y Beilïaid' WHERE description = 'Bailiffs fax';
UPDATE search_email SET description_cy = 'methdaliad' WHERE description = 'Bankruptcy';
UPDATE search_email SET description_cy = 'Swyddfa Birmingham' WHERE description = 'Birmingham office';
UPDATE search_email SET description_cy = 'Llysoedd busnes ac eiddo' WHERE description = 'Business and Property Courts';
UPDATE search_email SET description_cy = 'Swyddfa Caerdydd' WHERE description = 'Cardiff office';
UPDATE search_email SET description_cy = 'Achosion gofal' WHERE description = 'Care cases';
UPDATE search_email SET description_cy = 'Ffacs Achosion Gofal' WHERE description = 'Care cases fax';
UPDATE search_email SET description_cy = 'Hwyluso Achosion' WHERE description = 'Case progression';
UPDATE search_email SET description_cy = 'Hwyluso Achosion (A)' WHERE description = 'Case progression (A)';
UPDATE search_email SET description_cy = 'Hwyluso Achosion (B)' WHERE description = 'Case progression (B)';
UPDATE search_email SET description_cy = 'Hwyluso Achosion (C)' WHERE description = 'Case progression (C)';
UPDATE search_email SET description_cy = 'Ffacs Hwyluso Achosion' WHERE description = 'Case progression fax';
UPDATE search_email SET description_cy = 'System Ganolog Atafaelu Enillion (CAPS)' WHERE description = 'Centralised Attachment of Earnings (CAPS)';
UPDATE search_email SET description_cy = 'Cynhaliaeth plant' WHERE description = 'Child maintenance';
UPDATE search_email SET description_cy = 'Achosion sy''n ymwneud â phlant' WHERE description = 'Children cases';
UPDATE search_email SET description_cy = 'Gwasanaeth i Dystion Cyngor ar Bopeth' WHERE description = 'Citizens Advice Witness Service';
UPDATE search_email SET description_cy = 'Llys Sifil a''r Llys Teulu' WHERE description = 'Civil and family';
UPDATE search_email SET description_cy = 'Ffacs Materion Sifil' WHERE description = 'Civil fax';
UPDATE search_email SET description_cy = 'Rhestru - Sifil' WHERE description = 'Civil listing';
UPDATE search_email SET description_cy = 'Ymholiadau Sifil' WHERE description = 'Civil queries';
UPDATE search_email SET description_cy = 'Cyflwyno Hawliadau' WHERE description = 'Claims issue';
UPDATE search_email SET description_cy = 'Ffacs y Clerc' WHERE description = 'Clerk fax';
UPDATE search_email SET description_cy = 'Clercod' WHERE description = 'Clerks';
UPDATE search_email SET description_cy = 'Mater cwmni' WHERE description = 'Company issue';
UPDATE search_email SET description_cy = 'Cwmni yn dirwyn i ben' WHERE description = 'Company winding up';
UPDATE search_email SET description_cy = 'Atafaelu' WHERE description = 'Confiscation';
UPDATE search_email SET description_cy = 'Apwyntiadau cownter' WHERE description = 'Counter appointments';
UPDATE search_email SET description_cy = 'Llys Sirol' WHERE description = 'County Court';
UPDATE search_email SET description_cy = 'Canolfan Fusnes y Llys Sirol (CCBC)' WHERE description = 'County Court Business Centre (CCBC)';
UPDATE search_email SET description_cy = 'Ffacs y Llys Sirol' WHERE description = 'County Court fax';
UPDATE search_email SET description_cy = 'Rhestrau''r Llys Sirol' WHERE description = 'County Court listing';
UPDATE search_email SET description_cy = 'Adran Sifil y Llys Apêl' WHERE description = 'Court of Appeal Civil Division';
UPDATE search_email SET description_cy = 'Rhestru - Troseddol' WHERE description = 'Criminal listing';
UPDATE search_email SET description_cy = 'Ymholiadau Troseddol' WHERE description = 'Criminal queries';
UPDATE search_email SET description_cy = 'Ffacs ar gyfer Ymholiadau Troseddol' WHERE description = 'Criminal queries fax';
UPDATE search_email SET description_cy = 'Llys y Goron' WHERE description = 'Crown Court';
UPDATE search_email SET description_cy = 'Ffacs Llys y Goron' WHERE description = 'Crown Court fax';
UPDATE search_email SET description_cy = 'Rhestru - Llys y Goron' WHERE description = 'Crown Court listing';
UPDATE search_email SET description_cy = 'DX' WHERE description = 'DX';
UPDATE search_email SET description_cy = 'Mynediad i''r anabl' WHERE description = 'Disabled access';
UPDATE search_email SET description_cy = 'Ysgariad' WHERE description = 'Divorce';
UPDATE search_email SET description_cy = 'Ffacs Ysgariadau' WHERE description = 'Divorce fax';
UPDATE search_email SET description_cy = 'Tribiwnlys cyflogaeth' WHERE description = 'Employment tribunal';
UPDATE search_email SET description_cy = 'Gorfodaeth' WHERE description = 'Enforcement';
UPDATE search_email SET description_cy = 'Ymholiadau' WHERE description = 'Enquiries';
UPDATE search_email SET description_cy = 'Ymholiadau Cymraeg' WHERE description = 'Enquiries Welsh language';
UPDATE search_email SET description_cy = 'Ffacs y Llys Teulu' WHERE description = 'Family fax';
UPDATE search_email SET description_cy = 'Rhestru - Teulu' WHERE description = 'Family listing';
UPDATE search_email SET description_cy = 'Cyfraith gyhoeddus - teulu (plant mewn gofal)' WHERE description = 'Family public law (children in care)';
UPDATE search_email SET description_cy = 'Ymholiadau Teulu' WHERE description = 'Family queries';
UPDATE search_email SET description_cy = 'Ffacs' WHERE description = 'Fax';
UPDATE search_email SET description_cy = 'Ffioedd' WHERE description = 'Fees';
UPDATE search_email SET description_cy = 'Ymholiadau Dirwyon' WHERE description = 'Fine queries';
UPDATE search_email SET description_cy = 'Ffacs Dirwyon' WHERE description = 'Fines fax';
UPDATE search_email SET description_cy = 'Cosbau penodedig' WHERE description = 'Fixed penalties';
UPDATE search_email SET description_cy = 'Ffioedd graddedig' WHERE description = 'Graduated fees';
UPDATE search_email SET description_cy = 'Uchel Lys' WHERE description = 'High Court';
UPDATE search_email SET description_cy = 'Mewnfudo a lloches' WHERE description = 'Immigration and asylum';
UPDATE search_email SET description_cy = 'Mater' WHERE description = 'Issue';
UPDATE search_email SET description_cy = 'Ffacs Materion' WHERE description = 'Issue fax';
UPDATE search_email SET description_cy = 'Gwasanaethu ar reithgor' WHERE description = 'Jury service';
UPDATE search_email SET description_cy = 'Ffacs Gwasanaeth Rheithgor' WHERE description = 'Jury service fax';
UPDATE search_email SET description_cy = 'Swyddfa Leeds' WHERE description = 'Leeds office';
UPDATE search_email SET description_cy = 'Cymorth Cyfreithiol' WHERE description = 'Legal aid';
UPDATE search_email SET description_cy = 'Ffacs Cymorth Cyfreithiol' WHERE description = 'Legal aid fax';
UPDATE search_email SET description_cy = 'Ffacs Cynrychiolwyr Cyfreithiol' WHERE description = 'Legal representation fax';
UPDATE search_email SET description_cy = 'Rhestru' WHERE description = 'Listing';
UPDATE search_email SET description_cy = 'Rhestru (barnwyr cylchdaith)' WHERE description = 'Listing (circuit judges)';
UPDATE search_email SET description_cy = 'Rhestru (barnwyr rhanbarth)' WHERE description = 'Listing (district judges)';
UPDATE search_email SET description_cy = 'Ffacs Rhestru' WHERE description = 'Listing fax';
UPDATE search_email SET description_cy = 'Rhestru - Ynadon' WHERE description = 'Magistrates listing';
UPDATE search_email SET description_cy = 'Llys Ynadon' WHERE description = 'Magistrates'' court';
UPDATE search_email SET description_cy = 'Ffacs Llys Ynadon' WHERE description = 'Magistrates'' court fax';
UPDATE search_email SET description_cy = 'Ffacs Ynadon - ar ôl achos llys' WHERE description = 'Magistrates'' post court fax';
UPDATE search_email SET description_cy = 'Ffacs Ynadon - cyn achos llys' WHERE description = 'Magistrates'' pre court fax';
UPDATE search_email SET description_cy = 'Swyddfa Manceinion' WHERE description = 'Manchester office';
UPDATE search_email SET description_cy = 'Meistri' WHERE description = 'Masters';
UPDATE search_email SET description_cy = 'Ffacs Meistri' WHERE description = 'Masters fax';
UPDATE search_email SET description_cy = 'Cyfryngu' WHERE description = 'Mediation';
UPDATE search_email SET description_cy = 'Minicom' WHERE description = 'Minicom';
UPDATE search_email SET description_cy = 'Hawliadau am Arian Ar-lein (MCOL)' WHERE description = 'Money Claim Online (MCOL)';
UPDATE search_email SET description_cy = 'Gorchmynion a chyfrifon' WHERE description = 'Orders and accounts';
UPDATE search_email SET description_cy = 'Talu dirwy' WHERE description = 'Pay a fine';
UPDATE search_email SET description_cy = 'Taliadau' WHERE description = 'Payments';
UPDATE search_email SET description_cy = 'Uned cefnogaeth bersonol' WHERE description = 'Personal Support Unit';
UPDATE search_email SET description_cy = 'Cynadleddau ffôn' WHERE description = 'Phone conferencing';
UPDATE search_email SET description_cy = 'Ar ôl achos llys' WHERE description = 'Post court';
UPDATE search_email SET description_cy = 'Ffacs - Ar ôl achos llys' WHERE description = 'Post court fax';
UPDATE search_email SET description_cy = 'Cyn achos llys' WHERE description = 'Pre court';
UPDATE search_email SET description_cy = 'Llinell gymorth profiant' WHERE description = 'Probate helpline';
UPDATE search_email SET description_cy = 'Mainc y Frenhines' WHERE description = 'Queen''s Bench';
UPDATE search_email SET description_cy = 'Proses dramor Mainc y Frenhines' WHERE description = 'Queen''s Bench foreign process';
UPDATE search_email SET description_cy = 'Rhestru - Barnwyr Mainc y Frenhines' WHERE description = 'Queen''s Bench judges listing';
UPDATE search_email SET description_cy = 'Trethu rhanbarthol' WHERE description = 'Regional taxing';
UPDATE search_email SET description_cy = 'Gwrandawiadau''r Cofrestrydd' WHERE description = 'Registrar''s hearings';
UPDATE search_email SET description_cy = 'Cofrestrfa' WHERE description = 'Registry';
UPDATE search_email SET description_cy = 'Diogelwch' WHERE description = 'Security';
UPDATE search_email SET description_cy = 'Costau''r Uwch Lysoedd' WHERE description = 'Senior courts costs';
UPDATE search_email SET description_cy = 'Hawliadau bychain' WHERE description = 'Small claims';
UPDATE search_email SET description_cy = 'Cyfryngu ar gyfer hawliadau bychain' WHERE description = 'Small claims mediation';
UPDATE search_email SET description_cy = 'Minicom Nawdd Cymdeithasol a Chynnal Plant' WHERE description = 'Social Security and Child Support Minicom';
UPDATE search_email SET description_cy = 'Nawdd cymdeithasol a chynnal plant' WHERE description = 'Social security and child support';
UPDATE search_email SET description_cy = 'Ffacs Nawdd Cymdeithasol a Chynnal Plant' WHERE description = 'Social security and child support fax';
UPDATE search_email SET description_cy = 'Canolfan Gorfodi Rheoliadau Traffig (TEC)' WHERE description = 'Traffic Enforcement Centre (TEC)';
UPDATE search_email SET description_cy = 'Tribiwnlysoedd' WHERE description = 'Tribunals';
UPDATE search_email SET description_cy = 'Ffacs y Tribiwnlysoedd' WHERE description = 'Tribunals fax';
UPDATE search_email SET description_cy = 'Llinell Gymorth Gymraeg' WHERE description = 'Welsh Language Helpline';
UPDATE search_email SET description_cy = 'Gwasanaeth gofal uned' WHERE description = 'Witness care unit';
UPDATE search_email SET description_cy = 'Gwasanaeth tystion' WHERE description = 'Witness service';
