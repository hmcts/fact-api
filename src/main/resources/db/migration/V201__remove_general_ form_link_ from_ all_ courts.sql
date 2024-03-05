-- FACT-1647
-- remove general enquiry form link from all courts

DELETE FROM public.search_courtadditionallink
       WHERE additional_link_id in (select id from public.search_additionallink
                                     where url = 'https://hmcts-general-enquiry-eng.form.service.justice.gov.uk/');

DELETE FROM public.search_additionallink
where url = 'https://hmcts-general-enquiry-eng.form.service.justice.gov.uk/';




