-- Crearea tabelelor entitate

   CREATE TABLE tara (
       id_tara INT PRIMARY KEY,
       denumire VARCHAR2(300)
   );

   CREATE TABLE oras (
       id_oras INT PRIMARY KEY,
       denumire VARCHAR2(300),
       tara_id INT,
       FOREIGN KEY (tara_id) REFERENCES tara(id_tara)
   );

   CREATE TABLE strada (
       id_strada INT PRIMARY KEY,
       denumire VARCHAR2(300)
   );

   CREATE TABLE permisiuni (
       id_permisiuni INT PRIMARY KEY,
       tip VARCHAR2(10) UNIQUE NOT NULL CHECK (tip IN ('admin', 'user', 'trainer'))
   );

   CREATE TABLE subscriptie (
       id_subscriptie INT PRIMARY KEY,
       tip VARCHAR2(10) UNIQUE NOT NULL CHECK (tip IN ('bronz', 'silver', 'gold'))
   );

    CREATE TABLE account (
        id_account INT PRIMARY KEY,
        first_name VARCHAR2(30),
        last_name VARCHAR2(30),
        username VARCHAR2(30),
        email VARCHAR2(30),
        parola VARCHAR2(255),
        adresa_id INT NOT NULL,
        permisiuni_id INT NOT NULL,
        FOREIGN KEY (adresa_id) REFERENCES adresa(id_adresa),
        FOREIGN KEY (permisiuni_id) REFERENCES permisiuni(id_permisiuni)
    );

   CREATE TABLE adresa (
       id_adresa INT PRIMARY KEY,
       oras_id INT,
       tara_id INT,
       strada_id INT,
       numar INT,
       cod_postal INT,
       FOREIGN KEY (oras_id) REFERENCES oras(id_oras),
       FOREIGN KEY (tara_id) REFERENCES tara(id_tara),
       FOREIGN KEY (strada_id) REFERENCES strada(id_strada)
   );

   CREATE TABLE sala (
       id_sala INT PRIMARY KEY,
       denumire VARCHAR2(30),
       adresa_id INT,
       FOREIGN KEY (adresa_id) REFERENCES adresa(id_adresa)
   );

   CREATE TABLE grupe_musculare (
       id_grupe_musculare INT PRIMARY KEY,
       denumire VARCHAR2(40)
   );

   CREATE TABLE exercitii (
       id_exercitii INT PRIMARY KEY,
       denumire VARCHAR2(30),
       grupe_musculare_id INT,
       dificultate VARCHAR2(30) UNIQUE NOT NULL CHECK (dificultate IN ('Usor', 'Mediu', 'Dificil')),
       FOREIGN KEY (grupe_musculare_id) REFERENCES grupe_musculare(id_grupe_musculare)
   );


   CREATE TABLE tip_antrenament (
       id_tip_antrenament INT PRIMARY KEY,
       denumire VARCHAR2(30),
       exercitii_id INT,
       FOREIGN KEY (exercitii_id) REFERENCES exercitii(id_exercitii)
   );


   CREATE TABLE antrenament (
       id_antrenament INT PRIMARY KEY,
       tip_antrenament_id INT,
       trainer_id INT,
       dificultate VARCHAR2(25) UNIQUE NOT NULL CHECK (dificultate IN ('Usor', 'Mediu', 'Dificil')),
       durata INTERVAL DAY TO SECOND,
       FOREIGN KEY (tip_antrenament_id) REFERENCES tip_antrenament(id_tip_antrenament),
       FOREIGN KEY (trainer_id) REFERENCES trainer(id_trainer)
   );

    CREATE TABLE dieta (
        id_dieta INT PRIMARY KEY,
        denumire VARCHAR2(30)
    );

    CREATE TABLE alergeni (
        id_alergen INT PRIMARY KEY,
        denumire VARCHAR2(30)
    );

    CREATE TABLE aliment (
        id_aliment INT PRIMARY KEY,
        calorii NUMBER(5,2),
        proteine NUMBER(5,2),
        carbohidrati NUMBER(5,2),
        zaharuri NUMBER(5,2),
        grasimi_saturate NUMBER(5,2),
        grasimi_nesaturate NUMBER(5,2),
        fibre NUMBER(5,2),
        alergeni_id INT,
        diete_id INT,
        FOREIGN KEY (alergeni_id) REFERENCES alergeni(id_alergen),
        FOREIGN KEY (diete_id) REFERENCES dieta(id_dieta)
    );

    CREATE TABLE reteta (
        id_reteta INT PRIMARY KEY,
        trainer_id INT,
        alimente_id INT,
        cost NUMBER(5,2),
        FOREIGN KEY (trainer_id) REFERENCES trainer(id_trainer),
        FOREIGN KEY (alimente_id) REFERENCES aliment(id_aliment)
    );


    CREATE TABLE user (
        id_user INT PRIMARY KEY,
        subscriptie_id INT,
        trainer_id INT,
        retete_favorite_id INT,
        sali_id INT,
        FOREIGN KEY (id_user) REFERENCES account(id_account) ON DELETE CASCADE,
        FOREIGN KEY (subscriptie_id) REFERENCES subscriptie(id_subscriptie),
        FOREIGN KEY (trainer_id) REFERENCES trainer(id_trainer),
        FOREIGN KEY (sali_id) REFERENCES sala(id_sala)
    );

    CREATE TABLE trainer (
        id_trainer INT PRIMARY KEY,
        clienti_id INT,
        retete_id INT,
        antrenamente_id INT,
        FOREIGN KEY (id_trainer) REFERENCES account(id_account) ON DELETE CASCADE,
        FOREIGN KEY (clienti_id) REFERENCES user(id_user),
        FOREIGN KEY (retete_id) REFERENCES reteta(id_reteta),
        FOREIGN KEY (antrenamente_id) REFERENCES antrenament(id_antrenament)
    );

    CREATE TABLE admin (
        id_admin INT PRIMARY KEY,
        FOREIGN KEY (id_admin) REFERENCES account(id_account)
    );

   CREATE TABLE plati_user_to_admin (
       id_tranzactie INT PRIMARY KEY,
       user_id INT,
       admin_id INT,
       subscriptie_id INT,
       cost NUMBER(5,2),
       data_tranzactiei DATE DEFAULT SYSDATE NOT NULL,
       FOREIGN KEY (user_id) REFERENCES USER(id_user),
       FOREIGN KEY (admin_id) REFERENCES ADMIN(id_admin),
       FOREIGN KEY (subscriptie_id) REFERENCES SUBSCRIPTIE(id_subscriptie)
   );

   CREATE TABLE plati_user_to_trainer (
       id_tranzactie INT PRIMARY KEY,
       user_id INT,
       trainer_id INT,
       reteta_id INT NULL,
       tip_plata VARCHAR2(10) UNIQUE NOT NULL CHECK(tip_plata IN ('Abonament', 'Reteta')),
       cost NUMBER(5,2),
       data_tranzactiei DATE DEFAULT SYSDATE NOT NULL,
       FOREIGN KEY (user_id) REFERENCES USER(id_user),
       FOREIGN KEY (trainer_id) REFERENCES TRAINER(id_trainer),
       FOREIGN KEY (reteta_id) REFERENCES RETETA(id_reteta),
       CONSTRAINT CHECK (
           (tip_plata = 'Reteta' AND reteta_id IS NOT NULL) OR
           (tip_plata = 'Abonament' AND reteta_id IS NULL)
           )
   );

   CREATE TABLE plati_trainer_to_admin (
       id_tranzactie INT PRIMARY KEY,
       trainer_id INT,
       admin_id INT,
       cost NUMBER(5,2),
       data_tranzactiei DATE DEFAULT SYSDATE NOT NULL,
       FOREIGN KEY (trainer_id) REFERENCES TRAINER(id_trainer),
       FOREIGN KEY (admin_id) REFERENCES ADMIN(id_admin)
   );


