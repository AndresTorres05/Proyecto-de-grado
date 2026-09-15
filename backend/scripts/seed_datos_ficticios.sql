-- =====================================================================
-- VITA+ — Script de datos ficticios para analítica
-- =====================================================================
-- Genera: 5 organizaciones, 200 personas mayores, 25 acompañantes,
-- 25 voluntarios, catálogo de gustos, vínculos entre todos ellos,
-- medicamentos, actividades y participación/asistencia en esas
-- actividades.
--
-- Cómo correrlo:
--   - El proyecto ya no corre contra una BD local: los 4 módulos
--     (backend, auth-backend, personas-backend, messaging-backend)
--     apuntan a la MISMA base de datos Postgres en Supabase, con los
--     datos de conexión que ya están en application.properties de
--     cualquiera de ellos (spring.datasource.url/username/password).
--   - pgAdmin 4 o cualquier cliente: conéctate con esos mismos datos,
--     pega este archivo completo en un Query Tool y ejecútalo.
--   - psql: psql "$(grep spring.datasource.url ... )" -f seed_datos_ficticios.sql
--     (o arma la cadena de conexión a mano con host/usuario/password de
--     application.properties; no se repiten aquí para no duplicar el
--     secreto en dos archivos).
--
-- Es SEGURO volver a correrlo: al inicio borra únicamente lo que este
-- mismo script generó antes (todo correo termina en @vitaplus.test),
-- así que nunca toca tus usuarios reales ni los de prueba anteriores.
--
-- Contraseña de todas las cuentas ficticias: Vita2025*
-- (solo aplica para ORGANIZACION y VOLUNTARIO, que inician sesión con
-- correo+contraseña. PERSONA_MAYOR y ACOMPANANTE inician sesión por
-- teléfono + código OTP, así que su contraseña no se usa nunca; se les
-- guarda igual porque la columna es obligatoria en la base de datos).
--
-- Los voluntarios NO tienen una tabla propia que los conecte con
-- personas mayores (no existe en el modelo actual). Por eso quedan
-- "conectados" de forma indirecta: cada voluntario pertenece a una de
-- las 5 organizaciones ficticias, y esas mismas organizaciones atienden
-- a las 200 personas mayores. Si más adelante quieres una relación
-- explícita persona_mayor-voluntario, hay que agregar una tabla nueva
-- (y su entidad JPA), lo cual es un cambio de código, no solo de datos.
--
-- Cambios de modelo incorporados en esta versión (no existían cuando se
-- escribió la primera versión de este script):
--   - actividad ahora tiene descripcion, hora, cupos y responsable.
--   - persona_mayor_organizacion ahora tiene "estado" (PENDIENTE /
--     ACEPTADA / RECHAZADA), igual que persona_mayor_acompanante. Antes
--     este script no lo llenaba, lo cual además hacía que el botón de
--     emergencia (que solo notifica organizaciones con estado ACEPTADA)
--     no tuviera nada realista que probar.
--   - tabla nueva "participacion" (persona_mayor <-> actividad, con
--     asistio boolean): quién se inscribió a cada actividad y si
--     asistió. Solo se generan participaciones de personas mayores cuya
--     relación con la organización dueña de la actividad esté ACEPTADA.
-- =====================================================================

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ---------------------------------------------------------------------
-- 0. Limpieza idempotente (solo borra lo generado por este script)
-- ---------------------------------------------------------------------
DELETE FROM persona_mayor_gusto
 WHERE id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM persona_mayor_organizacion
 WHERE id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM persona_mayor_acompanante
 WHERE id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test')
    OR id_acompanante IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM medicamento
 WHERE id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM participacion
 WHERE id_actividad IN (
     SELECT id_actividad FROM actividad
      WHERE id_organizacion IN (SELECT id_organizacion FROM organizacion WHERE correo LIKE '%@vitaplus.test')
 );

