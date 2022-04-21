CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table tenant (
                      id uuid DEFAULT uuid_generate_v4() primary key,
                      rid text,
                      name text not null,
                      url text,
                      profile_url text,
                      attributes jsonb,
                      created_at timestamp with time zone not null DEFAULT now(),
                      updated_at timestamp with time zone,
                      deleted_at timestamp with time zone,
                      created_by text not null DEFAULT 'system',
                      updated_by text,
                      deleted_by text,
                      status text
);
create unique index idx_tenant_rid on public.tenant (rid);

INSERT INTO public.tenant (rid, name, url, created_at, created_by, status) VALUES ('0', 'Rapidor', 'https://www.rapidor.co', now(), 'system', 'Active');