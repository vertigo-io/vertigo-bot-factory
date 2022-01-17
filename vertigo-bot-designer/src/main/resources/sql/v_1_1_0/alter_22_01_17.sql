update HISTORY_ACTION set label_fr = 'Ajouté' where hac_cd = 'ADDED';
update HISTORY_ACTION set label_fr = 'Supprimé' where hac_cd = 'DELETED';
update HISTORY_ACTION set label_fr = 'Modifié' where hac_cd = 'UPDATED';