ALTER TABLE TRAINING 
ADD COLUMN NLU_THRESHOLD	 NUMERIC(3,2)	not null	default 0.6;

comment on column TRAINING.NLU_THRESHOLD is
'NLU Threshold';