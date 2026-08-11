
UPDATE search_court
SET
    info =
        case

            WHEN slug = 'old-2-traffic-enforcement-centre-tec'
                THEN 'Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc). Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'

            WHEN slug = 'clerkenwell-and-shoreditch-county-court-and-family-court'
                THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Court Fees. If unsure about your Court Fee, please see EX50: Fees in the civil and family courts – main fees (EX50) - GOV.UK (https://www.gov.uk/government/publications/fees-in-the-civil-and-family-courts-main-fees-ex50). If you think you qualify for Help with Fees, please see EX160A: How to apply for help with fees: EX160A - GOV.UK (https://www.gov.uk/government/publications/apply-for-help-with-court-and-tribunal-fees/how-to-apply-for-help-with-fees-ex160a-for-applications-made-or-fees-paid-on-or-after-27-november-2023). Please make sure your application / documents / paperwork is submitted with the payment method. Please note, if you have requested to be called to collect a court fee, the call will come from an unknown number. Cheques / Postal Orders (made payable to ‘HMCTS’). Post to the Court or place in the Drop Box with the relevant paperwork. Card Payments. Post to the Court, place in the Drop Box, or Email the Court your contact details, with the relevant paperwork. Alternatively, telephone the Enquiries line. A handoff will be sent to the Court, and you will be contacted by us.'

            WHEN slug = 'central-family-court'
                THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). For urgent without notice applications, e.g. non-molestation orders and prohibited steps orders please contact cfc.urgentapplications@justice.gov.uk'

            WHEN slug = 'barnet-civil-and-family-courts-centre'
                THEN 'Cases will be heard at alternative venues to minimise disruption, please check your hearing notice or the daily court list (https://www.courtserve.net/courtlists/current/county/indexv2county.php) for more info. If you have a case at Barnet, please write to us at: 9 Acton Lane, Harlesden, London, NW10 8SB. Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'

            WHEN slug = 'romford-social-security-and-child-support'
                THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Romford Magistrates'' Court (formerly Havering Magistrates'' Court). Access to Social Security Tribunals. The entrance to the Tribunal Service is separate to the Magistrates Court entrance. Access is via the car park on the left-hand side of the Magistrates court. Please enter the car park and keep to the right side. You will see a Tribunal service sign on the entrance door. If you attempt to enter the Magistrates court entrance, you will be sent back around to the side of the building to enter via the SSCS Tribunal Service door.'

            WHEN slug = 'traffic-enforcement-centre-tec'
                THEN 'Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc). DX code: 702885 Northampton 7. Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Appeal against a penalty charge notice: If you get a court order (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).'

            WHEN slug = 'traffic-enforcement-centre-tec-old'
                THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Appeal against a penalty charge notice: If you get a court order (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).'

            WHEN slug = 'northampton-crown-county-and-family-court'
                THEN 'The Civil National Business Centre (formerly CCBC) is also in Northampton and is an entirely separate entity to Northampton Crown, County and Family Court. Northampton County and Family Court cannot answer queries on behalf of CNBC. Follow link (https://www.find-court-tribunal.service.gov.uk/courts/county-court-business-centre-ccbc). Opening times - Monday to Friday 08:30 to 17:00. Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'

            WHEN slug = 'civil-national-business-centre-cnbc'
                THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). To enable us to better direct your contact please use our Signposting Tool (https://civil-national-business-centre-cnbc.form.service.justice.gov.uk/). Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc).'

            -- if not set then leave it blank
            WHEN info = '' or info is null
                THEN ''

            -- default about 95% of it to the main statement
            ELSE 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'
end;

UPDATE search_court
SET
    info_cy =
        case

            WHEN slug = 'old-2-traffic-enforcement-centre-tec'
                THEN 'Defnyddiwch y ddolen hon ar gyfer ein cyfleuster sgwrsio dros y we (https://www.moneyclaims.service.gov.uk/contact-cnbc). Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/).'

            WHEN slug = 'clerkenwell-and-shoreditch-county-court-and-family-court'
                THEN 'Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/). Os ydych yn ansicr am eich Ffi Llys, gweler EX50: Ffioedd yn y llysoedd sifil a theulu - prif ffioedd (EX50)- GOV.UK (https://www.gov.uk/government/publications/fees-in-the-civil-and-family-courts-main-fees-ex50). Os ydych yn meddwl eich bod gymwys i gael Help i Dalu Ffioedd, gweler EX160A: Sut i wneud cais am help i dalu ffioedd: EX160A - GOV.UK (https://www.gov.uk/government/publications/apply-for-help-with-court-and-tribunal-fees/how-to-apply-for-help-with-fees-ex160a-for-applications-made-or-fees-paid-on-or-after-27-november-2023). Gwnewch yn siŵr bod eich cais / dogfennau / gwaith papur yn cael ei gyflwyno gyda’r dull talu. Dalier sylw, os ydych wedi gwneud cais am alwad ffôn i gasglu ffi llys, bydd yr alwad yn dod o rif anhysbys. Sieciau / Archebion Post (yn daladwy i ‘GLlTEF’). Postio i’r Llys neu eu rhoi yn y Blwch gyda’r gwaith papur perthnasol. Ar gyfer taliadau gyda cherdyn, ei bostio i’r llys, eu rhoi yn y blwch, neu anfon e-bost i’r llys gyda’ch manylion cyswllt, gyda’r gwaith papur perthnasol. Fel arall, ffoniwch y llinell ymholiadau.  Bydd y gwaith papur yn cael ei anfon i’r llys, a byddwn yn cysylltu â chi.'

            WHEN slug = 'central-family-court'
                THEN 'Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/). Ar gyfer ceisiadau brys heb rybudd, e.e. gorchmynion rhag molestu a gorchmynion camau gwaharddedig, cysylltwch â cfc.urgentapplications@justice.gov.uk'

            WHEN slug = 'barnet-civil-and-family-courts-centre'
                THEN 'Bydd achosion yn cael eu gwrando mewn lleoliadau amgen i leihau unrhyw oedi, gwiriwch eich hysbysiad o wrandawiad neu restr achosion dyddiol y llys (https://www.courtserve.net/courtlists/current/county/indexv2county.php) i gael mwy o wybodaeth. Os oes gennych achos yn Barnet, ysgrifennwch atom yn: 9 Acton Lane, Harlesden, Llundain / London, NW10 8SB. Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/).'

            WHEN slug = 'romford-social-security-and-child-support'
                THEN 'Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/). Llys Ynadon Romford (Llys Ynadon Havering gynt). Mynediad at y Tribiwnlysoedd Nawdd Cymdeithasol.  Mae’r fynedfa i’r Gwasanaeth Tribiwnlys ar wahân i fynedfa’r Llys Ynadon. Cewch fynediad drwy’r maes parcio ar ochr chwith y Llys Ynadon.  Dewch i mewn i’r maes parcio a chadw at yr ochr dde. Fe welwch arwydd gwasanaeth Tribiwnlys ar ddrws y fynedfa. Os ydych yn ceisio dod drwy fynedfa’r llys ynadon, byddwch yn cael eich anfon yn ôl i ochr yr adeilad i fynd i mewn trwy ddrws y Gwasanaeth Tribiwnlys.'

            WHEN slug = 'traffic-enforcement-centre-tec'
                THEN 'Defnyddiwch y ddolen hon ar gyfer ein cyfleuster sgwrsio dros y we (https://www.moneyclaims.service.gov.uk/contact-cnbc). DX code: 702885 Northampton 7. Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/). Apelio yn erbyn hysbysiad o gosb ariannol: Os cewch orchymyn llys (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).'

            WHEN slug = 'traffic-enforcement-centre-tec-old'
                THEN 'Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/). Apelio yn erbyn hysbysiad o gosb ariannol: Os cewch orchymyn llys (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).'

            WHEN slug = 'northampton-crown-county-and-family-court'
                THEN 'Mae’r Ganolfan Fusnes Sifil Cenedlaethol (CCBC gynt) hefyd yn Northampton, ac mae’n gwbl ar wahân i Lys y Goron Northampton a llysoedd Sirol a Theulu Northampton. Ni alla Llysoedd Sifil a Theulu Northampton ymateb i ymholiadau ar ran y Ganolfan Fusnes. Dilynwch y ddolen (https://www.find-court-tribunal.service.gov.uk/courts/county-court-business-centre-ccbc). Oriau agor - dydd Llun i ddydd Gwener, 08:30 - 17:00. Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/).'

            WHEN slug = 'civil-national-business-centre-cnbc'
                THEN 'Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/). I’n galluogi i’ch cyfeirio yn well i’r lle cywir, defnyddiwch ein Hadnodd Cyfeirio (https://civil-national-business-centre-cnbc.form.service.justice.gov.uk/). Defnyddiwch y ddolen hon ar gyfer ein cyfleuster sgwrsio dros y we (https://www.moneyclaims.service.gov.uk/contact-cnbc).'

            -- if not set then leave it blank
            WHEN info_cy = '' or info_cy is null
                THEN ''

            -- default about 95% of it to the main statement
            ELSE 'Mae sgamwyr yn dynwared rhifau ffôn a chyfeiriadau e-bost swyddogol GLlTEF (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). Gallant fynnu taliad a honni eu bod nhw o Gyllid a Thollau EF (HMRC) neu o''r adran gorfodi. Os ydych yn ansicr, peidiwch â gwneud unrhyw daliad, a dylech adrodd am y sgam i Action Fraud (https://www.actionfraud.police.uk/).'
end;


UPDATE search_court
SET alert = REGEXP_REPLACE(REGEXP_REPLACE(alert, '<[^>]+>', '', 'g'), '&#39;', '''', 'g')
where slug in ('barnet-civil-and-family-courts-centre','gateshead-magistrates-court-and-family-court');

UPDATE search_court
SET alert = REGEXP_REPLACE(REGEXP_REPLACE(alert, '<[^>]+>', '', 'g'), '&#64;', '@', 'g')
where slug = 'blackpool-family-and-civil-court';

UPDATE search_court
SET alert = 'Our lift is out of service, with no level access to courtrooms. We apologise for any inconvenience. If this affects you, email enquiries.Kingstoncountycourt@justice.gov.uk with ''LEVEL ACCESS'' in the subject, and we’ll arrange alternatives.'
where slug = 'kingston-upon-thames-county-court-and-family-court';

UPDATE search_court
SET alert = 'The Chair Lift between the ground floor and the first floor is Out of Order until further notice. This building has a ramp to the building entrance only.'
where slug = 'edmonton-county-court-and-family-court';

UPDATE search_court
SET alert = REGEXP_REPLACE(REGEXP_REPLACE(alert, '<[^>]+>', '', 'g'), '&amp;', '&', 'g')
where slug = 'clerkenwell-and-shoreditch-county-court-and-family-court';

UPDATE search_court
SET alert = REGEXP_REPLACE(alert, '<[^>]+>', '', 'g')
where slug in ('east-london-tribunal-hearing-centre','doncaster-justice-centre-south');

UPDATE search_court
SET alert_cy = 'Yn dilyn marwolaeth Ei Mawrhydi Y Frenhines, ni fydd y rhan fwyaf o wrandawiadau llys a thribiwnlys yn cael eu cynnal ar 19 Medi 2022, sef diwrnod yr Angladd Wladol. (https://www.gov.uk/government/news/courts-and-tribunals-arrangements-for-the-queens-state-funeral) Rhagor o wybodaeth am drefniadaur llys ar tribiwnlys yn ystod y cyfnod hwn.'
WHERE slug = 'harrogate-justice-centre';



UPDATE search_servicecentre ss
SET intro_paragraph = REGEXP_REPLACE(intro_paragraph, '<[^>]+>', '', 'g'); -- do first for sc intro paragraph then bit below

UPDATE search_servicecentre ss
SET intro_paragraph = 'This location serves all of England and Wales for small claims mediation on most defended small claims cases. The service is free and helps resolve money disputes without the need for a court hearing. You can find out more in the guide to the (https://www.gov.uk/guidance/small-claims-mediation-service) Small Claims Mediation Service on GOV.UK. We do not provide an in-person service.'
WHERE court_id = 1480145;

UPDATE search_servicecentre ss
SET intro_paragraph_cy = 'Mae''r lleoliad hwn yn gwasanaethu Cymru a Lloegr gyfan ar gyfer cyfryngu hawliadau bychain ar y rhan fwyaf o achosion o hawliadau bychain a amddiffynnwyd. Mae''r gwasanaeth yn rhad ac am ddim ac yn helpu i ddatrys anghydfodau ariannol heb fod angen gwrandawiad llys. Gallwch gael rhagor o wybodaeth yn y canllaw ar gyfer (https://www.gov.uk/guidance/small-claims-mediation-service) Gwasanaeth Cyfryngu Hawliadau Bychain ar GOV.UK. Nid ydym yn darparu gwasanaeth wyneb yn wyneb.'
WHERE court_id = 1480145;



UPDATE search_facility ss
SET description  = REGEXP_REPLACE(description, '<[^>]+>', '', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '<[^>]+>', '', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&nbsp;', ' ', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&nbsp;', ' ', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&amp;', '&', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&amp;', '&', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&#39;', '''', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&#39;', '''', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&ndash;', '–', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&ndash;', '–', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&lsquo;', '''', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&lsquo;', '''', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&eacute;', 'é', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&eacute;', 'é', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&middot;', '·', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&middot;', '·', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&#64;', '@', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&#64;', '@', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&rsquo;', '''', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&rsquo;', '''', 'g');

UPDATE search_facility ss
SET description = REGEXP_REPLACE(description, '&acirc;', 'â', 'g');
UPDATE search_facility ss
SET description_cy = REGEXP_REPLACE(description_cy, '&acirc;', 'â', 'g');
