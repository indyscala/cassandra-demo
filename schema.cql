drop keyspace indyscala;
create keyspace indyscala with replication = {'class':'SimpleStrategy', 'replication_factor':3};

use indyscala;

create table tweets (
  tweet_id bigint primary key,
  text text,
  username text,
  lang map<text, text>
);
create index tweet_usernames on tweets(username);

create table tweet_terms (
  term text,
  tweet_id bigint,
  lang text,
  username text,
  text text,
  primary key((term, lang), tweet_id)
);

create table counters (
  name text primary key,
  count counter
);

create table user_profiles (
  username text primary key,
  languages set<text>,
  hashtags set<text>,
  mentions set<text>
);
