DO $$
DECLARE
    num_processors INT := 5;
    num_graphic_cards INT := 5;
    num_power_supplies INT := 5;
    num_motherboards INT := 5;
    num_rams INT := 5;
    num_coolers INT := 5;
    num_cases INT := 5;
BEGIN
	-- Вставка случайных процессоров
	INSERT INTO processors (name, brand, cores, threads, base_clock, boost_clock, link)
	SELECT 
		'Processor ' || gs AS name,
		(ARRAY['Intel', 'AMD', 'Qualcomm', 'Apple', 'IBM', 'Arm', 'Via', 'NVIDIA', 'Celeron', 'Pentium'])[floor(random() * 10) + 1] AS brand,
		(floor(random() * 16) + 1)::int AS cores,
		(floor(random() * 32) + 1)::int AS threads,
		(random() * 4 + 1)::decimal(5,2) AS base_clock,
		(random() * 5 + 1)::decimal(5,2) AS boost_clock,
		'https://example.com/processor/' || gs
	FROM generate_series(1, num_processors) AS gs;
	
	-- Вставка случайных видеокарт
	INSERT INTO graphic_cards (name, brand, memory_size, memory_type, link)
	SELECT 
		'Graphic Card ' || gs AS name,
		(ARRAY['NVIDIA', 'AMD', 'Intel', 'EVGA', 'ASUS', 'Gigabyte', 'MSI', 'Sapphire', 'ZOTAC', 'Palit'])[floor(random() * 10) + 1] AS brand,
		(floor(random() * 16) + 1)::int * 2 AS memory_size,
		CASE WHEN random() < 0.5 THEN 'GDDR6' ELSE 'GDDR5' END AS memory_type,
		'https://example.com/graphic_card/' || gs
	FROM generate_series(1, num_graphic_cards) AS gs;
	
	-- Вставка случайных блоков питания
	INSERT INTO power_supplies (name, brand, wattage, efficiency_rating, link)
	SELECT 
		'Power Supply ' || gs AS name,
		(ARRAY['Corsair', 'EVGA', 'Thermaltake', 'Seasonic', 'Cooler Master', 
			   'Antec', 'be quiet!', 'Silverstone', 'FSP', 
			   'Fractal Design'])[floor(random() * 10) + 1] AS brand,
		(floor(random() * 1000) + 300)::int AS wattage,
		CASE WHEN random() < 0.7 THEN '80 Plus Bronze' ELSE '80 Plus Gold' END AS efficiency_rating,
		'https://example.com/power_supply/' || gs
	FROM generate_series(1, num_power_supplies) AS gs;
	
	-- Вставка случайных материнских плат
	INSERT INTO motherboards (name, brand, socket_type, form_factor, max_memory, link)
	SELECT 
		'Motherboard ' || gs AS name,
		(ARRAY['ASUS', 'Gigabyte', 'MSI', 'ASRock', 
			   'Biostar', 'EVGA', 'Intel', 
			   'Supermicro', 'Razer', 
			   'Acer'])[floor(random() * 10) + 1] AS brand,
		(ARRAY['LGA1151','AM4','LGA1200','LGA1700','Socket TR4'])[floor(random() * 5) + 1] AS socket_type,
		(ARRAY['ATX','Micro ATX','Mini ITX'])[floor(random() * 3) + 1] AS form_factor,
		(floor(random() * 128) + 8)::int AS max_memory,
		'https://example.com/motherboard/' || gs
	FROM generate_series(1, num_motherboards) AS gs;
	
	-- Вставка случайной оперативной памяти
	INSERT INTO rams (name, brand, capacity, speed, link)
	SELECT 
		'RAM Module ' || gs AS name,
		(ARRAY['G.Skill', 'Corsair', 
			   'Crucial', 'Kingston',
			   'HyperX', 
			   'Patriot', 
			   'Team Group',
			   'V-Color', 
			   'ADATA', 
			   'Mushkin'])[floor(random() * 10) + 1] AS brand,
		(floor(random() * 64) + 4)::int AS capacity,
		(floor(random() * 3200) + 2400)::int AS speed,
		'https://example.com/ram/' || gs
	FROM generate_series(1, num_rams) AS gs;
	
	-- Вставка случайных кулеров
	INSERT INTO coolers (name, brand, type, cooling_capacity, link)
	SELECT 
		'Cooler ' || gs AS name,
		(ARRAY['Noctua', 'Cooler Master', 'be quiet!', 'Thermaltake', 'NZXT', 
			   'Corsair', 'Deepcool', 'ARCTIC', 'Fractal Design', 'Scythe'])[floor(random() * 10) + 1] AS brand,
		CASE WHEN random() < 0.5 THEN 'Air' ELSE 'Liquid' END AS type,
		(random() * 50 + 10)::decimal(5,2) AS cooling_capacity,
		'https://example.com/cooler/' || gs
	FROM generate_series(1, num_coolers) AS gs;
	
	-- Вставка случайных корпусов
	INSERT INTO cases (name, brand, form_factor, color, link)
	SELECT 
	   'Case ' || gs AS name,
	   (ARRAY['NZXT', 'Fractal Design', 'Corsair', 'Thermaltake', 'Cooler Master', 
			  'Phanteks', 'Lian Li', 'be quiet!', 'Silverstone', 'InWin'])[floor(random() * 10) + 1] AS brand,
	   CASE WHEN random() < 0.5 THEN 'Mid Tower' ELSE 'Full Tower' END AS form_factor,
	   CASE WHEN random() < 0.5 THEN 'Black' ELSE 'White' END AS color,
	   'https://example.com/case/' || gs
	FROM generate_series(1, num_cases) AS gs;
END $$;








-- ФУНКЦИЯ СОЗДАНИЯ СЛУЧАЙНЫХ КОМПЬЮТЕРОВ
CREATE OR REPLACE FUNCTION generate_random_computer() 
RETURNS VOID AS $$
DECLARE
    new_computer_id INT;
	rounded_price INT;
BEGIN
    new_computer_id := nextval('computers_id_seq');
	rounded_price := (floor((random() * 10000 + 2000) / 10) * 10)::int;
	
    INSERT INTO computers (id, name, description, price, processor_id, graphic_card_id, motherboard_id, ram_id, rams_count, power_supply_id, cooler_id, case_id, image_url, stock_quantity)
    VALUES (
        new_computer_id,
        'Computer ' || new_computer_id,
        'Description for Computer ' || new_computer_id,
        rounded_price::decimal(10,2),
        (SELECT id FROM processors ORDER BY random() LIMIT 1),
        (SELECT id FROM graphic_cards ORDER BY random() LIMIT 1),
        (SELECT id FROM motherboards ORDER BY random() LIMIT 1),
        (SELECT id FROM rams ORDER BY random() LIMIT 1),
        (floor(random() * 4) + 1)::int,
        (SELECT id FROM power_supplies ORDER BY random() LIMIT 1),
        (SELECT id FROM coolers ORDER BY random() LIMIT 1),
        (SELECT id FROM cases ORDER BY random() LIMIT 1),
        'https://example.com/computer/' || new_computer_id,
        (floor(random() * 20))::int
    );
END;
$$
 LANGUAGE plpgsql;


-- Вставка случайных пк
DO $$
DECLARE
	num_computers INT:=20;
BEGIN
    FOR i IN 1..num_computers LOOP
        PERFORM generate_random_computer();
    END LOOP;
END $$;
