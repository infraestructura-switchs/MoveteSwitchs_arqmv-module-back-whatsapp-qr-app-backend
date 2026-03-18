CREATE TABLE product_discount (
    product_discount_id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    description VARCHAR(255) NULL,
    discount_amount DECIMAL(12,2) NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_discount PRIMARY KEY (product_discount_id),
    CONSTRAINT fk_product_discount_product FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT ck_product_discount_amount_positive CHECK (discount_amount > 0),
    CONSTRAINT ck_product_discount_range CHECK (end_at >= start_at)
);

CREATE INDEX idx_product_discount_company_product ON product_discount (company_id, product_id);
CREATE INDEX idx_product_discount_active_range ON product_discount (status, start_at, end_at);