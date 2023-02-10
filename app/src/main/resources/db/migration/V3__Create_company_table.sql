CREATE TABLE public.company

(

    id                          bigserial              NOT NULL,

    taxIdentificationNumber     character varying(200) NOT NULL,

    address                     character varying(200) NOT NULL,

    name                        character varying(100) NOT NULL,

    PRIMARY KEY (id)

);




ALTER TABLE public.company

    OWNER to postgres;