-- phpMyAdmin SQL Dump
-- version 4.7.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 20, 2018 at 05:02 PM
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
-- Database: `judgels_jophiel`
--

--
-- Dumping data for table `jophiel_user`
--

DELETE FROM `jophiel_user` WHERE `username`='superadmin';

INSERT INTO `jophiel_user` (`jid`, `username`, `email`, `password`, `avatarFilename`, `createdAt`, `createdBy`, `createdIp`, `updatedAt`, `updatedBy`, `updatedIp`) VALUES
('JIDUSERGBsbuD2EJ9jYMlGclISJ', 'superadmin', 'superadmin@jophiel.judgels', '1000:ffe7adbe30cf950f3dfaf7036ec80cd30dc19f034a8301bb:51452bbf71298b8090fbf07dbfc945386bcc508f83a29b99', NULL, '2018-08-05 16:37:57.305', NULL, NULL, '2018-08-05 16:37:57.305', NULL, NULL),
('JIDUSER7uMucIkm1exJTu7sJvxR', 'andi', 'andi@jophiel.judgels', '1000:060ce62731682f2a43de103418b1afad8af98bd6f399b569:cbe47b1335de632a21c7a4b56e04214b7c3a349d120867b3', NULL, '2018-08-05 17:03:33.504', NULL, NULL, '2018-08-05 17:11:29.127', NULL, NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'budi', 'budi@jophiel.judgels', '1000:cdbd48d2ab492347a83ba0e8351ff4068f8109d7f77a371a:277f5b263e855fcd59cf3006f7b1c5bfac3de02ba24452f5', NULL, '2018-08-05 17:15:23.586', NULL, NULL, '2018-08-05 17:15:23.586', NULL, NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'caca', 'caca@jophiel.judgels', '1000:f6d2c169f4ed162e7078065e3c83baee8e07c80eb4c3ab19:66660990bc19491f9b60e6bf0912d74da45c94da524750b4', NULL, '2018-08-05 17:16:27.740', NULL, NULL, '2018-08-05 17:16:27.740', NULL, NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'dudi', 'dudi@jophiel.judgels', '1000:f35e1730d05f8c600a64ac56ecf609d639471dffa3223be4:c176c95c192f52a93d49c14f2cd9506b737a8e4188aee621', NULL, '2018-08-05 17:16:27.740', NULL, NULL, '2018-08-05 17:16:27.740', NULL, NULL),
('JIDUSERXCcE36qmrr8Tm46k3hSV', 'emir', 'emir@jophiel.judgels', '1000:72f3edcab574f0b1b814dd1fb761ad8a8d1bfe6298f0354f:cec5a264c4bd34da306271aead1678155577b75a61c6579c', NULL, '2018-08-05 17:16:27.740', NULL, NULL, '2018-08-05 17:16:27.740', NULL, NULL),
('JIDUSERP1gi91MNc1XbXLbPx3dq', 'fuad', 'fuad@jophiel.judgels', '1000:4da2460d8838a109ef8dde5f8183e6d958e2a4a43a0bb578:9cf73cfec1faea4b651cfdb1bafd6f5e3a25fb6385166dc6', NULL, '2018-08-05 17:16:27.740', NULL, NULL, '2018-08-05 17:16:27.740', NULL, NULL);

--
-- Dumping data for table `jophiel_user_info`
--

INSERT INTO `jophiel_user_info` (`userJid`, `name`, `gender`, `country`, `homeAddress`, `shirtSize`, `institutionName`, `institutionCountry`, `institutionProvince`, `institutionCity`, `createdAt`, `createdBy`, `createdIp`, `updatedAt`, `updatedBy`, `updatedIp`) VALUES
('JIDUSER7uMucIkm1exJTu7sJvxR', 'Andi', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:03:33.565', NULL, NULL, '2018-08-05 17:14:56.338', NULL, NULL),
('JIDUSERDMZOs8UHqjsw9DGbB31z', 'Budi', 'MALE', 'US', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:15:23.591', NULL, NULL, '2018-08-05 17:16:07.734', NULL, NULL),
('JIDUSERVfszUmquMh0Ae2laygDp', 'Caca', 'FEMALE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', NULL, NULL, '2018-08-05 17:18:18.115', NULL, NULL),
('JIDUSER5QiWnVJWzKdjtWtgEfN3', 'Dudi', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', NULL, NULL, '2018-08-05 17:18:18.115', NULL, NULL),
('JIDUSERXCcE36qmrr8Tm46k3hSV', 'Emir', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', NULL, NULL, '2018-08-05 17:18:18.115', NULL, NULL),
('JIDUSERP1gi91MNc1XbXLbPx3dq', 'Fuad', 'MALE', 'ID', NULL, NULL, NULL, NULL, NULL, NULL, '2018-08-05 17:16:27.746', NULL, NULL, '2018-08-05 17:18:18.115', NULL, NULL);

--
-- Dumping data for table `jophiel_user_rating`
--

INSERT INTO `jophiel_user_rating` (`time`, `userJid`, `hiddenRating`, `publicRating`, `createdAt`, `createdBy`, `createdIp`) VALUES
('1970-01-18 20:30:43.200', 'JIDUSERDMZOs8UHqjsw9DGbB31z', 2300, 2300, '2018-08-05 18:30:24.011', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1'),
('1970-01-18 20:30:43.200', 'JIDUSER7uMucIkm1exJTu7sJvxR', 3100, 3100, '2018-08-05 18:30:24.032', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1'),
('1970-01-18 20:30:43.200', 'JIDUSERVfszUmquMh0Ae2laygDp', 1700, 1700, '2018-08-05 18:30:24.033', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1'),
('1970-01-18 20:30:43.200', 'JIDUSER5QiWnVJWzKdjtWtgEfN3', 1651, 1651, '2018-08-05 18:30:24.033', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1'),
('1970-01-18 20:30:43.200', 'JIDUSERXCcE36qmrr8Tm46k3hSV', 1800, 1800, '2018-08-05 18:30:24.033', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1'),
('1970-01-18 20:30:43.200', 'JIDUSERP1gi91MNc1XbXLbPx3dq', 2600, 2600, '2018-08-05 18:30:24.033', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1');

--
-- Dumping data for table `jophiel_user_rating_event`
--

INSERT INTO `jophiel_user_rating_event` (`time`, `eventJid`, `createdAt`, `createdBy`, `createdIp`) VALUES
('1970-01-18 20:30:43.200', 'JIDCONT89YMatMq7GqaPQCyyUHU', '2018-08-05 18:30:23.968', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '127.0.0.1');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
