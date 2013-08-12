-- Table: ApplicationSettings
CREATE TABLE ApplicationSettings ( 
    ID           INT,
    ignoreImages INT NOT NULL,
    ignoreMovies INT NOT NULL,
    ignoreMusic  INT NOT NULL,
    name         VARCHAR(45),
	 PRIMARY KEY ( ID ) 
);


-- Table: AudioPart
CREATE TABLE AudioPart ( 
    ID INT NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: ImagePart
CREATE TABLE ImagePart ( 
    ID INT NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: LibraryEntry
CREATE TABLE LibraryEntry ( 
    ID               INT,
    libraryRoot      VARCHAR(45),
    parentLibrary_ID INT,
    user_ID          VARCHAR(45),
    PRIMARY KEY ( ID ) 
);


-- Table: MediaData
CREATE TABLE MediaData ( 
    ID               INT,
    addedDate        DATE,
    filehash         VARCHAR(45),
    filename         VARCHAR(45),
    mediaType        INT,
    opened           INT NOT NULL,
    rating           INT NOT NULL,
    relativeFilePath VARCHAR(45),
    parentLibrary_ID INT,
    PRIMARY KEY ( ID ) 
);


-- Table: MediaDataTag
CREATE TABLE MediaDataTag ( 
    MediaData_Id INT NOT NULL,
    Tag_Id       INT NOT NULL,
    PRIMARY KEY ( MediaData_Id, Tag_Id ) 
);


-- Table: MediaLibrary
CREATE TABLE MediaLibrary ( 
    ID           INT,
    ignoreImages INT NOT NULL,
    ignoreMovies INT NOT NULL,
    ignoreMusic  INT NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: MoviePart
CREATE TABLE MoviePart ( 
    ID INT NOT NULL,
    PRIMARY KEY ( ID ) 
);


-- Table: Tag
CREATE TABLE Tag ( 
    ID   INT,
    name VARCHAR(45),
    PRIMARY KEY ( ID ) 
);


-- Table: TagKeyoword
CREATE TABLE TagKeyoword ( 
    ID      INT,
    keyword VARCHAR(45),
    PRIMARY KEY ( ID ) 
);


-- Table: Tag_TagKeyword
CREATE TABLE Tag_TagKeyword ( 
    Tag_Id        INT NOT NULL,
    TagKeyword_Id INT NOT NULL,
    PRIMARY KEY ( Tag_Id, TagKeyword_Id ) 
);


-- Table: User
CREATE TABLE User ( 
    ID   INT,
    name VARCHAR(45) UNIQUE,
    PRIMARY KEY ( ID ) 
);



