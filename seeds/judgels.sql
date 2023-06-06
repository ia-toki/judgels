SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `judgels`
--

--
-- Dumping data for table `jophiel_user`
--

INSERT INTO `jophiel_user` (`jid`, `username`, `email`, `password`, `avatarFilename`, `createdAt`, `updatedAt`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'andi', 'andi@jophiel.judgels', '1000:060ce62731682f2a43de103418b1afad8af98bd6f399b569:cbe47b1335de632a21c7a4b56e04214b7c3a349d120867b3', NULL, '2018-08-05 17:03:33.504', '2018-08-05 17:11:29.127'),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'budi', 'budi@jophiel.judgels', '1000:cdbd48d2ab492347a83ba0e8351ff4068f8109d7f77a371a:277f5b263e855fcd59cf3006f7b1c5bfac3de02ba24452f5', NULL, '2018-08-05 17:15:23.586', '2018-08-05 17:15:23.586'),
('JIDUSERVfszUmquMh0Ae2laygDp', 'caca', 'caca@jophiel.judgels', '1000:f6d2c169f4ed162e7078065e3c83baee8e07c80eb4c3ab19:66660990bc19491f9b60e6bf0912d74da45c94da524750b4', NULL, '2018-08-05 17:16:27.740', '2018-08-05 17:16:27.740'),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'dudi', 'dudi@jophiel.judgels', '1000:f35e1730d05f8c600a64ac56ecf609d639471dffa3223be4:c176c95c192f52a93d49c14f2cd9506b737a8e4188aee621', NULL, '2018-08-05 17:16:27.740', '2018-08-05 17:16:27.740'),
('JIDUSERXCcE36qmrr8Tm46k3hSV', 'emir', 'emir@jophiel.judgels', '1000:72f3edcab574f0b1b814dd1fb761ad8a8d1bfe6298f0354f:cec5a264c4bd34da306271aead1678155577b75a61c6579c', NULL, '2018-08-05 17:16:27.740', '2018-08-05 17:16:27.740'),
('JIDUSERP1gi91MNc1XbXLbPx3dq', 'fuad', 'fuad@jophiel.judgels', '1000:4da2460d8838a109ef8dde5f8183e6d958e2a4a43a0bb578:9cf73cfec1faea4b651cfdb1bafd6f5e3a25fb6385166dc6', NULL, '2018-08-05 17:16:27.740', '2018-08-05 17:16:27.740');

--
-- Dumping data for table `jophiel_user_info`
--

