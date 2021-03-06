insert into SEC_GROUP
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, NAME, PARENT_ID)
values ('27946bf4-0de0-a95e-93eb-062005e61a64', 1, '2017-04-06 09:38:52', 'admin', '2017-04-06 09:38:52', null, null, null, 'Tenants', '0fa2b1a5-1d68-4d69-9fbd-dff348347f93');

insert into SEC_GROUP
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, NAME, PARENT_ID)
values ('fc806875-dd51-9820-11b5-0902b720e18d', 1, '2017-04-06 09:39:00', 'admin', '2017-04-06 09:39:00', null, null, null, 'Tenant 1', '27946bf4-0de0-a95e-93eb-062005e61a64');

insert into SEC_GROUP
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, NAME, PARENT_ID)
values ('5f7038d1-55fb-d203-d047-be682693194a', 1, '2017-04-06 09:39:07', 'admin', '2017-04-06 09:39:07', null, null, null, 'Tenant 2', '27946bf4-0de0-a95e-93eb-062005e61a64');


insert into SEC_SESSION_ATTR
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, NAME, STR_VALUE, DATATYPE, GROUP_ID)
values ('7bac16c2-d479-51d5-212b-8cba76cef922', 1, '2017-04-06 09:40:42', 'admin', '2017-04-06 09:40:42', null, null, null, 'tenantDbAddress', 'localhost/dbpt_tenant1', 'string', 'fc806875-dd51-9820-11b5-0902b720e18d');

insert into SEC_SESSION_ATTR
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, NAME, STR_VALUE, DATATYPE, GROUP_ID)
values ('73e14946-1fcb-1e68-6aea-5b4d6f9a2402', 1, '2017-04-06 09:41:44', 'admin', '2017-04-06 09:41:44', null, null, null, 'tenantDbAddress', 'localhost/dbpt_tenant2', 'string', '5f7038d1-55fb-d203-d047-be682693194a');


insert into SEC_USER
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK)
values ('f1e5d455-ec0d-7ec1-2656-db5a70f436e8', 1, '2017-04-06 09:50:32', 'admin', '2017-04-06 09:50:32', null, null, null, 'u1', 'u1', 'bdb3c3ecd5a4ae127a55308e6264660a6a7dc051', null, null, null, null, null, null, 'en', null, null, true, false, 'fc806875-dd51-9820-11b5-0902b720e18d', null);

insert into SEC_USER
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, LOGIN, LOGIN_LC, PASSWORD, NAME, FIRST_NAME, LAST_NAME, MIDDLE_NAME, POSITION_, EMAIL, LANGUAGE_, TIME_ZONE, TIME_ZONE_AUTO, ACTIVE, CHANGE_PASSWORD_AT_LOGON, GROUP_ID, IP_MASK)
values ('55c16860-0dff-9336-5fd4-3dbb0b37124c', 1, '2017-04-06 09:50:41', 'admin', '2017-04-06 09:50:41', null, null, null, 'u2', 'u2', '0750bcea7460976cc3552a38163c92e72f5d4462', null, null, null, null, null, null, 'en', null, null, true, false, '5f7038d1-55fb-d203-d047-be682693194a', null);
