create type klog_role as enum ('CONTRIBUTOR', 'ADMIN');
-- contributor can edit own entries + person
-- admin = full control

create table if not exists person
(
    id            uuid primary key not null,
    "name"        text             not null,
    email         text             not null,
    "hash"        text,      -- hex encoded
    salt          text,      -- hex encoded
    pwat          timestamp, -- password updated at
    "role"        klog_role                 default 'CONTRIBUTOR',
    uri           text,
    avatar        text,
    last_login_at timestamp,
    created_at    timestamp        not null default now(),
    updated_at    timestamp
);

create unique index if not exists person_email_uidx on person (email);

create table if not exists "tag"
(
    id         uuid primary key not null,
    term       text             not null, -- unique identifies the tag
    permalink  text             not null,
    created_at timestamp        not null default now(),
    updated_at timestamp
);

create unique index if not exists tag_term_uidx on "tag" (term);

create table if not exists entry
(
    id           uuid primary key not null,
    slug         text             not null,
    permalink    text             not null,
    title        text             not null,
    draft        bool             not null default true,
    created_at   timestamp        not null default now(),
    updated_at   timestamp,
    published_at timestamp,
    author_id    uuid references person on delete set null on update cascade,
    "content"    text             not null,
    summary      text
);

create unique index if not exists entry_slug_uidx on entry (slug);

create table if not exists entry_to_tag
(
    id       uuid primary key not null,
    entry_id uuid references entry,
    tag_id   uuid references "tag"
);

create index if not exists entry_to_tag_idx on entry_to_tag (entry_id, tag_id);

create table if not exists entry_metadata
(
    id         uuid primary key not null,
    entry_id   uuid references entry on delete set null on update cascade,
    "key"      text             not null,
    "value"    text             not null,
    created_at timestamp        not null default now(),
    updated_at timestamp
);

create index if not exists entry_metadata_key_idx on entry_metadata (entry_id, "key");

create or replace function table_updated_trgfn()
    returns trigger as
$$
begin
    NEW.updated_at = now();
    return NEW;
end
$$ language 'plpgsql';

create trigger person_updated_at_trg
    before update
    on "person"
    for each row
    when ((pg_trigger_depth() < 1))
execute function table_updated_trgfn();

create trigger tag_updated_at_trg
    before update
    on "tag"
    for each row
    when ((pg_trigger_depth() < 1))
execute function table_updated_trgfn();

create trigger entry_updated_at_trg
    before update
    on entry
    for each row
    when ((pg_trigger_depth() < 1))
execute function table_updated_trgfn();

create trigger entry_metadata_updated_at_trg
    before update
    on entry_metadata
    for each row
    when ((pg_trigger_depth() < 1))
execute function table_updated_trgfn();
