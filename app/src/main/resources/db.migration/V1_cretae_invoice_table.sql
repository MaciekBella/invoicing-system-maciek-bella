CREATE TABLE public.invoices
{
    id bigserial NOTNULL,
    issue_date date NOTNULL,
    "number" character varying(50) NOT NULL,
    PRIMARY KEY (id)
};