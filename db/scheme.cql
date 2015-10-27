-- if running locally you need to run these first against your standalone instance

-- stores the push id registrations, this is not replicated cross region
CREATE KEYSPACE IF NOT EXISTS omnistore
  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

CREATE TABLE IF NOT EXISTS omnistore.objects (
  id varchar, -- object id
  type varchar, -- object type
  scope varchar, -- object namespace/scope. appId,
  created timestamp, -- when we created it
  last_updated timestamp, -- last updated
  properties varchar, -- default properties
  PRIMARY KEY ((id, type), scope)
);

CREATE TABLE IF NOT EXISTS omnistore.connections (
  src_id varchar, -- source object id
  src_type varchar, -- object type
  scope varchar,
  type varchar,
  dst_id varchar, -- destination object id
  dst_type varchar, -- object type
  created timestamp, -- when we created it
  last_updated timestamp, -- last updated
  properties varchar,
  PRIMARY KEY ((src_id, src_type), scope, type, dst_id, dst_type)
);

-- allows duplicate connections
-- for events that re-occur over time
CREATE TABLE IF NOT EXISTS omnistore.multi_connections (
  src_id varchar, -- source object id
  src_type varchar, -- object type
  scope varchar,
  type varchar,
  dst_id varchar, -- destination object id
  dst_type varchar, -- object type
  created timestamp, -- when we created it
  last_updated timestamp, -- last updated
  properties varchar,
  PRIMARY KEY ((src_id, src_type), scope, type, dst_id, dst_type, created)
);





