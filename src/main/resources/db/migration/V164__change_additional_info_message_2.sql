-- FACT 1197 --
UPDATE public.search_court
SET info = '<p><a href=“https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers”>Scammers are mimicking ' ||
           'genuine HMCTS phone numbers and email addresses</a>. They may demand payment and claim to be from HMRC or enforcement. ' ||
           'If you''re unsure, do not pay anything and report the scam to <a href=“https://www.actionfraud.police.uk/”>' ||
           'Action Fraud</a>.</p>',
    info_cy = '<p><a href=“https://www.gov.uk/government/news/scammers-using-hmcts-telephone-numbers”>Mae sgamwyr yn ' ||
              'dynwared rhifau ffôn ac e-byst GLlTEM go iawn</a>. Efallai y byddan nhw’n mynnu cael taliad ac yn honni ' ||
              'eu bod yn cynrychioli CTEM neu’r gwasanaeth gorfodaeth. Os nad ydych yn siŵr, peidiwch â thalu a riportiwch ' ||
              'y peth i <a href=“https://www.actionfraud.police.uk/”>Action Fraud</a>.</p>'
WHERE displayed = true;
