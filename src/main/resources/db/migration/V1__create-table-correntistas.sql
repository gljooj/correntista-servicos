create table correntistas(

    id bigint not null auto_increment,
    id_usuario bigint not null unique,
    status_conta varchar(100) not null,
    saldo DECIMAL(10, 2) NOT NULL,
    limite_diario DECIMAL(10, 2) NOT NULL,

    primary key(id)

);