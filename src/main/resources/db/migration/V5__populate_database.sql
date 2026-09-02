-- Seed data: tech store categories + 30 products
-- Explicit IDs so product.category_id references stay stable across re-runs.
-- Prices are approximate MSRP and should be treated as placeholder data.

INSERT INTO categories (id, name)
VALUES (1, 'Laptops'),
       (2, 'Smartphones'),
       (3, 'Tablets'),
       (4, 'Audio'),
       (5, 'Monitors & Displays'),
       (6, 'Keyboards & Mice'),
       (7, 'Storage'),
       (8, 'Networking'),
       (9, 'Cameras'),
       (10, 'Gaming'),
       (11, 'Wearables'),
       (12, 'Power & Charging');

INSERT INTO products (name, price, `description`, category_id)
VALUES
-- Laptops
('Apple MacBook Air 13-inch (M4, 16GB, 256GB)', 1099.00,
 'Fanless ultraportable with the M4 chip, a 13.6-inch Liquid Retina display, and around 18 hours of battery life. Weighs just under 2.7 lbs.',
 1),
('Dell XPS 13 (Core Ultra 7, 16GB, 512GB)', 1299.99,
 'Compact Windows laptop with a 13.4-inch InfinityEdge display and a CNC-machined aluminum chassis. Includes an NPU for on-device AI features.',
 1),
('ASUS ROG Zephyrus G14 (Ryzen 9, RTX 4070)', 1599.99,
 'Slim 14-inch gaming laptop with an OLED 120Hz panel and a vapor chamber cooling system. Balances portability with discrete GPU performance.',
 1),

-- Smartphones
('Apple iPhone 16 Pro, 256GB', 1099.00,
 'Titanium frame, 6.3-inch ProMotion display, and a triple camera system with a 5x telephoto. Supports USB-C at USB 3 speeds.',
 2),
('Samsung Galaxy S25 Ultra, 256GB', 1299.99,
 'Flagship Android phone with a built-in S Pen, 200MP main sensor, and a 6.9-inch adaptive AMOLED display rated for 2600 nits peak brightness.',
 2),
('Google Pixel 9 Pro, 128GB', 999.00,
 'Runs on the Tensor G4 chip with seven years of OS and security updates. Known for computational photography features like Magic Eraser and Night Sight.',
 2),

-- Tablets
('Apple iPad Air 11-inch (M3, 128GB, Wi-Fi)', 599.00,
 'Mid-tier iPad with a laminated Liquid Retina display and support for the Apple Pencil Pro. Good balance of price and performance for note taking and light editing.',
 3),
('Samsung Galaxy Tab S10+ (256GB, Wi-Fi)', 999.99,
 'Large 12.4-inch AMOLED tablet with an IP68 rating and a bundled S Pen. DeX mode turns it into a desktop-style workspace.',
 3),

-- Audio
('Sony WH-1000XM6 Wireless Headphones', 449.99,
 'Over-ear headphones with industry-leading adaptive noise cancellation and up to 30 hours of playback. Multipoint pairing keeps two devices connected at once.',
 4),
('Apple AirPods Pro 3', 249.00,
 'In-ear buds with active noise cancellation, Transparency mode, and personalized spatial audio. Charging case supports USB-C, MagSafe, and Qi.',
 4),
('Sonos Era 100 Smart Speaker', 249.00,
 'Compact wireless speaker with stereo drivers and Trueplay room tuning. Works over Wi-Fi, Bluetooth, and line-in with the optional adapter.',
 4),

-- Monitors & Displays
('Dell UltraSharp U2723QE 27-inch 4K Monitor', 619.99,
 'IPS Black panel with a 2000:1 contrast ratio and 98% DCI-P3 coverage. Includes a built-in USB-C hub with 90W laptop charging.',
 5),
('LG UltraGear 27GR95QE 27-inch OLED Gaming Monitor', 799.99,
 'QHD OLED panel running at 240Hz with a 0.03ms response time. Near-instant pixel transitions and true blacks for competitive play.',
 5),