-- Creearea tabelelor asociative necesare modelarii relatiilor Many-To-Many

   CREATE TABLE STRAZI_ORAS (
       id_strada INT,
       id_oras INT,
       CONSTRAINT pk_strazi_oras PRIMARY KEY (id_strada, id_oras),
       CONSTRAINT fk_strada FOREIGN KEY (id_strada) REFERENCES STRADA(id_strada) ON DELETE CASCADE,
       CONSTRAINT fk_oras FOREIGN KEY (id_oras) REFERENCES ORAS(id_oras) ON DELETE CASCADE
   );

   CREATE TABLE TRAINER_SALA (
       id_trainer INT,
       id_sala INT,
       CONSTRAINT pk_trainer_sala PRIMARY KEY (id_trainer, id_sala),
       CONSTRAINT fk_trainer FOREIGN KEY (id_trainer) REFERENCES TRAINER(id_trainer) ON DELETE CASCADE,
       CONSTRAINT fk_sala FOREIGN KEY (id_sala) REFERENCES SALA(id_sala) ON DELETE CASCADE
   );

   CREATE TABLE USER_SALA (
       id_user INT,
       id_sala INT,
       CONSTRAINT pk_user_sala PRIMARY KEY (id_user, id_sala),
       CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES USER(id_user) ON DELETE CASCADE,
       CONSTRAINT fk_sala FOREIGN KEY (id_sala) REFERENCES SALA(id_sala) ON DELETE CASCADE
   );

   CREATE TABLE ANTRENAMENT_EXERCITII (
       id_antrenament INT,
       id_exercitii INT,
       CONSTRAINT pk_antrenament_exercitii PRIMARY KEY (id_antrenament, id_exercitii),
       CONSTRAINT fk_antrenament FOREIGN KEY (id_antrenament) REFERENCES ANTRENAMENT(id_antrenament) ON DELETE CASCADE,
       CONSTRAINT fk_exercitii FOREIGN KEY (id_exercitii) REFERENCES EXERCITII(id_exercitii) ON DELETE CASCADE
   );

   CREATE TABLE RETETA_ALIMENT (
       id_reteta INT,
       id_aliment INT,
       CONSTRAINT pk_reteta_aliment PRIMARY KEY (id_reteta, id_aliment),
       CONSTRAINT fk_reteta FOREIGN KEY (id_reteta) REFERENCES RETETA(id_reteta) ON DELETE CASCADE,
       CONSTRAINT fk_aliment FOREIGN KEY (id_aliment) REFERENCES ALIMENT(id_aliment) ON DELETE CASCADE
   );

   CREATE TABLE RETETA_ALERGENI (
       id_reteta INT,
       id_alergeni INT,
       CONSTRAINT pk_reteta_alergeni PRIMARY KEY (id_reteta, id_alergeni),
       CONSTRAINT fk_reteta FOREIGN KEY (id_reteta) REFERENCES RETETA(id_reteta) ON DELETE CASCADE,
       CONSTRAINT fk_alergeni FOREIGN KEY (id_alergeni) REFERENCES ALERGENI(id_alergen) ON DELETE CASCADE
   );

   CREATE TABLE ALERGENI_ALIMENT (
       id_alergeni INT,
       id_aliment INT,
       CONSTRAINT pk_alergeni_aliment PRIMARY KEY (id_alergeni, id_aliment),
       CONSTRAINT fk_alergeni FOREIGN KEY (id_alergeni) REFERENCES ALERGENI(id_alergen) ON DELETE CASCADE,
       CONSTRAINT fk_aliment FOREIGN KEY (id_aliment) REFERENCES ALIMENT(id_aliment) ON DELETE CASCADE
   );

   CREATE TABLE ALIMENTE_DIETA (
       id_alimente INT,
       id_dieta INT,
       CONSTRAINT pk_alimente_dieta PRIMARY KEY (id_alimente, id_dieta),
       CONSTRAINT fk_alimente FOREIGN KEY (id_alimente) REFERENCES ALIMENT(id_aliment) ON DELETE CASCADE,
       CONSTRAINT fk_dieta FOREIGN KEY (id_dieta) REFERENCES DIETA(id_dieta) ON DELETE CASCADE
   );



