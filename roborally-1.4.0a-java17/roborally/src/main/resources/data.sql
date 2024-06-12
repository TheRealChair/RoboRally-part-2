
-- Insert data into games table with unique game_id values
INSERT INTO games (no_of_players, board_id) VALUES
(2, 1),
(3, 1),
(4, 1);

INSERT INTO players (score, game_id, game_player_id) VALUES
(0, 1, 1),
(0, 1, 2),
(0, 1, 3);