DELETE FROM actividad
 WHERE id_organizacion IN (SELECT id_organizacion FROM organizacion WHERE correo LIKE '%@vitaplus.test');

DELETE FROM usuario_rol
 WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM voluntario
 WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM acompanante
 WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM persona_mayor
 WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE '%@vitaplus.test');

DELETE FROM usuario WHERE correo LIKE '%@vitaplus.test';

DELETE FROM organizacion WHERE correo LIKE '%@vitaplus.test';

-- ---------------------------------------------------------------------
-- 1. Roles base (por si esta base de datos nunca ha arrancado el backend)
-- ---------------------------------------------------------------------
INSERT INTO rol (nombre)
SELECT r FROM (VALUES ('ORGANIZACION'), ('VOLUNTARIO'), ('ACOMPANANTE'), ('PERSONA_MAYOR')) AS v(r)
ON CONFLICT (nombre) DO NOTHING;

-- ---------------------------------------------------------------------
-- 2. Organizaciones ficticias (+ su cuenta de usuario, como lo hace
--    el registro real: la organización inicia sesión con su correo)
-- ---------------------------------------------------------------------
WITH datos_org AS (
    SELECT * FROM (VALUES
      ('Fundación Entrenubes Usme',                      'Usme Centro',   '6010000001', 'org01@vitaplus.test'),
      ('Hogar de Paso San Rafael',                        'Yomasa',        '6010000002', 'org02@vitaplus.test'),
      ('Corporación Vida Plena Usme',                      'Santa Librada', '6010000003', 'org03@vitaplus.test'),
      ('Centro Día Renacer',                               'La Flora',      '6010000004', 'org04@vitaplus.test'),
      ('Parroquia Nuestra Señora de la Esperanza - Usme',  'Comuneros',     '6010000005', 'org05@vitaplus.test')
    ) AS v(nombre, direccion, telefono, correo)
),
ins_org AS (
    INSERT INTO organizacion (nombre, direccion, telefono, correo)
    SELECT nombre, direccion, telefono, correo FROM datos_org
    RETURNING id_organizacion, correo
),
ins_usuario AS (
    INSERT INTO usuario (nombre_usuario, contrasena_hash, correo, id_organizacion, activo, fecha_creacion)
    SELECT d.nombre, crypt('Vita2025*', gen_salt('bf', 10)), o.correo, o.id_organizacion, true,
           now() - make_interval(days => 60 + floor(random() * 500)::int)
    FROM ins_org o JOIN datos_org d ON d.correo = o.correo
    RETURNING id_usuario
)
INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT iu.id_usuario, r.id_rol
FROM ins_usuario iu, rol r
WHERE r.nombre = 'ORGANIZACION';

-- ---------------------------------------------------------------------
-- 3. Catálogo de gustos
-- ---------------------------------------------------------------------
INSERT INTO gusto (nombre, categoria)
VALUES
 ('Boleros', 'MUSICA'),
 ('Música ranchera', 'MUSICA'),
 ('Pintura', 'ARTE'),
 ('Fotografía', 'ARTE'),
 ('Caminatas', 'DEPORTE'),
 ('Yoga suave', 'DEPORTE'),
 ('Novelas', 'LECTURA'),
 ('Periódico', 'LECTURA'),
 ('Juegos de mesa', 'JUEGOS'),
 ('Bingo', 'JUEGOS'),
 ('Tejido', 'MANUALIDADES'),
 ('Bordado', 'MANUALIDADES'),
 ('Oración', 'ESPIRITUALIDAD'),
 ('Grupos de oración', 'ESPIRITUALIDAD'),
 ('Jardinería', 'NATURALEZA'),
 ('Cuidado de mascotas', 'NATURALEZA'),
 ('Repostería', 'GASTRONOMIA'),
 ('Cocina tradicional', 'GASTRONOMIA'),
 ('Redes sociales', 'TECNOLOGIA'),
 ('Ver televisión', 'TECNOLOGIA')
ON CONFLICT (nombre) DO NOTHING;

