ALTER TABLE public.Invoice
    ADD COLUMN buyer bigint NOT NULL;

ALTER TABLE public.Invoice
    ADD COLUMN seller bigint NOT NULL;

ALTER TABLE public.Invoice
    ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer)
        REFERENCES public.company (id);

ALTER TABLE public.Invoice
    ADD CONSTRAINT seller_fk FOREIGN KEY (seller)
        REFERENCES public.company (id);