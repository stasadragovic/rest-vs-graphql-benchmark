-- =====================================================================
-- Testni podaci za eksperiment
--
-- Obim: 50 korisnika, 200 projekata, 10.000 zadataka (50 po projektu),
--       30.000 komentara (u proseku 3 po zadatku)
-- =====================================================================

SELECT setseed(0.42);

-- ---------------------------------------------------------------------
-- Korisnici (id 1..50)
-- ---------------------------------------------------------------------
INSERT INTO users (name, email)
SELECT 'Korisnik ' || g,
       'korisnik' || g || '@example.com'
FROM generate_series(1, 50) AS g;

-- ---------------------------------------------------------------------
-- Projekti (id 1..200), vlasnik je nasumični korisnik
-- ---------------------------------------------------------------------
INSERT INTO projects (name, description, owner_id)
SELECT 'Projekat ' || g,
       'Opis projekta ' || g || '. ' || repeat('Lorem ipsum dolor sit amet, consectetur adipiscing elit. ', 3),
       1 + floor(random() * 50)::int
FROM generate_series(1, 200) AS g;

-- ---------------------------------------------------------------------
-- Zadaci (id 1..10.000), tačno 50 po projektu
-- Opis je namerno duži, jer tabla prikazuje samo naslov i status:
-- REST ga ipak vraća (over-fetching), a GraphQL ne mora.
-- Oko 10% zadataka nije dodeljeno nikome (assignee_id = NULL).
-- ---------------------------------------------------------------------
INSERT INTO tasks (project_id, title, description, status, priority, assignee_id, due_date)
SELECT ((g - 1) / 50) + 1,
       'Zadatak ' || g,
       'Detaljan opis zadatka ' || g || '. ' || repeat('Lorem ipsum dolor sit amet, consectetur adipiscing elit. ', 5),
       (ARRAY['TODO', 'IN_PROGRESS', 'DONE'])[1 + floor(random() * 3)::int],
       1 + floor(random() * 3)::int,
       CASE WHEN random() < 0.1 THEN NULL ELSE 1 + floor(random() * 50)::int END,
       DATE '2026-01-01' + floor(random() * 365)::int
FROM generate_series(1, 10000) AS g;

-- ---------------------------------------------------------------------
-- Komentari (id 1..30.000), svaki na nasumičnom zadatku
-- ---------------------------------------------------------------------
INSERT INTO comments (task_id, author_id, text)
SELECT 1 + floor(random() * 10000)::int,
       1 + floor(random() * 50)::int,
       'Komentar ' || g || '. ' || repeat('Lorem ipsum dolor sit amet. ', 2)
FROM generate_series(1, 30000) AS g;

-- Ažuriranje statistike, da planer upita radi sa tačnim podacima
ANALYZE;