-- ---------------------------------------------------------------------
-- 4. 200 personas mayores (usuario + persona_mayor)
-- ---------------------------------------------------------------------
WITH datos AS (
    SELECT
        i,
        (ARRAY['María','Rosa','Carmen','Luz','Ana','Blanca','Gloria','Esperanza','Cecilia','Teresa',
               'Marleny','Consuelo','Amparo','Stella','Nubia','Yolanda','Beatriz','Marina','Isabel','Elvia'])
            [1 + floor(random() * 20)::int] AS nombre_f,
        (ARRAY['José','Luis','Carlos','Jorge','Pedro','Manuel','Roberto','Alberto','Guillermo','Rafael',
               'Hernando','Gustavo','Eduardo','Fernando','Ricardo','Julio','Antonio','Miguel','Alfonso','Arturo'])
            [1 + floor(random() * 20)::int] AS nombre_m,
        (ARRAY['González','Rodríguez','Martínez','López','García','Pérez','Sánchez','Ramírez','Torres',
               'Rivera','Gómez','Díaz','Reyes','Cruz','Morales','Ortiz','Gutiérrez','Chaparro','Vargas',
               'Suárez','Castañeda','Bautista','Cárdenas','Rincón'])
            [1 + floor(random() * 24)::int] AS apellido1,
        (ARRAY['González','Rodríguez','Martínez','López','García','Pérez','Sánchez','Ramírez','Torres',
               'Rivera','Gómez','Díaz','Reyes','Cruz','Morales','Ortiz','Gutiérrez','Chaparro','Vargas',
               'Suárez','Castañeda','Bautista','Cárdenas','Rincón'])
            [1 + floor(random() * 24)::int] AS apellido2,
        (ARRAY['Usme Centro','La Flora','Yomasa','Danubio Azul','Santa Librada','Los Soches','El Uval',
               'Comuneros','Alfonso López','La Aurora','Arrayanes','El Destino','Tocaimita','La Requilina'])
            [1 + floor(random() * 14)::int] AS barrio,
        CASE WHEN random() < 0.58 THEN 'Femenino' ELSE 'Masculino' END AS genero,
        (CURRENT_DATE - make_interval(years => (60 + floor(random() * 33))::int,
                                       days  => floor(random() * 365)::int)) AS fecha_nacimiento
    FROM generate_series(1, 200) AS s(i)
),
filas AS (
    SELECT
        i, genero, fecha_nacimiento, barrio,
        (CASE WHEN genero = 'Femenino' THEN nombre_f ELSE nombre_m END
            || ' ' || apellido1 || ' ' || apellido2) AS nombre_usuario,
        'pm' || lpad(i::text, 3, '0') || '@vitaplus.test' AS correo,
        '300' || lpad(i::text, 7, '0') AS telefono
    FROM datos
),
ins_usuario AS (
    INSERT INTO usuario (nombre_usuario, contrasena_hash, correo, telefono, activo, fecha_creacion)
    SELECT nombre_usuario, crypt('Vita2025*', gen_salt('bf', 10)), correo, telefono, true,
           now() - make_interval(days => floor(random() * 400)::int)
    FROM filas
    RETURNING id_usuario, correo
)
INSERT INTO persona_mayor (id_usuario, fecha_nacimiento, genero, direccion)
SELECT u.id_usuario, f.fecha_nacimiento, f.genero, f.barrio
FROM ins_usuario u JOIN filas f ON f.correo = u.correo;

