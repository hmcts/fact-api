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

--UPDATE search_court
--SET
--	info_cy =
--	case

--    need to get welsh translation fixes

--
--	  WHEN slug = 'old-2-traffic-enforcement-centre-tec'
--	  THEN 'Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc). Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'
--
--	  WHEN slug = 'clerkenwell-and-shoreditch-county-court-and-family-court'
--	  THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Court Fees. If unsure about your Court Fee, please see EX50: Fees in the civil and family courts – main fees (EX50) - GOV.UK (https://www.gov.uk/government/publications/fees-in-the-civil-and-family-courts-main-fees-ex50). If you think you qualify for Help with Fees, please see EX160A: How to apply for help with fees: EX160A - GOV.UK (https://www.gov.uk/government/publications/apply-for-help-with-court-and-tribunal-fees/how-to-apply-for-help-with-fees-ex160a-for-applications-made-or-fees-paid-on-or-after-27-november-2023). Please make sure your application / documents / paperwork is submitted with the payment method. Please note, if you have requested to be called to collect a court fee, the call will come from an unknown number. Cheques / Postal Orders (made payable to ‘HMCTS’). Post to the Court or place in the Drop Box with the relevant paperwork. Card Payments. Post to the Court, place in the Drop Box, or Email the Court your contact details, with the relevant paperwork. Alternatively, telephone the Enquiries line. A handoff will be sent to the Court, and you will be contacted by us.'
--
--	  WHEN slug = 'central-family-court'
--	  THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). For urgent without notice applications, e.g. non-molestation orders and prohibited steps orders please contact cfc.urgentapplications@justice.gov.uk'
--
--	  WHEN slug = 'barnet-civil-and-family-courts-centre'
--	  THEN 'Cases will be heard at alternative venues to minimise disruption, please check your hearing notice or the daily court list (https://www.courtserve.net/courtlists/current/county/indexv2county.php) for more info. If you have a case at Barnet, please write to us at: 9 Acton Lane, Harlesden, London, NW10 8SB. Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'
--
--	  WHEN slug = 'romford-social-security-and-child-support'
--	  THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Romford Magistrates'' Court (formerly Havering Magistrates'' Court). Access to Social Security Tribunals. The entrance to the Tribunal Service is separate to the Magistrates Court entrance. Access is via the car park on the left-hand side of the Magistrates court. Please enter the car park and keep to the right side. You will see a Tribunal service sign on the entrance door. If you attempt to enter the Magistrates court entrance, you will be sent back around to the side of the building to enter via the SSCS Tribunal Service door.'
--
--	  WHEN slug = 'traffic-enforcement-centre-tec'
--	  THEN 'Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc). DX code: 702885 Northampton 7. Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Appeal against a penalty charge notice: If you get a court order (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).'
--
--	  WHEN slug = 'traffic-enforcement-centre-tec-old'
--	  THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). Appeal against a penalty charge notice: If you get a court order (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).'
--
--	  WHEN slug = 'northampton-crown-county-and-family-court'
--	  THEN 'The Civil National Business Centre (formerly CCBC) is also in Northampton and is an entirely separate entity to Northampton Crown, County and Family Court. Northampton County and Family Court cannot answer queries on behalf of CNBC. Follow link (https://www.find-court-tribunal.service.gov.uk/courts/county-court-business-centre-ccbc). Opening times - Monday to Friday 08:30 to 17:00. Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'
--
--	  WHEN slug = 'civil-national-business-centre-cnbc'
--	  THEN 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/). To enable us to better direct your contact please use our Signposting Tool (https://civil-national-business-centre-cnbc.form.service.justice.gov.uk/). Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc).'
--
--	  -- if not set then leave it blank
--	  WHEN info_cy = '' or info_cy is null
--	  THEN ''
--
--	  -- default about 95% of it to the main statement
--	  ELSE 'Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).'
--

--	end;






--	Scammers are mimicking genuine HMCTS phone numbers and email addresses (https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers). They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to Action Fraud (https://www.actionfraud.police.uk/).
--
--	Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc).
--
--	If unsure about your Court Fee, please see EX50: Fees in the civil and family courts – main fees (EX50) - GOV.UK (https://www.gov.uk/government/publications/fees-in-the-civil-and-family-courts-main-fees-ex50). If you think you qualify for Help with Fees, please see EX160A: How to apply for help with fees: EX160A - GOV.UK (https://www.gov.uk/government/publications/apply-for-help-with-court-and-tribunal-fees/how-to-apply-for-help-with-fees-ex160a-for-applications-made-or-fees-paid-on-or-after-27-november-2023). Please make sure your application / documents / paperwork is submitted with the payment method. Please note, if you have requested to be called to collect a court fee, the call will come from an unknown number. Cheques / Postal Orders (made payable to ‘HMCTS’). Post to the Court or place in the Drop Box with the relevant paperwork. for card payments, post it to the court, place it in the drop box, or email the court your contact details, with the relevant paperwork. Alternatively, telephone the enquiries line. A handoff will be sent to the court, and you will be contacted by us.
--
--	For urgent without notice applications, e.g. non-molestation orders and prohibited steps orders please contact cfc.urgentapplications@justice.gov.uk
--
--	Cases will be heard at alternative venues to minimise disruption, please check your hearing notice or the daily court list (https://www.courtserve.net/courtlists/current/county/indexv2county.php) for more info. If you have a case at Barnet, please write to us at: 9 Acton Lane, Harlesden, London, NW10 8SB.
--
--	Romford Magistrates' Court (formerly Havering Magistrates' Court). Access to Social Security Tribunals. The entrance to the Tribunal Service is separate to the Magistrates Court entrance. Access is via the car park on the left-hand side of the Magistrates court. Please enter the car park and keep to the right side. You will see a Tribunal service sign on the entrance door. If you attempt to enter the Magistrates court entrance, you will be sent back around to the side of the building to enter via the SSCS Tribunal Service door.
--
--	DX code: 702885 Northampton 7.
--
--	Appeal against a penalty charge notice: If you get a court order (https://www.gov.uk/appeal-against-a-penalty-charge-notice/court-order).
--
--	The Civil National Business Centre (formerly CCBC) is also in Northampton and is an entirely separate entity to Northampton Crown, County and Family Court. Northampton County and Family Court cannot answer queries on behalf of CNBC. Follow link (https://www.find-court-tribunal.service.gov.uk/courts/county-court-business-centre-ccbc). Opening times - Monday to Friday 08:30 to 17:00.
--
--	To enable us to better direct your contact please use our Signposting Tool (https://civil-national-business-centre-cnbc.form.service.justice.gov.uk/). Please use this link for our webchat facility (https://www.moneyclaims.service.gov.uk/contact-cnbc).
--


UPDATE search_court
SET alert = REGEXP_REPLACE(REGEXP_REPLACE(alert, '<[^>]+>', '', 'g'), '&#39;', '''', 'g')
where slug in ('barnet-civil-and-family-courts-centre','gateshead-magistrates-court-and-family-court');

UPDATE search_court
SET alert = REGEXP_REPLACE(REGEXP_REPLACE(alert, '<[^>]+>', '', 'g'), '&#64;', '@', 'g')
where slug = 'blackpool-family-and-civil-court';

UPDATE search_court
SET alert = 'Our lift is out of service, with no level access to courtrooms. We apologise for any inconvenience. If this affects you, email enquiries.Kingstoncountycourt&#64;justice.gov.uk with ''LEVEL ACCESS'' in the subject, and we’ll arrange alternatives.';
where slug = 'kingston-upon-thames-county-court-and-family-court';

UPDATE search_court
SET alert = 'The Chair Lift between the ground floor and the first floor is Out of Order until further notice. This building has a ramp to the building entrance only.'
where slug = 'edmonton-county-court-and-family-court';

UPDATE search_court
SET alert = REGEXP_REPLACE(REGEXP_REPLACE(alert, '<[^>]+>', '', 'g'), '&amp;', '&', 'g');
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
