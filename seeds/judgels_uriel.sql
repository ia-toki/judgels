-- phpMyAdmin SQL Dump
-- version 4.7.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 20, 2018 at 05:05 PM
-- Server version: 5.7.19
-- PHP Version: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `judgels_uriel`
--

--
-- Dumping data for table `uriel_contest`
--

INSERT INTO `uriel_contest` (`jid`, `slug`, `name`, `style`, `description`, `beginTime`, `duration`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'joc-1', 'Judgels Open Contest #1', 'ICPC', '<p>Welcome to the first Judgels open contest!</p>\r\n', '2018-08-01 12:00:00.000', 18000000000, '2018-08-05 17:30:35.297', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:15:28.710', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'joc-old', 'Judgels Old Contest', 'IOI', '<p>This is an old contest.</p>\r\n', '2018-01-01 00:00:00.000', 18000000, '2018-08-05 18:11:40.024', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:35:50.263', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_contest_announcement`
--

INSERT INTO `uriel_contest_announcement` (`jid`, `contestJid`, `title`, `content`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOANOtLvAkjKzbAw3vWaZI3t', 'JIDCONTYsWkzwPy8wCF3maJfxqN', 'Good luck', '<p>And have fun!</p>\r\n', 'PUBLISHED', '2018-08-05 17:39:58.816', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 17:39:58.816', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_contest_clarification`
--

INSERT INTO `uriel_contest_clarification` (`jid`, `contestJid`, `topicJid`, `title`, `question`, `answer`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCOCLskqUDQZdvWIRbKgNDFFP', 'JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDCONTYsWkzwPy8wCF3maJfxqN', 'Lunch', 'Will lunch be provided?', 'Yes.', 'ANSWERED', '2018-08-05 18:24:04.866', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 18:24:26.258', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_contest_contestant`
--

INSERT INTO `uriel_contest_contestant` (`contestJid`, `userJid`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERVfszUmquMh0Ae2laygDp', 'APPROVED', '2018-08-05 17:32:08.186', 'JIDUSERVfszUmquMh0Ae2laygDp', '2018-08-05 17:32:08.186', 'JIDUSERVfszUmquMh0Ae2laygDp'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 'APPROVED', '2018-08-05 17:32:25.052', 'JIDUSERDMZOs8UHqjsw9DGbB31z', '2018-08-05 17:32:25.052', 'JIDUSERDMZOs8UHqjsw9DGbB31z'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSER7uMucIkm1exJTu7sJvxR', 'APPROVED', '2018-08-05 17:32:38.079', 'JIDUSER7uMucIkm1exJTu7sJvxR', '2018-08-05 17:32:38.079', 'JIDUSER7uMucIkm1exJTu7sJvxR'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 'APPROVED', '2018-08-20 15:15:21.928', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:15:21.928', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 'APPROVED', '2018-08-20 15:15:26.677', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:15:26.677', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 'APPROVED', '2018-08-20 15:15:28.708', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:15:28.708', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSER7uMucIkm1exJTu7sJvxR', 'APPROVED', '2018-08-20 15:17:43.929', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:17:43.929', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 'APPROVED', '2018-08-20 15:17:47.499', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:17:47.499', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERVfszUmquMh0Ae2laygDp', 'APPROVED', '2018-08-20 15:17:50.373', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:17:50.373', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 'APPROVED', '2018-08-20 15:17:53.406', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:17:53.406', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 'APPROVED', '2018-08-20 15:17:56.161', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:17:56.161', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 'APPROVED', '2018-08-20 15:17:59.518', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:17:59.518', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_contest_module`
--

INSERT INTO `uriel_contest_module` (`contestJid`, `name`, `config`, `enabled`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'CLARIFICATION', '{}', b'1', '2018-08-05 19:59:23.330', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:23.330', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'SCOREBOARD', '{\"isIncognitoScoreboard\":false}', b'1', '2018-08-05 19:59:25.602', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:25.602', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'SUPERVISOR', '{}', b'1', '2018-08-05 19:59:28.084', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:28.084', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'REGISTRATION', '{\"registerStartTime\":1533473972812,\"manualApproval\":false,\"registerDuration\":432000000,\"maxRegistrants\":0}', b'1', '2018-08-05 19:59:32.812', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:32.812', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'CLARIFICATION', '{}', b'1', '2018-08-05 19:59:50.281', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:50.281', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'SUPERVISOR', '{}', b'1', '2018-08-05 19:59:52.148', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:52.148', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'SCOREBOARD', '{\"isIncognitoScoreboard\":false}', b'1', '2018-08-05 19:59:54.004', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:54.004', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'REGISTRATION', '{\"registerStartTime\":1533473997175,\"manualApproval\":false,\"registerDuration\":432000000,\"maxRegistrants\":0}', b'1', '2018-08-05 19:59:57.175', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 19:59:57.175', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_contest_problem`
--

INSERT INTO `uriel_contest_problem` (`contestJid`, `problemJid`, `alias`, `submissionsLimit`, `status`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'A', 0, 'OPEN', '2018-08-20 00:00:00.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 00:00:00.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONTYsWkzwPy8wCF3maJfxqN', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'B', 0, 'OPEN', '2018-08-20 00:00:00.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 00:00:00.000', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDPROGSxtSvuGAW4IKreFasSY0', 'A', 0, 'OPEN', '2018-08-20 15:23:43.499', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:23:43.499', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', 'JIDPROGw21Ya31fwCuEnNJLlVwO', 'B', 0, 'OPEN', '2018-08-20 15:24:55.672', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-20 15:24:55.672', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_contest_scoreboard`
--

INSERT INTO `uriel_contest_scoreboard` (`contestJid`, `scoreboard`, `type`, `createdAt`, `updatedAt`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', '{\"state\":{\"problemJids\":[\"JIDPROGSxtSvuGAW4IKreFasSY0\",\"JIDPROGme6wb9DCQPCKJBEPO4qR\"],\"problemAliases\":[\"A\",\"B\"],\"contestantJids\":[\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"JIDUSERVfszUmquMh0Ae2laygDp\",\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"JIDUSERP1gi91MNc1XbXLbPx3dq\"]},\"content\":{\"entries\":[{\"rank\":1,\"contestantJid\":\"JIDUSERP1gi91MNc1XbXLbPx3dq\",\"totalAccepted\":2,\"totalPenalties\":30,\"lastAcceptedPenalty\":20,\"attemptsList\":[1,1],\"penaltyList\":[10,20],\"problemStateList\":[2,1]},{\"rank\":2,\"contestantJid\":\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"totalAccepted\":2,\"totalPenalties\":31,\"lastAcceptedPenalty\":15,\"attemptsList\":[1,1],\"penaltyList\":[16,15],\"problemStateList\":[1,2]},{\"rank\":3,\"contestantJid\":\"JIDUSERVfszUmquMh0Ae2laygDp\",\"totalAccepted\":1,\"totalPenalties\":50,\"lastAcceptedPenalty\":30,\"attemptsList\":[2,1],\"penaltyList\":[30,15],\"problemStateList\":[1,0]},{\"rank\":4,\"contestantJid\":\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"totalAccepted\":0,\"totalPenalties\":0,\"lastAcceptedPenalty\":0,\"attemptsList\":[2,0],\"penaltyList\":[0,0],\"problemStateList\":[0,0]},{\"rank\":4,\"contestantJid\":\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"totalAccepted\":0,\"totalPenalties\":0,\"lastAcceptedPenalty\":0,\"attemptsList\":[0,0],\"penaltyList\":[0,0],\"problemStateList\":[0,0]},{\"rank\":4,\"contestantJid\":\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"totalAccepted\":0,\"totalPenalties\":0,\"lastAcceptedPenalty\":0,\"attemptsList\":[0,0],\"penaltyList\":[0,0],\"problemStateList\":[0,0]}]}}', 'OFFICIAL', '2018-08-20 00:00:00.000', '2018-08-20 15:41:43.186'),
('JIDCONT89YMatMq7GqaPQCyyUHU', '{\"state\":{\"problemJids\":[\"JIDPROGSxtSvuGAW4IKreFasSY0\",\"JIDPROGw21Ya31fwCuEnNJLlVwO\"],\"problemAliases\":[\"A\",\"B\"],\"contestantJids\":[\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"JIDUSERVfszUmquMh0Ae2laygDp\",\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"JIDUSERP1gi91MNc1XbXLbPx3dq\"]},\"content\":{\"entries\":[{\"rank\":1,\"contestantJid\":\"JIDUSERP1gi91MNc1XbXLbPx3dq\",\"scores\":[100,100],\"totalScores\":200,\"lastAffectingPenalty\":0},{\"rank\":2,\"contestantJid\":\"JIDUSER7uMucIkm1exJTu7sJvxR\",\"scores\":[100,50],\"totalScores\":150,\"lastAffectingPenalty\":0},{\"rank\":3,\"contestantJid\":\"JIDUSERVfszUmquMh0Ae2laygDp\",\"scores\":[75,0],\"totalScores\":75,\"lastAffectingPenalty\":0},{\"rank\":4,\"contestantJid\":\"JIDUSERXCcE36qmrr8Tm46k3hSV\",\"scores\":[0,null],\"totalScores\":0,\"lastAffectingPenalty\":0},{\"rank\":4,\"contestantJid\":\"JIDUSER5QiWnVJWzKdjtWtgEfN3\",\"scores\":[null,null],\"totalScores\":0,\"lastAffectingPenalty\":0},{\"rank\":4,\"contestantJid\":\"JIDUSERDMZOs8UHqjsw9DGbB31z\",\"scores\":[null,null],\"totalScores\":0,\"lastAffectingPenalty\":0}]}}', 'OFFICIAL', '2018-08-20 15:33:27.529', '2018-08-20 15:35:43.211');

--
-- Dumping data for table `uriel_contest_style`
--

INSERT INTO `uriel_contest_style` (`contestJid`, `config`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDCONTYsWkzwPy8wCF3maJfxqN', '{\"wrongSubmissionPenalty\":20,\"languageRestriction\":{\"allowedLanguageNames\":[]}}', '2018-08-05 17:30:35.327', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 17:30:35.327', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDCONT89YMatMq7GqaPQCyyUHU', '{\"usingLastAffectingPenalty\":false,\"languageRestriction\":{\"allowedLanguageNames\":[]}}', '2018-08-05 18:11:40.037', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 18:11:40.037', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');

--
-- Dumping data for table `uriel_role_admin`
--

INSERT INTO `uriel_role_admin` (`userJid`, `createdAt`) VALUES
('JIDUSERGBsbuD2EJ9jYMlGclISJ', '2018-08-05 00:00:00.000');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
