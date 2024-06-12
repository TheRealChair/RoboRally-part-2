-- Insert data into games table with a unique game_id value
INSERT INTO games (turn_id) VALUES (1);

-- Insert data into players table with references to the game_id from games table
INSERT INTO players (player_name, score, game_id) VALUES
('Player1', 0, 1);

INSERT INTO positions (game_game_id, player_player_id, position_x, position_y, heading)
VALUES (1, 1, 1, 1, 'south');