-- ---------------------------------------------------------------------
-- 5. 25 acompañantes (usuario + acompanante)
-- ---------------------------------------------------------------------
WITH datos AS (
    SELECT
        i,
        (ARRAY['María','Rosa','Carmen','Luz','Ana','José','Luis','Carlos','Jorge','Pedro',
               'Sandra','Diana','Patricia','Andrea','Camila','Julián','David','Andrés','Felipe','Daniela'])
            [1 + floor(random() * 20)::int] AS nombre,
        (ARRAY['González','Rodríguez','Martínez','López','García','Pérez','Sánchez','Ramírez','Torres',
               'Rivera','Gómez','Díaz','Reyes','Cruz','Morales','Ortiz','Gutiérrez','Chaparro','Vargas',
               'Suárez','Castañeda','Bautista','Cárdenas','Rincón'])
            [1 + floor(random() * 24)::int] AS apellido1,
        (ARRAY['González','Rodríguez','Martínez','López','García','Pérez','Sánchez','Ramírez','Torres',
               'Rivera','Gómez','Díaz','Reyes','Cruz','Morales','Ortiz','Gutiérrez','Chaparro','Vargas',
               'Suárez','Castañeda','Bautista','Cárdenas','Rincón'])
            [1 + floor(random() * 24)::int] AS apellido2,
        (ARRAY['Hijo/a', 'Nieto/a', 'Sobrino/a', 'Cuidador/a contratado', 'Hermano/a', 'Vecino/a de confianza'])
            [1 + floor(random() * 6)::int] AS parentesco
    FROM generate_series(1, 25) AS s(i)
),
filas AS (
    SELECT i, parentesco,
           (nombre || ' ' || apellido1 || ' ' || apellido2) AS nombre_usuario,
           'acomp' || lpad(i::text, 3, '0') || '@vitaplus.test' AS correo,
           '301' || lpad(i::text, 7, '0') AS telefono
    FROM datos
),
ins_usuario AS (
    INSERT INTO usuario (nombre_usuario, contrasena_hash, correo, telefono, activo, fecha_creacion)
    SELECT nombre_usuario, crypt('Vita2025*', gen_salt('bf', 10)), correo, telefono, true,
           now() - make_interval(days => floor(random() * 400)::int)
    FROM filas
    RETURNING id_usuario, correo
)
INSERT INTO acompanante (id_usuario, parentesco)
SELECT u.id_usuario, f.parentesco
FROM ins_usuario u JOIN filas f ON f.correo = u.correo;

-- ---------------------------------------------------------------------
-- 6. 25 voluntarios (usuario + voluntario), cada uno queda asignado a
--    una de las 5 organizaciones ficticias
-- ---------------------------------------------------------------------
WITH orgs AS (
    SELECT id_organizacion, row_number() OVER (ORDER BY id_organizacion) AS rn
    FROM organizacion WHERE correo LIKE 'org%@vitaplus.test'
),
n_org AS (SELECT count(*) AS n FROM orgs),
datos AS (
    SELECT
        i,
        (ARRAY['María','Rosa','Carmen','Luz','Ana','José','Luis','Carlos','Jorge','Pedro',
               'Sandra','Diana','Patricia','Andrea','Camila','Julián','David','Andrés','Felipe','Daniela'])
            [1 + floor(random() * 20)::int] AS nombre,
        (ARRAY['González','Rodríguez','Martínez','López','García','Pérez','Sánchez','Ramírez','Torres',
               'Rivera','Gómez','Díaz','Reyes','Cruz','Morales','Ortiz','Gutiérrez','Chaparro','Vargas',
               'Suárez','Castañeda','Bautista','Cárdenas','Rincón'])
            [1 + floor(random() * 24)::int] AS apellido1,
        (ARRAY['González','Rodríguez','Martínez','López','García','Pérez','Sánchez','Ramírez','Torres',
               'Rivera','Gómez','Díaz','Reyes','Cruz','Morales','Ortiz','Gutiérrez','Chaparro','Vargas',
               'Suárez','Castañeda','Bautista','Cárdenas','Rincón'])
            [1 + floor(random() * 24)::int] AS apellido2,
        (ARRAY['Fines de semana', 'Entre semana en las mañanas', 'Entre semana en las tardes',
               'Horario flexible', 'Solo festivos'])
            [1 + floor(random() * 5)::int] AS disponibilidad,
        (SELECT o.id_organizacion FROM orgs o, n_org WHERE o.rn = 1 + ((i - 1) % n_org.n)) AS id_organizacion
    FROM generate_series(1, 25) AS s(i)
),
filas AS (
    SELECT i, disponibilidad, id_organizacion,
           (nombre || ' ' || apellido1 || ' ' || apellido2) AS nombre_usuario,
           'vol' || lpad(i::text, 3, '0') || '@vitaplus.test' AS correo,
           '302' || lpad(i::text, 7, '0') AS telefono
    FROM datos
),
ins_usuario AS (
    INSERT INTO usuario (nombre_usuario, contrasena_hash, correo, telefono, id_organizacion, activo, fecha_creacion)
    SELECT nombre_usuario, crypt('Vita2025*', gen_salt('bf', 10)), correo, telefono, id_organizacion, true,
           now() - make_interval(days => floor(random() * 400)::int)
    FROM filas
    RETURNING id_usuario, correo
)
INSERT INTO voluntario (id_usuario, disponibilidad)
SELECT u.id_usuario, f.disponibilidad
FROM ins_usuario u JOIN filas f ON f.correo = u.correo;

