BEGIN;
-- Add DOCUMENTARY_RESOURCES export type
insert into TYPE_EXPORT_ANALYTICS(TEA_CD, LABEL, LABEL_FR) 
values ('DOCUMENTARY_RESOURCES', 'Documentary resources', 'Ressources documentaires');
COMMIT;