('Samsung Odyssey G9 49-inch Ultrawide', 1299.99,
 'Super ultrawide 32:9 curved display equivalent to two 27-inch QHD monitors side by side. 240Hz refresh with a 1000R curvature.',
 5),

-- Keyboards & Mice
('Logitech MX Master 3S Wireless Mouse', 99.99,
 'Ergonomic productivity mouse with an 8K DPI sensor and near-silent clicks. The MagSpeed scroll wheel shifts between ratcheted and free-spin modes.',
 6),
('Keychron K2 HE Wireless Mechanical Keyboard', 219.00,
 'Hall effect magnetic switches with adjustable actuation points and rapid trigger. Hot-swappable, 75% layout, works wired or over Bluetooth.',
 6),
('Logitech MX Keys S Wireless Keyboard', 109.99,
 'Low-profile full-size keyboard with spherically dished keys and smart backlighting. Pairs with up to three devices and charges over USB-C.',
 6),

-- Storage
('Samsung 990 PRO 2TB NVMe SSD', 179.99,
 'PCIe 4.0 internal drive with sequential reads up to 7450 MB/s. Includes a nickel-coated controller and heat spreader for thermal control.',
 7),
('SanDisk Extreme Portable SSD 1TB', 109.99,
 'Pocket-sized external drive rated IP65 for dust and water resistance. Reads up to 1050 MB/s over USB-C and survives a three-meter drop.',
 7),

-- Networking
('TP-Link Deco XE75 Mesh Wi-Fi 6E System, 3-Pack', 279.99,
 'Tri-band mesh system covering up to 7200 square feet with a dedicated 6GHz backhaul. Each unit has two gigabit Ethernet ports.',
 8),
('Ubiquiti UniFi Express Gateway', 149.00,
 'Compact all-in-one gateway and Wi-Fi 6 access point that runs the UniFi Network controller locally. Supports up to four additional UniFi devices.',
 8),

-- Cameras
('Sony Alpha a7 IV Mirrorless Camera (Body Only)', 2498.00,
 'Full-frame 33MP sensor with 759 phase-detect autofocus points and 10-bit 4K60 video. Fully articulating touchscreen and dual card slots.',
 9),
('DJI Osmo Pocket 3', 519.00,
 'Handheld gimbal camera with a 1-inch sensor and a rotating 2-inch touchscreen. Shoots stabilized 4K120 footage without any external rig.',
 9),

-- Gaming
('Sony PlayStation 5 Slim (Disc Edition)', 499.99,
 'Console with a custom AMD RDNA 2 GPU, 1TB SSD, and support for 4K120 output. Includes the DualSense controller with haptic feedback.',
 10),
('Nintendo Switch 2', 449.99,
 'Hybrid handheld and docked console with a 7.9-inch 1080p HDR screen and magnetic Joy-Con 2 controllers. Backward compatible with most Switch titles.',
 10),
('Valve Steam Deck OLED 512GB', 549.00,
 'Handheld gaming PC running SteamOS with a 7.4-inch HDR OLED display at 90Hz. Runs most of a Steam library natively via Proton.',
 10),

-- Wearables
('Apple Watch Series 10, 46mm GPS', 429.00,
 'Thinnest Apple Watch yet with a wide-angle OLED display and depth and water temperature sensors. Fast charges to 80% in about 30 minutes.',
 11),
('Garmin Forerunner 265 GPS Running Watch', 449.99,
 'AMOLED training watch with multi-band GPS and daily readiness scoring. Around 13 days of battery in smartwatch mode.',
 11),

-- Power & Charging
('Anker 737 Power Bank (PowerCore 24K)', 149.99,
 '24000mAh battery delivering up to 140W over USB-C, enough to fast charge most laptops. A smart display shows real-time wattage and remaining capacity.',
 12),
('Anker Prime 100W GaN Wall Charger, 3-Port', 79.99,
 'Compact gallium nitride charger with two USB-C ports and one USB-A. Splits power intelligently across a laptop, phone, and earbuds at once.',
 12);