-- Crearea unor secvente responsabile fiecarei incrementari ID

    CREATE SEQUENCE account_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE aliment_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE alergeni_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE dieta_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE oras_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE tara_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE strada_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE permisiuni_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE subscriptie_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE sala_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE grupe_musculare_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE tip_antrenament_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE exercitii_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE adresa_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE reteta_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE antrenament_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE plati_user_to_admin_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE plati_user_to_trainer_id_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE SEQUENCE plati_trainer_to_admin_id_seq
    START WITH 1
    INCREMENT BY 1;

-- Definirea unor triggere pentru generarea automata de ID-uri

CREATE OR REPLACE TRIGGER user_id_trigger
    BEFORE INSERT ON USER
    FOR EACH ROW
    BEGIN
        :NEW.id_user := account_id_seq.NEXTVAL;
        INSERT INTO account(id_account, permisiuni_id) VALUES(:NEW.id_user, 3);
    END;

CREATE OR REPLACE TRIGGER admin_id_trigger
    BEFORE INSERT ON ADMIN
    FOR EACH ROW
    BEGIN
        :NEW.id_admin := account_id_seq.NEXTVAL;
        INSERT INTO account(id_account, permisiuni_id) VALUES(:NEW.id_admin, 1);
    END;

CREATE OR REPLACE TRIGGER trainer_id_trigger
    BEFORE INSERT ON TRAINER
    FOR EACH ROW
    BEGIN
        :NEW.id_trainer := account_id_seq.NEXTVAL;
        INSERT INTO account(id_account, permisiuni_id) VALUES(:NEW.id_trainer, 2);
    END;

