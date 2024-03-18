create table transferencias(

    id bigint not null auto_increment,
    id_correntista_origem bigint not null,
    id_correntista_destino bigint not null,
    valor DECIMAL(10, 2) NOT NULL,
    status_transacao varchar(100) not null,
    data_transferencia DATETIME DEFAULT CURRENT_TIMESTAMP,
    status_bacen varchar(100) not null,
    FOREIGN KEY (id_correntista_origem) REFERENCES correntistas(id),
    FOREIGN KEY (id_correntista_destino) REFERENCES correntistas(id),

    primary key(id)

);