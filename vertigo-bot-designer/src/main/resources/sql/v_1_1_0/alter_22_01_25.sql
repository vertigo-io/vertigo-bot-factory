update HISTORY_ACTION set label_fr = 'Ajout' where hac_cd = 'ADDED';
update HISTORY_ACTION set label_fr = 'Suppression' where hac_cd = 'DELETED';
update HISTORY_ACTION set label_fr = 'Modification' where hac_cd = 'UPDATED';