INSERT INTO `jophiel_user_info` (`userJid`, `name`, `gender`, `country`, `homeAddress`, `shirtSize`, `institutionName`, `institutionCountry`, `institutionProvince`, `institutionCity`, `createdAt`, `updatedAt`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'Andi', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:03:33.565', '2018-08-05 17:14:56.338'),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'Budi', 'MALE', 'US', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:15:23.591', '2018-08-05 17:16:07.734'),
('JIDUSERVfszUmquMh0Ae2laygDp', 'Caca', 'FEMALE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', '2018-08-05 17:18:18.115'),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'Dudi', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', '2018-08-05 17:18:18.115'),
('JIDUSERXCcE36qmrr8Tm46k3hSV', 'Emir', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', '2018-08-05 17:18:18.115'),
('JIDUSERP1gi91MNc1XbXLbPx3dq', 'Fuad', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', '2018-08-05 17:18:18.115');

--
-- Dumping data for table `jophiel_user_rating`
--

INSERT INTO `jophiel_user_rating` (`time`, `userJid`, `hiddenRating`, `publicRating`, `createdAt`, `createdBy`) VALUES
('1970-01-18 20:30:43.200', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 2300, 2300, '2018-08-05 18:30:24.011', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('1970-01-18 20:30:43.200', 'JIDUSER7uMucIkm1exJTu7sJvxR', 3100, 3100, '2018-08-05 18:30:24.032', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('1970-01-18 20:30:43.200', 'JIDUSERVfszUmquMh0Ae2laygDp', 1700, 1700, '2018-08-05 18:30:24.033', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('1970-01-18 20:30:43.200', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 1651, 1651, '2018-08-05 18:30:24.033', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('1970-01-18 20:30:43.200', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 1800, 1800, '2018-08-05 18:30:24.033', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('1970-01-18 20:30:43.200', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 2600, 2600, '2018-08-05 18:30:24.033', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jophiel_user_rating_event`
--

INSERT INTO `jophiel_user_rating_event` (`time`, `eventJid`, `createdAt`, `createdBy`) VALUES
('1970-01-18 20:30:43.200', 'JIDCONT89YMatMq7GqaPQCyyUHU', '2018-08-05 18:30:23.968', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest`
--

INSERT INTO `uriel_contest` (`jid`, `slug`, `name`, `style`, `description`, `beginTime`, `duration`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'joc-1', 'Judgels Open Contest #1', 'ICPC', '<p>Welcome to the first Judgels open contest!</p>\r\n', '2020-01-01 12:00:00.000', 18000000000, '2018-08-05 17:30:35.297', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:15:28.710', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'joc-old', 'Judgels Old Contest', 'IOI', '<p>This is an old contest.</p>\r\n', '2018-01-01 00:00:00.000', 18000000, '2018-08-05 18:11:40.024', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:35:50.263', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'joq', 'Judgels Open Quiz', 'BUNDLE', '', '2020-01-01 07:00:00.000', 86400000000, '2020-01-18 19:20:22.052', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:12.435', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest_announcement`
--

INSERT INTO `uriel_contest_announcement` (`jid`, `contestJid`, `title`, `content`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOANOtLvAkjKzbAw3vWaZI3t', 'JIDCONTYsWkzwPy8wCF3maJfxqN', 'Good luck', '<p>And have fun!</p>\r\n', 'PUBLISHED', '2018-08-05 17:39:58.816', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 17:39:58.816', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest_clarification`
--

INSERT INTO `uriel_contest_clarification` (`jid`, `contestJid`, `topicJid`, `title`, `question`, `answer`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOCLskqUDQZdvWIRbKgNDFFP', 'JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDCONTYsWkzwPy8wCF3maJfxqN', 'Lunch', 'Will lunch be provided?', 'Yes.', 'ANSWERED', '2018-08-05 18:24:04.866', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 18:24:26.258', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest_contestant`
--

INSERT INTO `uriel_contest_contestant` (`contestJid`, `userJid`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERVfszUmquMh0Ae2laygDp', 'APPROVED', '2018-08-05 17:32:08.186', 'JIDUSERVfszUmquMh0Ae2laygDp', '2018-08-05 17:32:08.186', 'JIDUSERVfszUmquMh0Ae2laygDp'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 'APPROVED', '2018-08-05 17:32:25.052', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2018-08-05 17:32:25.052', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSER7uMucIkm1exJTu7sJvxR', 'APPROVED', '2018-08-05 17:32:38.079', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 17:32:38.079', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 'APPROVED', '2018-08-20 15:15:21.928', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:15:21.928', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 'APPROVED', '2018-08-20 15:15:26.677', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:15:26.677', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 'APPROVED', '2018-08-20 15:15:28.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:15:28.708', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSER7uMucIkm1exJTu7sJvxR', 'APPROVED', '2018-08-20 15:17:43.929', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:17:43.929', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 'APPROVED', '2018-08-20 15:17:47.499', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:17:47.499', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERVfszUmquMh0Ae2laygDp', 'APPROVED', '2018-08-20 15:17:50.373', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:17:50.373', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 'APPROVED', '2018-08-20 15:17:53.406', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:17:53.406', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 'APPROVED', '2018-08-20 15:17:56.161', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:17:56.161', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 'APPROVED', '2018-08-20 15:17:59.518', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:17:59.518', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDUSERVfszUmquMh0Ae2laygDp', 'APPROVED', '2020-01-18 19:21:24.490', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:24.490', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 'APPROVED', '2020-01-18 19:21:24.493', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:24.493', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 'APPROVED', '2020-01-18 19:21:24.495', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:24.495', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 'APPROVED', '2020-01-18 19:21:24.496', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:24.496', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 'APPROVED', '2020-01-18 19:21:24.498', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:24.498', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDUSER7uMucIkm1exJTu7sJvxR', 'APPROVED', '2020-01-18 19:21:24.500', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:21:24.500', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest_module`
--

INSERT INTO `uriel_contest_module` (`contestJid`, `name`, `config`, `enabled`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'CLARIFICATION', '{}', b'1', '2018-08-05 19:59:23.330', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:23.330', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'SCOREBOARD', '{\"isIncognitoScoreboard\":false}', b'1', '2018-08-05 19:59:25.602', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:25.602', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'SUPERVISOR', '{}', b'1', '2018-08-05 19:59:28.084', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:28.084', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'REGISTRATION', '{\"registerStartTime\":1533473972812,\"manualApproval\":false,\"registerDuration\":432000000,\"maxRegistrants\":0}', b'1', '2018-08-05 19:59:32.812', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:32.812', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'CLARIFICATION', '{}', b'1', '2018-08-05 19:59:50.281', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:50.281', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'SUPERVISOR', '{}', b'1', '2018-08-05 19:59:52.148', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:52.148', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'SCOREBOARD', '{\"isIncognitoScoreboard\":false}', b'1', '2018-08-05 19:59:54.004', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:54.004', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'REGISTRATION', '{\"registerStartTime\":1533473997175,\"manualApproval\":false,\"registerDuration\":432000000,\"maxRegistrants\":0}', b'1', '2018-08-05 19:59:57.175', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 19:59:57.175', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest_problem`
--

INSERT INTO `uriel_contest_problem` (`contestJid`, `problemJid`, `alias`, `submissionsLimit`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'A', 0, 'OPEN', '2018-08-20 00:00:00.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 00:00:00.000', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'B', 0, 'OPEN', '2018-08-20 00:00:00.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 00:00:00.000', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'A', 0, 'OPEN', '2018-08-20 15:23:43.499', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:23:43.499', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'B', 0, 'OPEN', '2018-08-20 15:24:55.672', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-20 15:24:55.672', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'A', 0, 'OPEN', '2020-01-18 19:24:07.036', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:24:07.036', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', 'JIDBUNDKkmglB5WGpSyTTFGScVk', 'B', 0, 'OPEN', '2020-01-18 19:24:07.038', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:24:07.038', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `uriel_contest_scoreboard`
--

INSERT INTO `uriel_contest_scoreboard` (`contestJid`, `scoreboard`, `type`, `createdAt`, `updatedAt`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', '{\"state\":{\"problemJids\":[\"JIDPROGSxtSvuGAW4IKreFasSY0\",\"JIDPROGme6wb9DCQPCKJBEPO4qR\"],\"problemAliases\":[\"A\",\"B\"],\"contestantJids\":[\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"JIDUSERVfszUmquMh0Ae2laygDp\",\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"JIDUSERP1gi91MNc1XbXLbPx3dq\"]},\"content\":{\"entries\":[{\"rank\":1,\"contestantJid\":\"JIDUSERP1gi91MNc1XbXLbPx3dq\",\"totalAccepted\":2,\"totalPenalties\":30,\"lastAcceptedPenalty\":20,\"attemptsList\":[1,1],\"penaltyList\":[10,20],\"problemStateList\":[2,1]},{\"rank\":2,\"contestantJid\":\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"totalAccepted\":2,\"totalPenalties\":31,\"lastAcceptedPenalty\":15,\"attemptsList\":[1,1],\"penaltyList\":[16,15],\"problemStateList\":[1,2]},{\"rank\":3,\"contestantJid\":\"JIDUSERVfszUmquMh0Ae2laygDp\",\"totalAccepted\":1,\"totalPenalties\":50,\"lastAcceptedPenalty\":30,\"attemptsList\":[2,1],\"penaltyList\":[30,15],\"problemStateList\":[1,0]},{\"rank\":4,\"contestantJid\":\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"totalAccepted\":0,\"totalPenalties\":0,\"lastAcceptedPenalty\":0,\"attemptsList\":[2,0],\"penaltyList\":[0,0],\"problemStateList\":[0,0]},{\"rank\":4,\"contestantJid\":\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"totalAccepted\":0,\"totalPenalties\":0,\"lastAcceptedPenalty\":0,\"attemptsList\":[0,0],\"penaltyList\":[0,0],\"problemStateList\":[0,0]},{\"rank\":4,\"contestantJid\":\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"totalAccepted\":0,\"totalPenalties\":0,\"lastAcceptedPenalty\":0,\"attemptsList\":[0,0],\"penaltyList\":[0,0],\"problemStateList\":[0,0]}]}}', 'OFFICIAL', '2018-08-20 00:00:00.000', '2018-08-20 15:41:43.186'),
('JIDCONT89YMatMq7GqaPQCyyUHU', '{\"state\":{\"problemJids\":[\"JIDPROGSxtSvuGAW4IKreFasSY0\",\"JIDPROGw21Ya31fwCuEnNJLlVwO\"],\"problemAliases\":[\"A\",\"B\"],\"contestantJids\":[\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"JIDUSERVfszUmquMh0Ae2laygDp\",\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"JIDUSERP1gi91MNc1XbXLbPx3dq\"]},\"content\":{\"entries\":[{\"rank\":1,\"contestantJid\":\"JIDUSERP1gi91MNc1XbXLbPx3dq\",\"scores\":[100,100],\"totalScores\":200,\"lastAffectingPenalty\":0},{\"rank\":2,\"contestantJid\":\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"scores\":[100,50],\"totalScores\":150,\"lastAffectingPenalty\":0},{\"rank\":3,\"contestantJid\":\"JIDUSERVfszUmquMh0Ae2laygDp\",\"scores\":[75,0],\"totalScores\":75,\"lastAffectingPenalty\":0},{\"rank\":4,\"contestantJid\":\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"scores\":[0,null],\"totalScores\":0,\"lastAffectingPenalty\":0},{\"rank\":4,\"contestantJid\":\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"scores\":[null,null],\"totalScores\":0,\"lastAffectingPenalty\":0},{\"rank\":4,\"contestantJid\":\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"scores\":[null,null],\"totalScores\":0,\"lastAffectingPenalty\":0}]}}', 'OFFICIAL', '2018-08-20 15:33:27.529', '2018-08-20 15:35:43.211'),
('JIDCONTLaAppdjyu94dGgzpnKqY', '{\"content\":{\"entries\":[{\"answeredItems\":[0,0],\"totalAnsweredItems\":0,\"lastAnsweredTime\":null,\"rank\":1,\"contestantJid\":\"JIDUSERP1gi91MNc1XbXLbPx3dq\"},{\"answeredItems\":[0,0],\"totalAnsweredItems\":0,\"lastAnsweredTime\":null,\"rank\":1,\"contestantJid\":\"JIDUSER5QiWnVJWzKdjtWtgEfN3\"},{\"answeredItems\":[0,0],\"totalAnsweredItems\":0,\"lastAnsweredTime\":null,\"rank\":1,\"contestantJid\":\"JIDUSERDMZOs8UHqjsw9DGbB31z\"},{\"answeredItems\":[0,0],\"totalAnsweredItems\":0,\"lastAnsweredTime\":null,\"rank\":1,\"contestantJid\":\"JIDUSER7uMucIkm1exJTu7sJvxR\"},{\"answeredItems\":[0,0],\"totalAnsweredItems\":0,\"lastAnsweredTime\":null,\"rank\":1,\"contestantJid\":\"JIDUSERXCcE36qmrr8Tm46k3hSV\"},{\"answeredItems\":[0,0],\"totalAnsweredItems\":0,\"lastAnsweredTime\":null,\"rank\":1,\"contestantJid\":\"JIDUSERVfszUmquMh0Ae2laygDp\"}]},\"state\":{\"problemJids\":[\"JIDBUNDcdQcvQ4sB6RgNQ9ryOsp\",\"JIDBUNDKkmglB5WGpSyTTFGScVk\"],\"problemAliases\":[\"A\",\"B\"],\"problemPoints\":null}}', 'OFFICIAL', '2020-01-18 19:20:57.398', '2020-01-18 19:24:57.634');

--
-- Dumping data for table `uriel_contest_style`
--

INSERT INTO `uriel_contest_style` (`contestJid`, `config`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', '{\"wrongSubmissionPenalty\":20,\"languageRestriction\":{\"allowedLanguageNames\":[]}}', '2018-08-05 17:30:35.327', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 17:30:35.327', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONT89YMatMq7GqaPQCyyUHU', '{\"usingLastAffectingPenalty\":false,\"languageRestriction\":{\"allowedLanguageNames\":[]}}', '2018-08-05 18:11:40.037', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 18:11:40.037', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTLaAppdjyu94dGgzpnKqY', '{\"languageRestriction\":{\"allowedLanguageNames\":[]},\"wrongSubmissionPenalty\":20}', '2020-01-18 19:20:22.068', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 19:20:22.068', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_archive`
--

INSERT INTO `jerahmeel_archive` (`jid`, `slug`, `parentJid`, `name`, `description`, `category`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDARCHElTynLFh3mPVc9d7oWFx', 'ioi', 'JIRARCHoEi3TLoO8jHt0HLMded3', 'IOI', '<p>International Olympiad in Informatics</p>', '1. International', '2019-11-17 15:55:10.000', NULL, '2019-11-17 15:55:10.000', NULL),
('JIDARCHxmGjLvIwXXdqFhngdOrG', 'icpc', 'JIDARCHoEi3TLoO8jHt0HLMded3', 'ICPC', '<p>International Collegiate Programming Contest</p>', '2. Regional', '2019-11-17 15:55:10.000', NULL, '2019-11-17 15:55:10.000', NULL);

--
-- Dumping data for table `jerahmeel_bundle_item_submission`
--

INSERT INTO `jerahmeel_bundle_item_submission` (`jid`, `containerJid`, `problemJid`, `itemJid`, `answer`, `verdict`, `score`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSUBBjr6rIH2fk4WaODaWCZAR', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEM985TWzImiwQqpFmXQsYQ', 'c', 'WRONG_ANSWER', 0, '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 14:48:17.248', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBClkHtTsGXinIa4Oi2HwZ', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEMIXQK2X9taO1LGBDVjR7N', 'c', 'ACCEPTED', 1, '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBoZmJhRwp4wqBVUYXrrDX', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEM985TWzImiwQqpFmXQsYQ', 'a', 'ACCEPTED', 1, '2020-01-18 22:06:34.537', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:34.537', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBjOIrJy20baoiq8LtBcUJ', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'JIDITEMIXQK2X9taO1LGBDVjR7N', 'c', 'ACCEPTED', 1, '2020-01-18 22:06:36.494', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:36.494', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBBk6IvVwfmVEB4Cg971RFe', 'JIDSESSx2sksHzyQNHvMn0yBzLr', 'JIDBUNDKkmglB5WGpSyTTFGScVk', 'JIDITEM9lipxqeitzP6ujFyp3YE', '2', 'ACCEPTED', 1, '2020-01-18 22:29:56.651', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:29:56.651', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_bundle_submission`
--

INSERT INTO `jerahmeel_bundle_submission` (`jid`, `containerJid`, `problemJid`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSUBMiaG02OaXHle61qVwPiaJ', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', '2019-12-23 12:57:00.420', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:57:00.420', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMyPVOA50XhAtsjYgvJ44u', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', '2019-12-23 12:58:00.060', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:58:00.060', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMntVc1WVBgzoBQAtjGoEc', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-23 12:58:12.708', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_chapter`
--

INSERT INTO `jerahmeel_chapter` (`jid`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSESSjboijeXaXam4wM7zwIkA', 'Input/Output', '<p>This is input/output</p>\r\n', '2019-10-19 18:52:44.931', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-19 18:52:44.931', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSpB4nAdte4tUwgSiihTFq', 'Branching', '', '2020-01-18 17:02:07.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 17:02:07.000', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSx2sksHzyQNHvMn0yBzLr', 'Review', '', '2020-01-18 17:02:53.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 17:02:53.000', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSnDc5JXrqXDUdPgqhrZXQ', 'Pre-Quiz', '', '2020-01-18 17:04:05.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 17:04:05.000', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_chapter_lesson`
--

INSERT INTO `jerahmeel_chapter_lesson` (`chapterJid`, `lessonJid`, `alias`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDLESSBQMkZlaJlfafbwDLHoP1', '1', '2019-10-20 20:50:02.112', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-20 20:50:02.112', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_chapter_problem`
--

INSERT INTO `jerahmeel_chapter_problem` (`chapterJid`, `problemJid`, `type`, `alias`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'PROGRAMMING', 'A', '2019-10-24 22:17:26.071', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-24 22:17:26.071', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'BUNDLE', 'X', '2019-10-29 07:37:25.180', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-29 07:37:25.180', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'PROGRAMMING', 'B', '2019-10-29 07:37:25.180', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-29 07:37:25.180', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSpB4nAdte4tUwgSiihTFq', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'PROGRAMMING', 'A', '2019-10-24 22:17:26.071', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-24 22:17:26.071', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSESSx2sksHzyQNHvMn0yBzLr', 'JIDBUNDKkmglB5WGpSyTTFGScVk', 'BUNDLE', 'X', '2019-10-29 07:37:25.180', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-29 07:37:25.180', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_course`
--

INSERT INTO `jerahmeel_course` (`jid`, `slug`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOURElTynLFh3mPVc9d7oWFx', 'basic', 'Basic Programming', '<p>Lorem ipsum dolor sit amet</p>', '2019-10-19 14:07:15.000', NULL, '2019-10-19 14:07:15.000', NULL),
('JIDCOURa19cx8dp4uVJabPcPsWN', 'competitive', 'Basic Competitive Programming', '', '2019-10-19 15:24:02.000', NULL, '2019-10-19 15:24:02.000', NULL);

--
-- Dumping data for table `jerahmeel_course_chapter`
--

INSERT INTO `jerahmeel_course_chapter` (`courseJid`, `chapterJid`, `alias`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOURElTynLFh3mPVc9d7oWFx', 'JIDSESSjboijeXaXam4wM7zwIkA', 'A', '2019-10-19 18:53:03.789', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-19 18:53:03.789', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCOURElTynLFh3mPVc9d7oWFx', 'JIDSESSpB4nAdte4tUwgSiihTFq', 'B', '2019-10-19 18:53:03.789', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-19 18:53:03.789', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCOURElTynLFh3mPVc9d7oWFx', 'JIDSESSx2sksHzyQNHvMn0yBzLr', 'C', '2019-10-19 18:53:03.789', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-10-19 18:53:03.789', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_curriculum`
--

INSERT INTO `jerahmeel_curriculum` (`jid`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCURR6UjDpTowryGRWXvEJlap', 'Main', 'Report if you find any mistake!', '2019-11-02 19:32:36.391', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-11-02 19:32:36.391', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_problem_set`
--

INSERT INTO `jerahmeel_problem_set` (`jid`, `slug`, `archiveJid`, `name`, `description`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDPRSEjboijeXaXam4wM7zwIkA', 'ioi-2020', 'JIDARCHElTynLFh3mPVc9d7oWFx', 'IOI 2020', '<p>Indonesia</p>', '2019-11-15 23:15:01.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-11-15 23:15:01.000', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDPRSEqfVvs19a3IaOwWYE9M35', 'ioi-2021', 'JIDARCHElTynLFh3mPVc9d7oWFx', 'IOI 2021', '', '2019-11-15 23:15:01.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-11-15 23:15:01.000', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDPRSEWW7fhFFtagpfHEodXRJw', 'icpc-jakarta', 'JIDARCHxmGjLvIwXXdqFhngdOrG', 'ICPC Jakarta', '<p>Jakarta</p>', '2019-11-15 23:15:01.000', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-11-15 23:15:01.000', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_problem_set_problem`
--

INSERT INTO `jerahmeel_problem_set_problem` (`problemSetJid`, `problemJid`, `type`, `alias`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'PROGRAMMING', 'A', '2019-11-17 15:56:58.616', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-11-17 15:56:58.616', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'BUNDLE', 'B', '2019-11-27 20:55:12.768', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-11-27 20:55:12.768', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'PROGRAMMING', 'C', '2019-12-14 10:58:01.522', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-14 10:58:01.522', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDPRSEqfVvs19a3IaOwWYE9M35', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'PROGRAMMING', 'A', '2019-12-14 10:58:01.522', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2019-12-14 10:58:01.522', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `jerahmeel_programming_grading`
--

INSERT INTO `jerahmeel_programming_grading` (`jid`, `submissionJid`, `verdictCode`, `verdictName`, `score`, `details`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDGRAD7XfQiYn5JGLKGsfsTsnH', 'JIDSUBMV2em0Eo91zaYkmuA2mMw', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[]},{\"id\":-1,\"testCaseResults\":[]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:04:42.650', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:04:46.582', NULL),
('JIDGRADdOPhgipWlnCRMtyW7H9Y', 'JIDSUBMCQDtcyVEKvsM6XYO2Y3q', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":11},\"subtaskIds\":[0]}]},{\"id\":-1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":5},\"subtaskIds\":[-1]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[-1]}]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:05:02.056', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:05:02.957', NULL),
('JIDGRADhMOmFl1FfkSkkcF5I9uO', 'JIDSUBMFIBGlEGTQxpeZXtLoeTD', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"hello.cpp: In function \'int main()\':\\nhello.cpp:6:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%s\\\", s);\\n  ~~~~~^~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":324,\"wallTime\":11},\"subtaskIds\":[0,2]}]},{\"id\":1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":6},\"subtaskIds\":[1,2]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[1,2]}]},{\"id\":2,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[2]}]}],\"subtaskResults\":[{\"id\":1,\"verdict\":{\"code\":\"AC\"},\"score\":30.0},{\"id\":2,\"verdict\":{\"code\":\"AC\"},\"score\":70.0}]}', '2020-01-18 22:06:07.701', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:10.436', NULL),
('JIDGRADbDiq8ytMIRJfXeU3cBTI', 'JIDSUBM4KDniVOMVzMQJEMXNlqd', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":280,\"wallTime\":11},\"subtaskIds\":[0]}]},{\"id\":-1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":3,\"memory\":320,\"wallTime\":3},\"subtaskIds\":[-1]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":3},\"subtaskIds\":[-1]}]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:07:54.555', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:07:56.968', NULL),
('JIDGRAD0fZVnolN9V4j5fkOytDE', 'JIDSUBMsj2mVKUY2UqLiIMNEl7U', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":11},\"subtaskIds\":[0]}]},{\"id\":-1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":336,\"wallTime\":4},\"subtaskIds\":[-1]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"50.0\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":2},\"subtaskIds\":[-1]}]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:31:22.997', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:26.718', NULL),
('JIDGRADXj7p2LCEtmR5q7f7aptn', 'JIDSUBMaUyN8hIBErqKCenJUFHe', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[]},{\"id\":-1,\"testCaseResults\":[]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:31:52.410', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:53.579', NULL),
('JIDGRADyUgmy2sfVKBDOQv5tJEv', 'JIDSUBM5fPqSOLoRHsssdgMI93a', 'AC', '', 100, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[]},{\"id\":-1,\"testCaseResults\":[]}],\"subtaskResults\":[{\"id\":-1,\"verdict\":{\"code\":\"AC\"},\"score\":100.0}]}', '2020-01-18 22:32:27.443', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:32:30.756', NULL),
('JIDGRADMZyJb1TiAR0PuRQs1bjC', 'JIDSUBMxm6eXnTKxDj046l6yD7Z', 'WA', '', 30, '{\"compilationOutputs\":{\"source\":\"hello.cpp: In function \'int main()\':\\nhello.cpp:7:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%s\\\", s);\\n  ~~~~~^~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"WA\"},\"score\":\"X\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":324,\"wallTime\":10},\"subtaskIds\":[0,2]}]},{\"id\":1,\"testCaseResults\":[{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":320,\"wallTime\":5},\"subtaskIds\":[1,2]},{\"verdict\":{\"code\":\"AC\"},\"score\":\"*\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":356,\"wallTime\":2},\"subtaskIds\":[1,2]}]},{\"id\":2,\"testCaseResults\":[{\"verdict\":{\"code\":\"SKP\"},\"score\":\"?\",\"executionResult\":null,\"subtaskIds\":[2]}]}],\"subtaskResults\":[{\"id\":1,\"verdict\":{\"code\":\"AC\"},\"score\":30.0},{\"id\":2,\"verdict\":{\"code\":\"WA\"},\"score\":0.0}]}', '2020-01-18 22:33:20.351', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:33:22.349', NULL),
('JIDGRADgfq3fKHiz6ImhdzX9TYD', 'JIDSUBMtXWk5Sh2Vuw2KyoPVaeG', 'WA', '', 0, '{\"compilationOutputs\":{\"source\":\"A.cpp: In function \'int main()\':\\nA.cpp:5:18: warning: format \'%d\' expects a matching \'int*\' argument [-Wformat=]\\n  scanf(\\\"%d%d\\\", &a);\\n                  ^\\nA.cpp:5:7: warning: ignoring return value of \'int scanf(const char*, ...)\', declared with attribute warn_unused_result [-Wunused-result]\\n  scanf(\\\"%d%d\\\", &a);\\n  ~~~~~^~~~~~~~~~~~\\n\"},\"testDataResults\":[{\"id\":0,\"testCaseResults\":[{\"verdict\":{\"code\":\"WA\"},\"score\":\"X\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":324,\"wallTime\":10},\"subtaskIds\":[0,2]}]},{\"id\":1,\"testCaseResults\":[{\"verdict\":{\"code\":\"WA\"},\"score\":\"X\",\"executionResult\":{\"status\":\"ZERO_EXIT_CODE\",\"time\":2,\"memory\":336,\"wallTime\":7},\"subtaskIds\":[1,2]},{\"verdict\":{\"code\":\"SKP\"},\"score\":\"?\",\"executionResult\":null,\"subtaskIds\":[1,2]}]},{\"id\":2,\"testCaseResults\":[{\"verdict\":{\"code\":\"SKP\"},\"score\":\"?\",\"executionResult\":null,\"subtaskIds\":[2]}]}],\"subtaskResults\":[{\"id\":1,\"verdict\":{\"code\":\"WA\"},\"score\":0.0},{\"id\":2,\"verdict\":{\"code\":\"WA\"},\"score\":0.0}]}', '2020-01-18 22:35:33.987', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', '2020-01-18 22:35:36.123', NULL);

--
-- Dumping data for table `jerahmeel_programming_submission`
--

INSERT INTO `jerahmeel_programming_submission` (`jid`, `containerJid`, `problemJid`, `gradingEngine`, `gradingLanguage`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDSUBMV2em0Eo91zaYkmuA2mMw', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'Batch', 'Cpp11', '2020-01-18 22:04:42.648', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:04:42.648', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMCQDtcyVEKvsM6XYO2Y3q', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'Batch', 'Cpp11', '2020-01-18 22:05:02.054', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:05:02.054', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMFIBGlEGTQxpeZXtLoeTD', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'BatchWithSubtasks', 'Cpp11', '2020-01-18 22:06:07.700', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:06:07.700', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBM4KDniVOMVzMQJEMXNlqd', 'JIDSESSjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'Batch', 'Cpp11', '2020-01-18 22:07:54.554', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 22:07:54.554', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDSUBMsj2mVKUY2UqLiIMNEl7U', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'Batch', 'Cpp11', '2020-01-18 22:31:22.996', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:22.996', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDSUBMaUyN8hIBErqKCenJUFHe', 'JIDSESSpB4nAdte4tUwgSiihTFq', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'Batch', 'Cpp11', '2020-01-18 22:31:52.408', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 22:31:52.408', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDSUBM5fPqSOLoRHsssdgMI93a', 'JIDPRSEqfVvs19a3IaOwWYE9M35', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'Batch', 'Cpp11', '2020-01-18 22:32:27.441', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:32:27.441', 'JIDUSERVfszUmquMh0Ae2laygDp'),
('JIDSUBMxm6eXnTKxDj046l6yD7Z', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'BatchWithSubtasks', 'Cpp11', '2020-01-18 22:33:20.349', 'JIDUSERVfszUmquMh0Ae2laygDp', '2020-01-18 22:33:20.349', 'JIDUSERVfszUmquMh0Ae2laygDp'),
('JIDSUBMtXWk5Sh2Vuw2KyoPVaeG', 'JIDPRSEjboijeXaXam4wM7zwIkA', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'BatchWithSubtasks', 'Cpp11', '2020-01-18 22:35:33.986', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', '2020-01-18 22:35:33.986', 'JIDUSER5QiWnVJWzKdjtWtgEfN3');

--
-- Dumping data for table `jerahmeel_stats_user`
--

INSERT INTO `jerahmeel_stats_user` (`userJid`, `score`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 300, '2020-01-18 22:25:46.051', NULL, '2020-01-18 22:25:46.108', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 200, '2020-01-18 22:31:26.731', NULL, '2020-01-18 22:31:53.594', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 130, '2020-01-18 22:32:30.769', NULL, '2020-01-18 22:33:22.358', NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 0, '2020-01-18 22:35:36.131', NULL, '2020-01-18 22:35:36.131', NULL);

--
-- Dumping data for table `jerahmeel_stats_user_problem`
--

INSERT INTO `jerahmeel_stats_user_problem` (`userJid`, `problemJid`, `submissionJid`, `verdict`, `score`, `time`, `memory`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'JIDSUBMV2em0Eo91zaYkmuA2mMw', 'AC', 100, 0, 0, '2020-01-18 22:25:46.003', NULL, '2020-01-18 22:25:46.003', NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'JIDSUBM4KDniVOMVzMQJEMXNlqd', 'AC', 100, 3, 356, '2020-01-18 22:25:46.054', NULL, '2020-01-18 22:25:46.098', NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'JIDSUBMFIBGlEGTQxpeZXtLoeTD', 'AC', 100, 2, 356, '2020-01-18 22:25:46.075', NULL, '2020-01-18 22:25:46.075', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'JIDSUBMsj2mVKUY2UqLiIMNEl7U', 'AC', 100, 2, 356, '2020-01-18 22:31:26.719', NULL, '2020-01-18 22:31:26.719', NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'JIDSUBMaUyN8hIBErqKCenJUFHe', 'AC', 100, 0, 0, '2020-01-18 22:31:53.581', NULL, '2020-01-18 22:31:53.581', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDPROGcKIp3DeCOcLRdz9TX5n9', 'JIDSUBM5fPqSOLoRHsssdgMI93a', 'AC', 100, 0, 0, '2020-01-18 22:32:30.758', NULL, '2020-01-18 22:32:30.758', NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'JIDSUBMxm6eXnTKxDj046l6yD7Z', 'WA', 30, 0, 0, '2020-01-18 22:33:22.351', NULL, '2020-01-18 22:33:22.351', NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'JIDSUBMtXWk5Sh2Vuw2KyoPVaeG', 'WA', 0, 0, 0, '2020-01-18 22:35:36.124', NULL, '2020-01-18 22:35:36.124', NULL);

--
-- Dumping data for table `sandalphon_lesson`
--

INSERT INTO `sandalphon_lesson` (`jid`, `slug`, `additionalNote`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDLESSBQMkZlaJlfafbwDLHoP1', 'lesson-1', '', '2019-10-20 20:44:47.315', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 18:02:05.893', 'JIDUSER7uMucIkm1exJTu7sJvxR');

--
-- Dumping data for table `sandalphon_problem`
--

INSERT INTO `sandalphon_problem` (`jid`, `slug`, `additionalNote`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDPROGSxtSvuGAW4IKreFasSY0', 'batch-1', '', '2019-10-24 22:15:14.603', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 10:35:37.635', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'bundle-1', '', '2019-10-29 07:33:16.638', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2020-01-18 11:52:35.854', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDPROGw21Ya31fwCuEnNJLlVwO', 'batch-with-subtasks-1', '', '2019-12-14 10:49:25.770', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 11:40:39.001', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDBUNDKkmglB5WGpSyTTFGScVk', 'bundle-2', '', '2020-01-18 11:56:13.741', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 11:59:04.140', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDPROGcKIp3DeCOcLRdz9TX5n9', 'batch-2', '', '2020-01-18 17:53:13.053', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2020-01-18 17:55:40.082', 'JIDUSERDMZOs8UHqjsw9DGbB31z');

COMMIT;
