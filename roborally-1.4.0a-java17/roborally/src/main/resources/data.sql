-- Insert data into games table with a unique game_id value
INSERT INTO games (turn_id) VALUES
(1);

-- Insert data into players table with references to the game_id from games table
INSERT INTO players (game_id, 1) VALUES
(1, 1);