-- ---------------------------------------------------------------------
-- 7. Asignación de roles para las 250 cuentas nuevas
-- ---------------------------------------------------------------------
INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u JOIN rol r ON r.nombre = 'PERSONA_MAYOR'
WHERE u.correo LIKE 'pm%@vitaplus.test'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u JOIN rol r ON r.nombre = 'ACOMPANANTE'
WHERE u.correo LIKE 'acomp%@vitaplus.test'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u JOIN rol r ON r.nombre = 'VOLUNTARIO'
WHERE u.correo LIKE 'vol%@vitaplus.test'
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
-- 8. Vínculo persona_mayor - acompañante (cada persona mayor con 1
--    acompañante; cada acompañante queda a cargo de ~8 personas)
-- ---------------------------------------------------------------------
WITH pm AS (
    SELECT u.id_usuario, row_number() OVER (ORDER BY u.id_usuario) AS rn
    FROM usuario u WHERE u.correo LIKE 'pm%@vitaplus.test'
),
ac AS (
    SELECT u.id_usuario, row_number() OVER (ORDER BY u.id_usuario) AS rn
    FROM usuario u WHERE u.correo LIKE 'acomp%@vitaplus.test'
),
n_ac AS (SELECT count(*) AS n FROM ac)
INSERT INTO persona_mayor_acompanante (id_persona_mayor, id_acompanante, estado)
SELECT pm.id_usuario, ac.id_usuario,
       CASE WHEN random() < 0.85 THEN 'ACEPTADA' ELSE 'PENDIENTE' END
FROM pm
CROSS JOIN n_ac
JOIN ac ON ac.rn = 1 + ((pm.rn - 1) % n_ac.n)
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
-- 9. Vínculo persona_mayor - organización (con estado: columna agregada
--    al modelo después de la primera versión de este script. La mayoría
--    queda ACEPTADA porque el botón de emergencia solo notifica
--    organizaciones con ese estado; el resto queda repartido entre
--    PENDIENTE y RECHAZADA para poder probar esos flujos también).
-- ---------------------------------------------------------------------
WITH pm AS (
    SELECT u.id_usuario, row_number() OVER (ORDER BY u.id_usuario) AS rn
    FROM usuario u WHERE u.correo LIKE 'pm%@vitaplus.test'
),
orgs AS (
    SELECT id_organizacion, row_number() OVER (ORDER BY id_organizacion) AS rn
    FROM organizacion WHERE correo LIKE 'org%@vitaplus.test'
),
n_org AS (SELECT count(*) AS n FROM orgs),
-- r se calcula una sola vez por fila aquí (no dentro del CASE de abajo)
-- porque cada llamado a random() da un valor distinto; si el CASE
-- llamara random() en cada rama, los porcentajes de la distribución no
-- serían los que dicen los comentarios.
asignaciones AS (
    SELECT pm.id_usuario AS id_persona_mayor, o.id_organizacion, random() AS r
    FROM pm
    CROSS JOIN n_org
    JOIN orgs o ON o.rn = 1 + ((pm.rn - 1) % n_org.n)
)
INSERT INTO persona_mayor_organizacion (id_persona_mayor, id_organizacion, estado)
SELECT id_persona_mayor, id_organizacion,
       CASE WHEN r < 0.75 THEN 'ACEPTADA'   -- 75%
            WHEN r < 0.90 THEN 'PENDIENTE'  -- 15%
            ELSE 'RECHAZADA' END            -- 10%
