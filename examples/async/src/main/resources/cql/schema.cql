CREATE KEYSPACE test WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

CREATE TABLE test.song (
    sid uuid,
    artist text,
    album text,
    title text,
    length int,
    PRIMARY KEY(sid)
 );

CREATE TABLE test.playlist (
    pid uuid,
    sid uuid,
    artist text,
    album text,
    title text,
    length int,
    PRIMARY KEY(pid, sid)
);


-- populate example song
--
insert into test.song(sid, artist, album, title, length) values(61e5ba21-3731-46d5-8d21-2c209a58c05c, 'foo', 'bar', 'baz', 10);
insert into test.song(sid, artist, album, title, length) values(112ae67f-ed88-4e1d-a1b6-fcc6723cd525, 'sd2', 'eew', '222', 23);
insert into test.song(sid, artist, album, title, length) values(b1266b93-b3ba-4d28-88ab-05709f94c36f, '22d', 'ggd', '333', 11);

-- populate example playlist
insert into test.playlist(pid,sid,album,artist,length,title) values(00000000-0000-0000-0000-000000000000, 61e5ba21-3731-46d5-8d21-2c209a58c05c, 'bar', 'foo', 10, 'baz');
insert into test.playlist(pid,sid,album,artist,length,title) values(00000000-0000-0000-0000-000000000000, b1266b93-b3ba-4d28-88ab-05709f94c36f, 'ggd', '22d', 11, '333');
insert into test.playlist(pid,sid,album,artist,length,title) values(00000000-0000-0000-0000-000000000000, 112ae67f-ed88-4e1d-a1b6-fcc6723cd525, 'eew', 'sd2', 23, '222');
