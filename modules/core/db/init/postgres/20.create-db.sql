-- begin SAMPLE_ORDER
alter table SAMPLE_ORDER add constraint FK_SAMPLE_ORDER_CUSTOMER foreign key (CUSTOMER_ID) references SAMPLE_CUSTOMER(ID)^
create index IDX_SAMPLE_ORDER_CUSTOMER on SAMPLE_ORDER (CUSTOMER_ID)^
-- end SAMPLE_ORDER
