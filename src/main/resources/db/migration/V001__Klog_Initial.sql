create table if not exists link
(
    id       uuid primary key,
    href     text not null,
    title    text,
    rel      text,
    "type"   text,
    hreflang text,
    "length" bigint
);

create table if not exists person
(
    id     uuid primary key not null,
    "name" text             not null,
    email  text             not null,
    uri    text
);

create unique index if not exists person_email_uidx on person (email);

create table if not exists "tag"
(
    id        uuid primary key not null,
    term      text             not null, -- identifies the category
    permalink text             not null,
    "label"   text,                      -- human-readable label for display
    scheme    text                       -- identifies the categorization scheme via a URI
);

create unique index if not exists tag_term_uidx on "tag" (term);

create table if not exists entry
(
    id             uuid primary key not null,
    slug           text             not null,
    permalink      text             not null,
    title          text             not null,
    draft          bool             not null default true,
    updated        timestamp        not null,
    published      timestamp,
    primary_author uuid references person on delete set null on update cascade,
    "content"      text             not null,
    summary        text,
    rights         text
);

create unique index if not exists entry_slug_uidx on entry (slug);

create table if not exists "page"
(
    id             uuid primary key not null,
    title          text             not null,
    "path"         text             not null,
    link_id        uuid references link on delete set null on update cascade,
    updated        timestamp        not null,
    published      timestamp,
    primary_author uuid references person on delete set null on update cascade,
    "content"      text             not null,
    rights         text
);

create unique index if not exists page_path_uidx on "page" ("path");

create table if not exists entry_to_link
(
    entry_id uuid references entry,
    link_id  uuid references link,
    primary key (entry_id, link_id)
);

create table if not exists entry_to_tag
(
    entry_id uuid references entry,
    tag_id   uuid references "tag",
    primary key (entry_id, tag_id)
);

create table if not exists entry_to_contributor
(
    entry_id  uuid references entry,
    person_id uuid references person,
    primary key (entry_id, person_id)
);

create table if not exists entry_to_source
(
    entry_id  uuid references entry,
    source_id uuid references link,
    primary key (entry_id, source_id)
);

create table if not exists page_to_link
(
    page_id uuid references page,
    link_id uuid references link,
    primary key (page_id, link_id)
);

create table if not exists page_to_contributor
(
    page_id   uuid references page,
    person_id uuid references person,
    primary key (page_id, person_id)
);