FROM asignaciones
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
-- 10. Entre 2 y 4 gustos aleatorios por persona mayor
-- ---------------------------------------------------------------------
WITH gusto_ids AS (
    SELECT array_agg(id_gusto) AS ids FROM gusto
),
pm AS (
    -- n_gustos se calcula aquí (columna normal) y no directamente dentro de
    -- generate_series(), porque Postgres evalúa una sola vez los argumentos
    -- de una función de conjunto cuando no dependen de una columna real de
    -- la fila anterior; como columna sí varía correctamente fila a fila.
    SELECT id_usuario, 2 + floor(random() * 3)::int AS n_gustos
    FROM usuario WHERE correo LIKE 'pm%@vitaplus.test'
),
asignaciones AS (
    SELECT pm.id_usuario AS id_persona_mayor,
           gusto_ids.ids[1 + floor(random() * array_length(gusto_ids.ids, 1))::int] AS id_gusto
    FROM pm
    CROSS JOIN gusto_ids
    CROSS JOIN LATERAL generate_series(1, pm.n_gustos) AS k
)
INSERT INTO persona_mayor_gusto (id_persona_mayor, id_gusto)
SELECT DISTINCT id_persona_mayor, id_gusto FROM asignaciones
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
-- 11. Medicamentos para ~50% de las personas mayores (1 o 2 cada una).
--     proxima_toma queda siempre en el FUTURO a propósito: el scheduler
--     de recordatorios corre cada minuto y envía SMS reales por TextBee
--     a "activo=true AND proxima_toma <= ahora"; si generáramos fechas
--     pasadas, apenas prendas el backend intentaría mandar mensajes a
--     estos números de teléfono ficticios.
-- ---------------------------------------------------------------------
WITH catalogo AS (
    SELECT * FROM (VALUES
        ('Losartán',                '50 mg',     24, '08:00'::time),
        ('Metformina',              '850 mg',    12, '07:00'::time),
        ('Atorvastatina',           '20 mg',     24, '21:00'::time),
        ('Omeprazol',               '20 mg',     24, '06:30'::time),
        ('Ácido acetilsalicílico',  '100 mg',    24, '08:00'::time),
        ('Levotiroxina',            '50 mcg',    24, '06:00'::time),
        ('Enalapril',               '10 mg',     12, '08:00'::time),
        ('Calcio + Vitamina D',     '1 tableta', 24, '13:00'::time)
    ) AS v(nombre, dosis, intervalo_horas, hora)
),
pm AS (
    SELECT id_usuario,
           CASE WHEN random() < 0.4 THEN 2 ELSE 1 END AS n_meds
    FROM usuario
    WHERE correo LIKE 'pm%@vitaplus.test' AND random() < 0.5
),
elegidos AS (
    SELECT pm.id_usuario, c.nombre, c.dosis, c.intervalo_horas, c.hora
    FROM pm
    CROSS JOIN LATERAL (SELECT * FROM catalogo ORDER BY random() LIMIT pm.n_meds) AS c
)
INSERT INTO medicamento (id_persona_mayor, nombre, dosis, frecuencia, intervalo_horas, hora,
                          fecha_inicio, fecha_fin, proxima_toma, ultima_toma, activo)
