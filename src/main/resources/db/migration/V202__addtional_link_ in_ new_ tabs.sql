-- FACT-1636
-- opening additional links in new tab

UPDATE public.search_court
SET info = '<p><a href="https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers" target="_blank" rel="noopener">Scammers are mimicking genuine HMCTS phone numbers and email addresses</a>. They may demand payment and claim to be from HMRC or enforcement. If you''re unsure, do not pay anything and report the scam to<a href="https://www.actionfraud.police.uk/" target="_blank" rel="noopener">Action Fraud</a>.</p>',
    info_cy = '<p><a href="https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers" target="_blank" rel="noopener">Mae sgamwyr yn dynwared rhifau ffôn ac e-byst GLlTEM go iawn</a>. Efallai y byddan nhw’n mynnu cael taliad ac yn honni eu bod yn cynrychioli CTEM neu’r gwasanaeth gorfodaeth. Os nad ydych yn siŵr, peidiwch â thalu a riportiwch y peth i<a href="https://www.actionfraud.police.uk/" target="_blank" rel="noopener">Action Fraud</a>.</p>'
where info like '<p><a href=https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers>Scammers %'
 and info like '%<a href=https://www.actionfraud.police.uk/>Action Fraud</a>.</p>';
