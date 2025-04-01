CREATE TABLE tara (
    id_tara INT PRIMARY KEY AUTO_INCREMENT,
    denumire VARCHAR(255) NOT NULL
);

CREATE TABLE oras (
    id_oras INT PRIMARY KEY AUTO_INCREMENT,
    denumire VARCHAR(255) NOT NULL,
    tara_id INT,
    FOREIGN KEY (tara_id) REFERENCES tara(id_tara) ON DELETE CASCADE
);

CREATE TABLE strada (
    id_strada INT PRIMARY KEY AUTO_INCREMENT,
    denumire VARCHAR(255) NOT NULL
);

CREATE TABLE adresa (
    id_adresa INT PRIMARY KEY AUTO_INCREMENT,
    oras_id INT,
    strada_id INT,
    numar INT,
    cod_postal VARCHAR(20),
    FOREIGN KEY (oras_id) REFERENCES oras(id_oras) ON DELETE CASCADE,
    FOREIGN KEY (strada_id) REFERENCES strada(id_strada) ON DELETE CASCADE
);

CREATE TABLE permisiuni (
    id_permisiuni INT PRIMARY KEY AUTO_INCREMENT,
    tip ENUM('admin', 'user', 'trainer') UNIQUE NOT NULL
);

CREATE TABLE subscriptie (
    id_subscriptie INT PRIMARY KEY AUTO_INCREMENT,
    tip ENUM('bronz', 'silver', 'gold') UNIQUE NOT NULL
);

CREATE TABLE account (
    id_account INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    parola VARCHAR(255) NOT NULL,
    adresa_id INT NOT NULL,
    permisiuni_id INT NOT NULL,
    FOREIGN KEY (adresa_id) REFERENCES adresa(id_adresa) ON DELETE CASCADE,
    FOREIGN KEY (permisiuni_id) REFERENCES permisiuni(id_permisiuni)
);

CREATE TABLE user (
    id_user INT PRIMARY KEY,
    subscriptie_id INT,
    FOREIGN KEY (id_user) REFERENCES account(id_account) ON DELETE CASCADE,
    FOREIGN KEY (subscriptie_id) REFERENCES subscriptie(id_subscriptie)
);

CREATE TABLE trainer (
    id_trainer INT PRIMARY KEY,
    FOREIGN KEY (id_trainer) REFERENCES account(id_account) ON DELETE CASCADE
);

CREATE TABLE admin (
    id_admin INT PRIMARY KEY,
    FOREIGN KEY (id_admin) REFERENCES account(id_account)
);

CREATE TABLE sala (
    id_sala INT PRIMARY KEY AUTO_INCREMENT,
    denumire VARCHAR(100) NOT NULL,
    adresa_id INT,
    FOREIGN KEY (adresa_id) REFERENCES adresa(id_adresa) ON DELETE CASCADE
);

CREATE TABLE user_sala (
    id_user INT,
    id_sala INT,
    PRIMARY KEY (id_user, id_sala),
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_sala) REFERENCES sala(id_sala) ON DELETE CASCADE
);

CREATE TABLE trainer_sala (
    id_trainer INT,
    id_sala INT,
    PRIMARY KEY (id_trainer, id_sala),
    FOREIGN KEY (id_trainer) REFERENCES trainer(id_trainer) ON DELETE CASCADE,
    FOREIGN KEY (id_sala) REFERENCES sala(id_sala) ON DELETE CASCADE
);

CREATE TABLE plati_user_to_admin (
    id_tranzactie INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    admin_id INT,
    subscriptie_id INT,
    cost DECIMAL(10,2) NOT NULL,
    data_tranzactiei TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES admin(id_admin),
    FOREIGN KEY (subscriptie_id) REFERENCES subscriptie(id_subscriptie)
);

CREATE TABLE plati_user_to_trainer (
    id_tranzactie INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    trainer_id INT,
    tip_plata ENUM('Abonament', 'Reteta') NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    data_tranzactiei TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainer(id_trainer) ON DELETE CASCADE
);

CREATE TABLE plati_trainer_to_admin (
    id_tranzactie INT PRIMARY KEY AUTO_INCREMENT,
    trainer_id INT,
    admin_id INT,
    cost DECIMAL(10,2) NOT NULL,
    data_tranzactiei TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trainer_id) REFERENCES trainer(id_trainer) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES admin(id_admin)
);   END;