SELECT id_usuario, nombre, dosis,
       'Cada ' || intervalo_horas || ' horas',
       intervalo_horas, hora,
       CURRENT_DATE - make_interval(days => 30 + floor(random() * 180)::int),
       CASE WHEN random() < 0.15
            THEN CURRENT_DATE - make_interval(days => floor(random() * 10)::int)
            ELSE NULL END,
       now() + make_interval(days => 2 + floor(random() * 5)::int, hours => floor(random() * 24)::int),
       now() - make_interval(hours => intervalo_horas),
       CASE WHEN random() < 0.15 THEN false ELSE true END
FROM elegidos;

-- ---------------------------------------------------------------------
-- 12. Actividades para las 5 organizaciones ficticias (36 en total,
--     con fechas repartidas entre pasado y futuro). Incluye descripcion,
--     hora, cupos y responsable: columnas agregadas al modelo después
--     de la primera versión de este script.
-- ---------------------------------------------------------------------
WITH orgs AS (
    SELECT id_organizacion, row_number() OVER (ORDER BY id_organizacion) AS rn
    FROM organizacion WHERE correo LIKE 'org%@vitaplus.test'
),
n_org AS (SELECT count(*) AS n FROM orgs),
plantillas AS (
    SELECT row_number() OVER () AS rn, nombre, tipo, descripcion FROM (VALUES
      ('Taller de manualidades',             'Manualidades',
       'Espacio para tejer, bordar y crear piezas decorativas guiado por un facilitador.'),
      ('Caminata ecológica',                 'Deportiva',
       'Recorrido corto y de bajo impacto por senderos cercanos, con pausas de hidratación.'),
      ('Charla de salud y bienestar',        'Salud y bienestar',
       'Charla informativa sobre hábitos saludables, prevención y cuidado personal.'),
      ('Encuentro espiritual',               'Espiritual',
       'Espacio de oración y reflexión comunitaria.'),
      ('Bingo comunitario',                  'Social',
       'Juego de bingo con premios sencillos para fomentar la integración.'),
      ('Clase de yoga suave',                'Deportiva',
       'Rutina de estiramientos y respiración adaptada a personas mayores.'),
      ('Jornada de vacunación',              'Salud y bienestar',
       'Aplicación de vacunas con apoyo de personal de salud.'),
      ('Celebración de cumpleaños del mes',  'Social',
       'Celebración conjunta de los cumpleaños del mes con torta y música.'),
      ('Taller de memoria',                  'Educativa',
       'Ejercicios y juegos para estimular la memoria y la concentración.'),
      ('Paseo recreativo',                   'Recreativa',
       'Salida grupal a un parque o punto de interés cercano.'),
      ('Grupo de lectura',                   'Educativa',
       'Lectura y comentario de cuentos, poemas o noticias en grupo.'),
      ('Noche de música y baile',            'Recreativa',
       'Encuentro con música en vivo o grabada para bailar y socializar.')
    ) AS v(nombre, tipo, descripcion)
)
INSERT INTO actividad (id_organizacion, nombre, descripcion, fecha, hora, lugar, tipo, cupos, responsable)
SELECT o.id_organizacion, p.nombre, p.descripcion,
       (CURRENT_DATE + make_interval(days => (floor(random() * 180) - 90)::int))::date,
       (ARRAY['08:00', '09:00', '10:00', '14:00', '15:00', '16:00'])[1 + floor(random() * 6)::int],
       (ARRAY['Usme Centro', 'La Flora', 'Yomasa', 'Santa Librada', 'Comuneros'])[1 + floor(random() * 5)::int],
       p.tipo,
       10 + floor(random() * 26)::int,
       (ARRAY['Sandra Melo', 'Diana Rojas', 'Patricia Uribe', 'Andrea Salazar', 'Camila Peña',
              'Julián Bermúdez', 'David Cortés', 'Andrés Franco', 'Felipe Aguilar', 'Daniela Reyes',
              'Marcela Duarte', 'Liliana Rico'])[1 + floor(random() * 12)::int]
