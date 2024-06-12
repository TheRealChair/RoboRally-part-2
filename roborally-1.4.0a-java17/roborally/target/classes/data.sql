-- Insert data into games table with a unique game_id value
INSERT INTO games (no_of_players, board_id) VALUES
(1, 1);
-- Insert data into players table with references to the game_id from games table
INSERT INTO players (game_id, game_player_id) VALUES
(1, 1);
