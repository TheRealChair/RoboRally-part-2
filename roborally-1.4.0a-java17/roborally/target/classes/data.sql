
-- Insert data into games table with unique game_id values
INSERT INTO games (no_of_players, board_id) VALUES
(2, 1),
(3, 1),
(4, 1);

INSERT INTO players (player_name, score, game_id) VALUES
('Player1', 0, 1),
('Player2', 0, 1),
('Player3', 0, 1);