FROM plantillas p
CROSS JOIN generate_series(1, 3) AS ronda
JOIN n_org ON true
JOIN orgs o ON o.rn = 1 + ((p.rn + ronda - 2) % n_org.n);

-- ---------------------------------------------------------------------
-- 13. Participación en actividades (tabla nueva desde la primera versión
--     de este script): inscripciones + asistencia.
--     - Solo se inscribe personas mayores cuya relación con la
--       organización dueña de la actividad esté ACEPTADA (si no, no
--       tendría sentido que aparezcan como participantes).
--     - Cada actividad llena entre 70% y 90% de sus cupos.
--     - asistio queda en NULL para actividades futuras (todavía no
--       ocurren, nadie ha podido asistir) y en true/false (85%/15%)
--       para las que ya pasaron.
-- ---------------------------------------------------------------------
WITH actividades_ficticias AS (
    SELECT a.id_actividad, a.id_organizacion, a.fecha,
           greatest(1, floor(a.cupos * (0.7 + random() * 0.2))::int) AS n_inscritos
    FROM actividad a
    JOIN organizacion o ON o.id_organizacion = a.id_organizacion
    WHERE o.correo LIKE 'org%@vitaplus.test'
),
candidatos AS (
    SELECT
        af.id_actividad, af.fecha, af.n_inscritos,
        pmo.id_persona_mayor,
        row_number() OVER (PARTITION BY af.id_actividad ORDER BY random()) AS rn
    FROM actividades_ficticias af
    JOIN persona_mayor_organizacion pmo
      ON pmo.id_organizacion = af.id_organizacion AND pmo.estado = 'ACEPTADA'
)
INSERT INTO participacion (id_persona_mayor, id_actividad, asistio)
SELECT id_persona_mayor, id_actividad,
       CASE WHEN fecha >= CURRENT_DATE THEN NULL
            WHEN random() < 0.85 THEN true
            ELSE false END
FROM candidatos
WHERE rn <= n_inscritos
ON CONFLICT DO NOTHING;

COMMIT;

-- =====================================================================
-- Resumen: corre esto después para confirmar qué quedó cargado
-- =====================================================================
SELECT 'personas_mayores' AS tabla, count(*) FROM usuario WHERE correo LIKE 'pm%@vitaplus.test'
UNION ALL SELECT 'acompanantes', count(*) FROM usuario WHERE correo LIKE 'acomp%@vitaplus.test'
UNION ALL SELECT 'voluntarios', count(*) FROM usuario WHERE correo LIKE 'vol%@vitaplus.test'
UNION ALL SELECT 'organizaciones_nuevas', count(*) FROM organizacion WHERE correo LIKE 'org%@vitaplus.test'
UNION ALL SELECT 'actividades', count(*) FROM actividad
    WHERE id_organizacion IN (SELECT id_organizacion FROM organizacion WHERE correo LIKE 'org%@vitaplus.test')
UNION ALL SELECT 'gustos_en_catalogo', count(*) FROM gusto
UNION ALL SELECT 'vinculos_persona_mayor_gusto', count(*) FROM persona_mayor_gusto
UNION ALL SELECT 'vinculos_persona_mayor_acompanante', count(*) FROM persona_mayor_acompanante
UNION ALL SELECT 'vinculos_persona_mayor_organizacion', count(*) FROM persona_mayor_organizacion
UNION ALL SELECT 'medicamentos', count(*) FROM medicamento
    WHERE id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'pm%@vitaplus.test')
UNION ALL SELECT 'vinculos_persona_mayor_organizacion_aceptados', count(*) FROM persona_mayor_organizacion
    WHERE estado = 'ACEPTADA'
      AND id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'pm%@vitaplus.test')
UNION ALL SELECT 'participaciones_en_actividades', count(*) FROM participacion
    WHERE id_persona_mayor IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'pm%@vitaplus.test');