CREATE OR REPLACE TRIGGER aliment_id_trigger
    BEFORE INSERT ON ALIMENT
    FOR EACH ROW
    BEGIN
        :NEW.id_aliment := aliment_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER reteta_id_trigger
    BEFORE INSERT ON RETETA
    FOR EACH ROW
    BEGIN
        :NEW.id_reteta := reteta_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER alergeni_id_trigger
    BEFORE INSERT ON ALERGENI
    FOR EACH ROW
    BEGIN
        :NEW.id_alergen := alergeni_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER dieta_id_trigger
    BEFORE INSERT ON DIETA
    FOR EACH ROW
    BEGIN
        :NEW.id_dieta := dieta_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER antrenament_id_trigger
    BEFORE INSERT ON ANTRENAMENT
    FOR EACH ROW
     BEGIN
         :NEW.id_antrenament := antrenament_id_seq.NEXTVAL;
     END;

CREATE OR REPLACE TRIGGER oras_id_trigger
    BEFORE INSERT ON ORAS
    FOR EACH ROW
    BEGIN
        :NEW.id_oras := oras_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER tara_id_trigger
    BEFORE INSERT ON TARA
    FOR EACH ROW
    BEGIN
        :NEW.id_tara := tara_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER strada_id_trigger
    BEFORE INSERT ON STRADA
    FOR EACH ROW
    BEGIN
        :NEW.id_strada := strada_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER permisiuni_id_trigger
    BEFORE INSERT ON PERMISIUNI
    FOR EACH ROW
    BEGIN
        :NEW.id_permisiuni := permisiuni_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER subscriptie_id_trigger
    BEFORE INSERT ON SUBSCRIPTIE
    FOR EACH ROW
    BEGIN
        :NEW.id_subscriptie := subscriptie_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER sala_id_trigger
    BEFORE INSERT ON SALA
    FOR EACH ROW
    BEGIN
        :NEW.id_sala := sala_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER grupe_musculare_id_trigger
    BEFORE INSERT ON GRUPE_MUSCULARE
    FOR EACH ROW
    BEGIN
        :NEW.id_grupe_musculare := grupe_musculare_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER tip_antrenament_id_trigger
    BEFORE INSERT ON TIP_ANTRENAMENT
    FOR EACH ROW
    BEGIN
        :NEW.id_tip_antrenament := tip_antrenament_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER exercitii_id_trigger
    BEFORE INSERT ON EXERCITII
    FOR EACH ROW
    BEGIN
        :NEW.id_exercitii := exercitii_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER adresa_id_trigger
    BEFORE INSERT ON ADRESA
    FOR EACH ROW
    BEGIN
        :NEW.id_adresa := adresa_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER plati_user_to_admin_id_trigger
    BEFORE INSERT ON PLATI_USER_TO_ADMIN
    FOR EACH ROW
    BEGIN
        :NEW.id_tranzactie := plati_user_to_admin_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER plati_user_to_trainer_id_trigger
    BEFORE INSERT ON PLATI_USER_TO_TRAINER
    FOR EACH ROW
    BEGIN
        :NEW.id_tranzactie := plati_user_to_trainer_id_seq.NEXTVAL;
    END;

CREATE OR REPLACE TRIGGER plati_trainer_to_admin_id_trigger
    BEFORE INSERT ON PLATI_TRAINER_TO_ADMIN
    FOR EACH ROW
    BEGIN
        :NEW.id_tranzactie := plati_trainer_to_admin_id_seq.NEXTVAL;
    END;