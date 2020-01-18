-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jan 18, 2020 at 09:51 AM
-- Server version: 8.0.12
-- PHP Version: 7.1.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `judgels_sandalphon`
--

--
-- Dumping data for table `sandalphon_lesson`
--

INSERT INTO `sandalphon_lesson` (`jid`, `slug`, `additionalNote`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDLESSBQMkZlaJlfafbwDLHoP1', 'lesson-1', '', '2019-10-20 20:44:47.315', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 18:02:05.893', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');
COMMIT;

--
-- Dumping data for table `sandalphon_problem`
--

INSERT INTO `sandalphon_problem` (`jid`, `slug`, `additionalNote`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`) VALUES
('JIDPROGSxtSvuGAW4IKreFasSY0', 'batch-1', '', '2019-10-24 22:15:14.603', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 10:35:37.635', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDBUNDcdQcvQ4sB6RgNQ9ryOsp', 'bundle-1', '', '2019-10-29 07:33:16.638', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 11:52:35.854', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPROGw21Ya31fwCuEnNJLlVwO', 'batch-with-subtasks-1', '', '2019-12-14 10:49:25.770', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 11:40:39.001', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDBUNDKkmglB5WGpSyTTFGScVk', 'bundle-2', '', '2020-01-18 11:56:13.741', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 11:59:04.140', 'JIDUSERGBsbuD2EJ9jYMlGclISJ'),
('JIDPROGcKIp3DeCOcLRdz9TX5n9', 'batch-2', '', '2020-01-18 17:53:13.053', 'JIDUSERGBsbuD2EJ9jYMlGclISJ', '2020-01-18 17:55:40.082', 'JIDUSERGBsbuD2EJ9jYMlGclISJ');
COMMIT;
