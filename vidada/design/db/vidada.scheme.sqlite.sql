-- Table: ApplicationSettings
CREATE TABLE ApplicationSettings ( 
    ID           INTEGER,
    ignoreImages INTEGER NOT NULL,
    ignoreMovies INTEGER NOT NULL,
    ignoreMusic  INTEGER NOT NULL,
    name         VARCHAR,
    PRIMARY KEY ( ID ) 
);


-- Table: AudioPart
CREATE TABLE AudioPart ( 
    ID INTEGER NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: ImagePart
CREATE TABLE ImagePart ( 
    ID INTEGER NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: LibraryEntry
CREATE TABLE LibraryEntry ( 
    ID               INTEGER,
    libraryRoot      VARCHAR,
    parentLibrary_ID INTEGER,
    user_ID          VARCHAR,
    PRIMARY KEY ( ID ) 
);


-- Table: MediaData
CREATE TABLE MediaData ( 
    ID               INTEGER,
    addedDate        BIGINT,
    filehash         VARCHAR,
    filename         VARCHAR,
    mediaType        INTEGER,
    opened           INTEGER NOT NULL,
    rating           INTEGER NOT NULL,
    relativeFilePath VARCHAR,
    parentLibrary_ID INTEGER,
    PRIMARY KEY ( ID ) 
);


-- Table: MediaDataTag
CREATE TABLE MediaDataTag ( 
    MediaData_Id INTEGER NOT NULL,
    Tag_Id       INTEGER NOT NULL,
    PRIMARY KEY ( MediaData_Id, Tag_Id ) 
);


-- Table: MediaLibrary
CREATE TABLE MediaLibrary ( 
    ID           INTEGER,
    ignoreImages INTEGER NOT NULL,
    ignoreMovies INTEGER NOT NULL,
    ignoreMusic  INTEGER NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: MoviePart
CREATE TABLE MoviePart ( 
    ID INTEGER NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: Tag
CREATE TABLE Tag ( 
    ID   INTEGER,
    name VARCHAR,
    PRIMARY KEY ( ID ) 
);


-- Table: TagKeyoword
CREATE TABLE TagKeyoword ( 
    ID      INTEGER,
    keyword VARCHAR,
    PRIMARY KEY ( ID ) 
);


-- Table: Tag_TagKeyword
CREATE TABLE Tag_TagKeyword ( 
    Tag_Id        INTEGER NOT NULL,
    TagKeyword_Id INTEGER NOT NULL,
    PRIMARY KEY ( Tag_Id, TagKeyword_Id ) 
);


-- Table: User
CREATE TABLE User ( 
    ID   INTEGER,
    name VARCHAR UNIQUE,
    PRIMARY KEY ( ID ) 
);



